package com.vhosting.netconf.frame;

import org.w3c.dom.Document;

/**
 * An element of the RPC structure that
 * implements this interface can itself
 * contain assignable elements.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 09/10/2010
 * 
 */
public interface Assignable
{

	/**
	 * Allows you to assign a value to a leaf element
	 * previously linked to this.
	 * 
	 * The value is the canonical value in a YANG context.
	 * See RFC6020
	 * 
	 * @param l
	 *            The leaf element.
	 * @param canonicalValue
	 *            The value of the leaf.
	 * @see Node
	 */
	void assignLeaf(Leaf l, String canonicalValue);

	/**
	 * Allows you to assign values to a leaf list element
	 * previously linked to this.
	 * 
	 * The values are the canonical values in a YANG context.
	 * See RFC6020
	 * 
	 * @param l
	 *            The leaf list element.
	 * @param canonicalValues
	 *            The values of the leaf list.
	 * @see Node
	 */
	void assignLeafList(LeafList l, String[] canonicalValues);

	/**
	 * Allows you to assign an XML document to an Anyxml element
	 * previously linked to this.
	 * 
	 * The XML document should be made in advance through the method:<br>
	 * Anyxml.createEmptyDocument();<br>
	 * of an instance of the Anyxml element.
	 * 
	 * @param a
	 *            The Anyxml element.
	 * @param e
	 *            The XML Document.
	 * @see Anyxml#createEmptyDocument()
	 */
	void assignAnyxml(Anyxml a, Document e);

	/**
	 * Get the value of the specified leaf element
	 * previously linked to this.
	 * 
	 * The value is the canonical value in a YANG context.
	 * See RFC6020
	 * 
	 * @param l
	 *            The leaf element.
	 * @return The value of the leaf element or null if not exists.
	 */
	String getLeafCanonicalValue(Leaf l);

	/**
	 * Get the values of the specified leaf list element
	 * previously linked to this.
	 * 
	 * The values are the canonical values in a YANG context.
	 * See RFC6020
	 * 
	 * @param l
	 *            The leaf list element.
	 * @return The values of the leaf list element or an empty array if not
	 *         exist.
	 */
	String[] getLeafListCanonicalValues(LeafList l);

	/**
	 * Get the XML Document of the specified Anyxml element
	 * previously linked to this.
	 * 
	 * @param anyxml
	 *            The Anyxml element.
	 * @return The XML document of the Anyxml element or null if not exists.
	 */
	Document getAnyxmlValue(Anyxml anyxml);

	/**
	 * Unset the value associated with the specified leaf element
	 * previously linked to this.
	 * 
	 * If no value was previously assigned to the element,
	 * the method does not perform any action.
	 * 
	 * @param l
	 *            The leaf element.
	 */
	void unsetLeaf(Leaf l);

	/**
	 * Unset the values associated with the specified leaf list element
	 * previously linked to this.
	 * 
	 * If no values was previously assigned to the element,
	 * the method does not perform any action.
	 * 
	 * @param l
	 *            The leaf list element.
	 */
	void unsetLeafList(LeafList l);

	/**
	 * Unset the XML document associated with the specified Anyxml element
	 * previously linked to this.
	 * 
	 * @param a
	 *            The Anyxml element.
	 */
	void unsetAnyxml(Anyxml a);

	/**
	 * Get the list of attributes of the specified leaf element
	 * previously linked to this.
	 * 
	 * The list can be populated with new attributes.
	 * 
	 * @param l
	 *            The leaf element.
	 * @return The list of attributes.
	 */
	java.util.List<Attribute> getLeafAttributes(Leaf l);

	/**
	 * Get the lists of attributes of the specified leaf list element
	 * previously linked to this.
	 * 
	 * Each list contained in the array refers to the corresponding
	 * value assigned to the leaf list.
	 * 
	 * The lists can be populated with new attributes.
	 * 
	 * @param l
	 *            The leaf list element.
	 * @return The array of lists of attributes.
	 */
	java.util.List<Attribute>[] getLeafListAttributes(LeafList l);

	/**
	 * Get the list of attributes of the specified Anyxml element
	 * previously linked to this.
	 * 
	 * The list can be populated with new attributes.
	 * 
	 * @param a
	 *            The Anyxml element.
	 * @return The list of attributes.
	 */
	java.util.List<Attribute> getAnyxmlAttributes(Anyxml a);

}
