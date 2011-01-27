package com.vhosting.netconf.transport;

/**
 * This interface provides all the information about errors that may occur
 * at the level of transport protocol and Netconf protocol when for
 * some reason, a message can not be sent or received.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public interface NetconfTransportError
{

	/**
	 * This interface represents the result of a mistake
	 * that can occur at any level of protocol.
	 * If you want to implement new transport protocols,
	 * you can extend this interface to establish new causes of failure.
	 * 
	 * @author Giuseppe Palmeri 10/11/2010
	 * 
	 */
	public interface FailCause
	{}

	/**
	 * This enum groups all the causes of failure are
	 * common to all types of transport protocol.
	 * 
	 * @author Giuseppe Palmeri 10/11/2010
	 * 
	 */
	public enum TransportFailCause implements FailCause
	{

		/**
		 * Cause of undefined failure.
		 */
		CAUSE_UNDEFINED,

		/**
		 * Cause of failure due to connection problems.
		 */
		CAUSE_CONNECTION_TROUBLES,

		/**
		 * Cause of failure due to receiving or sending a message netconf
		 * invalid.
		 */
		CAUSE_AUTHENTICATION_TROUBLES,
	}

	/**
	 * This enum groups all the causes of failure into
	 * the Netconf protocol.
	 * 
	 * @author Giuseppe Palmeri 10/11/2010
	 * 
	 */
	public enum NetconfFailCause implements FailCause
	{

		/**
		 * Cause of failure due to receiving or sending an invalid Netconf
		 * message. <br>
		 * For example, when the Hello message is not transmitted from the
		 * server.
		 */
		CAUSE_NETCONF_PROTOCOL_TROUBLES,

	}

	/**
	 * Get the fail cause.
	 * 
	 * @return The fail cause.
	 */
	public FailCause getFailCause();

	/**
	 * Get the fail message associated.
	 * 
	 * @return The fail message.
	 */
	public String getMessage();
}
