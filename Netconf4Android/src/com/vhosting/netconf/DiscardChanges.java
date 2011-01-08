package com.vhosting.netconf;

import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.transport.Session;

/**
 * The discard-changes Netconf operation.
 * 
 * If the client decides that the candidate configuration should not be
 * committed, this operation can be used to revert the
 * candidate configuration to the current running configuration.
 * 
 * Supported server capabilities:
 * 
 * <pre>
 * urn:ietf:params:netconf:capability:candidate:1.0
 * </pre>
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class DiscardChanges extends Operation
{

	/**
	 * Create the discard-changes Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :candidate:1.0 capability.
	 */
	public DiscardChanges(Session session) throws CapabilityException
	{
		super(session);
		if (!Session.CANDIDATE_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.CANDIDATE_1_0
							+ "; Please don't use this class because the <candidate> container is not supported by server.");

		operation = new Rpc(Session.CANDIDATE_1_0, "discard-changes");
	}

}
