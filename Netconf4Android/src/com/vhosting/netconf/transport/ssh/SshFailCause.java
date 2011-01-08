package com.vhosting.netconf.transport.ssh;

import com.vhosting.netconf.transport.NetconfTransportError.FailCause;

/**
 * This enum groups all the causes of failure are
 * specific to the SSH-2 transport protocol.
 * 
 * @author Giuseppe Palmeri 10/11/2010
 * 
 */
public enum SshFailCause implements FailCause
{

	/**
	 * Cause of failure due to problems in SSH-2 session.
	 */
	CAUSE_SSH_SESSION_TROUBLES,

	/**
	 * Cause of failure due to interface problems with the netconf SSH-2
	 * subsystem.
	 */
	CAUSE_SSH_SUBSYSTEM_TROUBLES
}
