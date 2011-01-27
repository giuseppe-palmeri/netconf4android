package com.vhosting.netconf.yuma;

import com.vhosting.netconf.CapabilityException;
import com.vhosting.netconf.Operation;
import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.frame.RpcReply;
import com.vhosting.netconf.transport.Session;

/**
 * Load a YANG module into the server.
 * 
 * @author Giuseppe Palmeri
 * 
 */
public class Load extends Operation
{

	YANGCapability cap;

	private Leaf modRevision;

	/**
	 * Create the Load operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param cap
	 *            The capability of the YANG module.
	 * @throws CapabilityException
	 *             Throw this exception when the YUMA SYSTEM capability
	 *             is not registered or not supported by the server.
	 */
	public Load(Session session, YANGCapability cap) throws CapabilityException
	{
		this(session, cap, null);
	}

	public Load(Session session, YANGCapability cap, Date revision)
			throws CapabilityException
	{
		super(session);
		this.cap = cap;

		if (cap.getServerCapabilityParams(session) == null || !cap.getServerCapabilityParams(session).isYANGImplSpecCapability())
			new IllegalArgumentException(
					"The specified capability does not refer to a YANG module on the server: "
							+ cap);

		if (!YumaCapabilities.YUMA_SYSTEM.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not registered into the system or supported by server: "
							+ YumaCapabilities.YUMA_SYSTEM
							+ "; Please don't use this operation.");

		operation = new Rpc(YumaCapabilities.YUMA_SYSTEM, "load");
		Leaf _revision = operation.getInput().linkLeaf(
				operation.createLeaf("revision"));
		Leaf _module = operation.getInput().linkLeaf(
				operation.createLeaf("module"));

		if (revision != null)
			operation.getInput().assignLeaf(_revision,
					revision.getCanonicalValue());
		operation.getInput().assignLeaf(_module, cap.getModuleName());

		// Definisce l'output
		modRevision = operation.getOutput().linkLeaf(
				operation.createLeaf("mod-revision"));

	}

	/**
	 * This class provides specific methods to process
	 * the data obtained in response to a RpcReply
	 * after a load operation.
	 * 
	 * @author Giuseppe Palmeri
	 * 
	 */
	public class LoadReply extends Reply
	{

		private String rev;

		/**
		 * Constructs an instance of the class.
		 * 
		 * @param reply
		 *            The Rpc Reply for the load operation.
		 */
		public LoadReply(RpcReply reply)
		{
			super(reply);
			rev = operation.getOutput().getLeafCanonicalValue(modRevision);
		}

		/**
		 * Allows you to retrieve the module revision in
		 * response to the Load operation.
		 * 
		 * @return The module revision date or null if not exists.
		 */
		public Date getModuleRevision() {
			if (rev == null)
				return null;
			Date d = new Date(rev);
			return d;
		}

		/**
		 * Allows you to run a comparison between the
		 * version of the module loaded on the server.
		 * 
		 * @param version
		 *            The server module revision date.
		 * @see #getModuleRevision()
		 * @return The value 0 if equal; a value less than 0 if
		 *         the module revision is before of the specified Date;
		 *         and a value greater than 0 if the module revision is
		 *         after the specified Date.
		 */
		public int compareVersion(Date version) {
			if (cap.getRevision() == null && version == null)
				return 0;

			if (cap.getRevision() == null && version != null)
				return -1;

			if (cap.getRevision() != null && version == null)
				return 1;

			Date d = new Date(cap.getRevision());
			return d.getValue().compareTo(version.getValue());

		}
	}
}
