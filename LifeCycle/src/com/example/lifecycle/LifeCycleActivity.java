package com.example.lifecycle;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.util.Log;

public class LifeCycleActivity extends Activity {
	private static final String TAG = "MyActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_life_cycle);
		Log.e(TAG, "onCreate() called!");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.life_cycle, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.e(TAG, "onStart() called!");
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.e(TAG, "onRestart() called!");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.e(TAG, "onStop called()!");
	}
	
	@Override
	protected void onResume() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity loses focus
		super.onResume();
		Log.e(TAG, "onResume() called!");
	}
	
	@Override
	protected void onPause() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity loses focus
		super.onPause();
		Log.e(TAG, "onPause called!");
	}
	
	@Override
	protected void onDestroy() {
		// Implement onDestroy() to release objects and free up memory 
		// when an Activity is terminated.
		super.onDestroy();
		Log.e(TAG, "onDestroy() called!");
	}
}
