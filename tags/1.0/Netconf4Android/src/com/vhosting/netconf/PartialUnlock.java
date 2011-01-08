package com.vhosting.netconf;

import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.transport.Session;

/**
 * The partial-unlock Netconf operation.
 * 
 * The operation unlocks the parts of the running Container that were
 * previously locked using the PartialLock operation during the same session.
 * 
 * Supported server capabilities:
 * 
 * <pre>
 * urn:ietf:params:netconf:base:1.0
 * rn:ietf:params:netconf:capability:partial-lock:1.0
 * urn:ietf:params:netconf:xpath:1.0
 * 
 * RFC5717
 * </pre>
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class PartialUnlock extends Operation
{

	/**
	 * Create the partial-unlock Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param lockId
	 *            The lock identifier.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :xpath:1.0 capability or the
	 *             :partial-lock:1.0
	 *             capability.
	 */
	public PartialUnlock(Session session, int lockId)
			throws CapabilityException
	{
		super(session);
		if (!Session.PARTIAL_LOCK_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.PARTIAL_LOCK_1_0
							+ "; Please don't use this class with any operation.");

		if (!Session.XPATH_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.XPATH_1_0
							+ "; Please don't use this class with any operation.");

		operation = new Rpc(Session.PARTIAL_LOCK_1_0, "partial-unlock");
		Leaf t = operation.getInput().linkLeaf(operation.createLeaf("lock-id"));
		operation.getInput().assignLeaf(t, "" + lockId);
	}

}
