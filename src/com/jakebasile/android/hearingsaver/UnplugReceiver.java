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

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * Handles the Headset Plug broadcast action. Cannot be registered in the manifest but
 * must be
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

	private int processIntent(Context context, Intent intent, boolean btEnabled)
	{
		int state = -1;
		if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG))
		{
			state = intent.getIntExtra("state", -1);
		}
		else
		{
			// it's a bluetooth broadcast.
			if(btEnabled)
			{
				BluetoothDevice device = (BluetoothDevice)intent.getExtras().get(
					BluetoothDevice.EXTRA_DEVICE);
				BluetoothClass btClass = device.getBluetoothClass();
				int classId = btClass.getMajorDeviceClass();
				if(classId == BluetoothClass.Device.Major.AUDIO_VIDEO)
				{
					if(intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED))
					{
						state = 1;
					}
					else if(intent.getAction().equals(
						BluetoothDevice.ACTION_ACL_DISCONNECTED))
					{
						state = 0;
					}
				}
			}
		}
		return state;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(!ignoreNext)
		{
			AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
			int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			int maxVolRinger = am.getStreamMaxVolume(AudioManager.STREAM_RING);
			VolumeSettings settings = new VolumeSettings(context);
			int state = processIntent(context, intent,
				settings.getBluetoothDetectionEnabled());
			switch(state)
			{
				// unplugged.
				case 0:
				{
					int newVol = (int)(maxVol * settings.getUnpluggedLevel());
					int newVolRinger = (int)(maxVolRinger * 
							settings.getUnpluggedLevelRinger());
					am.setStreamVolume(AudioManager.STREAM_MUSIC, newVol,
						AudioManager.FLAG_SHOW_UI);
					if(settings.getMuteOnPlug())
					{
						am.setRingerMode(settings.getRinger());
					}
					else
					{
						am.setStreamVolume(AudioManager.STREAM_RING, newVolRinger,
								AudioManager.FLAG_SHOW_UI);
					}
					break;
				}
				// plugged in
				case 1:
				{
					if(settings.getSaveUnplugLevel())
					{
						float oldVol = (float) am.getStreamVolume(AudioManager.STREAM_MUSIC) / maxVol;
						float oldVolRinger = (float) am.getStreamVolume(AudioManager.STREAM_RING) / maxVolRinger;
						
						settings.setUnpluggedLevel(oldVol);
						settings.setUnpluggedLevelRinger(oldVolRinger);
					}
					
					int newVol = (int)(maxVol * settings.getPluggedLevel());
					int newVolRinger = (int)(maxVolRinger * settings.getPluggedLevelRinger());
					am.setStreamVolume(AudioManager.STREAM_MUSIC, newVol,
						AudioManager.FLAG_SHOW_UI);
					if(settings.getMuteOnPlug())
					{
						settings.setRinger(am.getRingerMode());
						am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					}
					else
					{
						am.setStreamVolume(AudioManager.STREAM_RING, newVolRinger,
								AudioManager.FLAG_SHOW_UI);
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