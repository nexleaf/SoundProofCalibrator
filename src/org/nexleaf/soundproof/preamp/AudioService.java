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
	
	
	private double doSPL(short [] buf, int dataSize) {
		
		/******************************
		 * 	SPL CODE EXAMPLE!!! (Also called A-weighted LEQ)
		 * 
		 * 
	// values for A-weighting filter from here:
	// http://www.mathworks.com/matlabcentral/fileexchange/21384-continuous-sound-and-vibration-analysis
	//
	// Should we do c-weighted as well?
	//
	// For 48kHz data
	//const TReal64 b[] = {0.23430179, -0.46860358, -0.23430179,  0.93720717, -0.23430179, -0.46860358,  0.23430179};
	//const TReal64 a[] = {1.0, -4.11304341,  6.55312175, -4.99084929,  1.7857373 , -0.2461906 ,  0.01122425};
	// For 32kHz data
	//const TReal64 b[] = {0.34345834, -0.68691668, -0.34345834,  1.37383335, -0.34345834, -0.68691668,  0.34345834};
	//const TReal64 a[] = {1.0, -3.65644604,  4.83146845, -2.5575975,  0.25336804, 0.12244303,  0.00676407};
	// For 22.05kHz data
	const TReal64 b[] = {0.44929985, -0.89859970, -0.44929985, 1.79719940, -0.44929985, -0.89859970, 0.44929985};
	const TReal64 a[] = {1.00000000, -3.22907881, 3.35449488, -0.73178437, -0.62716276, 0.17721420, 0.05631717};
	// For 16kHz data
	//const TReal64 b[] = {0.53148983, -1.06297966, -0.53148983,  2.12595932, -0.53148983, -1.06297966,  0.53148983};
	//const TReal64 a[] = {1.0, -2.86783257,  2.22114441,  0.45526833, -0.98338686, 0.05592994,  0.1188781};
	TInt bsize = 7;
	TInt asize = 7;	
	
	// The integer data from the sound device
	TInt16* dataarr = (TInt16 *) iStreamBuffer.Ptr();
	
	// The temporary location of the filtered data ... zero it out first
	iFilterBuffer.FillZ((4096/2) * iFrameCount * sizeof(TReal64));
	TReal64* filtered = (TReal64 *) iFilterBuffer.Ptr();
	
	TReal64 finalanswer = 0.0;
	TReal64 printanswer = 0.0;
	TInt i = 0;
	TInt j = 0;
	TInt k = 0;
	TInt numsamps = (iFrameSize/2) * iFrameCount;
	TReal64 myzero = 0.0;
	TReal64 divmeby = (numsamps + myzero);
	
	// Filter
	for (i = 0; i < numsamps; i++) {
		for (j = i, k = 0; (j >= 0 && k < bsize); j--, k++) {
			filtered[i] += b[k] * (dataarr[j] + myzero);
		}
		for (j = i-1, k = 1; (j >= 0 && k < asize); j--, k++) {
			filtered[i] -= a[k] * filtered[j];
		}
	}

	// LEQ calculation approximatly from here:
	// http://digital.ni.com/public.nsf/allkb/FCE0EC0A6B193A028625722E006DE298
	//
	// square, sum, divide all in double
	for (i = 0; i < numsamps; i++) {
		finalanswer = finalanswer + (((filtered[i] * filtered[i]))); // divmeby);
	}

	finalanswer = finalanswer / divmeby;
	
	if (!Math::IsZero(finalanswer) && !Math::IsNaN(finalanswer) && !Math::IsInfinite(finalanswer)) {
		Math::Log(printanswer, finalanswer);
	}
	myzero = 10.0;
	iCurrSPL = printanswer * myzero;
	
	}
		// END SPL CODE
		 ******************/
		return 0.0;
	}
	
	
	private double doPeakFinder(short [] buf, int dataSize) {
		
		/***********************
		 * 
		 * Idea for peak finder
		 
		 // all three queues/lists store the indecies, not values
		 peak_list = array to append to;
		 peak_size = 10;
		 up_in_row_last_idx = 0;
		 up_in_row = 0;
		 down_in_row_last_idx = 0;
		 down_in_row = 0;
		 
		 for (i = 1; i < databufsize; i++) {
		 
	 
	 	
		 if (buf[i] >= buf[i-1])
		 { // if going up or equal

			if (up_in_row == peak_size)
			{
				// do nothing
			} 
			else
			{
				up_in_row += 1;
			}
			up_in_row_last_idx = i;
		 	down_in_row = 0;
		 } 
		 else
		 { // if going down
		 
			if (down_in_row == peak_size) 		// have seen at least peak_size up and peak_size down, so store and restart!
			{
		
				peak_list.append(up_in_row_last_idx);
				down_in_row = 0;
				up_in_row = 0;
				
			} 
			else // going down but not seen peak_size yet
			{
		 	   if (up_in_row != peak_size) {
		 	   		// restart! since going down after not peak_size up
		 	   		up_in_row = 0;
		 	   		down_in_row = 0;
		 	   } else {

		 	   	   down_in_row += 1
		 	   }
		 	}
		}	 
		 
		 double avg_peak = 0;
		 for (int i = 0; i < peak_list.lengt())
		 {
		 	avg_peak += buf[peak_list[i]];
		 }
		 avg_peak = avg_peak / peak_list.length();
		 
		 // avg_peak is the answer!
		 
		 * 
		 * 
		 * 
		 */
		return 0.0;
	}
		
		
		
	
}
