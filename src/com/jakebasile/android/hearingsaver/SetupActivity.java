/*
 * Copyright 2010-2011 Jake Basile
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jakebasile.android.hearingsaver;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.backup.BackupManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
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
		final Intent serviceIntent = new Intent();
		serviceIntent.setClassName(getPackageName(), RegistrationService.class.getName());
		startService(serviceIntent);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View dialogLayout = getLayoutInflater().inflate(R.layout.activity_setup, null);
		final SeekBar pluggedBar = (SeekBar)dialogLayout.findViewById(R.id.activity_setup_seekplugged);
		final SeekBar unpluggedBar = (SeekBar)dialogLayout.findViewById(R.id.activity_setup_seekunplugged);
		final CheckBox muteBox = (CheckBox)dialogLayout.findViewById(R.id.activity_setup_checkmute);
		final VolumeSettings settings = new VolumeSettings(this);
		pluggedBar.setProgress((int)(settings.getPluggedLevel() * 100));
		unpluggedBar.setProgress((int)(settings.getUnpluggedLevel() * 100));
		muteBox.setChecked(settings.getMuteOnPlug());
		builder.setView(dialogLayout);
		builder.setPositiveButton(R.string.set_levels, new OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				settings.setPluggedLevel(pluggedBar.getProgress() / 100f);
				settings.setUnpluggedLevel(unpluggedBar.getProgress() / 100f);
				settings.setMuteOnPlug(muteBox.isChecked());
				BackupManager.dataChanged("com.jakebasile.android.hearingsaver");
				startService(serviceIntent);
				finish();
			}
		});
		builder.setOnCancelListener(new OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialog)
			{
				finish();
			}
		});
		builder.create().show();
	}
}