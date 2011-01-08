package com.vhosting.netconf.frame;

import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.vhosting.netconf.messages.DOMUtils;
import com.vhosting.netconf.transport.Capability;
import com.vhosting.netconf.transport.Session;

/**
 * An element of the RPC structure that
 * implements this interface can be involved
 * in a process of saving as an XML document
 * or in a loading process from an XML document.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 09/10/2010
 * 
 */
interface Saveable extends Assignable
{

	/**
	 * Get all the unique names of the elements previously linked to this.
	 * 
	 * @return The set of unique names.
	 */
	Set<String> getUniqueNanes();

	/**
	 * Get an element of the PRC structure previously linked to
	 * this through its unique name.
	 * 
	 * @param uniqueName
	 *            The unique name of the element.
	 * @return The PRC structure element previously linked to this through its
	 *         unique name.
	 */
	Object getElemetByUniqueName(String uniqueName);

}

class Save
{

	static void saveContainer(Container c, Document d, Element root) {
		Element e = null;
		boolean hasValues = c.hasValues();
		if (c.presence || hasValues)
		{
			e = (Element) root.appendChild(d.createElementNS(c.namespaceURI,
					c.name));
			e.setPrefix(c.prefix);
		}
		if (hasValues)
			save(c, d, e);
	}

	static void saveList(List l, Document d, Element root) {
		ListValue[] values = l.getValues();

		if (values != null)
			for (ListValue lv : values)
			{
				Element e = null;
				boolean hasValues = lv.hasValues();
				if (hasValues)
				{
					e = (Element) root.appendChild(d.createElementNS(
							l.namespaceURI, l.name));
					e.setPrefix(l.prefix);
					save(lv, d, e);
				}
			}
	}

	static void save(Saveable saveable, Document d, Element root) {
		for (String uniqueName : saveable.getUniqueNanes())
		{
			Object n = saveable.getElemetByUniqueName(uniqueName);

			if (n instanceof Leaf)
			{
				Leaf l = (Leaf) n;
				String v = saveable.getLeafCanonicalValue(l);
				if (v != null)
				{
					Element e = (Element) root.appendChild(d.createElementNS(
							l.namespaceURI, l.name));
					e.setPrefix(l.prefix);
					e.setTextContent(v);

					saveAttributes(e, d, saveable.getLeafAttributes(l));

				}
			}
			if (n instanceof LeafList)
			{
				LeafList l = (LeafList) n;
				String[] vv = saveable.getLeafListCanonicalValues(l);
				int i = 0;
				if (vv != null)
					for (String v : vv)
					{
						Element e = (Element) root.appendChild(d
								.createElementNS(l.namespaceURI, l.name));
						e.setPrefix(l.prefix);
						if (v != null)
							e.setTextContent(v);

						saveAttributes(e, d,
								saveable.getLeafListAttributes(l)[i]);
						i++;
					}
			}

			if (n instanceof Anyxml)
			{
				Anyxml a = (Anyxml) n;
				Document doc = saveable.getAnyxmlValue(a);

				if (doc != null && doc.getDocumentElement() != null)
				{
					
					 try
					 {
					    Element vv = (Element) d.importNode(
					    doc.getDocumentElement(), true);
					    root.appendChild(vv);
					    saveAttributes(vv, d, saveable.getAnyxmlAttributes(a));
					 }
					 catch(Exception e)
					 {
						// This resolve a bug on Android.
						Element vv = doc.getDocumentElement();
	                    appendNode(d, root, vv);
	                    saveAttributes(vv, d, saveable.getAnyxmlAttributes(a));
					} 
				}

			}

			if (n instanceof Container)
			{
				Container c = (Container) n;
				saveContainer(c, d, root);
			}
			else if (n instanceof ContainerReference)
			{
				ListValue lv = (ListValue) saveable;
				ContainerReference cr = (ContainerReference) n;
				Container c = lv.getMirrorContainer(cr);
				saveContainer(c, d, root);
			}

			if (n instanceof List)
			{
				List l = (List) n;
				saveList(l, d, root);
			}
			else if (n instanceof ListReference)
			{

				ListValue lv = (ListValue) saveable;
				ListReference lr = (ListReference) n;
				List l = lv.getMirrorList(lr);
				saveList(l, d, root);
			}
		}

	}

	private static void appendNode(Document doc, Element root,
			org.w3c.dom.Node vv) {

		if (vv instanceof Element)
		{
			Element e = doc.createElementNS(vv.getNamespaceURI(),
					vv.getLocalName());
			e.setPrefix(vv.getPrefix());
			if (!vv.hasChildNodes())
				e.setTextContent(vv.getTextContent());
			else
			{
				appendNode(doc, e, (Element) vv.getFirstChild());
			}
			NamedNodeMap attrs = vv.getAttributes();
			for (int n = 0; n < attrs.getLength(); n++)
			{
				Attr att = (Attr) attrs.item(n);
				try
				{
					if (att.getNamespaceURI() == null)
					{
						Attr a = doc.createAttribute(att.getName());
						a.setValue(att.getValue());
						e.setAttributeNode(a);
					}
					else
					{
						Attr a = doc.createAttributeNS(att.getNamespaceURI(),
								att.getName());
						a.setPrefix(att.getPrefix());
						a.setValue(att.getValue());
						e.setAttributeNode(a);
					}
				}
				catch (Exception e1)
				{
					System.out.println(">>> " + att.getNamespaceURI());

					e1.printStackTrace();

				}
			}
			root.appendChild(e);
		}
		org.w3c.dom.Node s;
		while ((s = vv.getNextSibling()) != null)
		{
			if (s instanceof Element)
			{
				Element ss = (Element) s;
				Element x = doc.createElementNS(ss.getNamespaceURI(),
						ss.getLocalName());
				x.setPrefix(ss.getPrefix());

				NamedNodeMap attrs = ss.getAttributes();
				for (int n = 0; n < attrs.getLength(); n++)
				{
					Attr att = (Attr) attrs.item(n);
					if (att.getNamespaceURI() == null)
					{
						Attr a = doc.createAttribute(att.getName());
						a.setValue(att.getValue());
						x.setAttributeNode(a);
					}
					else
					{
						Attr a = doc.createAttributeNS(att.getNamespaceURI(),
								att.getName());
						a.setPrefix(att.getPrefix());
						a.setValue(att.getValue());
						x.setAttributeNode(a);
					}
				}

				if (!ss.hasChildNodes())
					x.setTextContent(ss.getTextContent());
				else
				{
					appendNode(doc, x, (Element) ss.getFirstChild());
				}
				root.appendChild(s);
			}
		}

	}

	private static void saveAttributes(Element e, Document doc,
			java.util.List<Attribute> attributes) {
		for (int i = 0; i < attributes.size(); i++)
		{
			Attribute a = attributes.get(i);
			if (a.getNamespaceURI() != null)
			{
				Attr attr = doc.createAttributeNS(a.getNamespaceURI(),
						a.getUniqueNane());
				attr.setValue(a.getValue());
				e.setAttributeNodeNS(attr);
			}
			else
			{
				e.setAttribute(a.getName(), a.getValue());
			}
		}

	}
}

class Load
{
	static void load(Element e, Saveable saveable) {

		if (e == null)
			return;

		Set<String> unames = saveable.getUniqueNanes();
		for (String uniqueName : unames)
		{
			Object n = saveable.getElemetByUniqueName(uniqueName);
			if (n instanceof Leaf)
			{

				final Leaf l = (Leaf) n;

				NodeList nl = e.getElementsByTagNameNS(l.namespaceURI, l.name);
				if (nl.getLength() < 1)
					continue;
				Element x = (Element) nl.item(0);
				saveable.assignLeaf(l, x.getTextContent());

				loadAttributes(x, saveable.getLeafAttributes(l));

			}
			if (n instanceof LeafList)
			{
				final LeafList l = (LeafList) n;

				NodeList nl = e.getElementsByTagNameNS(l.namespaceURI, l.name);
				int len = nl.getLength();
				if (len < 1)
				{
					saveable.assignLeafList(l, new String[0]);
					continue;
				}
				String[] values = new String[len];
				for (int i = 0; i < len; i++)
				{
					values[i] = nl.item(i).getTextContent();
				}
				saveable.assignLeafList(l, values);
				for (int i = 0; i < nl.getLength(); i++)
					loadAttributes((Element) nl.item(i),
							saveable.getLeafListAttributes(l)[i]);

			}
			if (n instanceof Anyxml)
			{
				final Anyxml a = (Anyxml) n;
				NodeList nl = e.getElementsByTagNameNS(a.namespaceURI, a.name);
				if (nl.getLength() < 1)
					continue;
				Element x = (Element) nl.item(0);
				Document doc = DOMUtils.newDocument();
				doc.appendChild(doc.importNode(x, true));
				saveable.assignAnyxml(a, doc);
				loadAttributes(x, saveable.getAnyxmlAttributes(a));
			}

			if (n instanceof Container)
			{
				final Container cc = (Container) n;

				NodeList nl = e
						.getElementsByTagNameNS(cc.namespaceURI, cc.name);
				if (nl.getLength() < 1)
					continue;
				Element x = (Element) nl.item(0);
				load((Element) x, cc);
			}
			else if (n instanceof ContainerReference)
			{
				final ContainerReference cr = (ContainerReference) n;

				ListValue lv = (ListValue) saveable;
				load(e, lv.getMirrorContainer(cr));
			}

			if (n instanceof List)
			{
				final List l = (List) n;

				NodeList nl = e.getElementsByTagNameNS(l.namespaceURI, l.name);
				int len = nl.getLength();
				if (len < 1)
				{
					l.assign(new ListValue[0]);
					continue;
				}
				ListValue[] values = new ListValue[len];
				for (int i = 0; i < len; i++)
				{
					Element x = (Element) nl.item(i);
					values[i] = l.createListValue();
					load(x, values[i]);
				}
				l.assign(values);

			}
			else if (n instanceof ListReference)
			{
				final ListReference l = (ListReference) n;

				ListValue lv = (ListValue) saveable;
				List ll = lv.getMirrorList(l);
				NodeList nl = e.getElementsByTagNameNS(l.namespaceURI, l.name);
				int len = nl.getLength();
				if (len < 1)
				{
					ll.assign(new ListValue[0]);
					continue;
				}
				ListValue[] values = new ListValue[len];
				for (int i = 0; i < len; i++)
				{
					Element x = (Element) nl.item(i);
					values[i] = ll.createListValue();
					load(x, values[i]);
				}
				ll.assign(values);
			}
		}

	}

	private static void loadAttributes(Element x,
			java.util.List<Attribute> attributes) {

		NamedNodeMap n = x.getAttributes();
		for (int i = 0; i < n.getLength(); i++)
		{
			Attr a = (Attr) n.item(i);
			Capability cap = Session.findCapabilityByNamespaceURI(a
					.getNamespaceURI());
			if (cap != null)
			{
				Attribute aa = new Attribute(cap, a.getName(), a.getValue());
				attributes.add(aa);
			}
			else
			{
				Attribute aa = new Attribute(a.getName(), a.getValue());
				attributes.add(aa);
			}

		}

	}
}
