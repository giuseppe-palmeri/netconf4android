package com.vhosting.netconf.frame;

import com.vhosting.netconf.transport.Capability;

/**
 * This class adds convenience methods to an Identity.
 * If you need these methods are readily available, an element 
 * can extend this class instead of directly to the Identity class.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 09/10/2010
 * 
 */
public abstract class IdentityCreator extends Identity
{

	private Capability cap;

	/**
	 * Use this constructor when you decide to create a subclass.
	 * @param cap The Capability.
	 * @param name The name of the Identity.
	 */
	protected IdentityCreator(Capability cap, String name)
	{
		super(cap, name);
		this.cap = cap;
	}
	
	
	
	/**
	 * This is a convenience method that creates
	 * an identity with the namespace of the
	 * RPC operation.
	 * 
	 * @param name
	 *            The name of the identity.
	 * @return The identity reference.
	 */
	public Identity createIdentity(String name) {
		return createIdentity(cap, name);
	}

	/**
	 * This is a convenience method that creates
	 * a leaf with the namespace of the RPC operation.
	 * 
	 * @param name
	 *            The name of the leaf.
	 * @return The leaf.
	 */
	public Leaf createLeaf(String name) {
		return createLeaf(cap, name);
	}

	/**
	 * This is a convenience method that creates
	 * a leaf list with the namespace of the RPC operation.
	 * 
	 * @param name
	 *            The name of the leaf list.
	 * @return The leaf list.
	 */
	public LeafList createLeafList(String name) {
		return createLeafList(cap, name);
	}

	/**
	 * This is a convenience method that creates
	 * a list with the namespace of the RPC operation.
	 * 
	 * @param name
	 *            The name of the list.
	 * @return The list.
	 */
	public List createList(String name) {
		return createList(cap, name);
	}

	/**
	 * This is a convenience method that creates
	 * a list reference with the namespace of the RPC operation.
	 * 
	 * @param name
	 *            The name of the list.
	 * @return The list reference.
	 */
	public ListReference createListReference(String name) {
		return createListReference(cap, name);
	}

	/**
	 * This is a convenience method that creates
	 * an Anyxml with the namespace of the RPC operation.
	 * 
	 * @param name
	 *            The name of the Anyxml.
	 * @return The Anyxml.
	 */
	public Anyxml createAnyxml(String name) {
		return createAnyxml(cap, name);
	}

	/**
	 * This is a convenience method that creates
	 * a Container with the namespace of the RPC operation.
	 * 
	 * @param name
	 *            The name of the Container.
	 * @return The Container.
	 */
	public Container createContainer(String name) {
		return createContainer(cap, name);
	}

	/**
	 * This is a convenience method that creates
	 * a Container reference with the namespace of the RPC operation.
	 * 
	 * @param name
	 *            The name of the Container.
	 * @return The Container reference.
	 */
	public ContainerReference createContainerReference(String name) {
		return createContainerReference(cap, name);
	}

	/**
	 * This is a convenience method that creates
	 * an identity with the namespace of the
	 * specified Capability.
	 * 
	 * @param name
	 *            The name of the identity.
	 * @return The identity reference.
	 */
	public static Identity createIdentity(Capability cap, String name) {
		Identity i = new Identity(cap, name);
		return i;
	}

	/**
	 * This is a convenience method that creates
	 * a leaf with the namespace of the
	 * specified Capability.
	 * 
	 * @param name
	 *            The name of the leaf.
	 * @return The leaf.
	 */
	public static Leaf createLeaf(Capability cap, String name) {
		Leaf l = new Leaf(cap, name);
		return l;
	}

	/**
	 * This is a convenience method that creates
	 * a leaf list with the namespace of the
	 * specified Capability.
	 * 
	 * @param name
	 *            The name of the leaf list.
	 * @return The leaf list.
	 */
	public static LeafList createLeafList(Capability cap, String name) {
		LeafList l = new LeafList(cap, name);
		return l;
	}

	/**
	 * This is a convenience method that creates
	 * a list with the namespace of the
	 * specified Capability.
	 * 
	 * @param name
	 *            The name of the list.
	 * @return The list.
	 */
	public static List createList(Capability cap, String name) {
		List l = new List(cap, name);
		return l;
	}

	/**
	 * This is a convenience method that creates
	 * a list reference with the namespace of the
	 * specified Capability.
	 * 
	 * @param name
	 *            The name of the list.
	 * @return The list reference.
	 */
	public static ListReference createListReference(Capability cap, String name) {
		ListReference l = new ListReference(cap, name);
		return l;
	}

	/**
	 * This is a convenience method that creates
	 * an Anyxml with the namespace of the
	 * specified Capability.
	 * 
	 * @param name
	 *            The name of the Anyxml.
	 * @return The Anyxml.
	 */
	public static Anyxml createAnyxml(Capability cap, String name) {
		Anyxml a = new Anyxml(cap, name);
		return a;
	}

	/**
	 * This is a convenience method that creates
	 * a Container with the namespace of the
	 * specified Capability.
	 * 
	 * @param name
	 *            The name of the Container.
	 * @return The Container.
	 */
	public static Container createContainer(Capability cap, String name) {
		Container c = new Container(cap, name);
		return c;
	}

	/**
	 * This is a convenience method that creates
	 * a Container reference with the namespace of the
	 * specified Capability.
	 * 
	 * @param name
	 *            The name of the Container.
	 * @return The Container reference.
	 */
	public static ContainerReference createContainerReference(Capability cap,
			String name) {
		ContainerReference c = new ContainerReference(cap, name);
		return c;
	}
	
	
	
}
