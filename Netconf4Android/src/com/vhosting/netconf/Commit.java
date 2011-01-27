package com.vhosting.netconf;

import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.transport.Session;

/**
 * The commit Netconf operation.
 * 
 * When a candidate configuration's content is complete, the
 * configuration data can be committed, publishing the data set to
 * the rest of the device and requesting the device to conform to
 * the behavior described in the new configuration.
 * To commit the candidate configuration as the device's new
 * current configuration, use the Commit operation.
 * 
 * Supported server capabilities:
 * 
 * <pre>
 * urn:ietf:params:netconf:capability:candidate:1.0
 * urn:ietf:params:netconf:capability:confirmed-commit:1.0
 * </pre>
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class Commit extends Operation
{

	private Leaf confirmed;
	private Leaf confirmedTimeout;

	/**
	 * Create the commit Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :candidate:1.0 capability.
	 */
	public Commit(Session session) throws CapabilityException
	{
		super(session);
		if (!Session.CANDIDATE_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.CANDIDATE_1_0
							+ "; Please don't use this class because the <candidate> container is not supported by server.");

		operation = new Rpc(Session.CANDIDATE_1_0, "commit");
		confirmed = operation.getInput().linkLeaf(
				operation.createLeaf("confirmed"));
		confirmedTimeout = operation.getInput().linkLeaf(
				operation.createLeaf("confirm-timeout"));
	}

	/**
	 * Perform a confirmed commit operation.
	 * 
	 * @param timeout
	 *            Timeout period for confirmed commit, in seconds.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :confirmed-commit:1.0 capability.
	 */
	public void setConfirmed(int timeout) throws CapabilityException {
		if (!Session.CONFIRMED_COMMIT_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.CONFIRMED_COMMIT_1_0
							+ "; Please don't use confirmed commits.");

		operation.getInput().assignLeaf(confirmed, "");
		operation.getInput().assignLeaf(confirmedTimeout, "" + timeout);
	}

	/**
	 * Perform a confirmed commit operation with
	 * the confirm timeout defaults to 600 seconds.
	 * 
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :confirmed-commit:1.0 capability.
	 */
	public void setConfirmed() throws CapabilityException {
		if (!Session.CONFIRMED_COMMIT_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.CONFIRMED_COMMIT_1_0
							+ "; Please don't use confirmed commits.");

		operation.getInput().assignLeaf(confirmed, "");
	}
}
