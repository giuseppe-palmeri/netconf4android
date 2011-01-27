package com.vhosting.netconf.frame;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.vhosting.netconf.messages.DOMUtils;
import com.vhosting.netconf.transport.Capability;
import com.vhosting.netconf.transport.Session;

/**
 * This class provides the basic structure for the
 * construction of RPC operations and reading the
 * associated RPC reply informations.
 * 
 * The class was designed having in mind the fact
 * that it can be used in case of automated code
 * generation from a YANG module.
 * Nothing prevents, however, be used freely.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 09/10/2010
 */
public class Rpc extends IdentityCreator
{

	/**
	 * Create an RPC request with the operation name
	 * for the specified capability.
	 * 
	 * @param cap
	 *            The capability.
	 * @param name
	 *            The name of the RPC operation.
	 */
	public Rpc(Capability cap, String name)
	{
		super(cap, name);
		inputContainer = new Container(cap, name);
		outputContainer = new Container(cap, name);
	}

	private Container inputContainer;
	private Container outputContainer;

	private Vector<Attribute> attributes = new Vector<Attribute>();

	/**
	 * Allows you to assign an attribute to the RPC operation.
	 * 
	 * @param attr
	 *            The attribute.
	 */
	public void addAttribute(Attribute attr) {
		attributes.add(attr);
	}

	/**
	 * Clear the Input container.
	 * After the invocation of this method all
	 * the values assigned to the operation RPC will be deleted.
	 * 
	 * For a specific RPC operation with default values may be useful
	 * to repopulate the property defaults to the values immediately
	 * after the invocation of this method.
	 * 
	 */
	public void clearInputValues() {
		inputContainer.clear();
	}

	/**
	 * Clear the Output container.
	 * After the invocation of this method all
	 * the assigned values through an RPC reply will be deleted.
	 */
	public void clearOutputValues() {
		outputContainer.clear();
	}

	/**
	 * Get the first entry point to the
	 * structure of the PRC operation as a container.
	 * 
	 * @return The RPC operation input container.
	 */
	public final Container getInput() {
		return inputContainer;
	}

	/**
	 * Get the first entry point to the
	 * structure of the PRC reply as a container.
	 * 
	 * @return The RPC reply output container.
	 */
	public final Container getOutput() {
		return outputContainer;
	}

	/**
	 * This method writes the RPC operation on the
	 * specified output stream as an XML Netconf message.
	 * 
	 * @param messageId
	 *            The message id.
	 * @param out
	 *            The output stream.
	 * @throws IOException
	 *             Throw this exception when there are problems when writing the
	 *             message.
	 */
	public void dumpRpcMessage(Session session, int messageId, OutputStream out)
			throws IOException {
		Document doc = DOMUtils.newDocument();
		// Pulisce il documento.
		if (doc.getDocumentElement() != null)
			doc.removeChild(doc.getDocumentElement());

		Element rpc = (Element) doc.appendChild(doc.createElementNS(
				Session.BASE_1_0.getNamespaceURI(), "rpc"));
		String basePrefix = Session.BASE_1_0.getPrefix();
		rpc.setPrefix(basePrefix);

		rpc.setAttributeNS(Session.BASE_1_0.getNamespaceURI(), basePrefix
				+ ":message-id", Integer.toString(messageId));

		Capability[] caps = session.getCapabilitiesOnServer();

		for (Capability cap : caps)
		{

			String pref = cap.getPrefix();
			String s = cap.getNamespaceURI();
			rpc.setAttributeNS("http://www.w3.org/2000/xmlns/",
					"xmlns:" + pref, s);

		}
		Element m = (Element) rpc.appendChild(doc.createElementNS(namespaceURI,
				this.getUniqueNane()));
		m.setPrefix(prefix);

		// Inserisce gli attributi del COMANDO
		for (int i = 0; i < attributes.size(); i++)
		{
			Attribute attr = attributes.get(i);
			m.setAttributeNS(attr.getNamespaceURI(), attr.getName(),
					attr.getValue());
		}

		Save.save(getInput(), doc, m);
		DOMUtils.dump(doc, out);
	}

	/**
	 * This method loads the RPC reply from the specified XML document.
	 * The XML document should be a Netconf RPC reply message.
	 * The values loaded are available within the output container.
	 * 
	 * @see #getOutput()
	 * @param doc
	 *            The XML document containing the Netconf RPC reply message.
	 * @return true if it has been possible to load values into the structure;
	 *         false otherwise.
	 */
	public boolean readFromRpcReplyMessage(Document doc) {
		Node n = doc.getDocumentElement();
		Element e = (Element) n;
		if (e == null)
			return false;
		Load.load(e, outputContainer);
		return true;
	}

}
