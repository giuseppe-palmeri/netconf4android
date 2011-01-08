package com.vhosting.netconf.example;

import java.io.IOException;
import java.net.InetSocketAddress;

import javax.xml.xpath.XPath;

import com.vhosting.netconf.CapabilityException;
import com.vhosting.netconf.CloseSession;
import com.vhosting.netconf.Commit;
import com.vhosting.netconf.CopyConfig;
import com.vhosting.netconf.CreateSubscription;
import com.vhosting.netconf.Datastore;
import com.vhosting.netconf.DeleteConfig;
import com.vhosting.netconf.DiscardChanges;
import com.vhosting.netconf.Get;
import com.vhosting.netconf.GetConfig;
import com.vhosting.netconf.KillSession;
import com.vhosting.netconf.Lock;
import com.vhosting.netconf.PartialLock;
import com.vhosting.netconf.PartialUnlock;
import com.vhosting.netconf.Unlock;
import com.vhosting.netconf.Validate;
import com.vhosting.netconf.XPathSelections;
import com.vhosting.netconf.frame.RpcHandler;
import com.vhosting.netconf.transport.NetconfCatcher;
import com.vhosting.netconf.transport.NetconfCatcherListener;
import com.vhosting.netconf.transport.NetconfTransportEvent;
import com.vhosting.netconf.transport.Session;
import com.vhosting.netconf.transport.ssh.NetconfSshCather;
import com.vhosting.netconf.transport.ssh.SshAuthInfo;

public class Test
{

	public static final void main(String s[]) throws IOException 
	{
		/* Prepare the connection */
		InetSocketAddress addr = new InetSocketAddress("vhosting.eu.org", 22);
        NetconfSshCather catcher = new NetconfSshCather("test", new SshAuthInfo(addr, "netconf", "pippo"));
		
        /* The log is full enabled. */
        NetconfCatcher.enableLog(NetconfCatcher.LogLevel.MESSAGES);
        
        /* The body of the application */
        catcher.setNetconfCatcherListener(new NetconfCatcherListener()
		{
			
			@Override
			public void processTransportEvents(NetconfTransportEvent event) {
				System.out.println("An event occur.");
				
			}
			
			@Override
			public void processReadyForRpcRequests(RpcHandler rpcHandler) {
				
				try
				{
					copyConfig(rpcHandler);
					
					deleteConfig(rpcHandler);
					
					commit(rpcHandler);
					
					createSubscription(rpcHandler);
					
					discardChanges(rpcHandler);
					
					get(rpcHandler);
					
					getConfig(rpcHandler);
					
					killSession(rpcHandler);
					
					lock(rpcHandler);
					
					partialLock(rpcHandler);
					
					partialUnLock(rpcHandler);
					
					unlock(rpcHandler);
					
					validate(rpcHandler);
					
				    testOnCloseSession(rpcHandler);
				}
				catch(Exception e)
				{
					e.printStackTrace();
					System.out.println("Impossibile inviare il messaggio: Connessione chiusa.");
				}
			}
			
		
		});
        
        
        Runnable connection = catcher.getRunnableConnection();
		/* Start the connection */
		new Thread(connection).start();
	}
	
	private static void testOnCloseSession(RpcHandler rpcHandler) throws IOException {
		CloseSession cs = new CloseSession(rpcHandler.getSession());
		cs.executeSync(rpcHandler);
	}
	private static void copyConfig(RpcHandler rpcHandler) throws IOException, CapabilityException {
		CopyConfig cc = new CopyConfig(rpcHandler.getSession(), Datastore.candidate, Datastore.running);
		cc.executeSync(rpcHandler);
	}
	private static void deleteConfig(RpcHandler rpcHandler) throws IOException, CapabilityException {
		DeleteConfig cc = new DeleteConfig(rpcHandler.getSession(), Datastore.candidate);
		cc.executeSync(rpcHandler);
	}
	private static void commit(RpcHandler rpcHandler) throws IOException, CapabilityException {
		Commit cc = new Commit(rpcHandler.getSession());
		cc.executeSync(rpcHandler);
	}
	private static void createSubscription(RpcHandler rpcHandler) throws IOException, CapabilityException {
		CreateSubscription cc = new CreateSubscription(rpcHandler.getSession());
		cc.executeSync(rpcHandler);
	}
	private static void discardChanges(RpcHandler rpcHandler) throws IOException, CapabilityException {
		DiscardChanges cc = new DiscardChanges(rpcHandler.getSession());
		cc.executeSync(rpcHandler);
	}
	private static void get(RpcHandler rpcHandler) throws IOException, CapabilityException {
		Get cc = new Get(rpcHandler.getSession());
		cc.executeSync(rpcHandler);
	}
	private static void getConfig(RpcHandler rpcHandler) throws IOException, CapabilityException {
		GetConfig cc = new GetConfig(rpcHandler.getSession(), Datastore.running);
		cc.executeSync(rpcHandler);
	}
	private static void killSession(RpcHandler rpcHandler) throws IOException, CapabilityException {
		KillSession cc = new KillSession(rpcHandler.getSession(), 3);
		cc.executeSync(rpcHandler);
	}
	private static void lock(RpcHandler rpcHandler) throws IOException, CapabilityException {
		
		Lock cc = new Lock(rpcHandler.getSession(), Datastore.candidate);
		cc.executeSync(rpcHandler);
	}
	private static void partialLock(RpcHandler rpcHandler) throws IOException, CapabilityException {
		XPathSelections sel = new XPathSelections();
		sel.addSelection("/", Session.BASE_1_0);
		PartialLock cc = new PartialLock(rpcHandler.getSession(), sel);
		cc.executeSync(rpcHandler);
	}
	private static void partialUnLock(RpcHandler rpcHandler) throws IOException, CapabilityException {

		PartialUnlock cc = new PartialUnlock(rpcHandler.getSession(), 3);
		cc.executeSync(rpcHandler);
	}
	private static void unlock(RpcHandler rpcHandler) throws IOException, CapabilityException {
		Unlock cc = new Unlock(rpcHandler.getSession(), Datastore.candidate);
		cc.executeSync(rpcHandler);
	}
	private static void validate(RpcHandler rpcHandler) throws IOException, CapabilityException {
		Validate cc = new Validate(rpcHandler.getSession(), Datastore.candidate);
		cc.executeSync(rpcHandler);
	}
	
}
