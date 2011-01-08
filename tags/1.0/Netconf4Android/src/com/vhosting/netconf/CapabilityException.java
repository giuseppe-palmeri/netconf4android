package com.vhosting.netconf;

/**
 * This exception must necessarily be thrown when uses an operation
 * and you are not meeting the set of capabilities available to the server.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class CapabilityException extends Exception
{

	private static final long serialVersionUID = 1L;

	/**
	 * Constructs an instance of the exception by providing a detailed message.
	 * 
	 * @param detailMessage
	 *            The detail message.
	 */
	public CapabilityException(String detailMessage)
	{
		super(detailMessage);
	}

}
