package com.vhosting.netconf.toaster;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.w3c.dom.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.vhosting.netconf.frame.Data;
import com.vhosting.netconf.frame.Identity;
import com.vhosting.netconf.frame.Leaf;
import com.vhosting.netconf.frame.Notification;
import com.vhosting.netconf.frame.Rpc;
import com.vhosting.netconf.frame.RpcHandler;
import com.vhosting.netconf.frame.RpcReply;
import com.vhosting.netconf.notification.NotificationEvent;
import com.vhosting.netconf.notification.NotificationsListener;
import com.vhosting.netconf.transport.NetconfCatcher;
import com.vhosting.netconf.transport.NetconfCatcherListener;
import com.vhosting.netconf.transport.NetconfTransportEvent;
import com.vhosting.netconf.transport.NetconfTransportEvent.EventType;
import com.vhosting.netconf.transport.ssh.NetconfSshCather;
import com.vhosting.netconf.transport.ssh.SshAuthInfo;
import com.vhosting.netconf.yuma.Load;
import com.vhosting.netconf.yuma.YANGCapability;

public class Toaster extends Activity {
	private SharedPreferences sp;
	private int id;
	private boolean makingToast;
	private String host;
	private int port;
	private String label;
	private String login;
	private String passwd;
	private String subsystem;
	private boolean hasProxy;
	private String proxyHost;
	private int proxyPort;
	private TextView toaster;
	private TextView text;
	private Button makeToastB;
	private Button materialB;
	private Button donenessB;
	private NetconfSshCather catcher;
	private String machineInfo;
	private RpcHandler rpcHandler;
	private Runnable r;
	private Handler h = new Handler();
	private ScrollView scroll;

	private static final BlockingQueue<Boolean> queue = new ArrayBlockingQueue<Boolean>(
			1, true);

	private static YANGCapability toasterCap = new YANGCapability(
			"http://netconfcentral.org/ns/toaster",
			"http://netconfcentral.org/ns/toaster", "toast", "toaster");

	private NotificationsListener nl = new NotificationsListener() {
		public void processNotification(NotificationEvent notification) {

			Notification n = new Notification(toasterCap, "toastDone");
			Leaf toastStatus = n.getNotification().linkLeaf(
					n.createLeaf("toastStatus"));
			notification.load(n);

			String status = n.getNotification().getLeafCanonicalValue(
					toastStatus);

			if (status != null) {
				// Done.
				if (status.equals("done")) {
					printlnDown("The toast is done!");

					Runnable r = new Runnable() {

						@Override
						public void run() {
							makingToast = false;
							makeToastB.setText(R.string.textMakeToast);
							makeToastB.setEnabled(true);
							materialB.setEnabled(true);
							donenessB.setEnabled(true);
						}
					};
					runOnUiThread(r);
					toastDone();
				}

				// Cancelled.
				else if (status.equals("cancelled"))
					printlnDown("The toast is cancelled now.");
				// Error.
				else if (status.equals("error"))
					printlnDown("Ops.. there was an error, the unit can be broken!");

				queue.add(true);
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.toaster);
		id = getIntent().getExtras().getInt("id");
		sp = getSharedPreferences(id + ".conf", Context.MODE_PRIVATE);
		boolean isPresent = sp.getBoolean("isPresent", false);

		if (isPresent) {
			toaster = (TextView) findViewById(R.id.toaster);
			text = (TextView) findViewById(R.id.text);
			makeToastB = (Button) findViewById(R.id.buttonMakeToast);
			materialB = (Button) findViewById(R.id.buttonMaterial);
			donenessB = (Button) findViewById(R.id.buttonCooking);

			makeToastB.setEnabled(false);
			materialB.setEnabled(true);
			donenessB.setEnabled(true);

			scroll = (ScrollView) findViewById(R.id.ScrollView02);
			scroll.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					return true;
				}
			});

			materialB.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					choiceMaterial();
				}
			});

			donenessB.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					choiceDonenes();
				}

			});

			makeToastB.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					if (!makingToast) {
						Runnable r = new Runnable() {
							@Override
							public void run() {
								makingToast = true;
								makeToastB.setText(R.string.cancel_toast);
								materialB.setEnabled(false);
								donenessB.setEnabled(false);
							}
						};
						runOnUiThread(r);
						r = new Runnable() {
							@Override
							public void run() {
								try {
									makeToast(rpcHandler);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						};
						makeToastB.post(r);
					} else {
						Runnable r = new Runnable() {
							@Override
							public void run() {
								makingToast = false;
								makeToastB.setText(R.string.textMakeToast);
								materialB.setEnabled(true);
								donenessB.setEnabled(true);
							}
						};
						runOnUiThread(r);
						r = new Runnable() {
							@Override
							public void run() {
								try {
									cancelToast(rpcHandler);
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						};
						makeToastB.post(r);
					}

				}
			});

			this.label = sp.getString("label", "");
			this.host = sp.getString("host", "");
			this.port = sp.getInt("port", 0);
			this.login = sp.getString("login", "");
			this.passwd = sp.getString("passwd", "");
			this.subsystem = sp.getString("subsystem", "");
			this.hasProxy = sp.getBoolean("haveProxy", false);
			this.proxyHost = sp.getString("proxyHost", "");
			this.proxyPort = sp.getInt("proxyPort", 0);

			Runnable r = new Runnable() {
				@Override
				public void run() {
					connect();
				}
			};

			h.post(r);

		} else {

			finish();
		}

	}

	@Override
	public void finish() {
		end();
		super.finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	private void printTop() {
		printTop(null);
	}

	private void printTop(final String s) {

		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					if (s != null) {
						toaster.setText(s);
						return;
					} else {
						toaster.setText("");
						System.out.println(machineInfo);
						toaster.append(machineInfo + "");
						String a = getResources().getStringArray(
								R.array.itemMaterial)[type];
						toaster.append("Material: " + a + "\n");
						a = getResources().getStringArray(R.array.itemDoneners)[doneness];
						toaster.append("Doneness: " + Integer.parseInt(a)
								+ "\n");
					}
				} catch (Exception e) {
					toaster.setText(R.string.waitConnection);
				}
			}
		};
		runOnUiThread(r);
	}

	private void printlnDown(final String s) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				System.out.println(s);
				text.append(s + "\n");
				scroll.post(new Runnable() {
					public void run() {
						scroll.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});
			}
		};
		runOnUiThread(r);
	}

	private void connect() {

		/* Prepare the connection */
		InetSocketAddress addr = new InetSocketAddress(host, port);
		SshAuthInfo authInfo = new SshAuthInfo(addr, login, passwd);

		if (hasProxy) {
			InetSocketAddress h = new InetSocketAddress(proxyHost, proxyPort);
			authInfo.setProxyHost(h);
		} else {
			authInfo.removeProxyHost();
		}

		catcher = new NetconfSshCather(label, authInfo, subsystem);

		/* The log is full enabled. */
		NetconfCatcher.enableLog(NetconfCatcher.LogLevel.MESSAGES);

		/* The body of the application */
		catcher.setNetconfCatcherListener(new NetconfCatcherListener() {
			public void processTransportEvents(NetconfTransportEvent event) {
				Exception e = event.getTransportErrorException();
				if (e != null)
					e.printStackTrace();
				if (e != null) {
					printlnDown(e.getMessage());
				}

				printTop("No connection.");

				if (event.getEventType() == EventType.CONNECTION_CLOSED_BY_SERVER) {
					printlnDown("The connection is closed by server.");
				} else if (event.getEventType() == EventType.CONNECTION_CLOSED_BY_USER) {
					printlnDown("The connection is closed by user.");
				} else if (event.getEventType() == EventType.CONNECTION_CANNOT_BE_OPENED) {
					printlnDown("The connection cannot be opened.");
				}
			}

			public void processReadyForRpcRequests(final RpcHandler rpcHandler) {

				Toaster.this.rpcHandler = rpcHandler;
				rpcHandler.setNotificationsListener(nl);
				try {
					printlnDown("");
					printlnDown("The server connection has been established...");

					printlnDown("Check the existence of the toaster on the server...");
					boolean check = checkToaster(catcher, rpcHandler);
					if (!check) {
						printlnDown("Create the toaster...");
						boolean hasToaster = createToaster(catcher, rpcHandler);

						if (!hasToaster) {
							printlnDown("Unable to create the toaster.");
							return;
						} else {
							hasToaster = checkToaster(catcher, rpcHandler);
							if (!hasToaster)
								return;
						}

					}

					printlnDown("The toaster is ready! :)");

					printlnDown("");

					printlnDown("Start the notifications system...");

					CreateSubscription create = new CreateSubscription(
							rpcHandler.getSession());

					RpcReply rep = create.executeSync(rpcHandler);

					if (rep.containsErrors()) {
						printlnDown("Unable to start the notifications system.");
						catcher.disconnect();
						return;
					}

					printlnDown("Notifications system is started.");
					printlnDown("The toaster is now ready to process RPC operations.");

					r = new Runnable() {
						@Override
						public void run() {
							makingToast = false;
							makeToastB.setEnabled(true);
						}
					};
					runOnUiThread(r);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		Runnable connection = catcher.getRunnableConnection();

		/* Start the connection */
		Thread t = new Thread(connection);
		t.start();
	}

	private static int doneness = 2;
	private static int type = 0;

	protected boolean checkToaster(NetconfSshCather catcher,
			RpcHandler rpcHandler) throws IOException {

		Get g = new Get(rpcHandler.getSession());
		SubtreeFilter sf = g.createSubtreeFilter();
		sf.addFilter(toasterCap).addFilterString("toaster");
		g.setSubtreeFilter(sf);
		RpcReply rep = g.executeSync(rpcHandler);
		if (rep.containsErrors()) {
			printlnDown("Unable to identify the toaster.");
			return false;
		}

		GetReply gr = g.new GetReply(rep);
		Document doc = gr.getData();

		Data data = new Data(toasterCap);
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
		if (manufacturer == null)
			return false;

		String s = "" + manufacturer + "\n" + "" + modelNumber + "\n"
				+ "Status: " + status + "\n";

		this.machineInfo = s;

		printTop();

		if (status.equals("down")) {
			printlnDown("Ops... note that the toaster is switched off!");
		}

		return true;
	}

	private boolean createToaster(NetconfSshCather catcher,
			RpcHandler rpcHandler) throws CapabilityException, IOException {
		Load l = new Load(rpcHandler.getSession(), toasterCap);
		RpcReply rep = l.executeSync(rpcHandler);

		if (rep.containsErrors()) {
			printlnDown("Can not load the toaster module.");
			return false;
		}
		Lock lk = new Lock(rpcHandler.getSession(), Datastore.candidate);
		rep = lk.executeSync(rpcHandler);
		if (rep.containsErrors()) {
			printlnDown("Can not lock the Candidate database.");
			return false;
		}

		Config config = EditConfig.createConfig(toasterCap, "toaster{create}");
		EditConfig ec = new EditConfig(rpcHandler.getSession(),
				Datastore.candidate, config);

		rep = ec.executeSync(rpcHandler);
		if (rep.containsErrors()) {
			printlnDown("Can not create the toaster.");

			return false;
		}

		Commit c = new Commit(rpcHandler.getSession());
		rep = c.executeSync(rpcHandler);

		if (rep.containsErrors()) {
			printlnDown("Can not create the toaster.");
			return false;
		}

		Unlock ul = new Unlock(rpcHandler.getSession(), Datastore.candidate);
		ul.executeSync(rpcHandler);

		return true;
	}

	private boolean cancelToast(RpcHandler rpcHandler) throws IOException {

		Rpc rpc = new Rpc(toasterCap, "cancel-toast");

		printlnDown("");
		printlnDown("Was sought cancellation of the operation.");

		RpcReply rep = rpcHandler.sendSyncRpc(rpc);

		if (rep.containsErrors()) {
			printlnDown("Unable to cancel the request.");
			return false;
		}

		printlnDown("The request was canceled.");
		return true;
	}

	protected void makeToast(RpcHandler rpcHandler) throws IOException {

		Rpc rpc = new Rpc(toasterCap, "make-toast");
		Leaf toasterDoneness = rpc.getInput().linkLeaf(
				rpc.createLeaf("toasterDoneness"));
		Leaf toasterToastType = rpc.getInput().linkLeaf(
				rpc.createLeaf("toasterToastType"));

		// List of materials with which you can make a toast.
		Identity whiteBread = new Identity(toasterCap, "white-bread");
		Identity wheatBread = new Identity(toasterCap, "wheat-bread");
		Identity wonderBread = new Identity(toasterCap, "wonder-bread");
		Identity frozenWaffle = new Identity(toasterCap, "frozen-waffle");
		Identity frozenBagel = new Identity(toasterCap, "frozen-bagel");
		Identity hashBrown = new Identity(toasterCap, "hash-brown");

		Identity[] types = new Identity[] { whiteBread, wheatBread,
				wonderBread, frozenWaffle, frozenBagel, hashBrown };

		String a = getResources().getStringArray(R.array.itemDoneners)[doneness];

		rpc.getInput().assignLeaf(toasterDoneness, Integer.parseInt(a) + "");
		rpc.getInput()
				.assignLeaf(toasterToastType, types[type].getUniqueNane());
		rpcHandler.sendSyncRpc(rpc);

		printlnDown("");
		printlnDown("You have requested a toast with " + types[type].getName()
				+ " and a doneness of level " + a + ".");
		printlnDown("");
		printlnDown("Please wait the notification... :)");

	}

	private void end() {
		if (catcher.isConnected())
			showToast("Disconnect.");
		catcher.disconnect();
	}

	private void showToast(String message) {
		Context context = getApplicationContext();

		int duration = 500;

		Toast toast = Toast.makeText(context, message, duration);
		toast.show();
	}

	private void choiceMaterial() {

		AlertDialog.Builder builder = new AlertDialog.Builder(Toaster.this);

		builder.setTitle(R.string.textMaterial);
		builder.setItems(R.array.itemMaterial,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						type = which;
						printTop();
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void choiceDonenes() {

		AlertDialog.Builder builder = new AlertDialog.Builder(Toaster.this);
		builder.setTitle(R.string.textCooking);
		builder.setItems(R.array.itemDoneners,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						doneness = which;
						printTop();
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void toastDone() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(
				Toaster.this);

		builder.setTitle(R.string.app_name);
		builder.setMessage(R.string.toastDone);
		ImageView v = new ImageView(this);
		v.setImageResource(R.drawable.toast);
		builder.setView(v);
		builder.setPositiveButton(R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						// Nothing.
					}
				});
		Runnable r = new Runnable() {
			@Override
			public void run() {
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		};
		runOnUiThread(r);

	}

}