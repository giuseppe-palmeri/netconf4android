package com.vhosting.netconf;

import java.net.URL;

import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.transport.Capability.CapabilityParams;
import com.vhosting.netconf.transport.Session;

/**
 * The copy-config Netconf operation.
 * 
 * Create or replace an entire configuration container with the
 * contents of another complete configuration container. If the
 * target Container exists, it is overwritten. Otherwise, a new one
 * is created, if allowed.
 * 
 * Supported server capabilities:
 * 
 * <pre>
 * urn:ietf:params:netconf:base:1.0
 * urn:ietf:params:netconf:capability:writable-running:1.0
 * urn:ietf:params:netconf:capability:startup:1.0
 * urn:ietf:params:netconf:capability:candidate:1.0
 * urn:ietf:params:netconf:capability:url:1.0
 * </pre>
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class CopyConfig extends Operation
{

	private com.vhosting.netconf.frame.Container target;
	private com.vhosting.netconf.frame.Container source;

	/**
	 * Create the copy-config Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param target
	 *            The target Container.
	 * @param source
	 *            The source Container.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :candidate:1.0 capability and the source or
	 *             target container is 'candidate'.
	 *             Throw this exception if the server
	 *             does not have the :writable-running:1.0 capability and the
	 *             source or target container is 'running'.
	 *             Throw this exception if the server
	 *             does not have the :startup:1.0 capability and the source or
	 *             target container is 'startup'.
	 */
	public CopyConfig(Session session, Datastore target, Datastore source)
			throws CapabilityException
	{
		super(session);
		validate(target, source);
		operation = new Rpc(Session.BASE_1_0, "copy-config");

		this.target = operation.getInput().linkContainer(
				operation.createContainer("target"));
		this.source = operation.getInput().linkContainer(
				operation.createContainer("source"));
		Leaf t = this.target.linkLeaf(operation.createLeaf(target.getName()));
		Leaf s = this.source.linkLeaf(operation.createLeaf(source.getName()));
		this.target.assignLeaf(t, "");
		this.source.assignLeaf(s, "");
	}

	/**
	 * Create the copy-config Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param target
	 *            The target URL Container.
	 * @param source
	 *            The source Container.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :candidate:1.0 capability and the source
	 *             container is 'candidate'.
	 *             Throw this exception if the server
	 *             does not have the :writable-running:1.0 capability and the
	 *             source container is 'running'.
	 *             Throw this exception if the server
	 *             does not have the :startup:1.0 capability and the source
	 *             container is 'startup'.
	 *             Throw this exception if the server
	 *             does not have the :url:1.0 capability or if the used URL
	 *             schema is not supported by server.
	 */
	public CopyConfig(Session session, URL target, Datastore source)
			throws CapabilityException
	{
		super(session);
		validate(target, source);
		this.target = operation.getInput().linkContainer(
				operation.createContainer("target"));
		this.source = operation.getInput().linkContainer(
				operation.createContainer("source"));
		Leaf t = this.target.linkLeaf(operation.createLeaf("url"));
		Leaf s = this.source.linkLeaf(operation.createLeaf(source.getName()));
		this.target.assignLeaf(t, target.toString());
		this.source.assignLeaf(s, "");
	}

	/**
	 * Create the copy-config Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param target
	 *            The target Container.
	 * @param source
	 *            The source URL Container.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :candidate:1.0 capability and the target
	 *             container is 'candidate'.
	 *             Throw this exception if the server
	 *             does not have the :writable-running:1.0 capability and the
	 *             target container is 'running'.
	 *             Throw this exception if the server
	 *             does not have the :startup:1.0 capability and the target
	 *             container is 'startup'.
	 *             Throw this exception if the server
	 *             does not have the :url:1.0 capability or if the used URL
	 *             schema is not supported by server.
	 */
	public CopyConfig(Session session, Datastore target, URL source)
			throws CapabilityException
	{
		super(session);
		validate(target, source);
		operation = new Rpc(Session.BASE_1_0, "copy-config");

		this.target = operation.getInput().linkContainer(
				operation.createContainer("target"));
		this.source = operation.getInput().linkContainer(
				operation.createContainer("source"));
		Leaf t = this.target.linkLeaf(operation.createLeaf(target.getName()));
		Leaf s = this.source.linkLeaf(operation.createLeaf("url"));
		this.target.assignLeaf(t, "");
		this.source.assignLeaf(s, source.toString());
	}

	/**
	 * Create the copy-config Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param target
	 *            The target URL Container.
	 * @param source
	 *            The source URL Container.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :url:1.0 capability or if the used URL
	 *             schema is not supported by server.
	 */
	public CopyConfig(Session session, URL target, URL source)
			throws CapabilityException
	{
		super(session);
		validate(target, source);
		operation = new Rpc(Session.BASE_1_0, "copy-config");

		this.target = operation.getInput().linkContainer(
				operation.createContainer("target"));
		this.source = operation.getInput().linkContainer(
				operation.createContainer("source"));
		Leaf t = this.target.linkLeaf(operation.createLeaf("url"));
		Leaf s = this.source.linkLeaf(operation.createLeaf("url"));
		this.target.assignLeaf(t, target.toString());
		this.source.assignLeaf(s, source.toString());
	}

	private void validate(Object target, Object source)
			throws CapabilityException {
		if ((source.equals(Datastore.startup) || target
				.equals(Datastore.startup))
				&& !Session.STARTUP_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.STARTUP_1_0
							+ "; Please don't specific a <startup> container as target or source in any operation.");

		if ((!(target instanceof URL) && target.equals(Datastore.running))
				&& !Session.WRITABLE_RUNNING_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.WRITABLE_RUNNING_1_0
							+ "; Please don't specific a <running> container as target for copy operations.");

		if ((source.equals(Datastore.candidate) || target
				.equals(Datastore.candidate))
				&& !Session.CANDIDATE_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.CANDIDATE_1_0
							+ "; Please don't specific a <candidate> container as target or source in any operation.");

		if ((target instanceof URL || source instanceof URL)
				&& !Session.URL_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.URL_1_0
							+ "; Please don't specific an URL container for copy operations.");

		CapabilityParams params = Session.BASE_1_0
				.getServerCapabilityParams(session);

		if (target instanceof URL)
		{
			String schema = ((URL) target).getProtocol();
			String schemas = params.getParam("schema").trim();
			if (!schemas.matches(".*?" + schema + ".*?"))
				throw new CapabilityException(
						"This capability is not fully supported by server: "
								+ Session.URL_1_0
								+ "; Please don't specific an URL with "
								+ schema
								+ " schema as target because the server not support it; Use these instead: "
								+ schemas);
		}

		else if (source instanceof URL)
		{
			String schema = ((URL) source).getProtocol();
			String schemas = params.getParam("schema").trim();
			if (!schemas.matches(".*?" + schema + ".*?"))
				throw new CapabilityException(
						"This capability is not fully supported by server: "
								+ Session.URL_1_0
								+ "; Please don't specific an URL with "
								+ schema
								+ " schema as source because the server not support it; Use these instead: "
								+ schemas);
		}
	}
}
