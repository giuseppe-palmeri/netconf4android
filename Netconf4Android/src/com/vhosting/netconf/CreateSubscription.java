package com.vhosting.netconf;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vhosting.netconf.frame.Anyxml;
import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.transport.Session;

/**
 * The create-subscription Netconf operation.
 * 
 * This operation initiates an event notification subscription that
 * will send asynchronous event notifications to the initiator of the
 * operation until the subscription terminates.
 * 
 * Supported server capabilities:
 * 
 * <pre>
 * urn:ietf:params:xml:ns:netconf:notification:1.0
 * </pre>
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class CreateSubscription extends Operation
{

	Leaf stream;
	Leaf startTime;
	Leaf stopTime;
	Anyxml filter;
	SubtreeFilter f;

	/**
	 * Create the create-subscription Netconf operation.
	 * 
	 * <pre>
	 * See the RFC 5277 - NETCONF Event Notifications
	 * </pre>
	 * 
	 * @param session
	 *            The active session.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :notification:1.0 capability.
	 */

	public CreateSubscription(Session session) throws CapabilityException
	{
		super(session);
		if (!Session.NOTIFICATION_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.NOTIFICATION_1_0
							+ "; Please don't use this class with any operation.");
		operation = new Rpc(Session.NOTIFICATION_1_0, "create-subscription");
		stream = operation.getInput().linkLeaf(operation.createLeaf("stream"));
		startTime = operation.getInput().linkLeaf(
				operation.createLeaf("startTime"));
		stopTime = operation.getInput().linkLeaf(
				operation.createLeaf("stopTime"));
		filter = operation.getInput().linkAnyxml(
				operation.createAnyxml("filter"));

	}

	/**
	 * This method allows you to specify a specific stream for
	 * the subscription.
	 * 
	 * @param stream
	 *            The stream.
	 */
	public void setStream(String stream) {
		operation.getInput().assignLeaf(this.stream, stream);
	}

	/**
	 * Allows you to specify the starting time for the subscription.
	 * 
	 * @param startTime
	 *            The start time.
	 */
	public void setStartTime(java.util.Date startTime) {
		String st = convert3339(startTime);
		operation.getInput().assignLeaf(this.startTime, st);
	}

	/**
	 * Allows you to specify the time to end the subscription.
	 * 
	 * @param stopTime
	 *            The stop time.
	 */
	public void setStopTime(java.util.Date stopTime) {
		String st = convert3339(stopTime);
		operation.getInput().assignLeaf(this.stopTime, st);
	}

	/**
	 * Create a subtree filter.
	 * Simply call this method to create a filter in the operation.
	 * 
	 * The created filter is an empty filter.
	 * 
	 * Through an empty filter, no data will be selected.
	 * 
	 * You will need to populate the filter created with references
	 * to the data you want.
	 * 
	 * @return The SubtreeFilter created.
	 */
	public SubtreeFilter createSubtreeFilter() {
		SubtreeFilter f = new SubtreeFilter(filter);

		return f;
	}

	public void setSubtreeFilter(SubtreeFilter f) {
		operation.getInput().assignAnyxml(this.filter, f.createAnyxmlValue());
	}

	/**
	 * Set an XPath filter.
	 * Use this method when the operation includes the possibility
	 * of an XPath filter on data received.
	 * 
	 * The filter will be interpreted by the server.
	 * 
	 * @param xpath
	 *            The XPath string.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :xpath:1.0 capability.
	 */
	public void setXPathFilter(String xpath) throws CapabilityException {
		// Verifica che non viene usato :xpath quando il server non lo
		// supporta.
		if (!Session.XPATH_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.XPATH_1_0
							+ "; Please use a SubtreeFilter instead.");

		Document doc = filter.createEmptyDocument();
		Element root = doc.getDocumentElement();
		root.setAttributeNS(filter.getNamespaceURI(), filter.getPrefix()
				+ ":type", "xpath");
		root.setAttributeNS(filter.getNamespaceURI(), filter.getPrefix()
				+ ":select", xpath);
		operation.getInput().assignAnyxml(this.filter, doc);

	}

	/**
	 * Converts a date into a string that conforms
	 * to the RFC3339 standard of the Internet.
	 * 
	 * @param d
	 *            The Java Date.
	 * @return The RFC3339 compliant Date string.
	 */
	public static String convert3339(Date d) {
		String ssZ = new SimpleDateFormat("Z").format(d);
		String sign = ssZ.substring(0, 1);
		int zH = Integer.parseInt(ssZ.substring(1, 3));
		int zM = Integer.parseInt(ssZ.substring(3, 5));
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(d);

		if (sign.equals("+"))
		{
			c.add(Calendar.HOUR, -zH);
			c.add(Calendar.MINUTE, -zM);
		}
		else
		{
			c.add(Calendar.HOUR, +zH);
			c.add(Calendar.MINUTE, +zM);
		}

		String st = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'").format(c
				.getTime());
		return st;
	}

}
