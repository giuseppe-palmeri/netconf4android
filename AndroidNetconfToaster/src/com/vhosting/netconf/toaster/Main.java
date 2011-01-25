package com.vhosting.netconf.toaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity
{ 
	private static final int MENU_NEW_CONNECTION = 0;
	private static final int MENU_INFO = 1;
	private static final int MENU_HELP = 2;
	private static final int MENU_ABOUT = 3;
	private static final int MENU_LICENSE = 4;
	private static final int CONNECTION_RESULT = 1;
	private static final int TOASTER_RESULT = 2;
	private MatrixCursor cursor;
	private SimpleCursorAdapter adapter;
	private SharedPreferences pref;
	private TextView message;
	private ListView lv;

	private Handler h = new Handler();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		pref = getSharedPreferences("__main", Context.MODE_PRIVATE);

		lv = (ListView) findViewById(android.R.id.list);

		message = (TextView) this.findViewById(R.id.message);

		populate();

		eula();

	}

	private void populate() {

		cursor = new MyCursor(new String[] { "_id", "label", "host", "proxy" });

		adapter = new SimpleCursorAdapter(this,
				R.layout.main_row,
				// Specify the row template to use.
				cursor, // Pass in the cursor to bind to.
				new String[] { "label", "host", "proxy" }, // Array of cursor columns to bind to.
				new int[] { R.id.text1, R.id.text2, R.id.text3 })
		{
			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {

				View v = super.getView(position, convertView, parent);
				ImageButton b = (ImageButton) v.findViewById(R.id.imageButton);

				b.setOnClickListener(new OnClickListener()
				{
					public void onClick(View v) {
						int id = ((MyCursor) cursor).getId(position);
						System.out.println(v);
						goToToasterMachine(id);
					}
				});
				b.setOnLongClickListener(new OnLongClickListener()
				{

					public boolean onLongClick(View v) {
						int id = ((MyCursor) cursor).getId(position);
						showEditActions(id);
						return true;
					}

				});

				return v;
			}

		};

		lv.setAdapter(adapter);

		String confs = pref.getString("confs", "");

		for (String s : confs.split(":"))
		{
			if (s.equals(""))
				continue;

			int c = Integer.parseInt(s);
			SharedPreferences p = getSharedPreferences(c + ".conf",
					Context.MODE_PRIVATE);
			boolean b = p.getBoolean("isPresent", false);

			if (b)
			{

				boolean exists = false;
				if (!exists)
				{
					cursor.addRow(new Object[] {
							c,

							p.getString("label", ""),

							p.getString("login", "") + "@"
									+ p.getString("host", "") + ":"
									+ p.getInt("port", 0),

							((p.getBoolean("haveProxy", false)) ? p.getString(
									"proxyHost", "")
									+ ":"
									+ p.getInt("proxyPort", 0) : null) });
				}
			}
		}

		adapter.notifyDataSetChanged();

		if (cursor.getCount() == 0)
			message.setVisibility(View.VISIBLE);
		else
			message.setVisibility(View.GONE);

	}

	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		menu.setGroupVisible(0, true);
		menu.setGroupVisible(1, true);
		return super.onMenuOpened(featureId, menu);
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, MENU_NEW_CONNECTION, 0, getString(R.string.new_connection));
		menu.add(0, MENU_INFO, 0, getString(R.string.info));
		menu.add(0, MENU_HELP, 0, getString(R.string.help));
		menu.add(0, MENU_ABOUT, 0, getString(R.string.about));
		menu.add(0, MENU_LICENSE, 0, getString(R.string.license));
		return true;
	}

	/* Handles item selections */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId())
		{

		case MENU_NEW_CONNECTION:
			newConnection();
			return true;
		case MENU_INFO:
			showInfo();
			return true;
		case MENU_HELP:
			showHelp();
			return true;
		case MENU_ABOUT:
			showAbout();
			return true;
		case MENU_LICENSE:
			showLicese();
			return true;
		}
		return false;
	}

	private void showAbout() {
		showMessage(R.string.about, R.raw.about);

	}

	private void showHelp() {
		showMessage(R.string.help, R.raw.help);

	}

	private void showInfo() {
		showMessage(R.string.info, R.raw.info);

	}

	private void showLicese() {
		showMessage(R.string.license, R.raw.license);

	}

	void showMessage(final int titleResId, final int docRawId) {
		Runnable r = new Runnable()
		{
			public void run() {
				AlertDialog.Builder builder3 = new AlertDialog.Builder(
						Main.this);
				builder3.setTitle(getString(titleResId));
				String m = readDoc(Main.this, docRawId).toString();
				builder3.setMessage(m);
				final Dialog dialog = builder3.create();
				dialog.show();
			}

		};
		h.postDelayed(r, 0);
	}

	static CharSequence readDoc(Activity activity, int fileRawId) {
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new InputStreamReader(activity
					.getResources().openRawResource(fileRawId)));
			String line;
			StringBuilder buffer = new StringBuilder();
			while ((line = in.readLine()) != null)
				buffer.append(line).append('\n');
			in.close();
			in = null;

			return buffer;
		}
		catch (IOException e)
		{
			return "";
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch (IOException e)
				{
					// Ignore
				}
			}
		}
	}

	private void newConnection() {
		int tot = pref.getInt("counter", 1);
		Editor e = pref.edit();
		e.putInt("counter", tot + 1);
		e.commit();
		final Intent i = new Intent(Main.this, Connection.class);
		i.putExtra("id", tot + 1);
		startActivityForResult(i, CONNECTION_RESULT);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode)
		{
		case CONNECTION_RESULT:
			System.out.println("CONNECTION_RESULT");
			populate();
			break;
		case TOASTER_RESULT:
			System.out.println("TOASTER_RESULT");

			break;
		}
	}

	private void goToToasterMachine(int id) {
		
		Context context = getApplicationContext();
		CharSequence text = "Connect.";
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		
		final Intent i = new Intent(Main.this, Toaster.class);
		i.putExtra("id", id);
		startActivityForResult(i, TOASTER_RESULT);
	}

	private void showEditActions(final int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);

		builder.setTitle(R.string.app_name);
		builder.setItems(R.array.itemOptions,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which) {
						switch (which)
						{
						case 0: // OPEN
							goToToasterMachine(id);
							break;
						case 1: // EDIT
							final Intent i = new Intent(Main.this,
									Connection.class);
							i.putExtra("id", id);
							startActivityForResult(i, CONNECTION_RESULT);
							break;
						case 2: // REMOVE
							removeConnection(id);
							break;
						case 3: // CANCEL
							return;
						}
					}
				});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	protected void removeConnection(int id) {

		String confs = pref.getString("confs", "");

		String newConfs = "";

		for (String s : confs.split(":"))
		{
			if (s.equals(""))
				continue;

			int c = Integer.parseInt(s);
			if (c != id)
				newConfs += ":" + s;
		}

		Editor ed = pref.edit();
		ed.putString("confs", newConfs);
		ed.commit();

		SharedPreferences p = getSharedPreferences(id + ".conf",
				Context.MODE_PRIVATE);
		ed = p.edit();
		ed.clear();
		ed.commit();
		populate();

		Context context = getApplicationContext();
		CharSequence text = getString(R.string.connectionRemoved);
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();

	}

	class MyCursor extends MatrixCursor
	{

		int idIdx;
		Vector<Object[]> objs = new Vector<Object[]>();

		public MyCursor(String[] columnNames)
		{
			super(columnNames);
			idIdx = this.getColumnIndex("_id");
		}

		@Override
		public void addRow(Object[] columnValues) {

			for (Object[] o : objs)
			{
				if (o[idIdx] == columnValues[idIdx])
				{
					for (int i = 0; i < columnValues.length; i++)
					{
						o[i] = columnValues[i];
					}
					return;
				}
			}

			objs.add(columnValues);
			super.addRow(columnValues);
		}

		public int getId(int position) {
			return (Integer) objs.get(position)[idIdx];

		}

	}

	private void eula() {
		boolean eulaAccepted = pref.getBoolean("eulaAccepted", false);
		if (!eulaAccepted)
		{

			Eula.show(this, new Eula.Listener()
			{

				public void accepted() {
					pref.edit().putBoolean("eulaAccepted", true).commit();
					Runnable r = new Runnable()
					{

						@Override
						public void run() {
							showInfo();

						}
					};
					h.postDelayed(r, 2500);
				}

				public void refuse() {
					finish();
				}

			});
		}
	}

}