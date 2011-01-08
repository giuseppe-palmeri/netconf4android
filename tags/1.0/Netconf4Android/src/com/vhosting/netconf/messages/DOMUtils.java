package com.vhosting.netconf.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This class provides some methods for manipulating XML
 * documents used to handle the protocol.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class DOMUtils
{
	private static DocumentBuilder db;
	private static ParserConfigurationException e;

	private static javax.xml.transform.Transformer transformer;
	private static SchemaFactory schemaFactory;

	private DOMUtils()
	{}

	static
	{
		try
		{
			DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
			fac.setIgnoringElementContentWhitespace(true);
			fac.setIgnoringComments(true);
			fac.setNamespaceAware(true);
			db = fac.newDocumentBuilder();
			TransformerFactory factory = SAXTransformerFactory.newInstance();
			//NOANDROID factory.setAttribute("indent-number", new Integer(3));
			transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");

			//NOANDROID schemaFactory = SchemaFactory
		    // 		.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		}
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		}
		catch (TransformerConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Create a new empty DOM XML Document.
	 * 
	 * @return The empty DOM XML Document.
	 */
	public static Document newDocument() {
		if (e != null)
		{
			RuntimeException ex = new RuntimeException(
					"Could not instantiate a new document.");
			ex.initCause(e);
			throw ex;
		}
		Document d = null;
		d = db.newDocument();
		return d;
	}

	/**
	 * Create a new DOM XML Document from an input stream.
	 * 
	 * @param in
	 *            The input stream.
	 * @return The new DOM XML Document.
	 * @throws SAXException
	 *             Throw this exception if there are parsing troubles.
	 * @throws IOException
	 *             Throw this exception if there are reading troubles.
	 */
	public synchronized static Document newDocument(InputStream in)
			throws SAXException, IOException {
		if (e != null)
		{
			RuntimeException ex = new RuntimeException(
					"Could not instantiate a new document.");
			ex.initCause(e);
			throw ex;
		}
		Document d = null;
		d = db.parse(in);
		return d;
	}

	/**
	 * Dump a DOM XML Document to an output stream.
	 * 
	 * @param doc
	 *            The DOM XML Document.
	 * @param out
	 *            The output stream.
	 * @throws IOException
	 *             Throw this exception if there are writing troubles.
	 */
	public static void dump(Document doc, OutputStream out) throws IOException {

		try
		{
			javax.xml.transform.dom.DOMSource domSource = new javax.xml.transform.dom.DOMSource(
					doc.getDocumentElement());
			javax.xml.transform.stream.StreamResult result = new javax.xml.transform.stream.StreamResult(
					new OutputStreamWriter(out, "utf-8"));
			transformer.transform(domSource, result);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
	}

	public static boolean validate(Document doc, URL xsd) {
		// This method is not implemented.

		/*
		Schema schema = null;
		 try {
		   schema = schemaFactory.newSchema(xsd);
		   DOMSource ds = new DOMSource(doc);
		   schema.newValidator().validate(ds);
		 } catch (Exception e) {
		   e.printStackTrace();
		   return false;
		}
		*/
		return true;
	}

}
