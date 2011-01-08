package com.vhosting.netconf.frame;

import com.vhosting.netconf.transport.Capability;

/**
 * 
 * This class represents a generic attribute
 * in a Netconf context.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class Attribute extends Identity
{

	private String value;

	/**
	 * Create an attribute with the namespace of the specified capability.
	 * 
	 * @param cap
	 *            The capability.
	 * @param name
	 *            The name of the attribute.
	 * @param value
	 *            The value of the attribute.
	 */
	public Attribute(Capability cap, String name, String value)
	{
		super(cap, name);
		this.value = value;
	}

	/**
	 * Create an attribute with the default namespace.
	 * 
	 * The default namespace in this implementation of Netconf is
	 * never used.
	 * 
	 * When you use this constructor, you may need to define
	 * the default namespace with another Attribute instance
	 * assigned to the same scope or a higher scope.
	 * 
	 * You can use the method createDefaultNamespaceAttribute(Capability cap)
	 * to create an attribute to be used in conjunction with this.
	 * 
	 * As it is, this attribute does not have its own identity.
	 * Methods getUniqueName() and getPrefix() will return null.
	 * 
	 * @param name
	 *            The name of the attribute.
	 * @param value
	 *            The value of the attribute.
	 * @see #createDefaultNamespaceAttribute(Capability cap)
	 * @see Identity#getUniqueNane()
	 * @see Identity#getPrefix()
	 */
	public Attribute(String name, String value)
	{
		super(null, name);
		this.value = value;
	}

	/**
	 * Convenience methods to create an attribute
	 * declaring the default namespace definition.
	 * 
	 * The attribute generated is to be used in
	 * conjunction with attributes whose namespace
	 * is not defined.
	 * 
	 * <pre>
	 * Example:
	 * 
	 * If the namespace is 'http://www.example.com/example' then the
	 * attribute is:
	 * 
	 *    xmlns='http://www.example.com/example'
	 * 
	 * </pre>
	 * 
	 * @param cap
	 *            The default capability namespace to declare.
	 * @return The attribute with the default namespace definition.
	 * @see #Attribute(String name, String value)
	 */
	public static Attribute createDefaultNamespaceAttribute(Capability cap) {
		Attribute a = new Attribute("xmlns", cap.getNamespaceURI());
		a.namespaceURI = "http://www.w3.org/2000/xmlns/";
		return a;
	}

	/**
	 * Get the attribute value.
	 * 
	 * @return The attribute value.
	 */
	public final String getValue() {
		return value;
	}

}
