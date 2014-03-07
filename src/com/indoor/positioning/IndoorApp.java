package com.indoor.positioning;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class IndoorApp extends Application {

	
	public static IndoorApp APP;
	public static DataConnector connector;
	public static String ITZ_GA_USER_APP;
	// Preference
	public SharedPreferences prefs;
	public SharedPreferences.Editor prefsEditor;
	public boolean restoreStateIsNeeded = false;
	public boolean forcedResyncIsNeeded = false;

	public static IndoorApp getInstance() {
		return APP;
	}

	@Override
	@SuppressWarnings({ "serial", "unused" })
	public void onCreate() {
		super.onCreate();
		APP = this;
		configApp();
		
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}


	// configure app
	protected void configApp() {
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefsEditor = prefs.edit();
	}
}
