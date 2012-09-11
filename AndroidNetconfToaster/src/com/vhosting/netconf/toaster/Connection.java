package com.vhosting.netconf.toaster;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Connection extends Activity {

	private SharedPreferences sp;
	private int id;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connection);

		id = getIntent().getExtras().getInt("id");

		sp = getSharedPreferences(id + ".conf", Context.MODE_PRIVATE);

		boolean isPresent = sp.getBoolean("isPresent", false);

		if (isPresent) {
			EditText label = (EditText) findViewById(R.id.editLabel);
			label.setText(sp.getString("label", ""));

			EditText host = (EditText) findViewById(R.id.editHost);
			host.setText(sp.getString("host", ""));

			EditText port = (EditText) findViewById(R.id.editPort);
			port.setText("" + sp.getInt("port", 0));

			EditText login = (EditText) findViewById(R.id.editLogin);
			login.setText(sp.getString("login", ""));

			EditText passwd = (EditText) findViewById(R.id.editPasswd);
			passwd.setText(sp.getString("passwd", ""));

			EditText subsystem = (EditText) findViewById(R.id.editSubsystem);
			subsystem.setText(sp.getString("subsystem", ""));

			CheckBox cb = (CheckBox) findViewById(R.id.label2Connection);
			cb.setChecked(sp.getBoolean("haveProxy", false));

			EditText proxyHost = (EditText) findViewById(R.id.editProxyHost);
			proxyHost.setText(sp.getString("proxyHost", ""));

			EditText proxyPort = (EditText) findViewById(R.id.editProxyPort);
			proxyPort.setText("" + sp.getInt("proxyPort", 0));
		}

		Button save = (Button) findViewById(R.id.saveButton);

		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isSaveable())
					saveChanges();
				else {
					Context context = getApplicationContext();
					CharSequence text = getString(R.string.cannotSave);
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(context, text, duration);
					toast.show();
				}
			}
		});

		Button discard = (Button) findViewById(R.id.discardButton);

		discard.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				discardChanges();
			}
		});

	}

	protected void discardChanges() {
		Context context = getApplicationContext();
		CharSequence text = getString(R.string.discardMsg);
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		finish();
	}

	protected void saveChanges() {

		Editor e = sp.edit();

		EditText label = (EditText) findViewById(R.id.editLabel);
		e.putString("label", label.getText().toString());

		EditText host = (EditText) findViewById(R.id.editHost);
		e.putString("host", host.getText().toString());

		EditText port = (EditText) findViewById(R.id.editPort);
		e.putInt("port", Integer.parseInt(port.getText().toString()));

		EditText login = (EditText) findViewById(R.id.editLogin);
		e.putString("login", login.getText().toString());

		EditText passwd = (EditText) findViewById(R.id.editPasswd);
		e.putString("passwd", passwd.getText().toString());

		EditText subsystem = (EditText) findViewById(R.id.editSubsystem);
		e.putString("subsystem", subsystem.getText().toString());

		CheckBox cb = (CheckBox) findViewById(R.id.label2Connection);
		e.putBoolean("haveProxy", cb.isChecked());

		EditText proxyHost = (EditText) findViewById(R.id.editProxyHost);
		e.putString("proxyHost", proxyHost.getText().toString());

		EditText proxyPort = (EditText) findViewById(R.id.editProxyPort);
		e.putInt("proxyPort", Integer.parseInt(proxyPort.getText().toString()));

		SharedPreferences pref = getSharedPreferences("__main",
				Context.MODE_PRIVATE);

		String confs = pref.getString("confs", "");
		int i = 0;
		boolean exist = false;

		for (String s : confs.split(":")) {
			if (s.equals(""))
				continue;
			int c = Integer.parseInt(s);

			if (c == id) {
				exist = true;
				break;
			}
			i++;
		}
		if (!exist) {
			Editor ed = pref.edit();

			ed.putString("confs", confs + ":" + id);
			ed.commit();
		}

		e.putBoolean("isPresent", true);

		e.commit();

		Context context = getApplicationContext();
		CharSequence text = getString(R.string.saveMsg);
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
		finish();
	}

	private boolean isSaveable() {
		EditText label = (EditText) findViewById(R.id.editLabel);
		if (label.getText().toString().equalsIgnoreCase(""))
			return false;

		EditText host = (EditText) findViewById(R.id.editHost);
		if (host.getText().toString().equalsIgnoreCase(""))
			return false;

		EditText port = (EditText) findViewById(R.id.editPort);
		if (port.getText().toString().equalsIgnoreCase(""))
			return false;

		EditText login = (EditText) findViewById(R.id.editLogin);
		if (login.getText().toString().equalsIgnoreCase(""))
			return false;

		EditText passwd = (EditText) findViewById(R.id.editPasswd);
		if (passwd.getText().toString().equalsIgnoreCase(""))
			return false;

		EditText subsystem = (EditText) findViewById(R.id.editSubsystem);
		if (subsystem.getText().toString().equalsIgnoreCase(""))
			return false;

		CheckBox cb = (CheckBox) findViewById(R.id.label2Connection);

		EditText proxyHost = (EditText) findViewById(R.id.editProxyHost);
		if (cb.isChecked()
				&& proxyHost.getText().toString().equalsIgnoreCase(""))
			return false;

		EditText proxyPort = (EditText) findViewById(R.id.editProxyPort);
		if (cb.isChecked()
				&& proxyPort.getText().toString().equalsIgnoreCase(""))
			return false;
		return true;
	}

}