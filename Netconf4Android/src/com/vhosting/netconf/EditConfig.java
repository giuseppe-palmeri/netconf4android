package com.vhosting.netconf;

import java.net.URL;

import com.vhosting.netconf.frame.Anyxml;
import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.transport.Capability;
import com.vhosting.netconf.transport.Session;

/**
 * The edit-config Netconf operation.
 * 
 * This operation loads all or part of a specified
 * configuration to the specified target configuration. This
 * operation allows the new configuration to be expressed in several
 * ways, such as using a local file, a remote file, or inline. If
 * the target configuration does not exist, it will be created.
 * 
 * The device analyzes the source and target configurations and
 * performs the requested changes.
 * 
 * Supported server capabilities:
 * 
 * <pre>
 * urn:ietf:params:netconf:base:1.0
 * urn:ietf:params:netconf:capability:url:1.0
 * urn:ietf:params:netconf:capability:writable-running:1.0
 * urn:ietf:params:netconf:capability:candidate:1.0
 * urn:ietf:params:netconf:capability:validate:1.0
 * urn:ietf:params:netconf:capability:rollback-on-error:1.0
 * 
 * </pre>
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class EditConfig extends Operation
{

	/**
	 * This enumeration lists all kinds of tests that can
	 * be undertaken during the editing of the configuration data.
	 * 
	 * @author Giuseppe Palmeri
	 * 
	 */
	public enum TestOption
	{
		/**
		 * Perform a validation test before attempting to
		 * set. If validation errors occur, do not perform the
		 * edit-config operation. This is the default test-option.
		 */
		test_then_set
		{
			public String toString() {
				return "test-then-set";
			}
		},
		/**
		 * Perform a set without a validation test first.
		 */
		set
	}

	/**
	 * This enumeration lists all kinds of error options.
	 * Through these you can specify how the server should
	 * behave if there are any errors.
	 * 
	 * @author Giuseppe Palmeri
	 * 
	 */
	public enum ErrorOption
	{
		/**
		 * Abort the edit-config operation on first error.
		 * This is the default error-option.
		 */
		stop_on_error
		{
			public String toString() {
				return "stop-on-error";
			}
		},
		/**
		 * Continue to process configuration data on
		 * error; error is recorded, and negative response is generated
		 * if any errors occur.
		 */
		continue_on_error
		{
			public String toString() {
				return "continue-on-error";
			}
		},
		/**
		 * If an error condition occurs such that an
		 * error severity RPC error element is generated, the server
		 * will stop processing the edit-config operation and restore
		 * the specified configuration to its complete state at the
		 * start of this edit-config operation.
		 */
		rollback_on_error
		{
			public String toString() {
				return "rollback-on-error";
			}
		},
	}

	/**
	 * This enumeration lists each type of operation by default
	 * when editing configuration data.
	 * 
	 * @author Giuseppe Palmeri
	 * 
	 */
	public enum DefaultEditOperation
	{
		/**
		 * The configuration data in the specified Config instance is
		 * merged with the configuration at the corresponding level in
		 * the target Container. This is the default behavior.
		 */
		merge,

		/**
		 * The configuration data in the specified Config instance is
		 * completely replaces the configuration in the target
		 * Container. This is useful for loading previously saved
		 * configuration data.
		 */
		replace,

		/**
		 * The target Container is unaffected by the configuration
		 * in the specified Config instance, unless and until the incoming
		 * configuration data uses the "operation" attribute to request
		 * a different operation. If the configuration in the Config instance
		 * parameter contains data for which there is not a
		 * corresponding level in the target Container, an RPC error
		 * is returned with an Error Tag value of data-missing.
		 * Using "none" allows operations like "delete" to avoid
		 * unintentionally creating the parent hierarchy of the element
		 * to be deleted.
		 */
		none
	}

	private com.vhosting.netconf.frame.Container target;
	private static Anyxml config = new Anyxml(Session.BASE_1_0, "config");
	private Leaf defaultOperation;
	private Leaf testOption;
	private Leaf errorOption;

	/**
	 * Create the edit-config Netconf operation.
	 * 
	 * Allows you to specify configuration data through a URL.
	 * 
	 * Allows you to specify a default operation on the data.
	 * 
	 * @param session
	 *            The active session.
	 * @param target
	 *            The target Container.
	 * @param config
	 *            The URL of the data configuration.
	 * @param defop
	 *            The default edit operation.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :url:1.0 capability.
	 *             Throw this exception if the server
	 *             does not have the :candidate:1.0 capability and the source
	 *             container is 'candidate', also.
	 *             Similarly, throw this exception if the server
	 *             does not have the :writable-running:1.0 capability and the
	 *             source container is 'running'.
	 * @throws IllegalArgumentException
	 *             Throw this exception if the URL specified not have a file:
	 *             schema.
	 */
	public EditConfig(Session session, Datastore target, URL config,
			DefaultEditOperation defop) throws CapabilityException,
			IllegalArgumentException
	{
		this(session, target, config);
		operation.getInput().assignLeaf(defaultOperation, defop.toString());
	}

	private final void struct(Datastore target) {
		operation = new Rpc(Session.BASE_1_0, "edit-config");
		defaultOperation = operation.getInput().linkLeaf(
				operation.createLeaf("default-operation"));
		testOption = operation.getInput().linkLeaf(
				operation.createLeaf("test-option"));
		errorOption = operation.getInput().linkLeaf(
				operation.createLeaf("error-option"));

		this.target = operation.getInput().linkContainer(
				operation.createContainer("target"));
		Leaf t = this.target.linkLeaf(operation.createLeaf(target.getName()));
		this.target.assignLeaf(t, "");
		config = operation.getInput().linkAnyxml(
				operation.createAnyxml("config"));
	}

	/**
	 * Create the edit-config Netconf operation.
	 * 
	 * Allows you to specify configuration data through a URL.
	 * 
	 * @param session
	 *            The active session.
	 * @param target
	 *            The target Container.
	 * @param config
	 *            The URL of the data configuration.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :url:1.0 capability.
	 *             Throw this exception if the server
	 *             does not have the :candidate:1.0 capability and the source
	 *             container is 'candidate', also.
	 *             Similarly, throw this exception if the server
	 *             does not have the :writable-running:1.0 capability and the
	 *             source container is 'running'.
	 * @throws IllegalArgumentException
	 *             Throw this exception if the URL specified not have a file:
	 *             schema.
	 */
	public EditConfig(Session session, Datastore target, URL config)
			throws CapabilityException, IllegalArgumentException
	{
		super(session);
		validate(target);

		if ((config instanceof URL)
				&& !Session.URL_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.URL_1_0
							+ "; Please don't specific an URL for config data.");

		if (config instanceof URL)
		{
			if (!config.getProtocol().equalsIgnoreCase("file"))
				throw new IllegalArgumentException(
						"Only a 'file:' schema URLs hare supported as config data files.");
		}

		struct(target);
	}

	/**
	 * Create the edit-config Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param target
	 *            The target Container.
	 * @param config
	 *            The configuration data.
	 * @param defop
	 *            The default edit operation.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :candidate:1.0 capability and the source
	 *             container is 'candidate', also.
	 *             Similarly, throw this exception if the server
	 *             does not have the :writable-running:1.0 capability and the
	 *             source container is 'running'.
	 */
	public EditConfig(Session session, Datastore target, Config config,
			DefaultEditOperation defop) throws CapabilityException
	{
		this(session, target, config);

		operation.getInput().assignLeaf(defaultOperation, defop.toString());

	}

	/**
	 * Create the edit-config Netconf operation.
	 * 
	 * @param session
	 *            The active session.
	 * @param target
	 *            The target Container.
	 * @param config
	 *            The configuration data.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :candidate:1.0 capability and the source
	 *             container is 'candidate', also.
	 *             Similarly, throw this exception if the server
	 *             does not have the :writable-running:1.0 capability and the
	 *             source container is 'running'.
	 */
	public EditConfig(Session session, Datastore target, Config config)
			throws CapabilityException
	{
		super(session);
		validate(target);
		struct(target);

		operation.getInput().assignAnyxml(EditConfig.config,
				config.createAnyxmlValue());

	}

	/**
	 * Create an instance of Config for use with the operation.
	 * 
	 * @param cap
	 *            The capability in which the data belong to configure.
	 * @param config
	 *            The configuration string.
	 * @param values
	 *            A set of values that are encoded and introduced into the
	 *            filter through the corresponding variables.
	 * @return The instance of the configuration.
	 * @see Config#Config(Anyxml anyxml, Capability cap, String config,
	 *      String... values)
	 * 
	 */
	public static Config createConfig(Capability cap, String config,
			String... values) {
		return new Config(EditConfig.config, cap, config, values);
	}

	private void validate(Datastore target) throws CapabilityException {
		if (target.equals(Datastore.running)
				&& !Session.WRITABLE_RUNNING_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.WRITABLE_RUNNING_1_0
							+ "; Please don't specific a <running> container as target for edit operations.");

		if (target.equals(Datastore.candidate)
				&& !Session.CANDIDATE_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.CANDIDATE_1_0
							+ "; Please don't specific a <candidate> container as target or source in any operation.");
	}

	/**
	 * Set the test option on the transaction.
	 * 
	 * @param option
	 *            The test option.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :validate:1.0 capability.
	 */
	public void setTestOption(TestOption option) throws CapabilityException {

		if (!Session.VALIDATE_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.VALIDATE_1_0
							+ "; Please don't specific a TestOption for edit operations.");

		operation.getInput().assignLeaf(testOption, option.toString());
	}

	/**
	 * Set the error option.
	 * 
	 * @param option
	 *            The error option.
	 * @throws CapabilityException
	 *             Throw this exception if the server
	 *             does not have the :rollback-on-error:1.0 capability.
	 */
	public void setErrorOption(ErrorOption option) throws CapabilityException {

		if (option.equals(ErrorOption.rollback_on_error)
				&& !Session.ROLLBACK_ON_ERROR_1_0.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not supported by server: "
							+ Session.ROLLBACK_ON_ERROR_1_0
							+ "; Please don't specific an ErrorOption "
							+ ErrorOption.rollback_on_error
							+ " for edit operations.");

		operation.getInput().assignLeaf(errorOption, option.toString());
	}

}
