package com.vhosting.netconf.messages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.frame.RpcReply;
import com.vhosting.netconf.frame.RpcReplyError;
import com.vhosting.netconf.frame.RpcReplyError.ErrorSeverity;
import com.vhosting.netconf.frame.RpcReplyError.ErrorTag;
import com.vhosting.netconf.frame.RpcReplyError.ErrorType;
import com.vhosting.netconf.frame.RpcReplyErrorInfo;
import com.vhosting.netconf.frame.RpcReplyErrorMessage;

/**
 * This class represents the RPC Reply message in the Netconf protocol.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class RpcReplyMsg implements Msg, RpcReply
{

	public static final String base_1_0_xmlns = "urn:ietf:params:xml:ns:netconf:base:1.0";

	private String messageId;
	private boolean isErr;
	private RpcReplyError[] errors;
	private Document doc;
	private URL xsd = getClass().getResource("base1_0.xsd");

	/**
	 * Build the server side rpc-reply message.
	 * 
	 * @param doc
	 *            validable Document rpc-deply.
	 */
	private RpcReplyMsg(Document doc)
	{
		this.doc = doc;
	}

	/**
	 * Create the server side Rpc Reply message from a byte array as source.
	 * 
	 * @param message
	 *            The byte array.
	 * @return The RPC Reply message or null if the message is not a valid RPC
	 *         Reply
	 *         message.
	 * @throws SAXException
	 *             Throw this exception if there are parsing troubles.
	 * @throws IOException
	 *             Throw this exception if there are reading troubles.
	 */
	public static final RpcReplyMsg createServerRpcReply(byte[] message)
			throws SAXException, IOException {
		Document doc = DOMUtils.newDocument(new ByteArrayInputStream(message));

		boolean is = doc.getElementsByTagNameNS(base_1_0_xmlns, "rpc-reply")
				.getLength() > 0;
		if (!is)
			return null;
		RpcReplyMsg h = new RpcReplyMsg(doc);
		if (!h.validate())
			return null;

		Element rpcReply = (Element) doc.getElementsByTagNameNS(base_1_0_xmlns,
				"rpc-reply").item(0);

		h.messageId = rpcReply.getAttributeNS(base_1_0_xmlns, "message-id");
		if (h.messageId.equals(""))
			h.messageId = null;

		final NodeList rpcErrors = doc.getDocumentElement()
				.getElementsByTagNameNS(base_1_0_xmlns, "rpc-error");
		int errLen = rpcErrors.getLength();
		h.isErr = errLen > 0;
		h.errors = new RpcReplyError[errLen];

		for (int i = 0; i < errLen; i++)
		{
			NodeList errTags = rpcErrors.item(i).getChildNodes();
			int errTagsLen = errTags.getLength();

			ErrorType errorType = null;
			ErrorTag errorTag = null;
			ErrorSeverity errorSeverity = null;
			String errorAppTag = null;
			String errorPath = null;
			String errorMessage = null;
			String errorMessageLang = "en";

			RpcReplyErrorInfo errorInfo = null;

			for (int y = 0; y < errTagsLen; y++)
			{
				Node n = errTags.item(y);
				if (!(n instanceof Element))
					continue;
				if (!n.getNamespaceURI().equalsIgnoreCase(base_1_0_xmlns))
					continue;

				Element errTag = (Element) n;
				String name = errTag.getLocalName();
				String value = errTag.getTextContent();

				if (name.equalsIgnoreCase("error-type"))
				{
					if (RpcReplyError.ErrorType.rpc.toString().equals(value))
						errorType = RpcReplyError.ErrorType.rpc;
					else if (RpcReplyError.ErrorType.application.toString()
							.equals(value))
						errorType = RpcReplyError.ErrorType.application;
					else if (RpcReplyError.ErrorType.protocol.toString()
							.equals(value))
						errorType = RpcReplyError.ErrorType.protocol;
					else if (RpcReplyError.ErrorType.transport.toString()
							.equals(value))
						errorType = RpcReplyError.ErrorType.transport;
				}
				else if (name.equalsIgnoreCase("error-tag"))
				{

					
					if (RpcReplyError.ErrorTag.access_denied.toString().equals(
							value))
						errorTag = RpcReplyError.ErrorTag.access_denied;
					else if (RpcReplyError.ErrorTag.bad_attribute.toString()
							.equals(value))
						errorTag = RpcReplyError.ErrorTag.bad_attribute;
					else if (RpcReplyError.ErrorTag.bad_element.toString()
							.equals(value))
						errorTag = RpcReplyError.ErrorTag.bad_element;
					else if (RpcReplyError.ErrorTag.data_exists.toString()
							.equals(value))
						errorTag = RpcReplyError.ErrorTag.data_exists;
					else if (RpcReplyError.ErrorTag.data_missing.toString()
							.equals(value))
						errorTag = RpcReplyError.ErrorTag.data_missing;
					else if (RpcReplyError.ErrorTag.in_use.toString().equals(
							value))
						errorTag = RpcReplyError.ErrorTag.in_use;
					else if (RpcReplyError.ErrorTag.invalid_value.toString()
							.equals(value))
						errorTag = RpcReplyError.ErrorTag.invalid_value;
					else if (RpcReplyError.ErrorTag.lock_denied.toString()
							.equals(value))
						errorTag = RpcReplyError.ErrorTag.lock_denied;
					else if (RpcReplyError.ErrorTag.missing_attribute
							.toString().equals(value))
						errorTag = RpcReplyError.ErrorTag.missing_attribute;
					else if (RpcReplyError.ErrorTag.missing_element.toString()
							.equals(value))
						errorTag = RpcReplyError.ErrorTag.missing_element;
					else if (RpcReplyError.ErrorTag.operation_failed.toString()
							.equals(value))
						errorTag = RpcReplyError.ErrorTag.operation_failed;
					else if (RpcReplyError.ErrorTag.operation_not_supported
							.toString().equals(value))
						errorTag = RpcReplyError.ErrorTag.operation_not_supported;
					else if (RpcReplyError.ErrorTag.partial_operation
							.toString().equals(value))
						errorTag = RpcReplyError.ErrorTag.partial_operation;
					else if (RpcReplyError.ErrorTag.resource_denied.toString()
							.equals(value))
						errorTag = RpcReplyError.ErrorTag.resource_denied;
					else if (RpcReplyError.ErrorTag.rollback_failed.toString()
							.equals(value))
						errorTag = RpcReplyError.ErrorTag.rollback_failed;
					else if (RpcReplyError.ErrorTag.too_big.toString().equals(
							value))
						errorTag = RpcReplyError.ErrorTag.too_big;
					else if (RpcReplyError.ErrorTag.unknown_attribute
							.toString().equals(value))
						errorTag = RpcReplyError.ErrorTag.unknown_attribute;
					else if (RpcReplyError.ErrorTag.unknown_element.toString()
							.equals(value))
						errorTag = RpcReplyError.ErrorTag.unknown_element;
					else if (RpcReplyError.ErrorTag.unknown_namespace
							.toString().equals(value))
						errorTag = RpcReplyError.ErrorTag.unknown_namespace;
				}
				else if (name.equalsIgnoreCase("error-severity"))
				{
					if (RpcReplyError.ErrorSeverity.error.toString().equals(
							value))
						errorSeverity = RpcReplyError.ErrorSeverity.error;
					else if (RpcReplyError.ErrorSeverity.warning.toString()
							.equals(value))
						errorSeverity = RpcReplyError.ErrorSeverity.warning;
				}
				else if (name.equalsIgnoreCase("error-app-tag"))
				{
					errorAppTag = value;
				}
				else if (name.equalsIgnoreCase("error-path"))
				{
					errorPath = value;
				}
				else if (name.equalsIgnoreCase("error-message"))
				{
					errorMessage = value;

					// Verifica l'esistenza di xml:lang e se esiste
					// imposta il valore della lingua.
					Element e = (Element) errTag;
					String lang = e.getAttributeNS(
							"http://www.w3.org/XML/1998/namespace", "lang");
					if (lang.length() > 0)
						errorMessageLang = lang;
				}
				else if (name.equalsIgnoreCase("error-info"))
				{
					if (errTag.hasChildNodes())
					{
						final NodeList errorInfoElements = errTag
								.getChildNodes();
						final int len = errorInfoElements.getLength();

						errorInfo = new RpcReplyErrorInfo()
						{

							public boolean hasProtocolErrorInfo(
									ErrorElement errorElement) {
								return getProtocolErrorInfo(errorElement) == null;
							}

							public String getProtocolErrorInfo(
									ErrorElement errorElement) {
								for (int i = 0; i < len; i++)
								{
									Node n = errorInfoElements.item(i);
									if (!(n instanceof Element))
										continue;
									if (!n.getNamespaceURI().equalsIgnoreCase(
											base_1_0_xmlns))
										continue;
									String name = n.getLocalName();
									String value = n.getTextContent();
									
									if (errorElement.equals(name))
										return value;
								}
								return null;
							}

							public boolean hasDataModelSpecificErrorInfo(
									String namespaceURI, String nodeName) {
								return getDataModelSpecificErrorInfo(
										namespaceURI, nodeName) == null;
							}

							public Element getDataModelSpecificErrorInfo(
									String namespaceURI, String nodeName) {
								for (int i = 0; i < len; i++)
								{
									Node n = errorInfoElements.item(i);
									String name = n.getNodeName();

									if (nodeName.equals(name)
											&& n.getNamespaceURI().equals(
													namespaceURI))
									{
										return (Element) n;
									}
								}
								return null;
							}
						};
					}
				}

			}
			final ErrorType retErrorType = errorType;
			final ErrorTag retErrorTag = errorTag;
			final ErrorSeverity retErrorSeveriy = errorSeverity;
			final String retErrorPath = errorPath;
			final String retErrorAppTag = errorAppTag;

			final String rtnErrorMessageLang = errorMessageLang;
			final String rtnErrorMessageTxt = errorMessage;
			final RpcReplyErrorMessage retErrorMessage = (errorMessage == null) ? null
					: new RpcReplyErrorMessage()
					{

						@Override
						public String getLanguage() {
							return rtnErrorMessageLang;
						}

						@Override
						public String getMessage() {
							return rtnErrorMessageTxt;
						}

					};

			final RpcReplyErrorInfo retErrorInfo = errorInfo;

			h.errors[i] = new RpcReplyError()
			{

				@Override
				public ErrorType getErrorType() {
					return retErrorType;
				}

				@Override
				public ErrorTag getErrorTag() {
					return retErrorTag;
				}

				@Override
				public ErrorSeverity getErrorSeverity() {
					return retErrorSeveriy;
				}

				@Override
				public String getErrorAppTag() {
					return retErrorAppTag;
				}

				@Override
				public String getErrorPath() {
					return retErrorPath;
				}

				@Override
				public RpcReplyErrorMessage getErrorMessage() {
					return retErrorMessage;
				}

				@Override
				public RpcReplyErrorInfo getErrorInfo() {
					return retErrorInfo;
				}

			};
		}
		return h;
	}

	/**
	 * Get the message id of the Rpc operation with this message as reply.
	 * 
	 * @return The message identifier.
	 */
	public Integer getMessageId() {
		if (messageId == null)
			return null;
		return Integer.parseInt(messageId);
	}

	@Override
	public boolean containsErrors() {
		return isErr;
	}

	@Override
	public RpcReplyError[] getErrors() {
		return errors;
	}

	@Override
	public boolean load(Rpc rpc) {
		return rpc.readFromRpcReplyMessage(doc);
	}

	@Override
	public boolean validate() {
		return DOMUtils.validate(doc, xsd);
	}
}
