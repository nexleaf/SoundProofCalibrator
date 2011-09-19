package org.nexleaf.soundproof.preamp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class AudioService extends WakefulIntentService {

	private static final String TAG = "AudioService";
	private static final int SAMPLE_RATE = 22050;
	private static final int CHANNEL_CONFIGURATION = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static final double DEFAULT_INTERVAL = 0.1;

	public AudioService() {
		super(TAG);
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		
		double interval = intent.getDoubleExtra("interval", DEFAULT_INTERVAL);
		
		record16bit(this, interval);
	}

//	TODO: The read call needs to go in a timer-called function. Can't hold up this
	//		service every time a file is being written to, makes this call blocking. 
	//		but the buffered output stream should be pretty good. Test to see if i still
	//		get the buffer overflow if im just copying the data into another buffer. cuz
	//		even a non-blocking call will have to do that. unless i alternate buffers i guess.
	
	// We implement a separate function for recording 16 bit, because all data structures below assume
	//	16 bit, so use shorts, instead of bytes.
	// If duration is set to -1, then record continously
	private void record16bit(Context context, double interval) {
				
			int minBufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIGURATION, AUDIO_ENCODING);
			int bufferSize = (int) (SAMPLE_RATE * interval);
			int bufferSizeInBytes = bufferSize * 2;
			
			if (bufferSizeInBytes < minBufferSizeInBytes) {
				Log.e(TAG, "Requested buffer size (" + bufferSizeInBytes + ") is smaller than minimum allowed buffer (" + minBufferSizeInBytes + ")");
				Toast.makeText(context, "Requested buffer size (" + bufferSizeInBytes + ") is smaller than minimum allowed buffer (" + minBufferSizeInBytes + ")", Toast.LENGTH_LONG).show();
			} else {
				short[] buffer = new short[bufferSize];
				
				AudioRecord audioRecord = new AudioRecord(
						MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIGURATION, AUDIO_ENCODING, bufferSizeInBytes);	
				audioRecord.startRecording();
				
				SharedPreferences prefs = context.getSharedPreferences("prefs", MODE_PRIVATE); 
				
				while (prefs.getBoolean("recording", false)) {
					int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
					
					if (bufferReadResult > 0) {
						doProcessing(buffer, bufferSize);
					} else {
						Log.e(TAG, "AudioRecord.read() returned " + bufferReadResult);
					}
				}
				
				audioRecord.stop();
			}
	}
	
	private void doProcessing(short [] data, int dataSize) {
		short max = 0;
		
		for (int i = 0; i < dataSize; i++) {
			if (data[i] > max) {
				max = data[i];
			}
		}
		
		Log.v(TAG, "" + max);
	}
}
