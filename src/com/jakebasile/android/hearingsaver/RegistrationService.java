package com.jakebasile.android.hearingsaver;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class RegistrationService extends Service
{
	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		this.registerReceiver(new UnplugReceiver(), new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		return Service.START_STICKY;
	}
}
