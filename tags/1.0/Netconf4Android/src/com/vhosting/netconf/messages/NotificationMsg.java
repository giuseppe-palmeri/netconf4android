package com.vhosting.netconf.messages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vhosting.netconf.notification.NotificationEvent;
import com.vhosting.netconf.frame.Notification;
import com.vhosting.netconf.transport.Session;

/**
 * This class represents the Notification message in the Netconf protocol.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class NotificationMsg implements Msg, NotificationEvent
{

	private static String notification_xmlns = Session.NOTIFICATION_1_0
			.getNamespaceURI();
	private Document doc;
	private URL xsd = getClass().getResource("notification1_0.xsd");
	private java.util.Date eventTime;

	/**
	 * Build the server side rpc-reply message.
	 * 
	 * @param doc
	 *            validable Document rpc-deply.
	 */
	private NotificationMsg(Document doc)
	{
		this.doc = doc;
	}

	/**
	 * Create the server Notification message from a byte array as source.
	 * 
	 * @param message
	 *            The byte array.
	 * @return The Notification message or null if the message is not a valid
	 *         Notification
	 *         message.
	 * @throws SAXException
	 *             Throw this exception if there are parsing troubles.
	 * @throws IOException
	 *             Throw this exception if there are reading troubles.
	 */
	public static final NotificationMsg createNotification(byte[] message)
			throws SAXException, IOException {
		Document doc = DOMUtils.newDocument(new ByteArrayInputStream(message));

		boolean is = doc.getElementsByTagNameNS(notification_xmlns,
				"notification").getLength() > 0;
		if (!is)
			return null;
		NotificationMsg h = new NotificationMsg(doc);
		if (!h.validate())
			return null;

		Element notification = (Element) doc.getElementsByTagNameNS(
				notification_xmlns, "notification").item(0);

		final NodeList eventTime = notification.getElementsByTagNameNS(
				notification_xmlns, "eventTime");

		Element et = (Element) eventTime.item(0);
		String eventTimeTxt = et.getTextContent();
		h.eventTime = convert3339Date(eventTimeTxt);

		return h;
	}

	@Override
	public Date getEventTime() {
		return eventTime;
	}

	private static Date convert3339Date(String timeString) {
		timeString = timeString.replaceFirst("[.]\\d\\d", "");
		timeString = timeString.replaceFirst("([+-]\\d\\d):(\\d\\d)$", "$1$2");
		timeString = timeString.replaceFirst("[Zz]$", "");
		try
		{

			return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ")
					.parse(timeString);

		}
		catch (ParseException e)
		{
			try
			{
				return new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
						.parse(timeString);
			}
			catch (ParseException e1)
			{
				e1.initCause(e);
				e1.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public boolean load(Notification notification) {
		return notification.readFromNotificationMessage(doc);
	}

	@Override
	public boolean validate() {
		return DOMUtils.validate(doc, xsd);
	}

}
