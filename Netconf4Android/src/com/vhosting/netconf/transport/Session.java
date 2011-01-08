package com.vhosting.netconf.transport;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

/**
 * This class represents a session of talks with a server.
 * An active session is delivered after it is established a connection
 * to the server.
 * A session becomes inactive if the connection is lost for any reason.
 * A session, when it becomes inactive, can no longer be used.
 * 
 * @author Giuseppe Palmeri
 * 
 */
public class Session
{

	private static String xmlns_base_1_0 = "urn:ietf:params:xml:ns:netconf:base:1.0";
	private static String xmlns_notification_1_0 = "urn:ietf:params:xml:ns:netconf:notification:1.0";
	private static String prefix_base_1_0 = "nc";
	private static String prefix_notification_1_0 = "nt";

	private static String xmlns_partial_lock_1_0 = "urn:ietf:params:xml:ns:netconf:partial-lock:1.0";
	private static String prefix_partial_lock_1_0 = "pl";

	private static NamespacesPool ns = new NamespacesPool();
	private static Vector<Capability> register = new Vector<Capability>();
	private Vector<Capability> availables = new Vector<Capability>();

	private static Vector<Session> activeSessions = new Vector<Session>();

	private boolean isActive = false;

	public static Capability BASE_1_0 = new Capability(
			"urn:ietf:params:netconf:base:1.0", xmlns_base_1_0, prefix_base_1_0);
	public static Capability WRITABLE_RUNNING_1_0 = new Capability(
			"urn:ietf:params:netconf:capability:writable-running:1.0",
			xmlns_base_1_0, prefix_base_1_0);
	public static Capability CANDIDATE_1_0 = new Capability(
			"urn:ietf:params:netconf:capability:candidate:1.0", xmlns_base_1_0,
			prefix_base_1_0);
	public static Capability CONFIRMED_COMMIT_1_0 = new Capability(
			"urn:ietf:params:netconf:capability:confirmed-commit:1.0",
			xmlns_base_1_0, prefix_base_1_0);
	public static Capability ROLLBACK_ON_ERROR_1_0 = new Capability(
			"urn:ietf:params:netconf:capability:rollback-on-error:1.0",
			xmlns_base_1_0, prefix_base_1_0);
	public static Capability VALIDATE_1_0 = new Capability(
			"urn:ietf:params:netconf:capability:validate:1.0", xmlns_base_1_0,
			prefix_base_1_0);
	public static Capability STARTUP_1_0 = new Capability(
			"urn:ietf:params:netconf:startup:1.0", xmlns_base_1_0,
			prefix_base_1_0);
	public static Capability URL_1_0 = new Capability(
			"urn:ietf:params:netconf:capability:url:1.0", xmlns_base_1_0,
			prefix_base_1_0);
	public static Capability XPATH_1_0 = new Capability(
			"urn:ietf:params:netconf:capability:xpath:1.0", xmlns_base_1_0,
			prefix_base_1_0);
	public static Capability PARTIAL_LOCK_1_0 = new Capability(
			"urn:ietf:params:netconf:capability:partial-lock:1.0",
			xmlns_partial_lock_1_0, prefix_partial_lock_1_0);
	public static Capability NOTIFICATION_1_0 = new Capability(
			"urn:ietf:params:netconf:capability:notification:1.0",
			xmlns_notification_1_0, prefix_notification_1_0);
	public static Capability INTERLEAVE_1_0 = new Capability(
			"urn:ietf:params:netconf:capability:interleave:1.0",
			xmlns_base_1_0, prefix_base_1_0);

	private Integer sessionId;

	static void registerCapability(Capability cap) {
		if (register.contains(cap))
			return;
		register.add(cap);
		String newPrefix = ns.put(cap.getNamespaceURI(), cap.prefix);
		if (newPrefix != null)
			cap.prefix = newPrefix;

		for (int i = 0; i < activeSessions.size(); i++)
		{
			Session session = activeSessions.elementAt(i);
			for (String uri : session.serverURIs)
			{
				cap.setServerCapabilityPresence(uri, session);
				if (cap.isPresentOnServer(session))
				{
					session.availables.add(cap);
				}
			}
		}

	}

	static Capability[] getRegisteredCapabilities() {
		return register.toArray(new Capability[register.size()]);
	}

	void inactiveSession() {

		if (!isActive()) return;
		for (int i = 0; i < register.size(); i++)
		{
			Capability cap = register.elementAt(i);
			cap.unsetServerCapabilityPresence(this);
		}
		serverURIs = null;
		availables.clear();
		activeSessions.remove(this);
		isActive = false;
	}

	private String[] serverURIs;

	void activeSession(String[] serverCapabilityURIs, Integer sessionId) {
		this.sessionId = sessionId;
		isActive = true;
		for (String uri : serverCapabilityURIs)
		{

			try
			{
				new URI(uri);
			}
			catch (URISyntaxException e)
			{

				continue;
			}

			for (int i = 0; i < register.size(); i++)
			{
				Capability cap = register.elementAt(i);
				if (cap.setServerCapabilityPresence(uri, this))
				{
					availables.add(cap);
				}
			}
		}
		serverURIs = serverCapabilityURIs;
		activeSessions.add(this);

	}

	/**
	 * Get the session identifier.
	 * 
	 * @return The session identifier; null if this session is inactive.
	 */
	public Integer getSessionId() {
		return this.sessionId;

	}

	/**
	 * Allows you to check if the session is active.
	 * 
	 * @return true if the session is active; false otherwise.
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Search for a capability that until now has been used by a namespace.
	 * 
	 * @param namespaceURI
	 *            The namespace URI.
	 * @return The capability or null if not found.
	 */
	public static Capability findCapabilityByNamespaceURI(String namespaceURI) {
		for (int i = 0; i < register.size(); i++)
		{
			Capability cap = register.get(i);
			if (cap.namespaceURI.equals(namespaceURI))
				return cap;
		}
		return null;
	}

	/**
	 * Get the capabilities that until now has been used and present on the
	 * server.
	 * 
	 * @return The capabilities.
	 */
	public Capability[] getCapabilitiesOnServer() {
		return availables.toArray(new Capability[availables.size()]);
	}

}

/**
 * This class is a container of namespaces.
 * 
 * @author Giuseppe Palmeri
 * 
 */
class NamespacesPool
{

	private Hashtable<String, String> ns = new Hashtable<String, String>();
	private Hashtable<String, String> prfxs = new Hashtable<String, String>();

	NamespacesPool()
	{

	}

	/**
	 * Inserts a new namespace in the container.
	 * 
	 * The namespace prefix specified is only candidate
	 * to be associated with the namespace.
	 * If you already have a prefix associated with another
	 * namespace, the method creates and returns a new prefix.
	 * 
	 * @param namespaceURI
	 *            The namespace URI.
	 * @param prefix
	 *            The namespace URI prefix.
	 * @return The new prefix if necessary: null otherwise.
	 */
	public String put(String namespaceURI, String prefix) {
		/*
		 * Controlla se ci sono conflitti.
		 * Nel caso di conflitti esegue una RIMAPPATURA
		 * automatica e assegna un prefisso univoco.
		 * anzichè quello che è stato passato.
		 * Utile solo nella costruzione del messaggio RPC.
		 * Quando preleva, fa riferimento solo ai Namespace URI.
		 */
		String ons = prfxs.get(prefix);
		if (ns.get(namespaceURI) != null
				&& !ns.get(namespaceURI).equals(prefix))
		{
			return ns.get(namespaceURI);
		}
		else if (ons != null)
		{
			if (namespaceURI.equals(ons))
				return null;
			else
			{
				int i = 1;
				while (true)
				{
					String newPrefix = prefix + i;
					String ons1 = prfxs.get(newPrefix);

					/*
					 * Se il nuovo prefisso metcha lo stesso NS
					 * Ritorna questo prefisso.
					 */
					if (ons1 != null && ons1.equals(namespaceURI))
					{
						return newPrefix;

					}
					/*
					 * Se il nuovo prefisso non corrisponde a nessun altro
					 * prefisso, utilizza il nuovo prefisso x il settaggio
					 * e ritorna il prefisso.
					 */
					if (ons1 == null)
					{
						put(namespaceURI, newPrefix);
						return newPrefix;
					}
					// Se il nuovo prefisso è associato a un NS diverso,
					// Prova con il prossimo prefisso.
					else
					{
						i++;
						continue;
					}
				}
			}
		}

		ns.put(namespaceURI, prefix);
		prfxs.put(prefix, namespaceURI);
		return null;
	}

	/**
	 * Get the prefix for the specified namespace URI.
	 * 
	 * @param namespaceURI
	 *            The namespace Uri.
	 * @return The prefix namespace.
	 */
	public String getPrefix(String namespaceURI) {
		return ns.get(namespaceURI);
	}

	String getNamespace(String prefix) {
		return prfxs.get(prefix);
	}

	Set<String> getPrefixes() {
		return prfxs.keySet();
	}

}
