package com.vhosting.netconf.frame;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vhosting.netconf.transport.Capability;
import com.vhosting.netconf.transport.Session;

/**
 * This class provides the basic structure for
 * reading data from a get or get-config operation.
 * 
 * The class was designed having in mind the fact
 * that it can be used in case of automated code
 * generation from a YANG module.
 * Nothing prevents, however, be used freely.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 09/10/2010
 */
public class Data extends IdentityCreator
{

	/**
	 * 
	 * Create a Data structure
	 * for the specified capability.
	 * 
	 * @param cap The Capability for which you want to read the data.
	 * 
	 * 
	 */
	public Data(Capability cap)
	{

		super(cap, null);
		data = new Container(cap, null);
	}

	private Container data;

	/**
	 * Clear the container container.
	 * After the invocation of this method all
	 * the assigned values into the structure will be deleted.
	 */
	public void clearValues() {
		data.clear();
	}

	/**
	 * Get the first entry point to the
	 * structure as a container.
	 * 
	 * @return The container.
	 */
	public final Container getData() {
		return data;
	}

	/**
	 * This method loads data from the specified XML document.
	 * The XML document should be coming from a get or get-config operation.
	 * The values loaded are available within the Data container.
	 * 
	 * @see #getData()
	 * @param doc
	 *            The XML document containing the data.
	 * @return true if it has been possible to load values into the structure;
	 *         false otherwise.
	 */
	public boolean read(Document doc) {
		Element n = doc.getDocumentElement();
		
		if (!n.getNamespaceURI().equals(Session.BASE_1_0.getNamespaceURI())
				&& !n.getNodeName().equals(Session.BASE_1_0.getPrefix() + ":data"))
		{
			return false;
		}

		Load.load(n, data);
		return true;
	}

}
