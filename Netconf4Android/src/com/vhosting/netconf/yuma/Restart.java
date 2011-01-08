package com.vhosting.netconf.yuma;

import com.vhosting.netconf.CapabilityException;

import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.transport.Session;
import com.vhosting.netconf.Operation;

/**
 * Restart the Yuma Netconf server.
 * 
 * @author Giuseppe Palmeri
 * 
 */
public class Restart extends Operation
{

	/**
	 * Create the Restart operation.
	 * 
	 * @throws CapabilityException
	 *             Throw this exception when the YUMA SYSTEM capability
	 *             is not registered or not supported by the server.
	 */
	public Restart(Session session) throws CapabilityException
	{
		super(session);
		if (!YumaCapabilities.YUMA_SYSTEM.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not registered into the system or supported by server: "
							+ YumaCapabilities.YUMA_SYSTEM
							+ "; Please don't use this operation.");

		operation = new Rpc(YumaCapabilities.YUMA_SYSTEM, "restart");

	}

}