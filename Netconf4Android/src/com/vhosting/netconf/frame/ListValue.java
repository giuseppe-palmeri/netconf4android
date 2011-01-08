package com.vhosting.netconf.frame;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import java.util.Vector;

import org.w3c.dom.Document;

/**
 * This class provides the methods necessary
 * for the assignment of the elements contained within a list.
 * A list can accept one or more instances of this class
 * to complete their own set of assignments.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 09/10/2010
 * 
 */
public class ListValue implements Saveable, Clearable
{

	List list;

	Hashtable<String, Object> value = new Hashtable<String, Object>();

	LinkedHashMap<String, Object> tree = new LinkedHashMap<String, Object>();

	ListValue(List l)
	{
		list = l;
	}

	@Override
	public void assignLeaf(Leaf l, String canonicalValue) {
		Object o = list.node.get(l.getUniqueNane());
		if (o == null)
			throw new RuntimeException(
					"The specified Leaf is not present into the struct: "
							+ l.getUniqueNane());
		value.put(l.getUniqueNane(), canonicalValue);
	}

	@Override
	public void assignLeafList(LeafList l, String[] canonicalValues) {
		Object o = list.node.get(l.getUniqueNane());
		if (o == null)
			throw new RuntimeException(
					"The specified LeafList is not present into the struct: "
							+ l.getUniqueNane());

		if (!(l.min <= canonicalValues.length && canonicalValues.length <= l.max))
			throw new RuntimeException("Invalid number of elements: "
					+ l.getName() + "(min=" + l.min + "; max=" + l.max
					+ ") Array elements: " + canonicalValues.length);

		value.put(l.getUniqueNane(), canonicalValues);
	}

	@Override
	public void assignAnyxml(Anyxml a, Document e) {
		Object o = list.node.get(a.getUniqueNane());
		if (o == null)
			throw new RuntimeException(
					"The specified Anyxml is not present into the struct: "
							+ a.getUniqueNane());
		value.put(a.getUniqueNane(), e);
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
	public Set<String> getUniqueNanes() {
		return list.node.keySet();
	}

	@Override
	public Object getElemetByUniqueName(String uniqueName) {
		return list.node.get(uniqueName);
	}

	@Override
	public void unsetLeaf(Leaf l) {
		value.remove(l.getUniqueNane());

	}

	@Override
	public void unsetLeafList(LeafList l) {
		value.remove(l.getUniqueNane());

	}

	@Override
	public void unsetAnyxml(Anyxml a) {
		value.remove(a.getUniqueNane());

	}

	@Override
	public void clear() {
		tree.clear();
		value.clear();
		attributes.clear();
	}

	@Override
	public boolean hasValues() {
		boolean hasValues = false;
		Iterator<Object> vals = tree.values().iterator();
		while (vals.hasNext())
		{
			Clearable s = (Clearable) vals.next();
			hasValues = hasValues || s.hasValues();
		}

		return hasValues || value.size() > 0;
	}

	/**
	 * Get the unique instance of container for this ListValue
	 * created through the container reference specified.
	 * The container reference must have been previously
	 * linked to the List owner of this ListValue.
	 * 
	 * @param cr
	 *            The container reference previously linked
	 *            to the List owner of this ListValue.
	 * @return The instance of container for this ListValue.
	 */
	public Container getMirrorContainer(ContainerReference cr) {
		Container cc = (Container) tree.get(cr.getUniqueNane());
		if (cc != null)
			return cc;

		Object o = list.node.get(cr.getUniqueNane());

		// null oppure IllegalArgument exception
		if (o == null)
			return null;

		// null oppure IllegalArgument exception
		if (!(o instanceof ContainerReference))
			return null;

		cc = cr.createMirrorContainer();
		tree.put(cc.getUniqueNane(), cc);

		return cc;
	}

	/**
	 * Get the unique instance of list for this ListValue
	 * created through the list reference specified.
	 * The list reference must have been previously
	 * linked to the List owner of this ListValue.
	 * 
	 * @param lr
	 *            The list reference previously linked
	 *            to the List owner of this ListValue.
	 * @return The instance of list for this ListValue.
	 */
	public List getMirrorList(ListReference lr) {
		List ll = (List) tree.get(lr.getUniqueNane());
		if (ll != null)
			return ll;

		Object o = list.node.get(lr.getUniqueNane());

		// null oppure IllegalArgument exception
		if (o == null)
			return null;

		// null oppure IllegalArgument exception
		if (!(o instanceof ListReference))
			return null;

		ll = lr.createMirrorList();
		tree.put(ll.getUniqueNane(), ll);

		return ll;
	}

	private LinkedHashMap<String, Object> attributes = new LinkedHashMap<String, Object>();

	@SuppressWarnings("unchecked")
	@Override
	public java.util.List<Attribute> getLeafAttributes(Leaf l) {
		Object o = list.node.get(l.getUniqueNane());
		if (o == null)
			throw new RuntimeException(
					"The specified Leaf is not present into the struct: "
							+ l.getUniqueNane());
		java.util.List<Attribute> rtn = (java.util.List<Attribute>) attributes
				.get(l.getUniqueNane());
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
		Object o = list.node.get(l.getUniqueNane());
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
		Object o = list.node.get(a.getUniqueNane());
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
