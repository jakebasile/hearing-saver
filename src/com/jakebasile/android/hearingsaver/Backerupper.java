package com.jakebasile.android.hearingsaver;

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public final class Backerupper extends BackupAgentHelper
{
	private static final String PREFS_KEY = "defaultPrefs";

	private static final String PREFS = "com.jakebasile.android.hearingsaver_preferences";

	@Override
	public void onCreate()
	{
		super.onCreate();
		SharedPreferencesBackupHelper prefsBackup = new SharedPreferencesBackupHelper(
			this, PREFS);
		addHelper(PREFS_KEY, prefsBackup);
	}
}