package com.vhosting.netconf.frame;

/**
 * This interface makes available information about an error
 * received in response to a human readable form.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public interface RpcReplyErrorMessage
{
	/**
	 * The language code that was written the message received.
	 * 
	 * The language code is the code that identifies a language.
	 * 
	 * Examples:
	 * 
	 * <pre>
	 * en - English (default)
	 * it - Italian
	 * fr - French
	 * ...
	 * </pre>
	 * 
	 * @return The language code.
	 */
	String getLanguage();

	/**
	 * Get the RPC reply error message in a human readable form.
	 * 
	 * @return The human readable message.
	 */
	String getMessage();
}