package com.vhosting.netconf;

import java.net.URL;

import com.vhosting.netconf.frame.Container;
import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.transport.Session;

/**
 * The delete-config Netconf operation.
 * 
 * Delete a configuration Container.
 * <b>Note:</b> The 'running' configuration Container cannot be deleted.
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
public class DeleteConfig extends Operation
{

	/**
	 * Create the delete-config Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param target
	 *            The target Container.
	 */
	public DeleteConfig(Session session, Datastore target)
	{
		super(session);
		Container targ;
		operation = new Rpc(Session.BASE_1_0, "delete-config");
		targ = operation.getInput().linkContainer(
				operation.createContainer("target"));
		Leaf t = targ.linkLeaf(operation.createLeaf(target.getName()));
		targ.assignLeaf(t, "");
	}

	/**
	 * Create the delete-config Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param target
	 *            The target URL Container.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :url:1.0 capability or if the used URL
	 *             schema is not supported by server.
	 */
	public DeleteConfig(Session session, URL target) throws CapabilityException
	{
		super(session);
		validate(target);
		Container targ;
		operation = new Rpc(Session.BASE_1_0, "delete-config");
		targ = operation.getInput().linkContainer(
				operation.createContainer("target"));
		Leaf t = targ.linkLeaf(operation.createLeaf("url"));
		targ.assignLeaf(t, target.toString());
	}

	private void validate(Object target) throws CapabilityException {
		if (target instanceof URL
				&& !Session.URL_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.URL_1_0
							+ "; Please don't specific an URL container for delete operations.");
	}
}
