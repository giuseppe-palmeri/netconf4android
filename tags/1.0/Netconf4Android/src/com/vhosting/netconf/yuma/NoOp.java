package com.vhosting.netconf.yuma;

import com.vhosting.netconf.CapabilityException;

import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.transport.Session;
import com.vhosting.netconf.Operation;

/**
 * The Yuma Netconf server no-op.
 * This operation not do anything.
 * Serves to keep the connection open for long periods,
 * when there may be a long time between one operation
 * to another.
 * 
 * @author Giuseppe Palmeri
 * 
 */
public class NoOp extends Operation
{

	/**
	 * Create the no-op operation.
	 * 
	 * @throws CapabilityException
	 *             Throw this exception when the YUMA SYSTEM capability
	 *             is not registered or not supported by the server.
	 */
	public NoOp(Session session) throws CapabilityException
	{
		super(session);
		if (!YumaCapabilities.YUMA_SYSTEM.isPresentOnServer(session))
			throw new CapabilityException(
					"This capability is not registered into the system or supported by server: "
							+ YumaCapabilities.YUMA_SYSTEM
							+ "; Please don't use this operation.");

		operation = new Rpc(YumaCapabilities.YUMA_SYSTEM, "no-op");

	}

}