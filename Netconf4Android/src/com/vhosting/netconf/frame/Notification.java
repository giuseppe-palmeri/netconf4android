package com.vhosting.netconf.frame;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vhosting.netconf.transport.Capability;

/**
 * This class provides the basic structure for
 * reading an RPC notification.
 * 
 * The class was designed having in mind the fact
 * that it can be used in case of automated code
 * generation from a YANG module.
 * Nothing prevents, however, be used freely.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 09/10/2010
 */
public class Notification extends IdentityCreator
{
	

	/**
	 * Create an RPC notification with its name
	 * for the specified capability.
	 * 
	 * @param cap
	 *            The capability.
	 * @param name
	 *            The name of the RPC notification.
	 */
	public Notification(Capability cap, String name)
	{

		super(cap, name);
		notification = new Container(cap, name);
	}

	private Container notification;

	/**
	 * Clear the Notification container.
	 * After the invocation of this method all
	 * the assigned values through an RPC notification will be deleted.
	 */
	public void clearValues() {
		notification.clear();
	}

	/**
	 * Get the first entry point to the
	 * structure of the PRC notification as a container.
	 * 
	 * @return The RPC notification output container.
	 */
	public final Container getNotification() {
		return notification;
	}

	/**
	 * This method loads the RPC notification from the specified XML document.
	 * The XML document should be a Netconf RPC notification message.
	 * The values loaded are available within the Notification container.
	 * 
	 * @see #getNotification()
	 * @param doc
	 *            The XML document containing the Netconf RPC
	 *            notification message.
	 * @return true if it has been possible to load values into the structure;
	 *         false otherwise.
	 */
	public boolean readFromNotificationMessage(Document doc) {
		Element n = doc.getDocumentElement();
		NodeList nl = n.getElementsByTagNameNS(namespaceURI, name);
		if (nl.getLength() < 1)
			return false;
		Element e = (Element) nl.item(0);
		Load.load(e, notification);
		return true;
	}

	
	
	

	
}
