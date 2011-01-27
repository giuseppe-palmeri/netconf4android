package com.vhosting.netconf.transport.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.HTTPProxyData;
import ch.ethz.ssh2.Session;

import com.vhosting.netconf.transport.NetconfCatcher;
import com.vhosting.netconf.transport.NetconfTransportError.TransportFailCause;

/**
 * This class is the entry point for to establish 
 * a connection Netconf over SSH-2.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class NetconfSshCather extends NetconfCatcher
{

	private SshAuthInfo ai;
	private String subSystem;

	private boolean isConnected;
	
	/**
	 * Building the catcher for a connection over SSH-2.
	 * @param labelConnection A label used to identify the connection. 
	 * @param ai
	 *            The authentication informations.
	 */
	public NetconfSshCather(String labelConnection, SshAuthInfo ai)
	{
		super(labelConnection);
		this.ai = ai;
		this.subSystem = "netconf";
	}
	
	/**
	 * Building the catcher for a connection over SSH-2.
	 * @param labelConnection A label used to identify the connection. 
	 * @param ai
	 *            The authentication informations.
	 * @param subSystem
	 *            The name of the SSH-2 Netconf subsystem.
	 */
	public NetconfSshCather(String labelConnection, SshAuthInfo ai, String subSystem)
	{
		super(labelConnection);
		this.ai = ai;
		this.subSystem = subSystem;
	}

	private Connection conn;
	private Session sess;
	private NetconfSshReader br;
	private Runnable rCon;


	/**
	 * Create a Runnable connection using a specific name for the remote Netconf
	 * sybsystem.
	 * 
	 * <pre>
	 * 1. make the connection with the server;
	 * 2. authenticate;
	 * 3. Send a welcome message;
	 * 4. Listen for any messages from the server.
	 * </pre>
	 * 
	 * @return A Runnable object that run the connection process.
	 */
	public Runnable getRunnableConnection() {

		if (rCon != null) return rCon;
		Runnable rCon = new Runnable()
		{

			

			public void run() {

				/* Create a runnable connection instance */

				conn = new Connection(ai.getHost().getHostName(), ai.getHost()
						.getPort());
				synchronized (conn)
				{
					if (ai.getProxyHost() != null)
						conn.setProxyData(new HTTPProxyData(ai.getProxyHost()
								.getHostName(), ai.getProxyHost().getPort()));

					/* Now connect */

					try
					{
						conn.connect();
						isConnected = true;
					}
					catch (Exception e)
					{
						String msg = "";
						if (ai.getProxyHost() != null)
						{
							msg = " (Http Proxy Server: "+ai.getProxyHost()+")";
						}
						throwTransportException(new Exception(e.getMessage() + msg),
								TransportFailCause.CAUSE_CONNECTION_TROUBLES,
								false);
						return;
					}

					/* Now try to authenticate. */
					try
					{
						boolean isAuthenticated = false;

						if (ai.authType == SshAuthInfo.AuthType.BASIC_AUTH)
							isAuthenticated = conn.authenticateWithPassword(
									ai.getUname(), ai.getPasswd());
						else if (ai.authType == SshAuthInfo.AuthType.PUBLICKEY_AUTH)
							isAuthenticated = conn.authenticateWithPublicKey(
									ai.getUname(), ai.getPemFile(),
									ai.getPemFilePass());

						if (isAuthenticated == false)
							throw new IOException("Authentication failed.");
					}
					catch (Exception e)
					{
						conn.close();
						throwTransportException(
								e,
								TransportFailCause.CAUSE_AUTHENTICATION_TROUBLES,
								true);
						return;
					}

					/* Create a session */

					try
					{
						sess = conn.openSession();
					}
					catch (IOException e)
					{
						conn.close();
						throwTransportException(e,
								SshFailCause.CAUSE_SSH_SESSION_TROUBLES, true);
						return;
					}

					/* Now start the Netconf sub system. */

					try
					{

						sess.startSubSystem(subSystem);
					}
					catch (Exception e)
					{
						conn.close();
						throwTransportException(e,
								SshFailCause.CAUSE_SSH_SUBSYSTEM_TROUBLES, true);
						return;
					}
				}

				br = new NetconfSshReader(sess.getStdout());

				try
				{

					/*
					 * Send the Netconf Hello message with the statement
					 * of capabilities.
					 */
					sendHelloMsg();

					/*
					 * At this point begins to read the messages from the
					 * server.
					 */
					whileRpcReplyCatching();

					/* The server is now ready to receive. */
				}
				catch (IOException e)
				{
					/* The connection is dropped. */
					/* Nothing to do. */

				}

				/* There are warrants that there is no longer a connection */

				/* Close this session */

				sess.close();

				/* Close the connection */

				conn.close();

				/* It is certain that at this point the connection is lost. */
				fireConnectionClosed();

			}
		};
		this.rCon = rCon;
		return rCon;
	}

	private static byte[] endMessage = { ']', ']', '>', ']', ']', '>', '\n' };

	private static void printSshEndStream(OutputStream out) throws IOException {
		out.write(endMessage);
		out.flush();
	}

	@Override
	protected boolean doDisconnect() {
		if (conn != null)
		{
			synchronized (conn)
			{
				try
				{
				    sess.close();
					conn.close();
					isConnected = false;
				}
				catch(Exception e)
				{
					// The connection is already closed.
				}
			}
			return true;
		}
		return false;
	}

	@Override
	protected void doSendDataToServer(byte[] bytes) throws IOException {
		OutputStream out = sess.getStdin();
		out.write(bytes);
		printSshEndStream(sess.getStdin());

	}

	@Override
	protected byte[] doReadDataFromServer() throws IOException {
		byte[] s = br.readMessage();
		
		if (s == null)
			throw new IOException("End of stream.");
		return s;
	}

	@Override
	public boolean isConnected() 
	{
		return isConnected;
	}
}

final class NetconfSshReader
{

	Vector<String> v = new Vector<String>();
	private InputStream in;
	private byte[] buff = new byte[10000];
	private int pos = 0;

	private char[] closeSeq = { ']', ']', '>', ']', ']', '>' };
	private String lastMessage;

	private MessageInputStream mis = new MessageInputStream();

	protected NetconfSshReader(InputStream is)
	{
		in = is;
	}

	synchronized byte[] readMessage() throws IOException {

		int c;
		pos = 0;

		v.clear();
		while ((c = in.read()) != -1)
		{
			buff[pos++] = (byte) c;

			if (pos >= closeSeq.length
					&& closeSeq[0] == buff[pos - closeSeq.length]
					&& closeSeq[1] == buff[pos - closeSeq.length + 1]
					&& closeSeq[2] == buff[pos - closeSeq.length + 2]
					&& closeSeq[3] == buff[pos - closeSeq.length + 3]
					&& closeSeq[4] == buff[pos - closeSeq.length + 4]
					&& closeSeq[5] == buff[pos - closeSeq.length + 5])
			{
				String s = new String(buff, 0, pos - closeSeq.length, "UTF-8");
				String rtn = "";
				for (String ss : v)
				{
					rtn += ss;
				}

				v.clear();
				lastMessage = rtn + s;
				return lastMessage.getBytes("UTF-8");
			}

			if (pos == buff.length)
			{
				String s = new String(buff, 0, pos - closeSeq.length, "UTF-8");

				v.add(s);
				System.arraycopy(buff, pos - closeSeq.length, buff, 0,
						closeSeq.length);
				pos = closeSeq.length;
			}
		}
		return null;
	}

	InputStream getMessageInputStream() {
		if (lastMessage == null)
			return null;
		mis.setString(lastMessage);
		return mis;
	}

	class MessageInputStream extends InputStream
	{
		int pos = 0;
		private byte[] s;

		@Override
		public int read() throws IOException {
			if (s == null)
				return -1;
			if (pos >= s.length)
				return -1;
			return s[pos++];
		}

		void setString(String s) {
			if (s == null)
				return;
			pos = 0;
			this.s = s.getBytes();
		}
	}
}
