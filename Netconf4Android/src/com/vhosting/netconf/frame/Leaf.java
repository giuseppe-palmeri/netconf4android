package com.vhosting.netconf.frame;

import com.vhosting.netconf.transport.Capability;

/**
 * This class represents an element of an RPC structure
 * that identifies a Leaf
 * in the Netconf RPC, RPC Reply or Notification message.
 * 
 * @version 1.00, 09/10/2010
 * 
 */
public class Leaf extends Identity
{

	/**
	 * Create a leaf element.
	 * 
	 * @param cap
	 *            The capability that this element belongs.
	 * @param name
	 *            The name of the leaf element.
	 */
	Leaf(Capability cap, String name)
	{
		super(cap, name);
	}

}
