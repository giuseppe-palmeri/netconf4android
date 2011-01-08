package com.vhosting.netconf.frame;

import java.util.LinkedHashMap;
import java.util.Vector;

import com.vhosting.netconf.transport.Capability;

/**
 * A list reference defines the identity of a list
 * with which it is possible to instantiate a new list
 * with the same characteristics and structure mirrored in common.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 09/10/2010
 * @see #createMirrorList()
 * @see List
 */
public class ListReference extends Identity implements Node
{

	Leaf[] key;
	Leaf[][] unique;

	Capability cap;

	long min = 0;
	long max = Long.MAX_VALUE;

	LinkedHashMap<String, Object> node = new LinkedHashMap<String, Object>();
	Vector<List> lists = new Vector<List>();

	/**
	 * Create a list reference.
	 * 
	 * @param cap
	 *            The capability that this list reference belongs.
	 * @param name
	 *            The name of the list.
	 * @param min
	 *            The minimum limit of set of assignments which can be assigned.
	 * @param max
	 *            The maximum limit of set of assignments which can be assigned.
	 */
	public ListReference(Capability cap, String name, long min, long max)
	{
		super(cap, name);
		this.min = min;
		this.max = max;
		this.cap = cap;

	}

	/**
	 * Create a list reference.
	 * 
	 * @param cap
	 *            The capability that this list reference belongs.
	 * @param name
	 *            The name of the list.
	 * @param min
	 *            The minimum limit of set of assignments which can be assigned.
	 */
	public ListReference(Capability cap, String name, long min)
	{
		super(cap, name);
		this.min = min;
		this.cap = cap;
	}

	/**
	 * Create a list reference without limits.
	 * 
	 * @param cap
	 *            The capability that this list reference belongs.
	 * @param name
	 *            The name of the list.
	 */
	public ListReference(Capability cap, String name)
	{
		super(cap, name);
		this.cap = cap;
	}

	/**
	 * Get the minimum limit of set of assignments which can be assigned.
	 * 
	 * @return The minimum limit of set of assignments which can be assigned.
	 */
	public final long getMinimum() {
		return min;
	}

	/**
	 * Get the maximum limit of set of assignments which can be assigned.
	 * 
	 * @return The maximum limit of set of assignments which can be assigned.
	 */
	public final long getMaximum() {
		return max;
	}

	/**
	 * Allows you to specify a primary key from the list of values.
	 * If a primary key is defined, will prevent the inclusion of a
	 * set of values with duplicate primary key.
	 * Only elements of type Leaf can be a primary key.
	 * In addition, leaf-type elements must have been previously
	 * linked to the List.
	 * 
	 * @param leafs
	 *            Leaf-type elements that form the primary key and
	 *            that have been previously linked to the List.
	 * 
	 */
	void setKey(Leaf[] leafs) {
		for (Leaf l : leafs)
		{
			Object o = node.get(l.getUniqueNane());
			if (o == null)
				throw new RuntimeException(
						"The specified Leaf not exists into the struct: "
								+ l.getUniqueNane());

		}
		this.key = leafs;
	}

	/**
	 * Allows you to specify an uniqueness into the list of values.
	 * If an uniqueness is specified, will prevent the inclusion of a
	 * set of values with duplicate set of values.
	 * Only elements of type Leaf can form an uniqueness.
	 * In addition, leaf-type elements must have been previously
	 * linked to the List.
	 * 
	 * @param leafs
	 *            Leaf-type elements that form the uniqueness and
	 *            that have been previously linked to the List.
	 * 
	 */
	void setUnique(Leaf[][] leafs) {
		for (Leaf[] ll : leafs)
		{
			for (Leaf l : ll)
			{
				Object o = node.get(l.getUniqueNane());
				if (o == null)
					throw new RuntimeException(
							"The specified Leaf not exists into the struct: "
									+ l.getUniqueNane());
			}
		}
		this.unique = leafs;
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
	 * Link a Container reference to this node as part of the RPC structure.
	 * 
	 * @param cr
	 *            The Container reference element to link.
	 * @return The linked Container reference element.
	 */
	public ContainerReference linkContainerReference(ContainerReference cr) {
		node.put(cr.getUniqueNane(), cr);
		return cr;
	}

	/**
	 * Link a List reference to this node as part of the RPC structure.
	 * 
	 * @param lr
	 *            The List reference element to link.
	 * @return The linked List reference element.
	 */
	public ListReference linkListReference(ListReference lr) {
		node.put(lr.getUniqueNane(), lr);
		return lr;
	}

	/**
	 * Create an instance of the list.
	 * This list dynamically inherits the
	 * entire property and structure associated with its reference.
	 * When its sub elements will be assigned, it will only be for it.
	 * 
	 * @return The instance (or mirror) of the list reference.
	 */
	List createMirrorList() {
		List l = new List(cap, name, min, max);
		l.node = node;
		lists.add(l);
		return l;
	}

}
