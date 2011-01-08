package com.vhosting.netconf.frame;

/**
 * This interface defines the reply to an RPC operation.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public interface RpcReply
{

	/**
	 * Get the message id of the associated RPC operation.
	 * 
	 * <b>Note:</b> This implementation is not necessary to
	 * check that the message id is null. Although the RFC4741
	 * puts it possible, but absolutely useless.
	 * 
	 * This happens when an operation is sent to the server without
	 * a message id to which the server will respond with an error.
	 * 
	 * This library does not send a message without its own mesage id.
	 * 
	 * @return The message id of the associated RPC
	 *         operation; null if an id was not specified.
	 */
	public Integer getMessageId();

	/**
	 * Check if the RPC Reply message contains errors.
	 * 
	 * @return false if the operation has not generated errors and
	 *         possible data can to be available; true otherwise.
	 */
	public boolean containsErrors();

	/**
	 * Returns the error associated with the RPC operation associated if any.
	 * 
	 * @return An array of errors; If empty, it means that there were no errors
	 *         to report.
	 */
	public RpcReplyError[] getErrors();

	/**
	 * Load into an RPC structure the values for it if exist.
	 * 
	 * @param rpc
	 *            The RPC structure.
	 * @return true if it has been possible to load values into the structure;
	 *         false otherwise.
	 */
	public boolean load(Rpc rpc);

}
