package com.vhosting.netconf.frame;

import com.vhosting.netconf.transport.Capability;

/**
 * This class represents an element of an RPC structure
 * that identifies a leaf list
 * in the Netconf RPC, RPC Reply or Notification message.
 * 
 * @version 1.00, 09/10/2010
 * 
 */
public class LeafList extends Identity
{

	long min = 0;
	long max = Long.MAX_VALUE;

	/**
	 * Create a leaf list element.
	 * 
	 * @param cap
	 *            The capability that this element belongs.
	 * @param name
	 *            The name of the leaf list element.
	 */
	LeafList(Capability cap, String name)
	{
		super(cap, name);
	}

	/**
	 * Reset the Minimum and Maximum limits in 0 to Long.MAX_VALUE.
	 */
	public void reset() {
		this.min = 0;
		this.max = Long.MAX_VALUE;
	}

	/**
	 * Set the minimum limit of values which can be assigned.
	 * 
	 * @param min
	 *            The minimum limit of values which can be assigned.
	 */
	public void setMinimun(long min) {
		this.min = min;
	}

	/**
	 * Set the maximum limit of values which can be assigned.
	 * 
	 * @param max
	 *            The maximum limit of values which can be assigned.
	 */
	public void setMaximun(long max) {
		this.max = max;
	}

	/**
	 * Get the minimum limit of values which can be assigned.
	 * 
	 * @return The minimum limit of values which can be assigned.
	 */
	public final long getMinimum() {
		return min;
	}

	/**
	 * Get the maximum limit of values which can be assigned.
	 * 
	 * @return The maximum limit of values which can be assigned.
	 */
	public final long getMaximum() {
		return max;
	}

}
