package com.vhosting.netconf.frame;

import java.util.LinkedHashMap;
import java.util.Vector;

import com.vhosting.netconf.transport.Capability;

/**
 * A container reference defines the identity of a container
 * with which it is possible to instantiate a new container
 * with the same characteristics and structure mirrored in common.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 09/10/2010
 * @see #createMirrorContainer()
 * @see Container
 */
public class ContainerReference extends Identity implements Node
{

	boolean presence;

	
	LinkedHashMap<String, Object> node = new LinkedHashMap<String, Object>();
	private Capability cap;
	private Vector<Container> containers = new Vector<Container>();

	/**
	 * Create a container reference.
	 * 
	 * @param cap
	 *            The capability that this container reference belongs.
	 * @param name
	 *            The name of the container.
	 */
	ContainerReference(Capability cap, String name)
	{
		super(cap, name);
		this.cap = cap;
	}

	/**
	 * Specifies whether the container should be considered
	 * to exist even when there are not assigned elements or sub elements.
	 * 
	 * @param b
	 *            True if this container is a presence container; false
	 *            otherwise.
	 */
	public void setPresence(boolean b) {
		this.presence = b;
		for (int i = 0; i < containers.size(); i++)
		{
			Container c = containers.elementAt(i);
			c.setPresence(b);
		}
	}

	@Override
	public Leaf linkLeaf(Leaf l) {
		node.put(l.getUniqueNane(), l);
		return l;
	}

	@Override
	public LeafList linkLeafList(LeafList l) {
		node.put(l.getUniqueNane(), l);
		return l;
	}

	@Override
	public Anyxml linkAnyxml(Anyxml anyxml) {
		node.put(anyxml.getUniqueNane(), anyxml);
		return anyxml;
	}

	/**
	 * Link a List to this container as part of the RPC structure.
	 * 
	 * @param l
	 *            The List element to link.
	 * @return The linked List element.
	 */
	public List linkList(List l) {
		node.put(l.getUniqueNane(), l);
		return l;
	}

	/**
	 * Link a Container to this container as part of the RPC structure.
	 * 
	 * @param c
	 *            The Container element to link.
	 * @return The linked Container element.
	 */
	public Container linkContainer(Container c) {
		node.put(c.getUniqueNane(), c);
		return c;
	}

	/**
	 * Create an instance of the container.
	 * This container dynamically inherits the
	 * entire property and structure associated with its reference.
	 * When its sub elements will be assigned, it will only be for it.
	 * 
	 * @return The instance (or mirror) of the container reference.
	 */
	Container createMirrorContainer() {
		Container c = new Container(cap, name);
		c.setPresence(presence);
		c.node = node;
		containers.add(c);
		return c;
	}
}
