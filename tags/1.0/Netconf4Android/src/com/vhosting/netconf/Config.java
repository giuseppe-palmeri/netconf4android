package com.vhosting.netconf;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vhosting.netconf.frame.Anyxml;
import com.vhosting.netconf.transport.Capability;

/**
 * This class provides the elements to modify, add or
 * remove information between the configuration data.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class Config
{
	private Capability cap;
	private String config;
	private Anyxml a;

/**
	 * Create the instance of the class with which to provide 
	 * instructions for changing the configuration data.
	 * 
	 * A string configuration is a special string that allows you to 
	 * instruct the server how to change the configuration data.
     * <br><br>
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
	 * _node = node-name [ [ '=' value ] | [ '{' < 'merge' | 'replace' | 'create' | 'delete' > '}' ] ]
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
     * new Config(anyxml, cap, "interfaces/interface{replace}/name=eth0;description=$1", "New eth0 description");
     * 
     * Result: interfaces/interface{replace}/name=eth0;description=New eth0 description
     * 
     * 
     * This configuration string is calling for changes in scope of the 
     * interfaces, the description of the interface eth0 with the new 
     * value "New eth0 description".
	 * </pre>
	 * 
	 * @param anyxml An Anyxml element for which the configuration should be valid.
	 * @param cap The capability of the data to set.
	 * @param config
	 *            The config string.
	 * @param values
	 *            A set of values that are encoded and introduced into the
	 *            filter through the corresponding variables.
	 */
	public Config(Anyxml anyxml, Capability cap, String config,
			String... values)
	{

		this.cap = cap;
		this.config = SubtreeFilter.encode(config, values);
		this.a = anyxml;
	}

	/**
	 * Get the configuration string.
	 * 
	 * @return The configuration string.
	 */
	public String getConfig() {
		return config;
	}

	/**
	 * Get the capability that owns this configuration.
	 * 
	 * @return The capability that owns this configuration.
	 */
	public Capability getCapability() {
		return cap;
	}

	/**
	 * Create a valid XML document as an argument for
	 * the Anyxml element passed to the constructor.
	 * 
	 * @return The XML document.
	 */
	public Document createAnyxmlValue() {
		Document doc = a.createEmptyDocument();
		Element root = doc.getDocumentElement();
		try
		{
			SubtreeFilter.parseNodesString(cap, doc, root, config, true);
		}
		catch (Exception e)
		{
			throw new ConfigException(e.getMessage());
		}

		return doc;
	}

}