package com.vhosting.netconf.frame;

/**
 * This listener is the listener of a specific RPC reply
 * to a specific operation.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public interface RpcReplySpecificListener
{

	/**
	 * Implement this method to intercept the response of the RPC operation.
	 * 
	 * @param reply
	 *            The RPC reply of the operation.
	 */
	public void processRpcReply(RpcReply reply);
}
