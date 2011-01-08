package com.vhosting.netconf.frame;

/**
 * This is the general listener of RPC reply
 * to operations that were not specified a specific listener.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public interface RpcReplyListener
{

	/**
	 * Implement this method when you do not associate
	 * a specific listener for RPC reply to an operation.
	 * 
	 * @param reply
	 *            A RPC reply.
	 * @param messageId
	 *            The message id of the operation which
	 *            is associated with the RPC reply.
	 */
	public void processRpcReply(RpcReply reply, int messageId);
}
