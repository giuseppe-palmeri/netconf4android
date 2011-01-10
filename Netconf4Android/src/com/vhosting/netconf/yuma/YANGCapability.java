package com.vhosting.netconf.yuma;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.vhosting.netconf.transport.Capability;

/**
 * This class adds new attributes to a capability belonging to a YANG module.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 * 
 */
public class YANGCapability extends Capability
{

	private String moduleName;
	private String revision;
	private String capUri;

	/**
	 * Create a capability associated with a specific YANG module.<br>
	 * <b>Note that in almost all cases, the capabilityBaseURI matches the namespaceUri.</b>
	 * <br>
	 * @param capabilityBaseURI
	 *            The capability base Uri or the capability Uri without
	 *            parameters.
	 * @param namespaceURI
	 *            The namespace Uri for this capability.
	 * @param prefix
	 *            The candidate namespace prefix of the namespace Uri.
	 * @param moduleName
	 *            The YANG module name.
	 */
	public YANGCapability(String capabilityBaseURI, String namespaceURI,
			String prefix, String moduleName)
	{
		this(capabilityBaseURI, namespaceURI, prefix, moduleName, null);

	}

	
	/**
	 * Create a capability associated with a specific YANG module.
	 * @param namespaceURI
	 *            The namespace Uri (or the capability base Uri) for this capability.
	 * @param prefix
	 *            The candidate namespace prefix of the namespace Uri.
	 * @param moduleName
	 *            The YANG module name.
	 */
	public YANGCapability(String namespaceURI, String prefix, String moduleName)
	{
		this(namespaceURI, namespaceURI, prefix, moduleName, null);
	}

	
	/**
	 * Create a capability associated with a specific YANG module.<br>
	 * <b>Note that in almost all cases, the capabilityBaseURI matches the namespaceUri.</b>
	 * <br>
	 * @param capabilityBaseURI
	 *            The capability base Uri or the capability Uri without
	 *            parameters.
	 * @param namespaceURI
	 *            The namespace Uri for this capability.
	 * @param prefix
	 *            The candidate namespace prefix of the namespace Uri.
	 * @param moduleName
	 *            The YANG module name.
	 * @param revision
	 *            The YANG module revision date.
	 */
	public YANGCapability(String capabilityBaseURI, String namespaceURI,
			String prefix, String moduleName, String revision)
	{
		super(capabilityBaseURI, namespaceURI, prefix);
		this.moduleName = moduleName;
		this.revision = revision;

		if (revision!=null) if (!revision.matches("\\d{4}-\\d{2}-\\d{2}"))
			throw new IllegalArgumentException(
					"Invalid revision paramenter: YYYY-MM-DD; Invalid format: "
							+ revision);

		try
		{
			capUri = this.getCapabilityBaseURI() + "?module="
					+ URLEncoder.encode(this.getModuleName(), "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Get the YANG module name.
	 * 
	 * @return The YANG module name.
	 */
	public final String getModuleName() {
		return moduleName;
	}

	/**
	 * Get the YANG module revision if exists.
	 * 
	 * @return The YANG module revision; null if the revision is not specified.
	 */
	public final String getRevision() {
		return revision;
	}

	/**
	 * Get the capability uri with the parameters module and revision.
	 * 
	 * <pre>
	 * For example:
	 * 
	 *    http://www.exaple.com/example?module=myexample
	 *    
	 *    http://www.exaple.com/example?module=myexample&revision=2010-12-09
	 * 
	 * </pre>
	 * 
	 * @return The capability uri with the parameters module and revision.
	 */
	public String getCapabilityURI() {
		String rtn = capUri;
		if (this.getRevision() != null)
		{
			try
			{
				rtn += "&revision="
						+ URLEncoder.encode(this.getRevision(), "UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				e.printStackTrace();
			}
		}
		return rtn;
	}

}
