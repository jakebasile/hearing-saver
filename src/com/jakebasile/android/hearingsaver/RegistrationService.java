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

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * This service stays running to keep the UnplugReceiver registered for the headset unplug
 * broadcast.
 * @author Jake Basile
 */
public class RegistrationService extends Service
{
	private UnplugReceiver receiver;

	@Override
	public IBinder onBind(Intent arg0)
	{
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		receiver = UnplugReceiver.getInstance();
		Log.d("Hearing Saver", "Registering receiver");
		registerReceiver(receiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
		return Service.START_STICKY;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d("Hearing Saver", "Unregistering receiver");
		// Be sure and unregister the receiver when the service is destroyed. This usually means
		// the service is being killed by the system, and it will be restarted again momentarily.
		// if we don't unregister, sometimes multiple instances of the receiver get registered.
		unregisterReceiver(receiver);
	}
}
