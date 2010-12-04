package com.jakebasile.android.hearingsaver;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;

public final class SetupActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent serviceIntent = new Intent();
		serviceIntent.setClassName(getPackageName(), RegistrationService.class.getName());
		startService(serviceIntent);
		setContentView(R.layout.activity_setup);
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SetupActivity.this);
		final SeekBar pluggedBar = (SeekBar)findViewById(R.id.activity_setup_seekplugged);
		pluggedBar.setProgress((int)(prefs.getFloat("plugged", .25f) * 100));
		final SeekBar unpluggedBar = (SeekBar)findViewById(R.id.activity_setup_seekunplugged);
		unpluggedBar.setProgress((int)(prefs.getFloat("unplugged", 0) * 100));
		final Button saveButton = (Button)findViewById(R.id.activity_setup_okbutton);
		saveButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Editor editPrefs = prefs.edit();
				editPrefs.putFloat("plugged", pluggedBar.getProgress() / 100f);
				editPrefs.putFloat("unplugged", unpluggedBar.getProgress() / 100f);
				editPrefs.commit();
				finish();
			}
		});
	}
}