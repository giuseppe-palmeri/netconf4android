package com.vhosting.netconf.transport;

import com.vhosting.netconf.frame.RpcHandler;

/**
 * 
 * This listener is used by NetconfCatcher to allow the
 * management of events relating to the connection-level
 * transport protocol.
 * 
 * <b>It is also the entry point for sending and receiving
 * Netconf messages.</b>
 * 
 * It can be said that this interface is also the entry
 * point of a Netconf application.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public interface NetconfCatcherListener
{

	/**
	 * Through the implementation of this method can handle any
	 * kind of event that can be triggered during the operation
	 * of the transport protocol and the netconf protocol when
	 * sending and receiving messages.
	 * 
	 * @param event
	 *            The Netconf transport event.
	 * @see NetconfTransportEvent
	 */
	public void processTransportEvents(NetconfTransportEvent event);




	/**
	 * The method is called when is finally an object of type
	 * RpcHandler that allows the sending and receiving Netconf messages.<br>
	 * <br>
	 * 
	 * <b>There are not other ways to get to an object of this type.</b>
	 * 
	 * @param rpcHandler
	 *            The Rpc handler.
	 */
	public void processReadyForRpcRequests(RpcHandler rpcHandler);
}
