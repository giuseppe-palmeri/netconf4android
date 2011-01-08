package com.vhosting.netconf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vhosting.netconf.frame.Anyxml;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.frame.RpcReply;
import com.vhosting.netconf.transport.Session;


/**
 * The get Netconf operation.
 * 
 * Retrieve running configuration and device state information.
 * 
 * Supported server capabilities:
 * <pre>
 * urn:ietf:params:netconf:base:1.0
 * urn:ietf:params:netconf:capability:xpath:1.0
 * </pre>
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class Get extends Operation {

	private Anyxml filter;

	private Anyxml outData = new Anyxml(Session.BASE_1_0, "data");

	/**
	 * Create the get Netconf operation.
	 * @param session The active session.
	 */
	public Get(Session session) {
		super(session);
		operation = new Rpc(Session.BASE_1_0, "get");
		filter = operation.getInput().linkAnyxml(operation.createAnyxml("filter"));
	    
		operation.getOutput().linkAnyxml(outData);
	}

	/**
	 * Create a subtree filter.
	 * Simply call this method to create a filter which can 
	 * be used with the operation.
     * 
     * The created filter is an empty filter.
     * 
     * Through an empty filter, no data will be available as an answer.
     * 
     * You will need to populate the filter created with references 
     * to the data you want.
	 * 
	 * @return The SubtreeFilter created.
	 */
	public SubtreeFilter createSubtreeFilter()
	{
		SubtreeFilter f = new SubtreeFilter(filter);
		
		
		return f;
	}
	
	
	/**
	 * Set the subtree filter.
	 * 
	 * @param f The subtree filter.
	 * @see #createSubtreeFilter()
	 */
	public void setSubtreeFilter(SubtreeFilter f)
	{
		operation.getInput().assignAnyxml(this.filter, f.createAnyxmlValue());
	}
	
	
	/**
	 * Set an XPath filter.
	 * Use this method when the operation includes the possibility 
	 * of an XPath filter on data received.
     * The XPath to be used as a filter for receiving data.
     * 
     * The filter will be interpreted by the server.
     * 
	 * @param xpath The XPath string.
	 * @throws CapabilityException Throw this exception if the server 
	 * does not have the :xpath:1.0 capability.
	 */
	public void setXPathFilter(String xpath) throws CapabilityException
	{

		if (!Session.XPATH_1_0.isPresentOnServer(session))
			throw new CapabilityException("This capability is not supported by server: " + 
					Session.XPATH_1_0 + "; Please use a SubtreeFilter instead.");
		
		Document doc = filter.createEmptyDocument();
		Element root = doc.getDocumentElement();
		root.setAttributeNS(filter.getNamespaceURI(), filter.getPrefix() + ":type", "xpath");
		root.setAttributeNS(filter.getNamespaceURI(), filter.getPrefix() + ":select", xpath);
		operation.getInput().assignAnyxml(this.filter, doc);
		
	}
	
	
	/**
	 * This class provides specific methods to process 
	 * the data obtained in response to a RpcReply 
	 * after a get operation.
	 * 
	 * @author Giuseppe Palmeri
	 *
	 */
	public class GetReply extends Reply
	{
		Document data;
		
		/**
		 * Constructs an instance of the class.
		 * @param reply The Rpc Reply for the get operation.
		 */
		public GetReply(RpcReply reply)
		{
			super(reply);
			data = operation.getOutput().getAnyxmlValue(outData);
			
		}
		
		/**
		 * Get the output data as an XML Document.
		 * 
		 * This document will contain all the required data.
         * The document root element is the &lt;data&gt; xml element.
         * <pre>
         * Example:
         * 
         * ...
         * Get g = new Get(c.getSession());
         * SubtreeFilter sf = g.createSubtreeFilter();
         * Filter f = sf.addFilter(MyCapabilities.EXAMPLE);
         * f.addFilterString("machines/machine|sysname=Linux");
         * g.setSubtreeFilter(sf);
         * RpcReply rep = g.executeSync(rpcHandler);
         * GetReply grp = g.new GetReply(rep);
         * Document d = <b>grp.getData();</b>
         * DOMUtils.dump(d, System.out);
         * ...
         * 
         * Into the stdout is printed in the 
         * following XML Document (or something similar):
         * 
         * <?xml version="1.0" encoding="UTF-8"?>
         * <nc:data xmlns:nc="urn:ietf:params:xml:ns:netconf:base:1.0">
         *    <ex:machines xmlns:sys="http://www.example.com/example">
         *       <ex:machine>
         *          <ex:sysname>Linux</ex:sysname>
         *          <ex:type>i686</ex:type>
         *       </ex:machine>
         *    </ex:machines>
         * </nc:data>
         * 
		 * </pre>
		 * @return The XML Document contain all the required data or null if the operation failed.
		 */
		public Document getData()
		{
			return data;
		}

	}
	

	

}
