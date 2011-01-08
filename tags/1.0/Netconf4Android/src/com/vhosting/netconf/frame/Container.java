package com.vhosting.netconf.frame;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.w3c.dom.Document;

import com.vhosting.netconf.transport.Capability;

/**
 * This class represents a container in the PRC structure.
 * A container is a special element that can be linked
 * to any other element to create complex structures.
 * Can be connected to a container elements such as:<br>
 * <br>
 * 
 * <pre>
 * Leaf, LeafList, Anyxml, Container, List.
 * </pre>
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 09/10/2010
 */
public class Container extends ContainerReference implements Node, Saveable,
		Clearable
{

	/**
	 * Create a Container.
	 * 
	 * @param cap
	 *            The capability that this container belongs.
	 * @param name
	 *            The name of the container.
	 */
	protected Container(Capability cap, String name)
	{
		super(cap, name);
	}

	@Override
	public void clear() {

		for (String uniqueName : node.keySet())
		{
			Object n = node.get(uniqueName);
			if (n instanceof Clearable)
			{
				Clearable cl = (Clearable) n;
				cl.clear();
			}
		}
		value.clear();
		attributes.clear();
	}

	@Override
	public boolean hasValues() {

		boolean hasValues = false;

		for (String uniqueName : node.keySet())
		{
			Object n = node.get(uniqueName);
			if (n instanceof Clearable)
			{
				hasValues = hasValues || ((Clearable) n).hasValues();
			}
		}

		return hasValues || value.size() > 0;
	}

	Hashtable<String, Object> value = new Hashtable<String, Object>();

	@Override
	public void assignLeaf(Leaf l, String canonicalValue) {
		Object o = node.get(l.getUniqueNane());
		if (o == null)
			throw new RuntimeException(
					"The specified Leaf is not present into the struct: "
							+ l.getUniqueNane());
		value.put(l.getUniqueNane(), canonicalValue);
		attributes.remove(l.getUniqueNane());
	}

	@Override
	public void assignLeafList(LeafList l, String[] canonicalValues) {
		Object o = node.get(l.getUniqueNane());
		if (o == null)
			throw new RuntimeException(
					"The specified LeafList is not present into the struct: "
							+ l.getUniqueNane());

		if (!(l.min <= canonicalValues.length && canonicalValues.length <= l.max))
			throw new RuntimeException("Invalid number of elements: "
					+ l.getName() + "(min=" + l.min + "; max=" + l.max
					+ ") Array elements: " + canonicalValues.length);

		value.put(l.getUniqueNane(), canonicalValues);
		attributes.remove(l.getUniqueNane());
	}

	@Override
	public void assignAnyxml(Anyxml a, Document e) {
		Object o = node.get(a.getUniqueNane());
		if (o == null)
			throw new RuntimeException(
					"The specified Anyxml is not present into the struct: "
							+ a.getUniqueNane());
		value.put(a.getUniqueNane(), e);
		attributes.remove(a.getUniqueNane());
	}

	@Override
	public String getLeafCanonicalValue(Leaf l) {
		return (String) value.get(l.getUniqueNane());
	}

	@Override
	public String[] getLeafListCanonicalValues(LeafList l) {
		return (String[]) value.get(l.getUniqueNane());
	}

	@Override
	public Document getAnyxmlValue(Anyxml a) {
		return (Document) value.get(a.getUniqueNane());
	}

	@Override
	public void unsetLeaf(Leaf l) {
		value.remove(l.getUniqueNane());
		attributes.remove(l.getUniqueNane());
	}

	@Override
	public void unsetLeafList(LeafList l) {
		value.remove(l.getUniqueNane());
		attributes.remove(l.getUniqueNane());
	}

	@Override
	public void unsetAnyxml(Anyxml a) {
		value.remove(a.getUniqueNane());
		attributes.remove(a.getUniqueNane());
	}

	@Override
	public Set<String> getUniqueNanes() {

		return node.keySet();
	}

	@Override
	public Object getElemetByUniqueName(String uniqueName) {
		return node.get(uniqueName);
	}

	Hashtable<String, Object> attributes = new Hashtable<String, Object>();

	@SuppressWarnings("unchecked")
	@Override
	public java.util.List<Attribute> getLeafAttributes(Leaf l) {
		Object o = node.get(l.getUniqueNane());
		if (o == null)
			throw new RuntimeException(
					"The specified Leaf is not present into the struct: "
							+ l.getUniqueNane());
		java.util.List<Attribute> rtn = (List<Attribute>) attributes.get(l
				.getUniqueNane());
		if (rtn == null)
		{
			rtn = (java.util.List<Attribute>) new Vector<Attribute>();
			attributes.put(l.getUniqueNane(), rtn);
			return rtn;
		}
		return rtn;
	}

	@SuppressWarnings("unchecked")
	@Override
	public java.util.List<Attribute>[] getLeafListAttributes(LeafList l) {
		Object o = node.get(l.getUniqueNane());
		if (o == null)
			throw new RuntimeException(
					"The specified LeafList is not present into the struct: "
							+ l.getUniqueNane());
		java.util.List<Attribute>[] rtn = (java.util.List<Attribute>[]) attributes
				.get(l.getUniqueNane());

		if (rtn == null)
		{
			String[] cv = getLeafListCanonicalValues(l);
			Vector<java.util.List<Attribute>> att = new Vector<java.util.List<Attribute>>();
			for (int i = 0; i < cv.length; i++)
				att.add(new Vector<Attribute>());
			rtn = att.toArray(new java.util.List[att.size()]);
			attributes.put(l.getUniqueNane(), rtn);
			return rtn;
		}

		return rtn;

	}

	@SuppressWarnings("unchecked")
	@Override
	public java.util.List<Attribute> getAnyxmlAttributes(Anyxml a) {
		Object o = node.get(a.getUniqueNane());
		if (o == null)
			throw new RuntimeException(
					"The specified Anyxml is not present into the struct: "
							+ a.getUniqueNane());
		java.util.List<Attribute> rtn = (java.util.List<Attribute>) attributes
				.get(a.getUniqueNane());
		if (rtn == null)
		{
			rtn = (java.util.List<Attribute>) new Vector<Attribute>();
			attributes.put(a.getUniqueNane(), rtn);
			return rtn;
		}
		return rtn;
	}

}
