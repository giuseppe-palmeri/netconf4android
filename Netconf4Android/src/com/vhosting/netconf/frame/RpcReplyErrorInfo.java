package com.vhosting.netconf.frame;

import org.w3c.dom.Element;

/**
 * The RPC reply error info.
 * This interface provides more detailed information
 * about an error contained in an RPC reply.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public interface RpcReplyErrorInfo
{

	/**
	 * This enumeration represents all the
	 * elements of which it is possible to have
	 * more information.
	 * 
	 * @author Giuseppe Palmeri
	 * 
	 */
	public enum ErrorElement
	{
		/**
		 * Name of the unexpected attribute.
		 */
		bad_attribute
		{
			public String toString() {
				return "bad-attribute";
			}
		},

		/**
		 * Name of the element that contains
		 * the bad element.
		 */
		bad_element
		{
			public String toString() {
				return "bad-element";
			}
		},
		/**
		 * Name of the unexpected namespace.
		 */
		bad_namespace
		{
			public String toString() {
				return "bad-namespace";
			}
		},

		/**
		 * Session ID of session holding the
		 * requested lock, or zero to indicate a non-NETCONF
		 * entity holds the lock.
		 */
		session_id
		{
			public String toString() {
				return "session-id";
			}
		},

		/**
		 * Identifies an element in the data model
		 * for which the requested operation has been completed
		 * for that node and all its child nodes.
		 */
		ok_element
		{
			public String toString() {
				return "ok-element";
			}
		},

		/**
		 * Identifies an element in the data model
		 * for which the requested operation has failed for that
		 * node and all its child nodes.
		 */
		err_element
		{
			public String toString() {
				return "err-element";
			}
		},

		/**
		 * Identifies an element in the data model
		 * for which the requested operation was not attempted for
		 * that node and all its child nodes.
		 */
		noop_element
		{
			public String toString() {
				return "noop-element";
			}
		},

	}

	/**
	 * Check if any information about the error element specified.
	 * 
	 * @param errorElement
	 *            The error element.
	 * @return true if the error element was returned; false otherwise.
	 */
	public boolean hasProtocolErrorInfo(ErrorElement errorElement);

	/**
	 * Get informations for the specific error element.
	 * 
	 * @param errorElement
	 *            The error element.
	 * @return The error element; null if it was not returned.
	 */
	public String getProtocolErrorInfo(ErrorElement errorElement);

	/**
	 * Check whether it was returned to a certain type of
	 * data model specific error informations.
	 * 
	 * @param namespaceURI
	 *            The XML namespace URI of the data model.
	 * @param nodeName
	 *            The XML root element for the data model.
	 * @return true if exists errors about the data model; false otherwise.
	 */
	public boolean hasDataModelSpecificErrorInfo(String namespaceURI,
			String nodeName);

	/**
	 * Get a data model specific error informations.
	 * 
	 * @param namespaceURI
	 *            The XML namespace URI of the data model.
	 * @param nodeName
	 *            The XML root element for the data model.
	 * @return The XML DOM element as root of the data model error informations;
	 *         null if there are no errors about the data model.
	 */
	public Element getDataModelSpecificErrorInfo(String namespaceURI,
			String nodeName);

}
