package com.vhosting.netconf.frame;

import java.io.IOException;

import com.vhosting.netconf.notification.NotificationsListener;
import com.vhosting.netconf.transport.Session;

/**
 * This interface defines the methods useful for handling 
 * the sending of RPC commands and the receiving of RPC replies.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public interface RpcHandler {

	/**
	 * Send an operation and waits until you get a reply.
	 * @param operation The RPC operation.
	 * @return The RPC reply.
	 * @throws IOException throws this exception when the connection is no 
	 * longer active before and during the exchange of messages.
	 */
	public RpcReply sendSyncRpc(Rpc operation) throws IOException;


	/**
	 * Send a RPC operation.
	 * 
	 * the response can be intercepted using the method:
	 * 
	 * setRpcReplyListener(RpcReplyListener listener);
	 * 
	 * @param operation The RPC operation.
	 * @return The message id of the message which was sent the operation.
	 * @throws IOException Throws this exception when the connection is no 
	 * longer active before and during the exchange of messages.
	 * @see #setRpcReplyListener(RpcReplyListener listener)
	 */
	public int sendRpc(Rpc operation) throws IOException;


	/**
	 * Send a RPC operation specifying a listener that will intercept the RPC reply.
	 * @param operation The RPC operation.
	 * @param listener The listener that will intercept the RPC reply.
	 * @throws IOException Throws this exception when the connection is no 
	 * longer active before and during the exchange of messages.
	 */
	public void sendRpc(Rpc operation, RpcReplySpecificListener listener) throws IOException;

	

	/**
	 * Set the general listener of RPC replies that will intercept 
	 * RPC replies to which there is no direct association with an operation.
	 * @param listener The listener that will intercept the RPC replies.
	 */
	public void setRpcReplyListener(RpcReplyListener listener);
	
	
	/**
	 * Set the Notifications listener that will intercept Netconf notifications.
	 * @param listener The listener that will intercept the Netconf notifications.
	 */
	public void setNotificationsListener(NotificationsListener listener);
	
	/**
	 * Get the session for this connection.
	 * The session will remain inactive until when
	 * will you make a connection with the server.
	 * 
	 * @return The session.
	 */
	public Session getSession();

}
