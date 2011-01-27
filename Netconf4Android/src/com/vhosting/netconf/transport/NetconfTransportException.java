package com.vhosting.netconf.transport;

/**
 * This exception is throws when an error occur into the transport layer or
 * a netconf message can not be sent for any reason.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class NetconfTransportException extends RuntimeException implements
		NetconfTransportError
{

	private static final long serialVersionUID = 1L;
	private FailCause cause = TransportFailCause.CAUSE_UNDEFINED;

	/**
	 * Get the fail cause.
	 * 
	 * @return The fail cause.
	 */
	public FailCause getFailCause() {
		return cause;
	}

	/**
	 * Create a new exception.
	 * 
	 * @param detailMessage
	 *            The exception message.
	 * @param cause
	 *            The fail cause.
	 */
	public NetconfTransportException(String detailMessage, FailCause cause)
	{
		super(detailMessage);
		this.cause = cause;
	}

	/**
	 * Create a new exception.
	 * 
	 * @param e
	 *            The original exception that originated this exception.
	 * @param cause
	 *            The fail cause.
	 */
	public NetconfTransportException(Exception e, FailCause cause)
	{
		super(e.getMessage());
		this.initCause(e);
		this.cause = cause;
	}
}
