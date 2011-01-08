package com.vhosting.netconf;

import java.util.Vector;

import com.vhosting.netconf.transport.Capability;

/**
 * This class represents a set of xpath that refer
 * to nodes within a datastore.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class XPathSelections
{

	/**
	 * Create an instance of XPathSelections.
	 */
	public XPathSelections()
	{}

	private Vector<Selection> selections = new Vector<Selection>();

	/**
	 * Adds an xpath selection.
	 * 
	 * @param xpath
	 *            The xpath selection.
	 * @param cap
	 *            The capability to which belongs this selection.
	 */
	public void addSelection(String xpath, Capability cap) {
		Selection s = new Selection(xpath, cap.getNamespaceURI(),
				cap.getPrefix());
		selections.add(s);
	}

	/**
	 * Get all the selections included.
	 * 
	 * @return All the selections included.
	 */
	public Selection[] getSelections() {
		return (Selection[]) selections
				.toArray(new Selection[selections.size()]);
	}

	/**
	 * This class identifies a xpath selection contained within
	 * a XPathSelections.
	 * 
	 * @author Giuseppe Palmeri
	 * 
	 */
	public final class Selection
	{

		private String xpath;
		private String namespaceURI;
		private String prefix;

		/**
		 * Get the XPath of the selection.
		 * 
		 * @return The XPath of the selection.
		 */
		public final String getXPath() {
			return xpath;
		}

		/**
		 * Get the capability namespace for this selection.
		 * 
		 * @return The capability namespace for this selection.
		 */
		public final String getNamespace() {
			return namespaceURI;
		}

		/**
		 * Get the capability namespace prefix for this selection.
		 * 
		 * @return The capability namespace prefix for this selection.
		 */
		public final String getPrefix() {
			return prefix;
		}

		private Selection(String xpath, String namespaceURI, String prefix)
		{
			this.xpath = xpath;
			this.namespaceURI = namespaceURI;
			this.prefix = prefix;
		}
	}
}
