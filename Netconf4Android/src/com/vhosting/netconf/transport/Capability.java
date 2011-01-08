package com.vhosting.netconf.transport;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/**
 * This class represents a generic capability.
 * 
 * When it is necessary to have a new capability,
 * this class can be instantiated and used the
 * instance to create new operations.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 * 
 */
public class Capability
{

	String capabilityBaseURI;
	String namespaceURI;
	protected String prefix;

	private Hashtable<Integer, CapabilityParams> params = new Hashtable<Integer, CapabilityParams>();

	private String stripBaseURI(String uri) {
		return uri.replaceAll("[?].*?$", "");
	}

	@Override
	public String toString()
	{
		return capabilityBaseURI;
	}
	
	
	/**
	 * Constructs the instance of a capability.
	 * 
	 * @param capabilityBaseURI
	 *            The capability base Uri or the capability Uri without
	 *            parameters.
	 * @param namespaceURI
	 *            The namespace Uri for this capability.
	 * @param prefix
	 *            The candidate namespace prefix of the namespace Uri.
	 */
	public Capability(String capabilityBaseURI, String namespaceURI,
			String prefix)
	{
		this.capabilityBaseURI = capabilityBaseURI;
		this.namespaceURI = namespaceURI;
		this.prefix = prefix;

		Session.registerCapability(this);

	}

	/**
	 * Use this method when you want to ensure that the
	 * capability is included in the Hello message before
	 * establishing a connection.
	 */
	public final void touch() {

	}

	/**
	 * Check if the capability is present on the server.
	 * 
	 * @param session
	 *            The active session.
	 * @return true if the capability is present on the server; false otherwise;
	 */
	public boolean isPresentOnServer(Session session) {
		if (!session.isActive())
			throw new RuntimeException("The server connection is not present.");
		return getServerCapabilityParams(session) != null;
	}

	/**
	 * When the session is active, takes the server side parameters set for this
	 * capability.
	 * 
	 * @param session
	 *            The active session.
	 * @return The capability params; null if the capability is not present on
	 *         the server
	 */
	public CapabilityParams getServerCapabilityParams(Session session) {
		if (!session.isActive())
			throw new RuntimeException("The server connection is not present.");
		CapabilityParams srvParams = params.get(session.getSessionId());
		return srvParams;
	}

	/**
	 * Assign the presence of a specific capability available through the
	 * server.
	 * 
	 * This method is used in very exceptional cases.
	 * You should never have occasion to use it unless your server has
	 * the ability to load netconf capabilities in a dynamic way in a different
	 * context other than the opening of the session.
	 * 
	 * This method does not load the capability directly on the server,
	 * but says its presence at the client side.
	 * 
	 * @param capabilityURI
	 *            A full capability Uri with any parameters.
	 * @param session
	 *            The active session.
	 * @return true if the specified server capability Uri is for this
	 *         capability; false otherwise;
	 */
	boolean setServerCapabilityPresence(String capabilityURI, Session session) {
		if (!session.isActive())
			throw new RuntimeException("The server connection is not present.");
		String baseURI = stripBaseURI(capabilityURI);
		if (baseURI.equals(this.capabilityBaseURI))
		{
			CapabilityParams srvParams = new CapabilityParams(capabilityURI);
			params.put(session.getSessionId(), srvParams);
			return true;
		}
		return false;
	}

	/**
	 * Unset the presence of the capability on the server side.
	 * After this method is invoked, the server side parameters are no longer
	 * available.
	 * 
	 * @param session
	 *            The active session.
	 */
	public void unsetServerCapabilityPresence(Session session) {
		if (!session.isActive())
			throw new RuntimeException("The server connection is not present.");
		params.remove(session.getSessionId());
	}

	/**
	 * Get the namespace prefix.
	 * 
	 * @return The namespace prefix.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Get the capability namespace Uri.
	 * 
	 * @return The namespace Uri.
	 */
	public String getNamespaceURI() {
		return namespaceURI;
	}

	/**
	 * Get the capability base Uri or the capability Uri without parameters.
	 * 
	 * @return The capability Uri without parameters.
	 */
	public String getCapabilityBaseURI() {
		return capabilityBaseURI;
	}

	/**
	 * This class defines the parameters for a capability provided by the
	 * server.
	 * 
	 * @author Giuseppe Palmeri
	 * 
	 */
	public final class CapabilityParams
	{
		private String revision;
		private String module;
		private String[] features;
		private String[] deviations;

		boolean isNetconfCapability = false;
		boolean isYANGCapability = false;

		Hashtable<String, String> params = new Hashtable<String, String>();

		CapabilityParams(String capabilityURI)
		{
			if (capabilityURI.matches("^urn:ietf:params:netconf:capability:"))
			{
				isNetconfCapability = true;
				return;
			}
			loadParameters(params, capabilityURI);
			revision = params.get("revision");
			module = params.get("module");
			String features = params.get("features");
			if (features != null)
				this.features = features.split(",");
			if (this.features != null)
				for (int i = 0; i < this.features.length; i++)
					this.features[i] = this.features[i].trim();
			String deviations = params.get("deviations");
			if (deviations != null)
				this.deviations = deviations.split(",");
			if (this.deviations != null)
				for (int i = 0; i < this.deviations.length; i++)
					this.deviations[i] = this.deviations[i].trim();
			if (module != null)
				isYANGCapability = true;
		}

		/**
		 * Get any YANG module name from the parameters.
		 * 
		 * @return The any YANG module name; null if it not exists.
		 */
		public String getYANGModuleName() {
			return module;
		}

		/**
		 * Get any YANG module revision date from the parameters.
		 * 
		 * @return The any YANG module revision date; null if it not exists.
		 */
		public String getYANGModuleRevision() {
			return revision;
		}

		/**
		 * Get any YANG module features from the parameters.
		 * 
		 * @return The any YANG module features; null if its not exist.
		 */
		public String[] getYANGModuleFeatures() {
			return features;
		}

		/**
		 * Get any YANG module deviations from the parameters.
		 * 
		 * @return The any YANG module deviations; null if its not exist.
		 */
		public String[] getYANGModuleDeviations() {
			return deviations;
		}

		/**
		 * Get the value of a parameter.
		 * 
		 * @param name
		 *            The name of the parameter.
		 * @return The value of the paramenter; null if not exists.
		 */
		public String getParam(String name) {
			return params.get(name);
		}

		/**
		 * Check to see if its features can be deduced that the capability is a
		 * Netconf capability.
		 * 
		 * @return true if the capability is a Netconf capability; false
		 *         otherwise.
		 */
		public boolean isNetconfCapability() {
			return isNetconfCapability;
		}

		/**
		 * Check if the parameters can be deduced that the capability represents
		 * a YANG module.
		 * 
		 * @return true if the capability represents a YANG module; false
		 *         otherwise.
		 */
		public boolean isYANGImplSpecCapability() {
			return isYANGCapability;
		}

		/**
		 * Check if the parameters can be deduced that the
		 * capability is a specific implementation of a capability.
		 * 
		 * @return true if the capability is a specific
		 *         implementation of a capability; false otherwise.
		 */
		public boolean isASpecImplCapability() {
			return !isNetconfCapability;
		}

		private void loadParameters(Hashtable<String, String> params,
				String capabilityURI) {
			params.clear();
			String q = null;
			String uri = capabilityURI;

			if (uri.matches("^.*?[?].*?$"))
			{
				q = uri.replaceAll("^.*?[?]", "");
			}

			if (q != null)
			{
				String[] nv = q.split("&");
				int i = 0;
				for (String a : nv)
				{

					try
					{
						String[] nnvv = a.split("=");
						String name = nnvv[0];
						String value = (nnvv.length > 1) ? nnvv[1] : null;
						name = java.net.URLDecoder.decode(name, "UTF-8");
						if (value != null)
							value = java.net.URLDecoder.decode(value, "UTF-8");
						if (value != null)
							params.put(name, value);

					}
					catch (UnsupportedEncodingException e)
					{

					}
					i++;
				}
			}
		}
	}
}
