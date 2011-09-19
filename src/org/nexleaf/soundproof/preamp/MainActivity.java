package org.nexleaf.soundproof.preamp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class MainActivity extends Activity {
	
    private static final String TAG = "MainActivity";
    
    SharedPreferences mPrefs;
    Button mButton;
    TextView mMicStatusText;
    ImageView mRecordingIndicatorImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mPrefs = getSharedPreferences("prefs", MODE_PRIVATE);
        
        mButton = (Button) findViewById(R.id.control_button);
        mMicStatusText = (TextView) findViewById(R.id.mic_status_text);
        mRecordingIndicatorImage = (ImageView) findViewById(R.id.recording_image);
        
        mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean recording = ! mPrefs.getBoolean("recording", false);
				mPrefs.edit().putBoolean("recording", recording).commit();
				updateButtonAndIndicatorState(recording);
				
				if (recording) {
					WakefulIntentService.sendWakefulWork(MainActivity.this, AudioService.class);
				}
			}
		});
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

	private void updateButtonAndIndicatorState(boolean isRecording) {
		if (isRecording) {
			mButton.setText("Stop");
			mRecordingIndicatorImage.setImageResource(R.drawable.recording_indicator);
			((AnimationDrawable) mRecordingIndicatorImage.getDrawable()).start();
		} else {
			mButton.setText("Start");
			mRecordingIndicatorImage.setImageResource(R.drawable.rec_ind_2_off);
		}
    }
	
	BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			mMicStatusText.setTextColor(Color.LTGRAY);
			
			Log.i("TAG", "state = " + intent.getIntExtra("state", -1));
			Log.i("TAG", "microphone = " + intent.getIntExtra("microphone", -1));
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