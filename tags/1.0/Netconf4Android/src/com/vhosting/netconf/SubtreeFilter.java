package com.vhosting.netconf;

import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vhosting.netconf.frame.Anyxml;
import com.vhosting.netconf.transport.Capability;
import com.vhosting.netconf.transport.Session;

/**
 * This class represents a SubtreeFilter. A SubtreeFilter is a special filter
 * used to be able to select the data when using operations that require the
 * return of data.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class SubtreeFilter
{

	Anyxml a;

	/**
	 * Create a subtree filter starting from the instance of a type element
	 * Anyxml. The value to assign for this Anyxml will be the filter.
	 * 
	 * @param anyxml
	 *            The Anyxml element.
	 * @see #createAnyxmlValue()
	 */
	public SubtreeFilter(Anyxml anyxml)
	{
		this.a = anyxml;
	}

	private Vector<Filter> filters = new Vector<Filter>();

	/**
	 * Add a filter to the subtreefilter.
	 * 
	 * The created filter is empty and to be present, must be populated.
	 * 
	 * @param cap
	 *            The capability they belong to the filtered data.
	 * @return An instance for the filter you just created.
	 */
	public Filter addFilter(Capability cap) {
		Filter f = new Filter(cap);
		filters.add(f);
		return f;
	}

	/**
	 * Get all the filters setup.
	 * 
	 * @return The filters setup.
	 */
	public Filter[] getFilters() {
		return (Filter[]) filters.toArray(new Filter[filters.size()]);
	}

	/**
	 * This class identifies a filter contained within a SubtreeFilter.
	 * 
	 * @author Giuseppe Palmeri
	 * 
	 */
	public final class Filter
	{

		private Capability cap;

		/**
		 * Get the capability they belong to the filtered data.
		 * 
		 * @return The capability they belong to the filtered data.
		 */
		public final Capability getCapability() {
			return cap;
		}

		/**
		 * Get the filter elements.
		 * 
		 * @return The filer elements.
		 */
		public final String[] getFilterElements() {
			return filterElements;
		}

		private Vector<String> validatedFilters = new Vector<String>();
		private String[] filterElements = new String[0];

		private Filter(Capability cap)
		{
			this.cap = cap;
		}

		/**
		 * Add a filter string.
		 * 
		 * A filter string is a special string with the following format:
		 * 
		 * <pre>
		 * < _nodes > [ '|' _nodes ]*
		 * 
		 * Where:
		 * 
		 * _nodes = < _node > [ ';' _node ]*
		 * 
		 * Where:
		 * 
		 * _node = node-name [ '=' value ]
		 * 
		 * 
		 * Legend:
		 * <> = mandatory;
		 * [] = optional;
		 * * = 0 or more times;
		 * 
		 * You can use variables inside and will be replaced by 
		 * the sight of the values passed as the second argument.
		 * This ensures the integrity of information entered into the filter.
		 * The variables are identified as:
		 * $n where n = 1 to the number of values passed.
		 * 
		 * 
		 * Examples:
		 * 
		 * f.addFilterString("interfaces|interface|name=$1;description", "eth0");
		 * 
		 * Result: interfaces|interface|name=eth0;description
		 * 
		 * This filter requests that the outcome of the request, 
		 * including the interfaces, is taken only description 
		 * of the interface eth0.
		 * </pre>
		 * 
		 * @param filter
		 *            The filter string.
		 * @param values
		 *            A set of values that are encoded and introduced into the
		 *            filter through the corresponding variables.
		 */
		public void addFilterString(String filter, String... values) {
			filter = encode(filter, values);
			validatedFilters.add(filter);
			filterElements = validatedFilters
					.toArray(new String[validatedFilters.size()]);
		}
	}

	/**
	 * Create a valid XML document as an argument for
	 * the Anyxml element passed to the constructor.
	 * 
	 * @return The XML document.
	 */
	public Document createAnyxmlValue() {
		Document doc = a.createEmptyDocument();
		Element filter = doc.getDocumentElement();

		filter.setAttributeNS(a.getNamespaceURI(), a.getPrefix() + ":type",
				"subtree");
		Filter[] filters = getFilters();

		Element root = filter;

		for (Filter f : filters)
		{

			for (String elm : f.getFilterElements())
			{
				try
				{
					parseNodesString(f.cap, doc, root, elm, false);
				}
				catch (Exception e)
				{
					throw new SubtreeFilterException(e.getMessage());
				}
			}

		}

		return doc;
	}

	static String encode(String filter, String... values) {
		int i = 1;
		for (String v : values)
		{
			filter = filter.replaceAll("((?![\\\\])[$]" + i + ")", encode(v));
			i++;
		}
		return filter;
	}

	private static String encode(String s) {
		s = s.replace("|", "\\\\|");
		s = s.replace(";", "\\\\;");
		s = s.replace("=", "\\\\=");
		s = s.replace("{", "\\\\{");
		s = s.replace("}", "\\\\}");
		return s;
	}

	private static String[] split(String elm, int chr) throws IOException {
		Vector<String> tokens = new Vector<String>();
		StringReader reader = new StringReader(elm);
		int c = -1;
		int cc = -1;
		int take = 0;
		int i = 0;
		int start = 0;
		while ((c = reader.read()) != -1)
		{
			if (c == '[' && cc != '\\')
				take++;
			if (c == ']' && cc != '\\')
				take--;

			if (c == chr && cc != '\\')
			{
				if (take == 0)
				{
					tokens.addElement(elm.substring(start, i));
					start = i + 1;
				}
			}
			i++;
			cc = c;
		}
		String[] paths = null;
		if (start == 0)
		{
			paths = new String[] { elm };
		}
		else
		{
			tokens.addElement(elm.substring(start));
			paths = tokens.toArray(new String[tokens.size()]);
		}

		return paths;
	}



	static final void parseNodesString(Capability cap, Document doc,
			Element root, String elm, boolean isConfig) throws Exception {
		try
		{
			if (elm.trim().equals(""))
				return;
			String[] paths = split(elm, '|');

			for (int i = 0; i < paths.length; i++)
			{

				String[] sub = split(paths[i], ';');
				for (int n = 0; n < sub.length; n++)
				{

					String[] nv = split(sub[n], '=');
					String name = null;
					String value = null;
					String node = null;

					node = nv[0].replaceAll(".*?\\[(.*)\\].*?$", "$1");
					if (node.equals(nv[0]))
						node = null;
					name = nv[0].replaceAll("^(.*?)(?<![\\\\])\\[.*?$", "$1");
					name = name.replaceAll("\\\\(.)", "$1");

					String delete = ".*?\\{\\s*?delete\\s*?\\}";
					boolean doDelete = false;

					String create = ".*?\\{\\s*?create\\s*?\\}";
					boolean doCreate = false;

					String replace = ".*?\\{\\s*?replace\\s*?\\}";
					boolean doReplace = false;

					String merge = ".*?\\{\\s*?merge\\s*?\\}";
					boolean doMerge = false;

					if (isConfig)
					{
						if (name.matches(delete))
							doDelete = true;
						else if (name.matches(create))
							doCreate = true;
						else if (name.matches(replace))
							doReplace = true;
						else if (name.matches(merge))
							doMerge = true;

						name = name.replaceAll("\\{.*\\}", "");

					}

					if (nv.length > 1)
						value = nv[1].replaceAll("\\\\(.)", "$1");

					String op = Session.BASE_1_0.getPrefix() + ":operation";

					Element el = null;
					try
					{
						el = (Element) doc.createElementNS(
								cap.getNamespaceURI(), name);
						el.setPrefix(cap.getPrefix());
						if (doDelete)
						{
							el.setAttribute(op, "delete");
						}
						else if (doCreate)
						{
							el.setAttribute(op, "create");
						}
						else if (doReplace)
						{
							el.setAttribute(op, "replace");
						}
						else if (doMerge)
						{
							el.setAttribute(op, "merge");
						}

					}
					catch (Exception ex)
					{
						throw new Exception(name);
					}
					if (n == 0)
					{
						root.appendChild(el);
						root = el;
						if (node != null)
							parseNodesString(cap, doc, root, node, isConfig);
					}
					else
					{
						root.getParentNode().appendChild(el);
						if (node != null)
							parseNodesString(cap, doc, el, node, isConfig);
					}

					el.setPrefix(cap.getPrefix());
					if (value != null)
						el.setTextContent(value);

				}
			}
		}
		catch (Exception e)
		{
			throw new Exception("Invalid "
					+ ((isConfig) ? "config: '" : "filter: '") + elm
					+ "'; error on token: " + e.getMessage());
		}
	}

}
