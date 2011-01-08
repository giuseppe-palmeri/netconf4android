package com.vhosting.netconf;

/**
 * This exception is thrown when errors occur during
 * parsing of a SubtreeFilter.
 * 
 * @author Giuseppe Palmeri
 * 
 */
public class SubtreeFilterException extends RuntimeException
{

	private static final long serialVersionUID = 1L;

	/**
	 * Create the exception with a detail message.
	 * 
	 * @param detailMessage
	 *            The detail message of the exception.
	 */
	public SubtreeFilterException(String detailMessage)
	{
		super(detailMessage);
	}
}
