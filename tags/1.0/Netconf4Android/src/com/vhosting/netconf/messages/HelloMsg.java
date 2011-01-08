package com.vhosting.netconf.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vhosting.netconf.transport.Capability;
import com.vhosting.netconf.transport.Session;

/**
 * This class represents the Hello message in the Netconf protocol.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public final class HelloMsg implements Msg
{

	private static String base_xmlns = Session.BASE_1_0.getNamespaceURI();

	private static int sessionId;
	private Document doc;

	private URL xsd = getClass().getResource("base1_0.xsd");

	/**
	 * Build the client side Hello message.
	 * 
	 * @param caps
	 *            The client side capabilities.
	 */
	public HelloMsg(Capability[] caps)
	{
		doc = DOMUtils.newDocument();

		String prefix = Session.BASE_1_0.getPrefix();

		Node hello = doc.appendChild(doc.createElementNS(base_xmlns, prefix
				+ ":hello"));
		((Element) hello).setAttributeNS("http://www.w3.org/2000/xmlns/",
				"xmlns:" + hello.getPrefix(), hello.getNamespaceURI());
		Node capabilities = hello.appendChild(doc.createElementNS(base_xmlns,
				prefix + ":capabilities"));
		for (Capability c : caps)
		{
			Node cap = capabilities.appendChild(doc.createElementNS(base_xmlns,
					prefix + ":capability"));
			cap.setTextContent(c.getCapabilityBaseURI());
		}
	}

	private HelloMsg(Document doc)
	{
		this.doc = doc;
	}

	/**
	 * Create the server side Hello message from a byte array as source.
	 * 
	 * @param message
	 *            The byte array.
	 * @return The Hello message or null if the message is not a valid Hello
	 *         message.
	 * @throws SAXException
	 *             Throw this exception if there are parsing troubles.
	 * @throws IOException
	 *             Throw this exception if there are reading troubles.
	 */
	public static final HelloMsg createServerHello(byte[] message)
			throws SAXException, IOException {
		Document doc = DOMUtils.newDocument(new ByteArrayInputStream(message));

		boolean is = doc.getElementsByTagNameNS(base_xmlns, "hello")
				.getLength() > 0;
		if (!is)
			return null;
		HelloMsg h = new HelloMsg(doc);
		if (!h.validate())
			return null;
		Element root = (Element) doc
				.getElementsByTagNameNS(base_xmlns, "hello").item(0);
		root.getChildNodes();
		// Check the sesionId element presence.
		if (root.getElementsByTagNameNS(base_xmlns, "session-id").getLength() == 0)
			return null;
		if (root.getElementsByTagNameNS(base_xmlns, "capability").getLength() == 0)
			return null;
		try
		{
			sessionId = Integer.parseInt(((Element) root
					.getElementsByTagNameNS(base_xmlns, "session-id").item(0))
					.getTextContent());
		}
		catch (Exception e)
		{
			return null;
		}
		return h;
	}

	/**
	 * Get the capabilities URI with the message.
	 * 
	 * @return The capabilities URI.
	 */
	public String[] getCapabilitiesURI() {
		Vector<String> v = new Vector<String>();
		NodeList nl = doc.getElementsByTagNameNS(base_xmlns, "capability");
		int len = nl.getLength();
		for (int i = 0; i < len; i++)
		{
			Node n = nl.item(i);
			v.add(n.getTextContent());
		}
		return (String[]) v.toArray(new String[v.size()]);
	}

	/**
	 * Get the session id with the message.
	 * 
	 * The session Id is null in case of a client side Hello message.
	 * 
	 * @return The session identifier or null if not exists.
	 */
	public static Integer getSessionId() {
		return sessionId;
	}

	@Override
	public final boolean validate() {
		return DOMUtils.validate(doc, xsd);
	}

	/**
	 * Dump the message to an array output stream.
	 * 
	 * @param out
	 *            The array output stream.
	 * @throws IOException
	 *             Throw this exception if there are writing troubles.
	 */
	public void dump(ByteArrayOutputStream out) throws IOException {
		DOMUtils.dump(doc, out);
	}

}
