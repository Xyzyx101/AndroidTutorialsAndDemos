package com.badlogic.androidgames;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class SoundPoolTest extends Activity implements OnTouchListener {
	SoundPool soundPool;
	int explosionId = -1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView textView = new TextView(this);
        textView.setOnTouchListener(this);
        setContentView(textView);
        
        textView.setText("Touch the screen to play a sound");
        
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
        
        try {
        	AssetManager assetManager = getAssets();
        	AssetFileDescriptor descriptor = assetManager.openFd("explosion.ogg");
        	int priority = 1;
        	explosionId = soundPool.load(descriptor, priority);
        } catch (IOException e) {
        	textView.setText("Couldn't load sound effect from asset, " + e.getMessage());
        }
	}
	
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP) {
			if(explosionId != -1) {
				float leftVolume = 1f;
				float rightVolume = 1f;
				int priority = 0;
				int loop = 0;
				int rate = 1;
				soundPool.play(
						explosionId
						, leftVolume
						, rightVolume
						, priority
						, loop
						, rate);
			}
		}
		return true;
	}
}
