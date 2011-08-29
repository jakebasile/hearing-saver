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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.preference.PreferenceManager;

final class VolumeSettings
{
	private static final String MUTE_WHEN_PLUGGED = "muteWhenPlugged";

	private static final String OLD_RINGER = "oldRinger";

	private static final String PLUGGED = "plugged";

	private static final String UNPLUGGED = "unplugged";

	private static final String ENABLED = "enabled";

	private static final String BLUETOOTH_DETECTION = "btDetection";

	private Context context;

	public VolumeSettings(Context context)
	{
		this.context = context;
	}

	public void setEnabled(boolean enabled)
	{
		Editor editPrefs = getSharedPrefs().edit();
		editPrefs.putBoolean(ENABLED, enabled);
		editPrefs.commit();
	}

	public boolean getEnabled()
	{
		return getSharedPrefs().getBoolean(ENABLED, true);
	}

	public void setUnpluggedLevel(float level)
	{
		Editor editPrefs = getSharedPrefs().edit();
		editPrefs.putFloat(UNPLUGGED, level);
		editPrefs.commit();
	}

	public float getUnpluggedLevel()
	{
		return getSharedPrefs().getFloat(UNPLUGGED, 0f);
	}

	public void setPluggedLevel(float level)
	{
		Editor editPrefs = getSharedPrefs().edit();
		editPrefs.putFloat(PLUGGED, level);
		editPrefs.commit();
	}

	public float getPluggedLevel()
	{
		return getSharedPrefs().getFloat(PLUGGED, .25f);
	}

	public void setRinger(int ringer)
	{
		Editor editPrefs = getSharedPrefs().edit();
		editPrefs.putInt(OLD_RINGER, ringer);
		editPrefs.commit();
	}

	public int getRinger()
	{
		return getSharedPrefs().getInt(OLD_RINGER, AudioManager.RINGER_MODE_NORMAL);
	}

	public boolean getMuteOnPlug()
	{
		return getSharedPrefs().getBoolean(MUTE_WHEN_PLUGGED, false);
	}

	public void setMuteOnPlug(boolean mute)
	{
		Editor editPrefs = getSharedPrefs().edit();
		editPrefs.putBoolean(MUTE_WHEN_PLUGGED, mute);
		editPrefs.commit();
	}

	public boolean getBluetoothDetectionEnabled()
	{
		return getSharedPrefs().getBoolean(BLUETOOTH_DETECTION, true);
	}

	public void setBluetoothDetectionEnabled(boolean enabled)
	{
		Editor editPrefs = getSharedPrefs().edit();
		editPrefs.putBoolean(BLUETOOTH_DETECTION, enabled);
		editPrefs.commit();
	}

	private SharedPreferences getSharedPrefs()
	{
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
