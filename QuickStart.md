## Introduction ##

This guide shows how to create a simple application using the API.

Our aim is to implement a simple Java application that controls a toaster through a Netconf server that has this capability:

> http://netconfcentral.org/ns/toaster

All the features that make the capability to control a toaster is described through the following YANG module:

> [toaster.yang](http://www.netconfcentral.org/modulereport/toaster)

Our example, we plan to use a YUMA Server that has the ability to make this capability available in a dynamic way through the LOAD Operation.

To learn how to install the server, please consult the appropriate documentation of the project and in particular, for this example, the following guides:

> [Yuma Installation Guide](http://www.netconfcentral.org/static/manuals/yuma-installation-guide.pdf)

> [Yuma Quickstart Guide](http://www.netconfcentral.org/static/manuals/yuma-quickstart-guide.pdf)

The home directory of the YUMA Tools project is:

> http://www.netconfcentral.org

Once you have familiarized yourself with the server you can easily access the next step.

Please note that for our example, for simplicity, the user with which you connect must have **superuser privileges** on the YUMA Server.

Only a super user can dynamically load a module and initialize it.

A better Android (and no Java) version of our example is [Android Netconf Toaster](http://code.google.com/p/netconf4android/downloads/detail?name=AndroidNetconfToaster.zip).

This is an Eclipse project that you can safely explore and from which to take ideas for your Android applications.

## Step 1: Setup the Development Environment on Eclipse ##

First you must download the **netconf4Android-Binaries.zip** archive of the last version of **netconf4Android API** at this URL:

> http://code.google.com/p/netconf4android/downloads/list

You'll find in the archive the libraries to use.
Unzip the archive to a location of your choice.

Please read very carefully the ****README.txt** and LICENSE.txt** files.

You will find them into the **netconf4Android-Binaries** directory.

The libraries you need to include in your project are:

  * netconf4android.jar - The netconf4Android API
  * lib/ganymed-ssh2-build210/ganymed-ssh2-build210.jar - The Ganymed-SSH2 API to use the SSH-2 transport protocol.


**Please, open Eclipse now and create a new project called 'Netconf Toaster':**

  * File -> New -> Java Project

  * Insert this values into the form:

> Project name: Netconf Toaster

  * Press the OK button.

**After creating the project, create the public class 'Toaster' as the entry point for the application:**

  * File -> New -> Class

  * Insert this values into the form:

> Package: com.vhosting.netconf.example (or a package of your choice)

> Nane: Toaster

> Add the public static void main method.

  * Press the OK button.

**At this point, you will need to add the libraries to the project to use them:**

  * Project -> Properties -> Java Build Path -> Add External JARs...

  * Select the location of the netconf4android.jar library and repeat the action for the ganymed-ssh2-build210.jar library.

  * Press the OK button.

The development environment is ready now.
| **Step 1 - Video 1** | **Step 1 - Video 2** |
|:---------------------|:---------------------|
| <a href='http://www.youtube.com/watch?feature=player_embedded&v=8uTzylpXzmk' target='_blank'><img src='http://img.youtube.com/vi/8uTzylpXzmk/0.jpg' width='425' height=344 /></a> | <a href='http://www.youtube.com/watch?feature=player_embedded&v=igCGVY5lNPM' target='_blank'><img src='http://img.youtube.com/vi/igCGVY5lNPM/0.jpg' width='425' height=344 /></a> |

## Step 2: Toaster Capability declaration ##

The first step for our example is the definition of Toaster Capability to be used internally to the code.

Data that are retrieved from the Toaster YANG module: [toaster.yang](http://www.netconfcentral.org/modulereport/toaster)

```

package com.vhosting.netconf.example;

import com.vhosting.netconf.yuma.YANGCapability;

public class Toaster {

	
	
	/*
	 * These data were obtained by consulting the YANG module: 
	 * 
	 *     toaster.yang
	 *
	 * A copy of the YANG module can be found at URL:
	 *
	 *     http://www.netconfcentral.org/modulereport/toaster
	 *
	 */
	private YANGCapability toasterCap = 
		new YANGCapability("http://netconfcentral.org/ns/toaster", 
				"http://netconfcentral.org/ns/toaster", 
				"toast", "toaster");
	
	
	// Connection parameters
	// ...
	
	
	/**
	 * Toaster Entry Point.
	 */
	public static void main(String[] args) {

	}

}

```

| **Step 2 - Video 1** |
|:---------------------|
| <a href='http://www.youtube.com/watch?feature=player_embedded&v=atnvbsQi8B0' target='_blank'><img src='http://img.youtube.com/vi/atnvbsQi8B0/0.jpg' width='425' height=344 /></a> |

## Step 3: Connect to the Server ##
After declaring the Toaster Capability, as essential, we set the SSH-2 connection to the NETCONF server.
```

package com.vhosting.netconf.example;

import java.net.InetSocketAddress;

import com.vhosting.netconf.frame.RpcHandler;
import com.vhosting.netconf.transport.NetconfCatcherListener;
import com.vhosting.netconf.transport.NetconfTransportEvent;
import com.vhosting.netconf.transport.ssh.NetconfSshCather;
import com.vhosting.netconf.transport.ssh.SshAuthInfo;
import com.vhosting.netconf.yuma.YANGCapability;

public class Toaster {

	
	
	/*
	 * These data were obtained by consulting the YANG module: 
	 * 
	 *     toaster.yang
	 *
	 * A copy of the YANG module can be found at URL:
	 *
	 *     http://www.netconfcentral.org/modulereport/toaster
	 *
	 */
	private YANGCapability toasterCap = 
		new YANGCapability("http://netconfcentral.org/ns/toaster", 
				"http://netconfcentral.org/ns/toaster", 
				"toast", "toaster");
	
	
	// SSH-2 Connection parameters
	
	// The Netconf server host. Note: The dafault port is 830.
	private static InetSocketAddress host = 
		new InetSocketAddress("vhosting.eu.org", 22);
	
	// The user name.
	private static String uname = "netconf";
	
	// The password.
	private static String passwd = "passwd";
	
	// The SSH-2 Netconf Sybsystem.
	private static String subsystem = "netconf";
	
	
	/**
	 * Toaster Entry Point.
	 */
	public static void main(String[] args) {

		// Authentication Info.
		SshAuthInfo ai = new SshAuthInfo(host, uname, passwd);
		
		// The Netconf Catcher instance.
		NetconfSshCather catcher = 
			new NetconfSshCather("My Toaster Example", ai, subsystem);
		
		// The connection.
		Runnable runnableConnection = catcher.getRunnableConnection();
		
		// Set the Netconf Catcher Listener.
		// Note: This is the entry point for Netconf operations.
		catcher.setNetconfCatcherListener(new NetconfCatcherListener() {
			
			
			// Transport events here.
			@Override
			public void processTransportEvents(NetconfTransportEvent arg0) {
				// ...
				
			}
			
			// RPC requests here.
			@Override
			public void processReadyForRpcRequests(RpcHandler arg0) {
				// ...
				
			}
		});
		
		
		// Start the connection here.
		Thread t = new Thread(runnableConnection);
		t.start();
		
	}

}

```

| **Step 3 - Video 1** |
|:---------------------|
| <a href='http://www.youtube.com/watch?feature=player_embedded&v=GSgx2RCAZbg' target='_blank'><img src='http://img.youtube.com/vi/GSgx2RCAZbg/0.jpg' width='425' height=344 /></a> |


## Step 4: Enable the Log Stream ##
Now we add some instructions that allow us to record the activity of the connection.
This feature is likely to be essential for debugging.
In our example we want to monitor all activity that affects both the protocol that the messages sent or received.

```

package com.vhosting.netconf.example;

...
import com.vhosting.netconf.transport.NetconfCatcher;
import com.vhosting.netconf.transport.NetconfCatcher.LogLevel;
...

public class Toaster {
	
        ...
	
	/**
	 * Toaster Entry Point.
	 */
	public static void main(String[] args) {


                // The log is enabled.
		
		// This level ensures that all server activity, 
		// including the messages, are written to the log stream.
		NetconfCatcher.enableLog(LogLevel.MESSAGES);
		
		
		// The log stream is redirected to STDERR.
		NetconfCatcher.setLogStream(System.err);

                ...

	}
}

```

| **Step 4 - Video 1** |
|:---------------------|
| <a href='http://www.youtube.com/watch?feature=player_embedded&v=5nHzhnGru0A' target='_blank'><img src='http://img.youtube.com/vi/5nHzhnGru0A/0.jpg' width='425' height=344 /></a> |


## Step 5: Transport Events ##

Now we want to handle events generated by the transport protocol, such as the connection closing or connection errors.

```

package com.vhosting.netconf.example;

...
import com.vhosting.netconf.frame.RpcHandler;
import com.vhosting.netconf.transport.NetconfCatcher.LogLevel;
import com.vhosting.netconf.transport.NetconfCatcherListener;
import com.vhosting.netconf.transport.NetconfTransportEvent;
import com.vhosting.netconf.transport.NetconfTransportEvent.EventType;
...

public class Toaster {
	
	...
	
	/**
	 * Toaster Entry Point.
	 */
	public static void main(String[] args) {

		...
		
		// Set the Netconf Catcher Listener.
		// Note: This is the entry point for Netconf operations.
		catcher.setNetconfCatcherListener(new NetconfCatcherListener() {
			
			
			// Transport events here.
			@Override
			public void processTransportEvents(NetconfTransportEvent event) {
				
				// Get the transport event type.
				EventType eventType = event.getEventType();
				
				// The connection cannot be opened for any reason.
				if (eventType == EventType.CONNECTION_CANNOT_BE_OPENED)
				{
					System.out.println("The connection cannot be opened for any reason.");
				}
				
				// The connection is closed by server.
				else if (eventType == EventType.CONNECTION_CLOSED_BY_SERVER)
				{
					System.out.println("The connection is closed by server.");
				}
				
				// The connection is closed by user ( or by this client ).
				else if (eventType == EventType.CONNECTION_CLOSED_BY_USER)
				{
					System.out.println("The connection is closed by user.");
				}
				
				// If the event was generated due to an abnormal 
				// situation, even the exception is returned with the event.
				Exception e = event.getTransortErrorException();
				
				// Check the existence of the exception.
				if (e != null)
				{
					
					// Notification of the abnormal situation occurring.
					
					System.out.println("There was an abnormal situation.");
					System.out.println("This is the exception {");
					
					e.printStackTrace();
					
					System.out.println("}");
				}
				
			}
			
			// RPC requests here.
			@Override
			public void processReadyForRpcRequests(RpcHandler arg0) {
				// ...
				
			}
		});
		
		
		...
		
	}

}

```

| **Step 5 - Video 1** |
|:---------------------|
| <a href='http://www.youtube.com/watch?feature=player_embedded&v=RyOJXW26vkU' target='_blank'><img src='http://img.youtube.com/vi/RyOJXW26vkU/0.jpg' width='425' height=344 /></a> |


## Step 6: Program Flow ##
Now that all the elements necessary to manage the connection has been handled, we want to define the flow of our program.
That is actions that allow us to initialize the toaster if does not exist on the server, make toast and exit the program.

```

package com.vhosting.netconf.example;

...
import com.vhosting.netconf.frame.RpcHandler;
import com.vhosting.netconf.transport.NetconfCatcherListener;
import com.vhosting.netconf.transport.ssh.NetconfSshCather;
import com.vhosting.netconf.transport.NetconfTransportEvent;
...

public class Toaster {

	...
        // The Netconf SSH-2 as attribute.
	private static NetconfSshCather catcher;
        ...
	
	/**
	 * Toaster Entry Point.
	 */
	public static void main(String[] args) {

		...
		
		// Set the Netconf Catcher Listener.
		// Note: This is the entry point for Netconf operations.
		catcher.setNetconfCatcherListener(new NetconfCatcherListener() {
			
			
			// Transport events here.
			@Override
			public void processTransportEvents(NetconfTransportEvent event) {
				
				...
				
			}
			
			// RPC requests here.
			@Override
			public void processReadyForRpcRequests(RpcHandler handler) {
				
				try
				{
					// Upload your toaster if it does not 
					// exist on the server and quit if you can not load the toaster.
					if (loadToaster(handler))
					{
						// If the toaster is available, it makes toast 
						// until the user decides to quit.
						if (!makeToasts(handler))
						{
							disconnectAndExit();
						}
					}
					else disconnectAndExit();
				}
				catch(Exception e)
				{
					// Intercept all exceptions That May Occur 
					// while messages are sent and received.
					
					System.out.println("There is a problem in sending or receiving messages.");
					System.out.println("This is the exception {");
					
					e.printStackTrace();
					
					System.out.println("}");
				}
			}
		});
		
		
		...
		
	}

	/*
	 * It makes toast until the user decides to quit.
	 */
	protected static boolean makeToasts(RpcHandler handler) {
		// ...
		return false;
	}

        /*
         * Upload your toaster if it does not exist on the server 
         * and quit if you can not load the toaster.
         */
	protected static boolean loadToaster(RpcHandler handler) {
		// ...
		return false;
	}

	/*
	 * Disconnect and exit.
	 */
	protected static void disconnectAndExit() {
		
		// Disconnect.
		catcher.disconnect();
		
		// Exit.
		System.exit(0);
		
	}

}

```


| **Step 6 - Video 1** |
|:---------------------|
| <a href='http://www.youtube.com/watch?feature=player_embedded&v=yELzp1iFOEE' target='_blank'><img src='http://img.youtube.com/vi/yELzp1iFOEE/0.jpg' width='425' height=344 /></a> |


## Step 7: Load the Toaster YANG Module and Initialize It ##

Now is the time to check if there is the Toaster YANG module on the server.
If not, we ask to be loaded and make it available.
Then proceed with its initialization.

If the Toaster YANG module is already present and the datastore of the capability has already been initialized, no action is taken.

```

package com.vhosting.netconf.example;

...
import java.io.IOException;
import org.w3c.dom.Document;
import com.vhosting.netconf.Commit;
import com.vhosting.netconf.Config;
import com.vhosting.netconf.Datastore;
import com.vhosting.netconf.EditConfig;
import com.vhosting.netconf.Get;
import com.vhosting.netconf.Lock;
import com.vhosting.netconf.SubtreeFilter;
import com.vhosting.netconf.Unlock;
import com.vhosting.netconf.Get.GetReply;
import com.vhosting.netconf.frame.Data;
import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.RpcHandler;
import com.vhosting.netconf.frame.RpcReply;
import com.vhosting.netconf.yuma.Load;
...

public class Toaster {
	
    ...


    /*
     * Upload your toaster if it does not exist on the server 
     * and quit if you can not load the toaster.
     */
	protected static boolean loadToaster(RpcHandler handler) throws IOException, CapabilityException {
		
		// Constructs a GET operation to see if there 
		// is already an instance of the toaster on the server.
		Get g = new Get(handler.getSession());
		SubtreeFilter sf = g.createSubtreeFilter();
		sf.addFilter(toasterCap).addFilterString("toaster");
		g.setSubtreeFilter(sf);
		
		// Exec the operation.
		/*
		 * The message will be sent is:
                 *
                 * <?xml version="1.0" encoding="UTF-8"?>
                 *   <nc:rpc xmlns:nc="urn:ietf:params:xml:ns:netconf:base:1.0" 
                 *                     nc:message-id="1">
                 *    <nc:get>
                 *      <nc:filter nc:type="subtree">
                 *         <toast:toaster 
                 *              xmlns:toast="http://netconfcentral.org/ns/toaster"/>
                 *      </nc:filter>
                 *    </nc:get>
                 *   </nc:rpc>
		 */
		RpcReply reply = g.executeSync(handler);
		
		// Check if the server has encountered errors when requesting information.
		if (reply.containsErrors())
		{
			System.out.println("Unable to check the existence of the toaster.");
			return false;
		}
		
		
		// Get the information required by the GET operation.
		GetReply gr = g.new GetReply(reply);
		
		/*
		 * The XML document should contain something like this if there:
   
                 *   <?xml version="1.0" encoding="UTF-8"?>
                 *   <nc:data xmlns:nc="urn:ietf:params:xml:ns:netconf:base:1.0">
                 *     <toast:toaster xmlns:toast="http://netconfcentral.org/ns/toaster">
                 *       <toast:toasterManufacturer>Acme, Inc.</toast:toasterManufacturer>
                 *       <toast:toasterModelNumber>Super Toastamatic 2000</toast:toasterModelNumber>
                 *       <toast:toasterStatus>up</toast:toasterStatus>
                 *     </toast:toaster>
                 *   </nc:data>
                 *   
                 *   otherwise, it contains a blank document:
                 *
                 *    <?xml version="1.0" encoding="UTF-8"?>
                 *    <nc:data xmlns:nc="urn:ietf:params:xml:ns:netconf:base:1.0">
                 *    </nc:data>
                 *   
                 *   
		 */
		Document doc = gr.getData();
		
		
		
		// Create a DATA structure to read the contents of the XML document.
		Data data = new Data(toasterCap);
		Leaf toasterManufacturer = 
			data.getData().linkLeaf(data.createLeaf("toasterManufacturer"));
		
		Leaf toasterModelNumber = 
			data.getData().linkLeaf(data.createLeaf("toasterModelNumber"));
		
		Leaf toasterStatus = 
			data.getData().linkLeaf(data.createLeaf("toasterStatus"));
		
		// Read the document into the DATA structure.
		data.read(doc);
		
		// Get the toaster manufaturer from the DATA structure.
		String manufacturer = 
			data.getData().getLeafCanonicalValue(toasterManufacturer);
		
		// True if the XML document contains the information of 
		// the toaster (the toaster exists), false otherwise.
		boolean toasterExists = (manufacturer != null);
		
		if (!toasterExists)
		{
			// Load the toaster YANG module into the server.
			Load l = new Load(handler.getSession(), toasterCap);
			reply = l.executeSync(handler);
			
			// Check if there were any errors.
			if (reply.containsErrors())
			{
				System.out.println("Can not load the toaster YANG module.");
				return false;
			}
			
			// Lock the CANDIDATE datastore.
			Lock lk = new Lock(handler.getSession(), Datastore.candidate);
			reply = lk.executeSync(handler);
			
			
			// Check if there were any errors.
			if (reply.containsErrors())
			{
				System.out.println("Unable to lock the Candidate datastore.");
				return false;
			}
			
			
			
			// Building the toaster through an EDIT CONFIG operation.
			// To create the toaster, with all its values, simply ask 
			// for the establishment of the root node of the data structure 
			// of the toaster in the configuration candidate datastore.
			Config config = EditConfig.createConfig(toasterCap, "toaster{create}");
			EditConfig ec = new EditConfig(handler.getSession(), 
					Datastore.candidate, config);
			
			/*
			 * The message being sent is like this:

                         *   <?xml version="1.0" encoding="UTF-8"?>
                         *   <nc:rpc xmlns:nc="urn:ietf:params:xml:ns:netconf:base:1.0" 
                                  nc:message-id="4">
                         *      <nc:edit-config>
                         *        <nc:target>
                         *         <nc:candidate/>
                         *        </nc:target>
                         *        <nc:config>
                         *          <toast:toaster 
                         *               nc:operation="create" 
                         *               xmlns:toast="http://netconfcentral.org/ns/toaster"/>
                         *        </nc:config>
                         *      </nc:edit-config>
                         *   </nc:rpc>
			 */
			reply = ec.executeSync(handler);
			
			// Check if there were any errors.
			if (reply.containsErrors())
			{
				System.out.println("Unable to create the toaster.");
				return false;
			}
			
			
			// Performs a commit of the candidate datastore, 
			// making the changes you made available to the system.
			Commit c = new Commit(handler.getSession());
			reply = c.executeSync(handler);
			
			// Check if there were any errors.
			if (reply.containsErrors())
			{
				System.out.println("Unable to commit the candidate datastore.");
				return false;
			}
			
			// Unlock the Candidate datastore.
			Unlock ul = new Unlock(handler.getSession(), Datastore.candidate);
			ul.execute(handler);
			
		}
		
		// Rerun the GET operation to retrieve all the 
		// information of the toaster.
		reply = g.executeSync(handler);
		
		// Check if There Were Any errors.
		if (reply.containsErrors())
		{
			System.out.println("The information can not be found on the toaster.");
			return false;
		}
		
		// Get the information about the toaster.
		gr = g.new GetReply(reply);
		doc = gr.getData();
		data.read(doc);
		
		manufacturer = data.getData().getLeafCanonicalValue(toasterManufacturer);
		String modelNumber = data.getData().getLeafCanonicalValue(toasterModelNumber);
		String status = data.getData().getLeafCanonicalValue(toasterStatus);
		
		
		// View the features of the toaster.
		System.out.println("Available toaster:");
		System.out.println();
		System.out.println("Manufacturer -> " + manufacturer);
		System.out.println("Model Number -> " + modelNumber);
		System.out.println("Status       -> " + status);
		
		// If the state of the toaster is 'down' the toaster can not be used.
		if (status.equals("down"))
		{
			System.out.println("The toaster is switched off!");
			System.out.println("It may not be used.");
			return false;
		}
		
		
		// The toaster exists at this point.
                return true;
	}

	
	...

}



```


| **Step 7 - Video 1** | **Step 7 - Video 2** |
|:---------------------|:---------------------|
| <a href='http://www.youtube.com/watch?feature=player_embedded&v=9EGwSlzO5U0' target='_blank'><img src='http://img.youtube.com/vi/9EGwSlzO5U0/0.jpg' width='425' height=344 /></a> | <a href='http://www.youtube.com/watch?feature=player_embedded&v=W7us9IcMrMQ' target='_blank'><img src='http://img.youtube.com/vi/W7us9IcMrMQ/0.jpg' width='425' height=344 /></a> |
| **Step 7 - Video 3** |
| <a href='http://www.youtube.com/watch?feature=player_embedded&v=Cs0cROQITyQ' target='_blank'><img src='http://img.youtube.com/vi/Cs0cROQITyQ/0.jpg' width='425' height=344 /></a> |


## Step 8: Enable Notifications ##

In this passage we see how to handle the notifications and how to ask for a subscription to receive notifications.

```



package com.vhosting.netconf.example;

...
import java.io.IOException;
import com.vhosting.netconf.CapabilityException;
import com.vhosting.netconf.CreateSubscription;
import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Notification;
import com.vhosting.netconf.frame.RpcHandler;
import com.vhosting.netconf.frame.RpcReply;
import com.vhosting.netconf.notification.NotificationEvent;
import com.vhosting.netconf.notification.NotificationsListener;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
...


public class Toaster {

	...


	// A blocking queue.
	// This is useful for synchronize the 'toastDone' 
	// notification when you make toasts.
	
	// NOTE: The toaster is designed for accept only a request at a time.
	
	private static final BlockingQueue<Boolean> 
			queue = new ArrayBlockingQueue<Boolean>(1000);
	
	
	static /*
	 * The Notifications Listener.
	 */
	NotificationsListener nl = new NotificationsListener()
	{

		// Process a notification.
		@Override
		public void processNotification(NotificationEvent event) {
			
			// Create a Notification structure named 'toastDone' 
			// for the toaster YANG module.
			// Through this structure, you can load the data 
			// from the notification event, if available.
			Notification n = new Notification(toasterCap, "toastDone");
			Leaf toastStatus = 
				n.getNotification().linkLeaf(n.createLeaf("toastStatus"));
			event.load(n);
			
			
			
			// Get the status of the 'toastDone' notification.
			String status = n.getNotification().getLeafCanonicalValue(toastStatus);
			
			
			// If status exists, it is certain that 
			// the 'toastDone' notification is present. 
			if (status != null)
			{
				// Done.
				if (status.equals("done"))
				{
					System.out.println("The toast is done!");
				}
				// Cancelled.
				else if (status.equals("cancelled"))
				{
					System.out.println("The toast is cancelled!");
				}
				// Error.
				else if (status.equals("error"))
				{
					System.out.println("There was an error, the unit can be broken!");
				}
				
				
				// Announced that a notification is already present.
				queue.add(true);
				
			}
			
		}
		
	};
	
	
	/*
	 * It makes toast until the user decides to quit.
	 */
	protected static boolean makeToasts(RpcHandler handler) throws CapabilityException, IOException {
		
		// Set the Notification Listener.
		handler.setNotificationsListener(nl);
		
		
		System.out.println("Start the notifications system...");
		
		// Enable notifications system through a CREATE-SUBSCRIPTION operation.
		// In this way, the server can let us know when our toast is ready.
		CreateSubscription subscription = 
			new CreateSubscription(handler.getSession());
		
		RpcReply reply = subscription.executeSync(handler);
		
		// Check if there were any errors.
		if (reply.containsErrors())
		{
			System.out.println("Unable to start the notifications system.");
			return false;
		}
		
		System.out.println("The Notifications system is started.");

		// ...
		
		return false;
	}


}

```


| **Step 8 - Video 1** | **Step 8 - Video 2** |
|:---------------------|:---------------------|
| <a href='http://www.youtube.com/watch?feature=player_embedded&v=1IXcrtceTXc' target='_blank'><img src='http://img.youtube.com/vi/1IXcrtceTXc/0.jpg' width='425' height=344 /></a> | <a href='http://www.youtube.com/watch?feature=player_embedded&v=8FYfXPNI79Y' target='_blank'><img src='http://img.youtube.com/vi/8FYfXPNI79Y/0.jpg' width='425' height=344 /></a> |


## Step 9: Make Toasts ##

Our example ends by implementing everything you need to ask the server to make toast.

```

package com.vhosting.netconf.example;

...
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.vhosting.netconf.CapabilityException;
import com.vhosting.netconf.CreateSubscription;
import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Notification;
import com.vhosting.netconf.frame.RpcHandler;
import com.vhosting.netconf.frame.RpcReply;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.notification.NotificationEvent;
import com.vhosting.netconf.notification.NotificationsListener;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import com.vhosting.netconf.frame.Identity;
...


public class Toaster {

	...


	// A blocking queue.
	// This is useful for synchronize the 'toastDone' 
	// notification when you make toasts.
	
	// NOTE: The toaster is designed for accept only a request at a time.
	
	private static final BlockingQueue<Boolean> 
			queue = new ArrayBlockingQueue<Boolean>(1000);
	
	
	static /*
	 * The Notifications Listener.
	 */
	NotificationsListener nl = new NotificationsListener()
	{

		// Process a notification.
		@Override
		public void processNotification(NotificationEvent event) {
			
			// Create a Notification structure named 'toastDone' 
			// for the toaster YANG module.
			// Through this structure, you can load the data 
			// from the notification event, if available.
			Notification n = new Notification(toasterCap, "toastDone");
			Leaf toastStatus = 
				n.getNotification().linkLeaf(n.createLeaf("toastStatus"));
			event.load(n);
			
			
			
			// Get the status of the 'toastDone' notification.
			String status = n.getNotification().getLeafCanonicalValue(toastStatus);
			
			
			// If status exists, it is certain that 
			// the 'toastDone' notification is present. 
			if (status != null)
			{
				// Done.
				if (status.equals("done"))
				{
					System.out.println("The toast is done!");
				}
				// Cancelled.
				else if (status.equals("cancelled"))
				{
					System.out.println("The toast is cancelled!");
				}
				// Error.
				else if (status.equals("error"))
				{
					System.out.println("There was an error, the unit can be broken!");
				}
				
				
				// Announced that a notification is already present.
				queue.add(true);
				
			}
			
		}
		
	};
	
	
	/*
	 * It makes toast until the user decides to quit.
	 */
	protected static boolean makeToasts(RpcHandler handler) throws CapabilityException, IOException {
		
		// Set the Notification Listener.
		handler.setNotificationsListener(nl);
		
		
		System.out.println("Start the notifications system...");
		
		// Enable notifications system through a CREATE-SUBSCRIPTION operation.
		// In this way, the server can let us know when our toast is ready.
		CreateSubscription subscription = 
			new CreateSubscription(handler.getSession());
		
		RpcReply reply = subscription.executeSync(handler);
		
		// Check if there were any errors.
		if (reply.containsErrors())
		{
			System.out.println("Unable to start the notifications system.");
			return false;
		}
		
		System.out.println("The Notifications system is started.");

		// Useful to intercept the user's commands.
		BufferedReader reader = 
			new BufferedReader(new InputStreamReader(System.in));
		
		
		// Constructs which is useful to ask the server to make toast.
		
		// This is the 'MAKE-TOAST' RPC operation of the toaster YANG module.
		Rpc rpc = new Rpc(toasterCap, "make-toast");
		Leaf toasterDoneness = 
			rpc.getInput().linkLeaf(rpc.createLeaf("toasterDoneness"));
		Leaf toasterToastType = 
			rpc.getInput().linkLeaf(rpc.createLeaf("toasterToastType"));
		
		// List of materials with which you can make a toast.
		// These identities are as described in the toaster YANG module.
		Identity whiteBread = new Identity(toasterCap, "white-bread");
		Identity wheatBread = new Identity(toasterCap, "wheat-bread");
		Identity wonderBread = new Identity(toasterCap, "wonder-bread");
		Identity frozenWaffle = new Identity(toasterCap, "frozen-waffle");
		Identity frozenBagel = new Identity(toasterCap, "frozen-bagel");
		Identity hashBrown = new Identity(toasterCap, "hash-brown");
		
		
		// Collects identities in an array.
		Identity[] types = new Identity[] { whiteBread, wheatBread, wonderBread,
				frozenWaffle, frozenBagel, hashBrown };
		
		// Doneness level set at 5 for this example.
		int doneness = 5;
		
		// Type of material set for the toast to Bheat Bread for this example.
		// Note: This is an index on the Identity[] array.
		int toasterToastTypeIdx = 1;
		
		
		// Assign the arguments 'toasterDoneness' and 'toasterToastType' 
		// for the RPC operation.
		rpc.getInput().assignLeaf(toasterDoneness, doneness + "");
		rpc.getInput().assignLeaf(toasterToastType, 
				types[toasterToastTypeIdx].getUniqueNane());
		
		// Enter into an infinite loop asking the user to 
		// make a toast or exit the program.
		while (true)
		{
			
			System.out.println("Please make your choice (m - Make a toast; q - Quit): ");
			
			String line = reader.readLine();
			
			// The user requests to exit the program.
			if (line.equals("q"))
			{
				System.out.println("Bye!");
				return false;
			}
			// The user requests to make a toast.
			else if (line.equals("m"))
			{
				// Make the toast.
				handler.sendSyncRpc(rpc);
				
				
				System.out.println("You have requested a toast with "
						+ types[toasterToastTypeIdx].getName() 
						+ " and a doneness of level "
						+ doneness + ".");
				
				System.out.println("Please wait the notification... :)");

				
				// Waits for the 'toastDone' notification.
				try
				{
					queue.take();
				}
				catch(Exception e)
				{
					// Nothing.
				}
				
			}
			
		}
		
	}


}


```


| **Step 9 - Video 1** |
|:---------------------|
| <a href='http://www.youtube.com/watch?feature=player_embedded&v=ye67QLCsFMQ' target='_blank'><img src='http://img.youtube.com/vi/ye67QLCsFMQ/0.jpg' width='425' height=344 /></a> |

## Step 10: Final Note ##

The source code of our example can be downloaded [here](http://code.google.com/p/netconf4android/downloads/list).

Note: This archive contains only the source code of this example.


For any questions on our example is that on this project, please feel free to contact me at:

> g.palmeri @ yahoo.it

Copyright (c) 2010 Giuseppe Palmeri