package com.vhosting.netconf.toaster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

class Eula
{

	static interface Listener
	{
		void accepted();
		void refuse();
	}

	static void show(final Activity activity, final Listener l) {

		final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.eula_title);
		builder.setCancelable(true);
		builder.setPositiveButton(R.string.eula_accept,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which) {

						l.accepted();
					}
				});
		builder.setNegativeButton(R.string.eula_refuse,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which) {
						l.refuse();
					}
				});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			public void onCancel(DialogInterface dialog) {
				l.refuse();
			}
		});
		builder.setMessage(Main.readDoc(activity, R.raw.license));
		builder.create().show();
		return;

	}

}