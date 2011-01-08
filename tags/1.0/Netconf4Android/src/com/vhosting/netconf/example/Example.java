package com.vhosting.netconf.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.w3c.dom.Document;

import com.vhosting.netconf.CapabilityException;
import com.vhosting.netconf.Commit;
import com.vhosting.netconf.Config;
import com.vhosting.netconf.CreateSubscription;
import com.vhosting.netconf.Datastore;
import com.vhosting.netconf.EditConfig;
import com.vhosting.netconf.Get;
import com.vhosting.netconf.Get.GetReply;
import com.vhosting.netconf.Lock;
import com.vhosting.netconf.SubtreeFilter;
import com.vhosting.netconf.Unlock;
import com.vhosting.netconf.notification.NotificationEvent;
import com.vhosting.netconf.notification.NotificationsListener;
import com.vhosting.netconf.frame.Data;
import com.vhosting.netconf.frame.Identity;
import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Notification;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.frame.RpcHandler;
import com.vhosting.netconf.frame.RpcReply;
import com.vhosting.netconf.transport.NetconfCatcherListener;
import com.vhosting.netconf.transport.NetconfTransportEvent;
import com.vhosting.netconf.transport.NetconfTransportEvent.EventType;
import com.vhosting.netconf.transport.ssh.NetconfSshCather;
import com.vhosting.netconf.transport.ssh.SshAuthInfo;
import com.vhosting.netconf.yuma.Load;
import com.vhosting.netconf.yuma.YANGCapability;

public class Example
{

	private static BufferedReader reader = new BufferedReader(
			new InputStreamReader(System.in));

	private static int port = 380;
	private static String host = "localhost";
	private static String sshSubSystem = "netconf";

	private static String user = null;
	private static String passwd = null;

	private static String httpProxy = "localhost";
	private static int httpProxyPort = 8080;

	private static boolean hasProxy = false;

	private static final BlockingQueue<Boolean> queue = new ArrayBlockingQueue<Boolean>(
			1000, true);

	private static YANGCapability toaster = new YANGCapability(
			"http://netconfcentral.org/ns/toaster",
			"http://netconfcentral.org/ns/toaster", "toast", "toaster");

	public static final void connectConfig() throws IOException {

		System.out.println();
		System.out
				.println("Configure the connection with the Netconf Server...");

		System.out.print("Server host[" + host + "]: ");
		String l = reader.readLine();
		if (l.length() > 0)
			host = l;

		boolean invalid = true;
		while (invalid)
		{
			System.out.println();
			System.out.print("Server port number[" + port + "]: ");
			l = reader.readLine();
			try
			{
				if (l.length() > 0)
					port = Integer.parseInt(l);
				invalid = false;
			}
			catch (Exception e)
			{
				System.out.println("Invalid port number.");
				invalid = true;
			}
		}

		System.out.print("Ssh-2 Subsystem[" + sshSubSystem + "]: ");
		l = reader.readLine();
		if (l.length() > 0)
			host = l;

		invalid = true;
		while (invalid)
		{
			System.out.print("User name[" + ((user == null) ? "" : user) + "]: ");
			l = reader.readLine();
			try
			{
				if (l.length() > 0)
					user = l;
				else if (user == null)
					throw new Exception();
				invalid = false;
			}
			catch (Exception e)
			{
				System.out.println("The user name must be specified.");
				invalid = true;
			}
		}

		invalid = true;
		while (invalid)
		{
			System.out.print("Password[" + ((passwd == null) ? "" : passwd) + "]: ");
			l = reader.readLine();
			try
			{
				if (l.length() > 0)
					passwd = l;
				else if (passwd == null)
					throw new Exception();
				invalid = false;
			}
			catch (Exception e)
			{
				System.out.println("The password must be specified.");
				invalid = true;
			}
		}

		System.out
				.print("Your Ssh connection require an HTTP Proxy server? [No] <Yes/No>: ");
		l = reader.readLine();
		if (l.length() > 0 && l.toLowerCase().matches("^y.*?$"))
		{
			hasProxy = true;
			System.out.print("HTTP Proxy Server host[" + httpProxy + "]: ");
			l = reader.readLine();
			if (l.length() > 0)
				httpProxy = l;

			invalid = true;
			while (invalid)
			{
				System.out.print("HTTP Proxy Server port number["
						+ httpProxyPort + "]: ");
				l = reader.readLine();
				try
				{
					if (l.length() > 0)
						httpProxyPort = Integer.parseInt(l);
					invalid = false;
				}
				catch (Exception e)
				{
					System.out.println("Invalid port number.");
					invalid = true;
				}
			}

		}
		else
			hasProxy = false;
		System.out.println("Done.");
	}

	public static final void main(String s[]) throws IOException {
		while (true)
		{
			System.out.println("Netconf Toaster Example");
			
			URL u = Example.class.getResource("_LICENSE.txt");
			InputStream is = u.openStream();
			
			int i = 0;
			while ((i = is.read()) != -1)
			{
				System.out.print((char)i);
			}
			
			System.out.println();
			System.out.println("Please, specific an action...");
			System.out.println("1. Configure the connection parameters;");
			System.out.println("2. Connect to the Netconf Server.");
			System.out.println("3. Quit.");

			boolean invalid = true;
			int choice = 0;
			while (invalid)
			{
				System.out.print("Action number: ");
				String l = reader.readLine();
				try
				{
					if (l.length() > 0)
						choice = Integer.parseInt(l);
					invalid = false;

					if (choice > 3 || choice < 1)
					{
						System.out.println("Invalid action number.");
						invalid = true;
					}
				}
				catch (Exception e)
				{
					System.out.println("Invalid action number.");
					invalid = true;
				}

			}
			switch (choice)
			{
			case 1:
				connectConfig();
				break;
			case 2:
				connect();
				break;
			case 3:
				System.out.println("Bye!");
				System.exit(0);
				break;
			default:
				break;
			}
		}
	}

	private static void connect() {

		/* Prepare the connection */
		InetSocketAddress addr = new InetSocketAddress(host, port);
		SshAuthInfo authInfo = new SshAuthInfo(addr, user, passwd);

		if (hasProxy)
		{
			InetSocketAddress h = new InetSocketAddress(httpProxy,
					httpProxyPort);
			authInfo.setProxyHost(h);
		}
		else
		{
			authInfo.removeProxyHost();
		}

		final NetconfSshCather catcher = new NetconfSshCather("toaster",
				authInfo, sshSubSystem);

		/* The log is full enabled. */
		NetconfSshCather.enableLog(NetconfSshCather.LogLevel.MESSAGES);

		/* The body of the application */
		catcher.setNetconfCatcherListener(new NetconfCatcherListener()
		{

			@Override
			public void processTransportEvents(NetconfTransportEvent event) {
				Exception e = event.getTransortErrorException();
				if (e != null)
				{
					System.out.println(e.getMessage());
				}

				if (event.getEventType() == EventType.CONNECTION_CLOSED_BY_SERVER)
				{
					System.out.println("The connection is closed by server.");
				}
				else if (event.getEventType() == EventType.CONNECTION_CLOSED_BY_USER)
				{
					System.out.println("The connection is closed by user.");
				}
				else if (event.getEventType() == EventType.CONNECTION_CANNOT_BE_OPENED)
				{
					System.out.println("The connection cannot be opened.");
				}
			}

			@Override
			public void processReadyForRpcRequests(RpcHandler rpcHandler) {

				rpcHandler.setNotificationsListener(nl);
				try
				{
					System.out.println();
					System.out
							.println("The server connection has been established...");

					System.out
							.println("Check the existence of the toaster on the server...");
					boolean check = checkToaster(catcher, rpcHandler);
					if (!check)
					{
						System.out.println("Create the toaster...");
						boolean hasToaster = createToaster(catcher, rpcHandler);

						while (!hasToaster)
						{
							System.out.print("Retry? [No] <Yes/No>: ");
							String l = reader.readLine();

							if (l.length() > 0
									&& l.toLowerCase().matches("^y.*?$"))
							{
								hasToaster = createToaster(catcher, rpcHandler);
							}
							else
							{
								catcher.disconnect();
								return;
							}
						}

						checkToaster(catcher, rpcHandler);

					}

					System.out.println("The toaster is ready! :)");
					
					System.out.println();
					
					System.out.println("Start the notifications system...");

					CreateSubscription create = new CreateSubscription(
							rpcHandler.getSession());

					RpcReply rep = create.executeSync(rpcHandler);

					if (rep.containsErrors())
					{
						System.out
								.println("Unable to start the notifications system.");
						catcher.disconnect();
						return;
					}

					System.out.println("Notifications system is started.");
					System.out
							.println("The toaster is now ready to process RPC operations.");

					while (true)
					{
						System.out.println();
						System.out.println("Please, specific an action...");
						System.out.println("1. Make a toast;");
						System.out.println("2. Disconnect.");
						boolean invalid = true;
						int choice = 0;
						while (invalid)
						{
							System.out.print("Action number: ");
							String l = reader.readLine();
							try
							{
								if (l.length() > 0)
									choice = Integer.parseInt(l);
								invalid = false;

								if (choice > 2 || choice < 1)
								{
									System.out
											.println("Invalid action number.");
									invalid = true;
								}
							}
							catch (Exception e)
							{
								System.out.println("Invalid action number.");
								invalid = true;
							}

						}
						switch (choice)
						{
						case 1:
							makeToast(rpcHandler);
							break;
						case 2:
							catcher.disconnect();
							return;
						default:
							break;
						}

					}

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		});

		Runnable connection = catcher.getRunnableConnection();
		/* Start the connection */
		Thread t = new Thread(connection);
		t.start();
		try
		{
			t.join();
		}
		catch (InterruptedException e)
		{}
	}

	private static int donenes = 5;
	private static int type = 1;

	protected static void makeToast(RpcHandler rpcHandler) throws IOException {

		Rpc rpc = new Rpc(toaster, "make-toast");
		Leaf toasterDoneness = rpc.getInput().linkLeaf(
				rpc.createLeaf("toasterDoneness"));
		Leaf toasterToastType = rpc.getInput().linkLeaf(
				rpc.createLeaf("toasterToastType"));

		// List of materials with which you can make a toast.
		Identity whiteBread = new Identity(toaster, "white-bread");
		Identity wheatBread = new Identity(toaster, "wheat-bread");
		Identity wonderBread = new Identity(toaster, "wonder-bread");
		Identity frozenWaffle = new Identity(toaster, "frozen-waffle");
		Identity frozenBagel = new Identity(toaster, "frozen-bagel");
		Identity hashBrown = new Identity(toaster, "hash-brown");

		Identity[] types = new Identity[] { whiteBread, wheatBread,
				wonderBread, frozenWaffle, frozenBagel, hashBrown };

		System.out.println();
		System.out.println("How do you want to be your toast?");

		String l;
		boolean invalid = true;
		while (invalid)
		{
			System.out.print("Specifies the level of cooking (1...10) ["
					+ donenes + "]: ");
			l = reader.readLine();
			try
			{
				if (l.length() > 0)
					donenes = Integer.parseInt(l);
				if (donenes > 10 || donenes < 1)
					throw new Exception();

				invalid = false;
			}
			catch (Exception e)
			{
				System.out.println("Invalid level of donenes.");
				invalid = true;
			}
		}

		invalid = true;
		while (invalid)
		{
			System.out
					.println("Specifies the type of material used for the toast... ");
			int i = 1;
			for (Identity identity : types)
			{
				System.out.println(i + ". " + identity.getName());
				i++;
			}
			System.out.print("Make your choice[" + types[type - 1].getName()
					+ "]: ");
			l = reader.readLine();
			try
			{
				if (l.length() > 0)
					type = Integer.parseInt(l);
				if (type > types.length || type < 1)
				{
					type = 1;
					throw new Exception();
				}
				invalid = false;
			}
			catch (Exception e)
			{
				System.out.println("Invalid choice.");
				invalid = true;
			}
		}
		rpc.getInput().assignLeaf(toasterDoneness, donenes + "");
		rpc.getInput().assignLeaf(toasterToastType,
				types[type - 1].getUniqueNane());
		rpcHandler.sendSyncRpc(rpc);
		System.out.println();
		System.out.println("You have requested a toast with "
				+ types[type - 1].getName() + " and a doneness of level "
				+ donenes + ".");
		System.out.println();
		System.out.println("Please wait the notification... :)");

		try
		{
			queue.take();
		}
		catch (InterruptedException e)
		{

		}

	}

	private static boolean createToaster(NetconfSshCather catcher,
			RpcHandler rpcHandler) throws CapabilityException, IOException {
		Load l = new Load(rpcHandler.getSession(), toaster);
		RpcReply rep = l.executeSync(rpcHandler);

		if (rep.containsErrors())
		{
			System.out.println("Can not load the toaster module.");
			return false;
		}
		Lock lk = new Lock(rpcHandler.getSession(), Datastore.candidate);
		rep = lk.executeSync(rpcHandler);
		if (rep.containsErrors())
		{
			System.out.println("Can not lock the Candidate database.");
			return false;
		}

		Config config = EditConfig.createConfig(toaster, "toaster{create}");
		EditConfig ec = new EditConfig(rpcHandler.getSession(),
				Datastore.candidate, config);

		rep = ec.executeSync(rpcHandler);
		if (rep.containsErrors())
		{
			System.out.println("Can not create the toaster.");
			return false;
		}

		Commit c = new Commit(rpcHandler.getSession());
		rep = c.executeSync(rpcHandler);

		if (rep.containsErrors())
		{
			System.out.println("Can not create the toaster.");
			return false;
		}

		Unlock ul = new Unlock(rpcHandler.getSession(), Datastore.candidate);
		ul.executeSync(rpcHandler);

		return true;
	}

	protected static boolean checkToaster(NetconfSshCather catcher,
			RpcHandler rpcHandler) throws IOException {

		Get g = new Get(rpcHandler.getSession());
		SubtreeFilter sf = g.createSubtreeFilter();
		sf.addFilter(toaster).addFilterString("toaster");
		g.setSubtreeFilter(sf);
		RpcReply rep = g.executeSync(rpcHandler);
		
		if (rep.containsErrors())
		{
			System.out.println("Unable to identify the toaster.");
			return false;
		}
		GetReply gr = g.new GetReply(rep);
		Document doc = gr.getData();

		Data data = new Data(toaster);
		Leaf toasterManufacturer = data.getData().linkLeaf(
				data.createLeaf("toasterManufacturer"));
		Leaf toasterModelNumber = data.getData().linkLeaf(
				data.createLeaf("toasterModelNumber"));
		Leaf toasterStatus = data.getData().linkLeaf(
				data.createLeaf("toasterStatus"));

		data.read(doc);

		String manufacturer = data.getData().getLeafCanonicalValue(
				toasterManufacturer);
		String modelNumber = data.getData().getLeafCanonicalValue(
				toasterModelNumber);
		String status = data.getData().getLeafCanonicalValue(toasterStatus);

		// The toaster module not exists on the server.
		if (manufacturer == null) return false;
		
		System.out.println("Available toaster:");
		System.out.println();
		System.out.println("Manufacturer -> " + manufacturer);
		System.out.println("Model Number -> " + modelNumber);
		System.out.println("Status       -> " + status);

		if (status.equals("down"))
		{
			System.out.println("Ops... note that the toaster is switched off!");
		}

		return true;
	}

	private static NotificationsListener nl = new NotificationsListener()
	{
		@Override
		public void processNotification(NotificationEvent notification) {

			Notification n = new Notification(toaster, "toastDone");
			Leaf toastStatus = n.getNotification().linkLeaf(
					n.createLeaf("toastStatus"));
			notification.load(n);

			String status = n.getNotification().getLeafCanonicalValue(
					toastStatus);

			if (status != null)
			{
				// Done.
				if (status.equals("done"))
					System.out.println("The toast is done!");
				// Cancelled.
				else if (status.equals("cancelled"))
					System.out.println("The toast is cancelled now.");
				// Error.
				else if (status.equals("error"))
					System.out
							.println("Ops.. there was an error, the unit can be broken!");

				queue.add(true);
			}
		}
	};
}
