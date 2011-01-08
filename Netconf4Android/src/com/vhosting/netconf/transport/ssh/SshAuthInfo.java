package com.vhosting.netconf.transport.ssh;

import java.io.File;
import java.net.InetSocketAddress;

/**
 * This class provides the information needed to make
 * a connection Netconf over SSH-2 such as a remote host name,
 * user name and password.
 * 
 * @author Giuseppe Palmeri
 * @version 1.00, 02/11/2010
 */
public class SshAuthInfo
{

	/**
	 * This enum contains the authentications types
	 * of an SSH-2 connection.
	 */
	enum AuthType
	{

		BASIC_AUTH,

		PUBLICKEY_AUTH
	}

	/**
	 * Contains the type of authentication to be performed.
	 */
	AuthType authType = AuthType.BASIC_AUTH;

	// localhost with default port
	private InetSocketAddress host = new InetSocketAddress("127.0.0.1", 380);

	// No proxy for default
	private InetSocketAddress proxyHost = null;

	// Empty uname and passwd for default
	private String passwd;
	private String uname;
	private File pemFile;
	private String pemFilePass;

	/**
	 * Prepares a connection of type 'AuthType.BASIC_AUTH' on
	 * the host localhost at port 380 (default Netconf port)
	 * 
	 * @param uname
	 *            The User name.
	 * @param passwd
	 *            The password.
	 */
	public SshAuthInfo(String uname, String passwd)
	{
		this.uname = uname;
		this.passwd = passwd;
		authType = AuthType.BASIC_AUTH;
	}

	/**
	 * Prepares a connection of type 'AuthType.BASIC_AUTH' on
	 * the specified host at port 380 (default Netconf port)
	 * 
	 * @param hostName
	 *            The Host name.
	 * @param uname
	 *            The User name.
	 * @param passwd
	 *            The password.
	 */
	public SshAuthInfo(String hostName, String uname, String passwd)
	{
		this.host = new InetSocketAddress(hostName, 380);
		this.uname = uname;
		this.passwd = passwd;
		authType = AuthType.BASIC_AUTH;

	}

	/**
	 * Prepares a connection of type 'AuthType.BASIC_AUTH' on
	 * the specified host.
	 * 
	 * @param host
	 *            The Host.
	 * @param uname
	 *            The User name.
	 * @param passwd
	 *            The password.
	 */
	public SshAuthInfo(InetSocketAddress host, String uname, String passwd)
	{
		this.host = host;
		this.uname = uname;
		this.passwd = passwd;
		authType = AuthType.BASIC_AUTH;
	}

	/**
	 * 
	 * Prepares a connection of type 'AuthType.PUBLICKEY_AUTH' on
	 * the specified host using a PEM file.
	 * 
	 * @param host
	 *            The Host.
	 * @param uname
	 *            The User name.
	 * @param pemFile
	 *            The PEM file.
	 * @param pemFilePass
	 *            The PEM file password.
	 */
	public SshAuthInfo(InetSocketAddress host, String uname, File pemFile,
			String pemFilePass)
	{
		this.host = host;
		this.uname = uname;
		this.pemFile = pemFile;
		this.pemFilePass = pemFilePass;
		authType = AuthType.PUBLICKEY_AUTH;
	}

	/**
	 * 
	 * Prepares a connection of type 'AuthType.PUBLICKEY_AUTH' on
	 * the specified host name at the default port 380 using a PEM file.
	 * 
	 * @param hostName
	 *            The Host name.
	 * @param uname
	 *            The User name.
	 * @param pemFile
	 *            The PEM file.
	 * @param pemFilePass
	 *            The PEM file password.
	 */
	public SshAuthInfo(String hostName, String uname, File pemFile,
			String pemFilePass)
	{
		this.host = new InetSocketAddress(hostName, 380);
		this.uname = uname;
		this.pemFile = pemFile;
		this.pemFilePass = pemFilePass;
		authType = AuthType.PUBLICKEY_AUTH;
	}

	/**
	 * This method permit to specific an HTTP Proxy when exist.
	 * 
	 * @param proxyHost
	 *            The HTTP Proxy host name.
	 */
	public final void setProxyHost(InetSocketAddress proxyHost) {
		this.proxyHost = proxyHost;
	}

	/**
	 * Retrieve the HTTP Proxy host.
	 * 
	 * @return The HTTP proxy if exists; null if the proxy is not set.
	 */
	public InetSocketAddress getProxyHost() {
		return proxyHost;
	}

	/**
	 * Get the User name.
	 * 
	 * @return The User name.
	 */
	public String getUname() {
		return uname;
	}

	/**
	 * Get the PEM file.
	 * 
	 * @return The User name.
	 */
	public File getPemFile() {
		return pemFile;
	}

	/**
	 * Get the PEM file password.
	 * 
	 * @return The PEM file pass.
	 */
	public String getPemFilePass() {
		return pemFilePass;
	}

	/**
	 * Get the password.
	 * 
	 * @return The password.
	 */
	public String getPasswd() {
		return passwd;
	}

	/**
	 * Get the host.
	 * 
	 * @return The host.
	 */
	public InetSocketAddress getHost() {
		return host;
	}

	/**
	 * Remove the HTTP Proxy when is set.
	 */
	public void removeProxyHost() {
		
		proxyHost = null;
	}
}