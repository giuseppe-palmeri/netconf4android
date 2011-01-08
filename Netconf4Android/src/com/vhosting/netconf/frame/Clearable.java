package com.vhosting.netconf.frame;

/**
 * An element of the RPC structure that
 * implements this interface can remove all
 * assignments of values made up for himself
 * or on their sub elements in a recursive manner.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public interface Clearable
{

	/**
	 * Remove all assignments of values made up
	 * for himself or on their sub elements in
	 * a recursive manner.
	 */
	void clear();

	/**
	 * Check if assignments were made up
	 * for himself or on their sub elements in
	 * a recursive manner.
	 */
	boolean hasValues();
}
