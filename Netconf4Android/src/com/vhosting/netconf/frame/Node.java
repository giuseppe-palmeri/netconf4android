package com.vhosting.netconf.frame;

/**
 * This interface defines the methods that are
 * attributed to an element of Node type that is part
 * of a structure PRC.
 * 
 * For an element of Node type is possible to
 * combine primitive elements such as leaf,
 * leaf list or Anyxml.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 * 
 */
public interface Node
{

	/**
	 * Link a leaf to this node as part of the RPC structure.
	 * 
	 * @param l
	 *            The leaf element to link.
	 * @return The linked leaf element.
	 */
	Leaf linkLeaf(Leaf l);

	/**
	 * Link a leaf list to this node as part of the RPC structure.
	 * 
	 * @param l
	 *            The leaf list element to link.
	 * @return The linked leaf list element.
	 */
	LeafList linkLeafList(LeafList l);

	/**
	 * Link an Anyxml to this node as part of the RPC structure.
	 * 
	 * @param anyxml
	 *            The Anyxml element to link.
	 * @return The linked Anyxml element.
	 */
	Anyxml linkAnyxml(Anyxml anyxml);

}
