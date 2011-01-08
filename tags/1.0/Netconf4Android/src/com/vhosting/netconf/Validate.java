package com.vhosting.netconf;

import java.net.URL;

import com.vhosting.netconf.frame.Container;
import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.transport.Session;

/**
 * The validate Netconf operation.
 * This operation validates the contents of the specified
 * configuration.
 * 
 * <pre>
 * urn:ietf:params:netconf:capability:validate:1.0
 * urn:ietf:params:netconf:capability:candidate:1.0
 * urn:ietf:params:netconf:capability:startup:1.0
 * urn:ietf:params:netconf:capability:url:1.0
 * </pre>
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class Validate extends Operation
{

	/**
	 * Create the validate Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param source
	 *            The source URL Container.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :validate:1.0 capability.
	 *             Throw this exception if the server
	 *             does not have the :url:1.0 capability or if the used URL
	 *             schema is not supported by server.
	 */
	public Validate(Session session, URL source) throws CapabilityException
	{
		super(session);
		if (!Session.VALIDATE_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.VALIDATE_1_0
							+ "; Please don't use this class with any operation.");

		if (Session.URL_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.URL_1_0
							+ "; Please don't specific an URL container for validate operations.");

		Container sou;
		operation = new Rpc(Session.VALIDATE_1_0, "validate");

		sou = operation.getInput().linkContainer(
				operation.createContainer("source"));
		Leaf s = sou.linkLeaf(operation.createLeaf("url"));
		sou.assignLeaf(s, source.toString());
	}

	/**
	 * Create the validate Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param source
	 *            The source Container.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :validate:1.0 capability.
	 *             Throw this exception if the server
	 *             does not have the :candidate:1.0 capability and the source
	 *             container is 'candidate'..
	 *             Throw this exception if the server
	 *             does not have the :startup:1.0 capability and the source
	 *             container is 'startup'.
	 */
	public Validate(Session session, Datastore source)
			throws CapabilityException
	{
		super(session);
		if (!Session.VALIDATE_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.VALIDATE_1_0
							+ "; Please don't use this class with any operation.");

		if (source.equals(Datastore.candidate)
				&& !Session.CANDIDATE_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.CANDIDATE_1_0
							+ "; Please don't specific a <candidate> container as target or source in any operation.");

		if (source.equals(Datastore.startup)
				&& !Session.STARTUP_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.STARTUP_1_0
							+ "; Please don't specific a <startup> container as target or source in any operation.");

		operation = new Rpc(Session.VALIDATE_1_0, "validate");

		Container sou;
		sou = operation.getInput().linkContainer(
				operation.createContainer("source"));
		Leaf s = sou.linkLeaf(operation.createLeaf(source.getName()));
		sou.assignLeaf(s, "");
	}

}
