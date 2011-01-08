package com.vhosting.netconf.notification;

/**
 * This is the notifications listener.
 * You can intercept a notification implementing this interface.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public interface NotificationsListener
{

	/**
	 * Implement this method when you want intercept notifications.
	 * 
	 * @param notification
	 *            The Netconf notification.
	 */
	public void processNotification(NotificationEvent notification);
}
