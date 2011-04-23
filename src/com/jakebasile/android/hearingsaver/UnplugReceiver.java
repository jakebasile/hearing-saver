/* 
 * Copyright 2010-2011 Jake Basile
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
import android.media.AudioManager;

/**
 * Handles the Headset Plug broadcast action. Cannot be registered in the manifest but must be
 * manually registered by a service.
 * @author Jake Basile
 */
public class UnplugReceiver extends BroadcastReceiver
{
	private static UnplugReceiver instance;

	private boolean ignoreNext;

	private UnplugReceiver()
	{
	}

	/**
	 * Get the UnplugReceiver singleton.
	 */
	public static UnplugReceiver getInstance()
	{
		if(instance == null)
		{
			instance = new UnplugReceiver();
		}
		return instance;
	}

	/**
	 * Tell the receiver to ignore its next broadcast.
	 */
	public void setIgnoreNext()
	{
		ignoreNext = true;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(!ignoreNext)
		{
			int state = intent.getIntExtra("state", -1);
			AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			VolumeSettings settings = new VolumeSettings(context);
			switch(state)
			{
				// unplugged.
				case 0:
				{
					int newVol = (int)(maxVol * settings.getUnpluggedLevel());
					am.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, AudioManager.FLAG_SHOW_UI);
					if(settings.getMuteOnPlug())
					{
						am.setRingerMode(settings.getRinger());
					}
					break;
				}
				// plugged in
				case 1:
				{
					int newVol = (int)(maxVol * settings.getPluggedLevel());
					am.setStreamVolume(AudioManager.STREAM_MUSIC, newVol, AudioManager.FLAG_SHOW_UI);
					if(settings.getMuteOnPlug())
					{
						settings.setRinger(am.getRingerMode());
						am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					}
					break;
				}
			}
		}
		else
		{
			ignoreNext = false;
		}
	}
}