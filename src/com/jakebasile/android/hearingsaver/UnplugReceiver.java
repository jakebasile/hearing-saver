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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Handles the Headset Plug broadcast action. Cannot be registered in the manifest but must be
 * manually registered by a service.
 * @author Jake Basile
 */
public class UnplugReceiver extends BroadcastReceiver
{
	private static UnplugReceiver instance;

	private boolean isFirst;

	private UnplugReceiver()
	{
	}

	public static UnplugReceiver getInstance()
	{
		if(instance == null)
		{
			instance = new UnplugReceiver();
		}
		instance.isFirst = true;
		return instance;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// This receiver (I think erroneously) gets called when it is registered. This means that
		// when the user starts the service for the first time, reboots, or when the service is destroyed
		// and restarted by the system, their volume will be set. Aside from being unintended behavior,
		// it also causes their "old mode" to be overwritten, which means that if they are already
		// plugged in (and the ringer is muted), their old mode will be saved as silent and the ringer will
		// *not* be turned back on when they unplug. To prevent this, I'm using a member variable to record
		// if it is the first try or not.
		if(!isFirst)
		{
			int state = intent.getIntExtra("state", -1);
			Log.d("Hearing Saver", "state = " + state);
			AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			float plugged = prefs.getFloat("plugged", .25f);
			float unplugged = prefs.getFloat("unplugged", 0);
			switch(state)
			{
				// unplugged.
				case 0:
				{
					am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(maxVol * unplugged),
						AudioManager.FLAG_SHOW_UI);
					if(prefs.getBoolean("muteWhenPlugged", false))
					{
						int oldMode = prefs.getInt("oldRinger", AudioManager.RINGER_MODE_NORMAL);
						Log.d("Hearing Saver", "oldMode = " + oldMode);
						am.setRingerMode(oldMode);
					}
					break;
				}
				// plugged in
				case 1:
				{
					am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(maxVol * plugged),
						AudioManager.FLAG_SHOW_UI);
					if(prefs.getBoolean("muteWhenPlugged", false))
					{
						int currentMode = am.getRingerMode();
						Log.d("Hearing Saver", "currentMode = " + currentMode);
						Editor edit = prefs.edit();
						edit.putInt("oldRinger", currentMode);
						edit.commit();
						am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					}
					break;
				}
			}
		}
		else
		{
			Log.d("Hearing Saver", "First run receieved.");
			isFirst = false;
		}
	}
}