package com.vhosting.netconf;

import java.util.List;

import com.vhosting.netconf.XPathSelections.Selection;
import com.vhosting.netconf.frame.Attribute;
import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.LeafList;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.frame.RpcReply;
import com.vhosting.netconf.transport.Session;

/**
 * The partial-lock Netconf operation.
 * 
 * The partial-lock operation allows the client to lock a portion of
 * the running Container. The portion to lock is specified with XPath
 * expressions.
 * 
 * Supported server capabilities:
 * 
 * <pre>
 * urn:ietf:params:netconf:base:1.0
 * rn:ietf:params:netconf:capability:partial-lock:1.0
 * urn:ietf:params:netconf:xpath:1.0
 * </pre>
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class PartialLock extends Operation
{

	private LeafList select;

	private Leaf lockId;

	/**
	 * Create the partial-lock Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param selections
	 *            The set of XPath selections of nodes on which to perform the
	 *            operation.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :xpath:1.0 capability or the
	 *             :partial-lock:1.0
	 *             capability.
	 */
	public PartialLock(Session session, XPathSelections selections)
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

		operation = new Rpc(Session.PARTIAL_LOCK_1_0, "partial-lock");
		lockId = operation.createLeaf("lock-id");
		select = operation.getInput().linkLeafList(
				operation.createLeafList("select"));

		Selection[] sels = selections.getSelections();

		if (sels.length < 1)
			throw new IllegalArgumentException(
					"The XPathSelections must contain at least one item.");

		String[] values = new String[sels.length];
		for (int i = 0; i < sels.length; i++)
			values[i] = sels[i].getXPath();
		operation.getInput().assignLeafList(select, values);
		List<Attribute>[] attr = operation.getInput().getLeafListAttributes(
				select);
		for (int i = 0; i < sels.length; i++)
		{
			List<Attribute> a = attr[i];
			a.add(new Attribute("xmlns", sels[i].getNamespace()));
		}

		operation.getOutput().linkLeaf(lockId);

	}

	/**
	 * This class provides specific methods to process
	 * the data obtained in response to a RpcReply
	 * after a partial-lock operation.
	 * 
	 * @author Giuseppe Palmeri
	 * 
	 */
	public class PartialLockReply extends Reply
	{

		private String lId;

		/**
		 * Constructs an instance of the class.
		 * 
		 * @param reply
		 *            The Rpc Reply for the lock-id operation.
		 */
		public PartialLockReply(RpcReply reply)
		{
			super(reply);
			lId = operation.getOutput().getLeafCanonicalValue(lockId);
		}

		/**
		 * Get the lock identifier associated with the partial-lock operation.
		 * 
		 * @return The lock identifier or null if the operation failed.
		 */
		public Integer getLockId() {
			Integer id = null;
			if (lId != null)
			{
				try
				{
					return Integer.parseInt(lId);
				}
				catch (Exception e)
				{
					return null;
				}
			}
			return id;
		}
	}

}
