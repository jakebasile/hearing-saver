package com.jakebasile.android.hearingsaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.util.Log;

public class UnplugReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
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
				break;
			}
			// plugged in
			case 1:
			{
				am.setStreamVolume(AudioManager.STREAM_MUSIC, (int)(maxVol * plugged),
					AudioManager.FLAG_SHOW_UI);
				break;
			}
		}
	}
}