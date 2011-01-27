package com.vhosting.netconf;

import com.vhosting.netconf.frame.Container;
import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.transport.Session;

/**
 * The unlock Netconf operation.
 * 
 * The unlock operation is used to release a configuration lock,
 * previously obtained with the Lock operation.
 * 
 * Supported server capabilities:
 * 
 * <pre>
 * urn:ietf:params:netconf:base:1.0
 * urn:ietf:params:netconf:candidate:1.0
 * urn:ietf:params:netconf:startup:1.0
 * </pre>
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class Unlock extends Operation
{

	/**
	 * Create the unlock Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param target
	 *            The target Container to lock.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :candidate:1.0 capability and the target
	 *             container is 'candidate'.
	 *             Similarly, throw this exception if the server
	 *             does not have the :startup:1.0 capability and the target
	 *             container is 'startup'.
	 */
	public Unlock(Session session, Datastore target) throws CapabilityException
	{
		super(session);
		if (target.equals(Datastore.candidate)
				&& !Session.CANDIDATE_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.CANDIDATE_1_0
							+ "; Please don't specific a <candidate> container as target or source in any operation.");

		if (target.equals(Datastore.startup)
				&& !Session.STARTUP_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.STARTUP_1_0
							+ "; Please don't specific a <startup> container as target or source in any operation.");

		operation = new Rpc(Session.BASE_1_0, "unlock");
		Container targ;
		targ = operation.getInput().linkContainer(
				operation.createContainer("target"));
		Leaf t = targ.linkLeaf(operation.createLeaf(target.getName()));
		targ.assignLeaf(t, "");
	}

}
