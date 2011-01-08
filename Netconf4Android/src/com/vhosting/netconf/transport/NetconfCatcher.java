package com.vhosting.netconf.transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.vhosting.netconf.messages.HelloMsg;
import com.vhosting.netconf.messages.NotificationMsg;
import com.vhosting.netconf.messages.RpcReplyMsg;
import com.vhosting.netconf.notification.NotificationsListener;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.frame.RpcHandler;
import com.vhosting.netconf.frame.RpcReply;
import com.vhosting.netconf.frame.RpcReplyListener;
import com.vhosting.netconf.frame.RpcReplySpecificListener;
import com.vhosting.netconf.transport.NetconfTransportError.FailCause;
import com.vhosting.netconf.transport.NetconfTransportError.NetconfFailCause;
import com.vhosting.netconf.transport.NetconfTransportEvent.EventType;


/**
 * This abstract class implements all the aspects that
 * characterize a Netconf connection and through its
 * protected methods you can create a subclass that
 * implements directly a certain type of transport protocol.
 * 
 * <pre>
 * Examples include:
 * 
 * 1. Netconf over SSH2 (RFC 4742)
 * 2. Netconf over SOAP (RFC 4743)
 * 3. Netconf over BEEP (RFC 4744)
 * ...
 * </pre>
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public abstract class NetconfCatcher
{

	private static Integer wiredMessageId = 0;
	private HashMap<Integer, RpcReplySpecificListener> listeners = new HashMap<Integer, RpcReplySpecificListener>();

	private Session session = new Session();
	private RpcHandler handler;
	private String label;

	

	/**
	 * It enables a subclass to be instantiated.
	 * @param labelConnection A label used to identify the connection. 
	 */
	protected NetconfCatcher(String labelConnection)
	{
		label = labelConnection;
		handler = createRpcHandler();
	}

	/**
	 * Set the NetconfCatcherListener in order to intercept transport
	 * events.
	 * Only one listener is allowed.
	 * 
	 * @param ncl
	 *            A NetconfCatcherListener instance.
	 * @see NetconfCatcherListener
	 */
	public final void setNetconfCatcherListener(NetconfCatcherListener ncl) {
		this.ncl = ncl;
	}

	private NetconfCatcherListener ncl;

	/**
	 * Packages the message hello with the abilities of this implementation of
	 * netconf and send the message to the server.
	 * 
	 * <b>This method should be called
	 * immediately after the connection has been established with the
	 * server.</b>
	 * 
	 * This is usually the first method that is invoked in the
	 * process of connecting with the server That Will Be Implemented.
	 * 
	 * @throws IOException
	 *             throw if there are not a connection and
	 *             the message sent can not exercise.
	 */
	protected final void sendHelloMsg() throws IOException {
		HelloMsg hello = new HelloMsg(Session.getRegisteredCapabilities());
		ByteArrayOutputStream b;
		hello.dump(b = new ByteArrayOutputStream());
		b.flush();
		byte[] array = b.toByteArray();
		doSendDataToServer(array);

		writeLog(array, true);
	}

	/**
	 * The first package is expected to arrive from
	 * the server is a Hello message.
	 * 
	 * <b>If this is not received, the method is committed
	 * to dropping the connection, if established.</b>
	 * 
	 * This method can be invoked before or after sendHelloMsg()
	 * in the process of connecting with the server that
	 * will be implemented.
	 * 
	 * @throws IOException
	 *             throw if there are not a connection and
	 *             the message sent can not exercise.
	 */
	private final boolean checkServerHelloMsg() throws IOException {
		try
		{
			boolean b = false;
			HelloMsg hello = HelloMsg
					.createServerHello(_doReadDataFromServer());
			if (hello == null)
				b = false;

			session.activeSession(hello.getCapabilitiesURI(),
					HelloMsg.getSessionId());

			b = true;

			if (!b)
				throw new RuntimeException("Invalid hello message.");
		}
		catch (IOException e)
		{
			_doDisconnect();
			throw e;

		}
		catch (Exception e)
		{
			_doDisconnect();
			throwTransportException(e,
					NetconfFailCause.CAUSE_NETCONF_PROTOCOL_TROUBLES, true);
			return false;
		}

		return true;

	}

	/**
	 * This method must be used into connection process when
	 * an exception occurs or a condition in which the connection
	 * can not be established.
	 * 
	 * Which in a condition when the connection can not be
	 * established occurs, an exception will be created and
	 * passed as first argument.
	 * 
	 * @param e
	 *            The exception that occurred during the connection
	 *            process or the exception is created when there is a
	 *            condition in which the connection can not be established.
	 * 
	 * @param cause
	 *            The fail cause.
	 * @param withClosedConnection
	 *            true if the connection is closed
	 *            after it was opened in prior periods during the connection
	 *            process;
	 *            false if the connection was never opened before.
	 */
	protected final void throwTransportException(Exception e, FailCause cause,
			boolean withClosedConnection) {
		NetconfTransportException ex = new NetconfTransportException(e, cause);
		//ex.printStackTrace();
		
		if (withClosedConnection) writeLog(ex, EventType.CONNECTION_CLOSED_BY_SERVER);
		else writeLog(ex, EventType.CONNECTION_CANNOT_BE_OPENED);
		
		if (ncl != null)
		{
			try
			{
				if (withClosedConnection)
				ncl.processTransportEvents(new NetconfTransportEvent(ex, EventType.CONNECTION_CLOSED_BY_SERVER));
				else 
					ncl.processTransportEvents(new NetconfTransportEvent(ex, EventType.CONNECTION_CANNOT_BE_OPENED));
			}
			catch (Exception ee)
			{
				ee.printStackTrace();
			}
		}
		else
			throw ex;
	}

	private void writeLog(NetconfTransportException ex, EventType e) {
		if (getLogLevel() != LogLevel.NONE)
		{
			FailCause cause = ex.getFailCause();

			writeLog(label + " {");
			writeLog("    "+e+" {");
			writeLog("        Fail Cause: " + cause);
			writeLog("        Message   : " + ex.getMessage());
			writeLog("    }");
			writeLog("}");

		}
	}

	private void writeLog(byte[] array, boolean isOutgoing) {
		if (getLogLevel() == LogLevel.MESSAGES)
		{
			Integer s = session.getSessionId();
			String sessId = (s == null) ? "<not yet>" : "" + s;
			writeLog(label + " {");
			if (isOutgoing)
				writeLog("    Outgoing Netconf message; Session Id: " + sessId
						+ " {");
			else
				writeLog("    Input Netconf message; Session Id: " + sessId + " {");
			String msg;
			try
			{
				msg = new String(array, "UTF-8");
				msg = "        " + msg.replaceAll("\n", "\n        ");
			}
			catch (UnsupportedEncodingException e)
			{
				msg = "    Unable to print the Netconf message: " + e.getMessage();
			}
			writeLog(msg);
			writeLog("    }");
			writeLog("}");
		}

	}

	/**
	 * Check if the connection is alive or not.
	 * @return true if the connection is alive; false otherwise.
	 */
	public abstract boolean isConnected();

	
	private void writeLog(EventType e) {
		if (getLogLevel() != LogLevel.NONE)
		{
			Integer s = session.getSessionId();
			String sessId = (s == null) ? "<not yet>" : "" + s;
			writeLog(label + " {");
			if (EventType.CONNECTION_CLOSED_BY_USER == e)
			{
				writeLog("    Connection closed by user; Session Id: " + sessId
						+ ".");
			}
			else if (EventType.CONNECTION_CLOSED_BY_SERVER == e)
			{
				writeLog("    Connection closed by server; Session Id: " + sessId
						+ ".");
			}
			writeLog("}");
		}
	}

	/**
	 * This method must necessarily be invoked at the end of the process
	 * when the server connection is lost to natural causes.
	 * This even though one of the methods:
	 * 
	 * <pre>
	 * sendHelloMsg();
	 * checkServerHelloMsg();
	 * whileRpcReplyCatching();
	 * </pre>
	 * 
	 * fails for an exception, which means that the connection has been
	 * terminated by the server.
	 */
	protected void fireConnectionClosed() {
		if (ncl != null)
		{
			try
			{
				if (!closedByUser)
				{
					writeLog(EventType.CONNECTION_CLOSED_BY_SERVER);
					ncl.processTransportEvents(new NetconfTransportEvent(EventType.CONNECTION_CLOSED_BY_SERVER));
				}
				else
				{
					writeLog(EventType.CONNECTION_CLOSED_BY_USER);
					ncl.processTransportEvents(new NetconfTransportEvent(EventType.CONNECTION_CLOSED_BY_USER));
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		closedByUser = false;
	}

	/**
	 * This method must necessarily be implemented so that any
	 * type of message can be sent to the Netconf server.
	 * 
	 * This method must necessarily throw an exception when the
	 * server connection was lost.
	 * 
	 * In particular, for the ssh protocol this method should be implemented
	 * by adding the string ']]>]]>' after the message.
	 * See the RFC 4742.
	 * 
	 * Similarly, this method be overridden if necessary for other types
	 * of transport protocols.
	 * 
	 * @param bytes
	 *            The byte array containing the Netconf message.
	 * @throws IOException
	 *             Throw this exception when the server connection was lost.
	 */
	protected abstract void doSendDataToServer(byte[] bytes) throws IOException;

	/**
	 * This method must necessarily be implemented when required
	 * to read the next message from the server.
	 * 
	 * This method must necessarily throw an exception when the
	 * server connection was lost.
	 * 
	 * In particular, for the ssh protocol this method should be implemented
	 * by removing the string ']]>]]>' after the message.
	 * See the RFC 4742.
	 * 
	 * @return The message read and made available.
	 * @throws IOException
	 *             Throw this exception when the server connection was lost.
	 */
	protected abstract byte[] doReadDataFromServer() throws IOException;

	private synchronized byte[] _doReadDataFromServer() throws IOException {
		byte[] in = doReadDataFromServer();
		writeLog(in, false);
		return in;
	}

	private boolean closedByUser = false;

	/**
	 * Invoke this method if you want to end the connection at
	 * any point in your application, such as when the user
	 * wants to do it manually.
	 */
	public final void disconnect() {
		closedByUser = true;

		doDisconnect();
		session.inactiveSession();
	}

	/**
	 * This method must necessarily be implemented with the
	 * code necessary to perform a disconnect with the server.
	 * 
	 * <b>Not have to worry about managing any exceptions,
	 * due to the disconnection.</b>
	 * 
	 * @return true se si è provveduto ad effettuare la
	 *         disconnessione; false se non è stato necessario
	 *         effettuare la disconnessione.
	 */
	protected abstract boolean doDisconnect();

	private synchronized boolean _doDisconnect() {
		closedByUser = false;
		boolean rtn = doDisconnect();
		session.inactiveSession();
		return rtn;
	}

	private final BlockingQueue<RpcReplyMsg> queue = new ArrayBlockingQueue<RpcReplyMsg>(
			24, true);

	/**
	 * This method must be called immediately following methods:
	 * 
	 * <pre>
	 * sendHelloMsg();
	 * checkServerHelloMsg();
	 * </pre>
	 * 
	 * The invocation of the method will generate an infinite loop on
	 * reading a message from the server.
	 * 
	 * This infinite loop will end only by throwing an exception that
	 * indicates the end of the connection to the server.
	 * 
	 * @throws IOException
	 *             Throw this exception if the connection is closed for any
	 *             reason.
	 */
	protected synchronized final void whileRpcReplyCatching()
			throws IOException {
		if (!checkServerHelloMsg())
			return;
		try
		{
			if (ncl != null)
			{

				Thread t = new Thread(new Runnable()
				{
					public void run() {

						ncl.processReadyForRpcRequests(handler);
					}
				});
				t.start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		while (true)
		{
			byte[] in;
			try
			{

				in = _doReadDataFromServer();

			}
			catch (IOException e)
			{

				throw e;
			}
			try
			{

				RpcReplyMsg rpcReply = RpcReplyMsg.createServerRpcReply(in);

				if (rpcReply != null)
				{
					Integer messageId = rpcReply.getMessageId();
					synchronized (v)
					{

						if (messageId == null || !v.contains(messageId))
						{
							RpcReplySpecificListener l = null;
							Set<Integer> kl = listeners.keySet();
							for (Integer mId : kl)
							{
								if (messageId != null && messageId == mId)
								{
									l = listeners.get(mId);
									break;
								}
							}

							if (l != null)
							{
								try
								{
									l.processRpcReply(rpcReply);
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}

							}
							else
							{
								try
								{
									if (rpcl != null)
										rpcl.processRpcReply(rpcReply,
												messageId);
								}
								catch (Exception e)
								{
									e.printStackTrace();
								}
							}

						}
						else
							queue.put(rpcReply);
					}
				}
				if (nl != null)
				{
					NotificationMsg notification = NotificationMsg
							.createNotification(in);
					if (notification != null)
					{
						try
						{
							nl.processNotification(notification);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}

			}
			catch (Exception e)
			{
				_doDisconnect();
				throwTransportException(e,
						NetconfFailCause.CAUSE_NETCONF_PROTOCOL_TROUBLES, true);
			}

		}
	}

	private Vector<Integer> v = new Vector<Integer>();

	private RpcReplyListener rpcl;

	private NotificationsListener nl;

	private RpcHandler createRpcHandler() {
		RpcHandler handler = new RpcHandler()
		{

            @Override
			public Session getSession() {
				return session;
			}
			
			
			@Override
			public final void setRpcReplyListener(RpcReplyListener listener) {
				rpcl = listener;

			}

			@Override
			public final void setNotificationsListener(
					NotificationsListener listener) {
				nl = listener;

			}

			@Override
			public final RpcReply sendSyncRpc(Rpc operation) throws IOException {

				int messageId = sendRpc(operation);

				synchronized (v)
				{
					v.add(messageId);
				}
				RpcReplyMsg rpcReply = null;

				int tic = 0;

				while (true)
				{

					Iterator<RpcReplyMsg> i = queue.iterator();

					while (i.hasNext())
					{
						RpcReplyMsg r = i.next();
						if (r.getMessageId() == messageId)
						{
							rpcReply = r;
							break;
						}
					}
					if (rpcReply != null)
					{
						queue.remove(rpcReply);
						synchronized (v)
						{
							v.removeElement(messageId);
						}
						return rpcReply;
					}

					try
					{

						Thread.sleep(500);
					}
					catch (InterruptedException e)
					{

					}
					if (tic >= syncTimeoutTic)
					{
						// _doDisconnect();
						throwTransportException(
								new IOException(
										"Timeout on receiving the RPC reply for the synchronous request: "
												+ syncTimeoutTic * 500),
								NetconfFailCause.CAUSE_NETCONF_PROTOCOL_TROUBLES,
								false);
					}
					tic++;
				}
			}

			@Override
			public final int sendRpc(Rpc operation) throws IOException {
				return _sendRpc(operation, null);
			}

			@Override
			public final void sendRpc(Rpc operation,
					RpcReplySpecificListener listener) throws IOException {
				_sendRpc(operation, listener);
			}

			private final int _sendRpc(Rpc operation,
					RpcReplySpecificListener listener) throws IOException {
				synchronized (wiredMessageId)
				{
					wiredMessageId++;

					ByteArrayOutputStream b;
					operation.dumpRpcMessage(session, wiredMessageId,
							b = new ByteArrayOutputStream());
					b.flush();

					// Imposta il listener prima di spedire.
					if (listener != null)
					{
						listeners.put(wiredMessageId, listener);
					}
					byte[] array = b.toByteArray();
					doSendDataToServer(array);

					writeLog(array, true);

					return wiredMessageId;
				}
			}
		};
		return handler;
	}

	private int syncTimeoutTic = 10000;

	/**
	 * Allows you to specify a request timeout for synchronous RPC calls.
	 * 
	 * @param timeout
	 *            The timeout.
	 */
	public final void setSyncRequestsTimeout(int timeout) {
		this.syncTimeoutTic = timeout / 500 + (timeout % 500);
	}

	private static LogLevel logLevel = LogLevel.NONE;
	private static PrintStream logStream = System.out;

	/**
	 * Set the stream on which the log will be reversed.
	 * The default is System.out.
	 * 
	 * @param stream
	 *            The stream on which the log will be
	 *            reversed.
	 */
	public static final void setLogStream(PrintStream stream) {
		logStream = stream;
	}

	/**
	 * This enumeration lists the possible levels
	 * of writing logs.
	 * 
	 * @author Giuseppe Palmeri
	 */
	public static enum LogLevel
	{
		/**
		 * No log will be produced.
		 * This is the default level.
		 */
		NONE,

		/**
		 * Only the activity of the
		 * protocol is being recorded.
		 */
		PROTOCOL,

		/**
		 * The activity of the protocol will be
		 * recorded, so too, the XML Netconf messages.
		 */
		MESSAGES
	}

	/**
	 * Enables the writing of the logs.
	 * 
	 * @see #setLogStream(PrintStream stream)
	 * @see #getLogLevel()
	 * @see LogLevel
	 * @param level
	 *            The log level flag.
	 */
	public final static void enableLog(LogLevel level) {
		logLevel = level;
	}

	/**
	 * Get the log stream.
	 * 
	 * @return The log stream.
	 */
	public static final PrintStream getLogStream() {
		return logStream;
	}

	/**
	 * Get the log level set.
	 * 
	 * @return the log level set.
	 * @see #enableLog(LogLevel level)
	 * @see LogLevel
	 */
	public static final LogLevel getLogLevel() {
		return logLevel;
	}


	private static final void writeLog(String s) {
		logStream.println(s);
	}

}
