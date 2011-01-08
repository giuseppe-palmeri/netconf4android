package com.vhosting.netconf.frame;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vhosting.netconf.messages.DOMUtils;
import com.vhosting.netconf.transport.Capability;

/**
 * This class represents an element of an RPC structure
 * that identifies a generic portions of XML
 * in the Netconf RPC, RPC Reply or Notification message.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 09/10/2010
 * 
 */
public class Anyxml extends Identity
{

	/**
	 * Create an empty XML document where the
	 * root is equal to the element name with the some namespace.
	 * 
	 * @return The empty XML document.
	 */
	public Document createEmptyDocument() {
		Document doc = DOMUtils.newDocument();
		Element e = doc.createElementNS(namespaceURI, name);
		e.setPrefix(prefix);
		doc.appendChild(e);
		return doc;
	}

	/**
	 * Create an Anyxml element.
	 * 
	 * @param cap
	 *            The capability that this element belongs.
	 * @param name
	 *            The name of the Anyxml element.
	 */
	public Anyxml(Capability cap, String name)
	{
		super(cap, name);
	}

}
