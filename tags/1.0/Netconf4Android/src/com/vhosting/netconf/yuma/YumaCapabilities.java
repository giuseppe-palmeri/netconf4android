package com.vhosting.netconf.yuma;


/**
 * This interface defines all the Yuma capabilities can be
 * used with this implementation of netconf.
 * 
 * This is not a derived YANG module.
 * 
 * @author Giuseppe Palmeri
 * 
 */
public interface YumaCapabilities
{
	public static final YANGCapability YUMA_SYSTEM = new YANGCapability(
			"http://netconfcentral.org/ns/yuma-system",
			"http://netconfcentral.org/ns/yuma-system", "sys", "yuma-system",
			"2010-05-24");

	public static final YANGCapability YUMA_TYPES = new YANGCapability(
			"http://netconfcentral.org/ns/yuma-types",
			"http://netconfcentral.org/ns/yuma-types", "yt", "yuma-types",
			"2010-01-25");

}
