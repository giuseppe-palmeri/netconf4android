package com.vhosting.netconf.transport;



/**
 * 
 * This is an event that is passed through NetconfCatcherListener when 
 * events concerning the transport protocol are presented.
 * 
 * At an event may be associated with the cause of an error.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class NetconfTransportEvent {

	private NetconfTransportError e;

	private EventType type;

	
	NetconfTransportEvent(NetconfTransportError e, EventType eventType)
	{
		this.e = e;
		type = eventType;
	}
	

	NetconfTransportEvent(EventType eventType)
	{
		type = eventType;
	}

	
	
	
	
	/**
	 * This enumeration lists all types of events that may 
	 * belong to an Event.
	 * @author Giuseppe Palmeri 10/11/2010
	 *
	 */
	public enum EventType {
		
		/**
		 * The connection is closed by user interaction.
		 */
		CONNECTION_CLOSED_BY_USER,
		
		/**
		 * The connection is closed by the server.
		 * @see NetconfTransportEvent#getTransortError()
		 * @see NetconfTransportEvent#getTransortErrorException()
		 */
		CONNECTION_CLOSED_BY_SERVER,
		
		/**
	     * The connection can not be opened.
	     * @see NetconfTransportEvent#getTransortError()
		 * @see NetconfTransportEvent#getTransortErrorException()
		 */
		CONNECTION_CANNOT_BE_OPENED,
	}
	
	
	/**
	 * Get the event type.
	 * @return The event type.
	 */
	public EventType getEventType()
	{
		return type;
	}

	
	/**
	 * Return the transport error if present.
	 * @return The Netconf transport error; null if the event no have errors.
	 */
	public NetconfTransportError getTransortError()
	{
		return e;
	}
	
	/**
	 * Return the transport error exception if present.
	 * @return The Netconf transport error exception; null if the event no have error exceptions.
	 */
	public Exception getTransortErrorException()
	{
		return (Exception) e;
	}
	
}
