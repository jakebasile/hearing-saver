/* 
 * Copyright 2010 Jake Basile
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jakebasile.android.hearingsaver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;

/**
 * Handles setup for the hearing saver app, and will start the registration service.
 * @author Jake Basile
 */
public final class SetupActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		Intent serviceIntent = new Intent();
		serviceIntent.setClassName(getPackageName(), RegistrationService.class.getName());
		startService(serviceIntent);
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SetupActivity.this);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View dialogLayout = getLayoutInflater().inflate(R.layout.activity_setup, null);
		final SeekBar pluggedBar = (SeekBar)dialogLayout.findViewById(R.id.activity_setup_seekplugged);
		pluggedBar.setProgress((int)(prefs.getFloat("plugged", .25f) * 100));
		final SeekBar unpluggedBar = (SeekBar)dialogLayout.findViewById(R.id.activity_setup_seekunplugged);
		unpluggedBar.setProgress((int)(prefs.getFloat("unplugged", 0) * 100));
		final CheckBox muteBox = (CheckBox)dialogLayout.findViewById(R.id.activity_setup_checkmute);
		muteBox.setChecked(prefs.getBoolean("muteWhenPlugged", false));
		builder.setView(dialogLayout);
		builder.setPositiveButton(R.string.set_levels, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				Editor editPrefs = prefs.edit();
				editPrefs.putFloat("plugged", pluggedBar.getProgress() / 100f);
				editPrefs.putFloat("unplugged", unpluggedBar.getProgress() / 100f);
				editPrefs.putBoolean("muteWhenPlugged", muteBox.isChecked());
				editPrefs.commit();
				finish();
			}
		});
		builder.create().show();
	}
}