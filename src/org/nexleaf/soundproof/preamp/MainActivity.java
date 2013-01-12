/*
Copyright (c) 2011, Nexleaf Analytics
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the Nexleaf Analytics nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL NEXLEAF ANALYTICS BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.nexleaf.soundproof.preamp;

import java.text.DecimalFormat;
import java.util.Map;

import org.nexleaf.soundproof.preamp.AudioService.AudioBinder;
import org.nexleaf.soundproof.preamp.AudioService.IListener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class MainActivity extends Activity implements IListener{
	
    private static final String TAG = "MainActivity";
    
    AudioService mService;
    SharedPreferences mPrefs;
    Button mButton;
    TextView mPeakText;
    TextView mSplText;
    TextView mMicStatusText;
    ImageView mRecordingIndicatorImage;
    RadioGroup mRadioGroup;
    RadioButton mRadio10;
    RadioButton mRadio25;
    RadioButton mRadio50;
    double mInterval;
    
    private AudioManager audio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);
        
        mPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
        
        mButton = (Button) findViewById(R.id.control_button);
        mPeakText = (TextView) findViewById(R.id.peak_text);
        mSplText = (TextView) findViewById(R.id.spl_text);
        mMicStatusText = (TextView) findViewById(R.id.mic_status_text);
        mRecordingIndicatorImage = (ImageView) findViewById(R.id.recording_image);
        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        mRadio10 = ((RadioButton)findViewById(R.id.radio_10));
        mRadio25 = ((RadioButton)findViewById(R.id.radio_25));
        mRadio50 = ((RadioButton)findViewById(R.id.radio_50));
        
        mRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				switch (checkedId) {
				case R.id.radio_10:
					mInterval = 0.10;
					break;
					
				case R.id.radio_25:
					mInterval = 0.25;
					break;
					
				case R.id.radio_50:
					mInterval = 0.50;
					break;
				}
			}
		});
        
        mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean recording = ! mPrefs.getBoolean("recording", false);
				mPrefs.edit().putBoolean("recording", recording).commit();
				updateButtonAndIndicatorState(recording);
				
				if (recording) {
					Intent intent = new Intent(MainActivity.this, AudioService.class);
			    	intent.putExtra(AudioService.EXTRA_INTERVAL, mInterval);
			    	WakefulIntentService.sendWakefulWork(MainActivity.this, intent);
					bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
				}
			}
		});

        mPeakText.setText("-----");
		mSplText.setText("-----");
        
        mRadio50.setChecked(true);
        
        mPrefs.edit().putInt("vol", 30).commit();
        
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        
        Log.i(TAG, "BOARD: |" + android.os.Build.BOARD + "|");
        Log.i(TAG, "BOOTLOADER: |" + android.os.Build.BOOTLOADER + "|");
        Log.i(TAG, "BRAND: |" + android.os.Build.BRAND + "|");
        Log.i(TAG, "CPU_ABI: |" + android.os.Build.CPU_ABI + "|");
        Log.i(TAG, "CPU_ABI2: |" + android.os.Build.CPU_ABI2 + "|");
        Log.i(TAG, "DEVICE: |" + android.os.Build.DEVICE + "|");
        Log.i(TAG, "DISPLAY: |" + android.os.Build.DISPLAY + "|");
        Log.i(TAG, "FINGERPRINT: |" + android.os.Build.FINGERPRINT + "|");
        Log.i(TAG, "HARDWARE: |" + android.os.Build.HARDWARE + "|");
        Log.i(TAG, "HOST: |" + android.os.Build.HOST + "|");
        Log.i(TAG, "ID: |" + android.os.Build.ID + "|");
        Log.i(TAG, "MANUFACTURER: |" + android.os.Build.MANUFACTURER + "|");
        Log.i(TAG, "MODEL: |" + android.os.Build.MODEL + "|");
        Log.i(TAG, "PRODUCT: |" + android.os.Build.PRODUCT + "|");
        Log.i(TAG, "RADIO: |" + android.os.Build.RADIO + "|");
        //Log.i(TAG, "SERIAL: |" + android.os.Build.SERIAL + "|");
        Log.i(TAG, "TAGS: |" + android.os.Build.TAGS + "|");
        Log.i(TAG, "TIME: |" + Long.toString(android.os.Build.TIME) + "|");
        Log.i(TAG, "TYPE: |" + android.os.Build.TYPE + "|");
        Log.i(TAG, "USER: |" + android.os.Build.USER + "|");
        
    }
    
    @Override
	protected void onStart() {
		super.onStart();
		registerReceiver(mHeadsetReceiver, new IntentFilter(Intent.ACTION_HEADSET_PLUG));
	}
    
    @Override
	protected void onResume() {
		super.onResume();
		updateButtonAndIndicatorState(mPrefs.getBoolean("recording", false));
	}
    
    @Override
	protected void onPause() {
		super.onPause();
		mPrefs.edit().putBoolean("recording", false).commit();
	}
    
    @Override
	protected void onStop() {
		super.onStop();
		unregisterReceiver(mHeadsetReceiver);
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int curvol = 30;

    	switch (keyCode) {
        case KeyEvent.KEYCODE_VOLUME_UP:
        	setMaxVol();
        	curvol = mPrefs.getInt("vol", 30);
        	mPrefs.edit().putInt("vol",getNextVolLevel(curvol, true)).commit();
            return true;
        case KeyEvent.KEYCODE_VOLUME_DOWN:
        	setMaxVol();
        	curvol = mPrefs.getInt("vol", 30);
        	mPrefs.edit().putInt("vol",getNextVolLevel(curvol, false)).commit();
            return true;
        default:
        	return super.onKeyDown(keyCode, event); 
        }
    }

    private void setMaxVol() {
        AudioManager am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int maxvol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		int currvol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		Log.d(TAG, "Curr volume " + Integer.toString(currvol) + " Max volume: " + Integer.toString(maxvol));
		if (currvol != maxvol) {
			am.setStreamVolume(AudioManager.STREAM_MUSIC, maxvol, 0);
		}
		Log.d(TAG, "Curr volume now " + Integer.toString(currvol) + " Max volume: " + Integer.toString(maxvol));
    }
    
    private int getNextVolLevel(int vol, boolean up) {
    	if (up)
    		vol = vol + 1;
    	else
    		vol = vol - 1;
    	if (vol == 101)
    		vol = 1;
    	if (vol == 0)
    		vol = 100;
    	return vol;
    }

	private void updateButtonAndIndicatorState(boolean isRecording) {
		if (isRecording) {
			mRadio10.setEnabled(false);
			mRadio25.setEnabled(false);
			mRadio50.setEnabled(false);
			mPeakText.setEnabled(false);
			mSplText.setEnabled(false);
			mButton.setText("Stop");
			mRecordingIndicatorImage.setImageResource(R.drawable.recording_indicator);
			((AnimationDrawable) mRecordingIndicatorImage.getDrawable()).start();
		} else {
			mRadio10.setEnabled(true);
			mRadio25.setEnabled(true);
			mRadio50.setEnabled(true);
			mPeakText.setEnabled(true);
			mSplText.setEnabled(true);
			mButton.setText("Start");
			mRecordingIndicatorImage.setImageResource(R.drawable.rec_ind_2_off);
		}
    }
	
	@Override
	public void onPeakUpdated(final int value) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mPeakText.setText(String.valueOf(value));
			}
		});
	}
	
	@Override
	public void onSplUpdated(final double value) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				DecimalFormat df = new DecimalFormat("#.");
				mSplText.setText(df.format(value));
			}
		});
	}
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			AudioBinder binder = (AudioBinder) service;
			mService = binder.getService();
			mService.setListener(MainActivity.this);
		}
	};
	
	BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			mMicStatusText.setTextColor(Color.LTGRAY);
			
			Log.i(TAG, "state = " + intent.getIntExtra("state", -1));
			Log.i(TAG, "microphone = " + intent.getIntExtra("microphone", -1));
			Toast.makeText(MainActivity.this, "Headset event:\nstate = " + intent.getIntExtra("state", -1) + "\nmicrophone = " + intent.getIntExtra("microphone", -1), Toast.LENGTH_LONG).show();
			
			if (intent.hasExtra("state")) {
				if (intent.getIntExtra("state", 0) == 1) {
					if (intent.getIntExtra("microphone", 1) == 1) {
						mMicStatusText.setText("Plugged in");
						mMicStatusText.setTextColor(Color.GREEN);
					} else if (intent.getIntExtra("microphone", 1) == 0) {
						mMicStatusText.setText("Mic not detected");
						mMicStatusText.setTextColor(Color.RED);
					} else {
						mMicStatusText.setText("Unexpected: microphone=" + intent.getIntExtra("microphone", 1));
					}
				} else if (intent.getIntExtra("state", 0) == 0) {
					mMicStatusText.setText("Not plugged in");
					mMicStatusText.setTextColor(Color.RED);
				} else {
					mMicStatusText.setText("Unexpected: state=" + intent.getIntExtra("state", 0));
				}
			} else {
				mMicStatusText.setText("Headset state unavailable");
			}
		}
	};
}
