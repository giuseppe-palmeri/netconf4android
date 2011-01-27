package com.vhosting.netconf;

import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.transport.Session;

/**
 * The close-session Netconf operation.
 * 
 * Request graceful termination of a NETCONF session.
 * 
 * Supported server capabilities:
 * 
 * <pre>
 * urn:ietf:params:netconf:base:1.0
 * </pre>
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class CloseSession extends Operation
{

	/**
	 * Create the close-session Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 */
	public CloseSession(Session session)
	{
		super(session);
		operation = new Rpc(Session.BASE_1_0, "close-session");
	}

}
