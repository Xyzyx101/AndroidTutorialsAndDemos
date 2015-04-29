package com.badlogic.androidgames;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class PreferencesTest extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView;
		textView = new TextView(this);
		setContentView(textView);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();
		
		// save prefs
		editor.putString("key1", "banana");
		editor.putInt("key2", 5);
		editor.commit();
		
		// load prefs - the second value is default if key is not found
		String value1 = prefs.getString("key1", null);
		int value2 = prefs.getInt("key2", 0);
		StringBuilder builder = new StringBuilder();
		builder.setLength(0);
		builder.append("Preferences: \n");
		builder.append("key1: " + value1 + "\n");
		builder.append("key2: " + value2 + "\n");
		textView.setText(builder.toString());
	}
}
