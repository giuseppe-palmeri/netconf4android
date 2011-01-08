package com.vhosting.netconf.frame;

import com.vhosting.netconf.transport.Capability;

/**
 * This class allows to implement objects that
 * possess a specific identity within the Netconf system.
 * An identity is characterized by its own name,
 * namespace and its own unique name.
 * 
 * <pre>
 * The shape of a unique name is:
 * 
 * &lt;namespace prefix&gt; : &lt;name&gt;
 * 
 * Identity example:
 * 
 * Namespace = http://www.example.com/example
 * Name = myexample
 * Unique Name = ex:myexample
 * 
 * Where 'ex' is the prefix of the 'http://www.example.com/example' namespace.
 * </pre>
 * 
 * 
 * An identity can be treated as a value
 * within a Netconf structure such as Rpc.
 * The value to assign to the element, in this case is
 * given by the method getUniqueName();
 * 
 * @see #getUniqueNane()
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 09/10/2010
 * 
 */
public class Identity
{

	/**
	 * The identity namespace URI.
	 */
	protected String namespaceURI;

	/**
	 * The identity prefix.
	 */
	protected String prefix;

	/**
	 * The identity name.
	 */
	protected String name;


	/**
	 * Create an identity for a specified capability.
	 * 
	 * @param cap
	 *            The capability of this identity.
	 * @param name
	 *            The name of the identity.
	 */
	public Identity(Capability cap, String name)
	{
		if (cap != null)
		{
		   this.namespaceURI = cap.getNamespaceURI();
		   this.prefix = cap.getPrefix();
		}
		this.name = name;
	}
	
	/**
	 * Get the unique name of the identity.
	 * 
	 * @return The unique name of the identity.
	 */
	public String getUniqueNane() {
		if (prefix == null || name == null)
			return null;
		return prefix + ":" + name;
	}

	/**
	 * Get the name of the identity.
	 * 
	 * @return The name of the identity.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the namespace URI of the identity.
	 * 
	 * @return The namespace URI of the identity.
	 */
	public String getNamespaceURI() {
		return namespaceURI;
	}

	/**
	 * Get the namespace prefix of the identity.
	 * @return The prefix.
	 */
	public String getPrefix() {
		return prefix;
	}

}
