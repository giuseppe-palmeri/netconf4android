package com.vhosting.netconf;

/**
 * This class represents a datastore.
 * A datastore is a special tag used in the
 * RPC messages to refer precisely to the configuration
 * data in a given datastore.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class Datastore
{

	/**
	 * Identifies the "running" datastore.
	 * This is the Datastore of the
	 * configuration data that are currently used by the system.
	 */
	public static final Datastore running = new Datastore("running");

	/**
	 * Identifies the "candidate" datastore.
	 * This is the Datastore of the
	 * configuration data that is used as the working copy.
	 */
	public static final Datastore candidate = new Datastore("candidate");

	/**
	 * Identifies the "startup" datastore.
	 * This is the Datastore of the configuration
	 * data that is used as a copy of data initialization.
	 */
	public static final Datastore startup = new Datastore("startup");

	private String name;

	/**
	 * Create a datastore.
	 * 
	 * @param name
	 *            The name of the datastore.
	 */
	Datastore(String name)
	{
		this.name = name;
	}

	/**
	 * Return the datastore name.
	 * 
	 * @return the name of the datastore.
	 */
	public final String getName() {
		return name;
	}

}
