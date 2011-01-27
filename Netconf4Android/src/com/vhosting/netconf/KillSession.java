package com.vhosting.netconf;

import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.transport.Session;

/**
 * The kill-session Netconf operation.
 * 
 * Force the termination of a NETCONF session.
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
public class KillSession extends Operation
{

	private Leaf sessionid;

	/**
	 * Create the kill-session Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param sesionId
	 *            The session id of the session to kill.
	 */
	public KillSession(Session session, int sesionId)
	{
		super(session);
		operation = new Rpc(Session.BASE_1_0, "kill-session");
		sessionid = operation.getInput().linkLeaf(
				operation.createLeaf("session-id"));
		operation.getInput().assignLeaf(sessionid, "" + sesionId);
	}

}
