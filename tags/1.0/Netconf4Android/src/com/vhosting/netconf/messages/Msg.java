package com.vhosting.netconf.messages;

/**
 * This interface defines several methods that a Netconf message must provide.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
interface Msg
{

	/**
	 * Validate the message.
	 * 
	 * @return true if the document is a validated Netconf XML Document; false
	 *         otherwise.
	 */
	public boolean validate();

}
