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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class AudioService extends WakefulIntentService {

	private static final String TAG = "AudioService";
	private static final int SAMPLE_RATE = 22050;
	private static final int CHANNEL_CONFIGURATION = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private static final double DEFAULT_INTERVAL = 0.1;
	
	public static final String EXTRA_INTERVAL = "interval";
	public static final String PEAK = "peak";
	public static final String SPL = "spl";
	
	private IListener mListener;

	public AudioService() {
		super(TAG);
	}
	
	public interface IListener {
		void onPeakUpdated(int value);
		void onSplUpdated(double value);
	}
	
	private final IBinder mBinder = new AudioBinder();
	
	public class AudioBinder extends Binder {
		AudioService getService() {
			return AudioService.this;
		}
	}
	
	@Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
	
	public void setListener(IListener listener) {
		mListener = listener;
	}

	@Override
	protected void doWakefulWork(Intent intent) {
		
		double interval = intent.getDoubleExtra(EXTRA_INTERVAL, DEFAULT_INTERVAL);
		
		Log.v(TAG, "Starting recording with interval " + interval);
		
		record16bit(this, interval);
	}

	
	private int pickSoundVol(int vol) {
		switch(vol) {
		
		case 1:
			return R.raw.signed_16bit_44100_400hz_01;
		case 2:
			return R.raw.signed_16bit_44100_400hz_02;
		case 3:
			return R.raw.signed_16bit_44100_400hz_03;
		case 4:
			return R.raw.signed_16bit_44100_400hz_04;
		case 5:
			return R.raw.signed_16bit_44100_400hz_05;
		case 6:
			return R.raw.signed_16bit_44100_400hz_06;
		case 7:
			return R.raw.signed_16bit_44100_400hz_07;
		case 8:
			return R.raw.signed_16bit_44100_400hz_08;
		case 9:
			return R.raw.signed_16bit_44100_400hz_09;
		case 10:
			return R.raw.signed_16bit_44100_400hz_10;
		case 11:
			return R.raw.signed_16bit_44100_400hz_11;
		case 12:
			return R.raw.signed_16bit_44100_400hz_12;
		case 13:
			return R.raw.signed_16bit_44100_400hz_13;
		case 14:
			return R.raw.signed_16bit_44100_400hz_14;
		case 15:
			return R.raw.signed_16bit_44100_400hz_15;
		case 16:
			return R.raw.signed_16bit_44100_400hz_16;
		case 17:
			return R.raw.signed_16bit_44100_400hz_17;
		case 18:
			return R.raw.signed_16bit_44100_400hz_18;
		case 19:
			return R.raw.signed_16bit_44100_400hz_19;
		case 20:
			return R.raw.signed_16bit_44100_400hz_20;
		case 21:
			return R.raw.signed_16bit_44100_400hz_21;
		case 22:
			return R.raw.signed_16bit_44100_400hz_22;
		case 23:
			return R.raw.signed_16bit_44100_400hz_23;
		case 24:
			return R.raw.signed_16bit_44100_400hz_24;
		case 25:
			return R.raw.signed_16bit_44100_400hz_25;
		case 26:
			return R.raw.signed_16bit_44100_400hz_26;
		case 27:
			return R.raw.signed_16bit_44100_400hz_27;
		case 28:
			return R.raw.signed_16bit_44100_400hz_28;
		case 29:
			return R.raw.signed_16bit_44100_400hz_29;
		case 30:
			return R.raw.signed_16bit_44100_400hz_30;
		case 31:
			return R.raw.signed_16bit_44100_400hz_31;
		case 32:
			return R.raw.signed_16bit_44100_400hz_32;
		case 33:
			return R.raw.signed_16bit_44100_400hz_33;
		case 34:
			return R.raw.signed_16bit_44100_400hz_34;
		case 35:
			return R.raw.signed_16bit_44100_400hz_35;
		case 36:
			return R.raw.signed_16bit_44100_400hz_36;
		case 37:
			return R.raw.signed_16bit_44100_400hz_37;
		case 38:
			return R.raw.signed_16bit_44100_400hz_38;
		case 39:
			return R.raw.signed_16bit_44100_400hz_39;
		case 40:
			return R.raw.signed_16bit_44100_400hz_40;
		default:
			return R.raw.signed_16bit_44100_400hz_30;		
		}
		
	}

	private AudioTrack playbackFile(Context context, int vol) {
		
		byte[] byteoutbuffer = new byte[4410*2];
		int audioFileBytesIn = 0;
		try {
			audioFileBytesIn = getResources().openRawResource(pickSoundVol(vol)).read(byteoutbuffer);
			audioFileBytesIn += getResources().openRawResource(pickSoundVol(vol)).read(byteoutbuffer, audioFileBytesIn, 4410);
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		short[] outbuffer = new short[audioFileBytesIn/2];
		ByteBuffer.wrap(byteoutbuffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(outbuffer);
				
		int playMinBufferSizeInBytes = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIGURATION, AUDIO_ENCODING);

		Log.d(TAG, "-- Play min buf size: " + playMinBufferSizeInBytes + " -- Read in file: " + audioFileBytesIn);
		
		AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, CHANNEL_CONFIGURATION, AUDIO_ENCODING, audioFileBytesIn, AudioTrack.MODE_STATIC);
		track.write(outbuffer, 0, audioFileBytesIn/2);
		track.setLoopPoints(0, audioFileBytesIn / 2, -1);
		Log.e(TAG, "Native sample rate: " + AudioTrack.getNativeOutputSampleRate(AudioTrack.MODE_STATIC));
		track.play();
		
		return track;
		
	}
	
	
	private void record16bit(Context context, double interval) {
			SharedPreferences prefs = context.getSharedPreferences("prefs", MODE_PRIVATE);
			
			int vol = prefs.getInt("vol", 30);
			int volnew = vol;
			
			AudioTrack track = playbackFile(context, vol);
			
			int minBufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIGURATION, AUDIO_ENCODING);
			int bufferSize = (int) (SAMPLE_RATE * interval);
			int bufferSizeInBytes = bufferSize * 2;
			
			Log.d(TAG, "Record min buf size: " + minBufferSizeInBytes + " -- Actual buff size: " + bufferSizeInBytes);
			
			if (bufferSizeInBytes < minBufferSizeInBytes) {
				Log.e(TAG, "Requested buffer size (" + bufferSizeInBytes + ") is smaller than minimum allowed buffer (" + minBufferSizeInBytes + ")");
				Toast.makeText(context, "Requested buffer size (" + bufferSizeInBytes + ") is smaller than minimum allowed buffer (" + minBufferSizeInBytes + ")", Toast.LENGTH_LONG).show();
			} else {
				short[] buffer = new short[bufferSize];

				AudioRecord audioRecord = new AudioRecord(
						MediaRecorder.AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIGURATION, AUDIO_ENCODING, bufferSizeInBytes);	
				audioRecord.startRecording();
								
				while (prefs.getBoolean("recording", false)) {
					int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
					
					if (bufferReadResult > 0) {
						doProcessing(buffer, bufferSize, prefs);
					} else {
						Log.e(TAG, "AudioRecord.read() returned " + bufferReadResult);
					}
					volnew = prefs.getInt("vol", 30);
					if (volnew != vol) {
						vol = volnew;
						track.stop();
						track = playbackFile(context, vol);
					}
				}
				
				audioRecord.stop();
				track.stop();
			}
	}
	
	private void doProcessing(short [] data, int dataSize, SharedPreferences prefs) {
	
		int avgPeak = doPeakFinder(data, dataSize);
		mListener.onPeakUpdated(avgPeak);
		mListener.onSplUpdated((double) prefs.getInt("vol", 30));
		//mListener.onSplUpdated(doTempCalculation(avgPeak));				
		//mListener.onSplUpdated(doSPL(data, dataSize));		
	}
	
	private double doTempCalculation(double avgPeak) {
		double res1 = 10000;
		double res2 = 0;
		double inputpcm = 34362;
		double ohms_d = ((res1*inputpcm)/avgPeak) - (res1 + res2);


		// A, B, and C are the thermistor parameters for our particular thermistor:
		// http://mcshaneinc.com/html/TS165_Specs.html
		double trA = 0.000382031593048618;
		double trB = 0.000231333043863669;
		double trC = 0.0000000449148;

		// we need ln(ohms) and ln^3(ohms)
		double lnohms = Math.log(ohms_d);
		double lnohms_cubed = Math.pow(lnohms, 3);
		
		// convert the ohms into temperature using thermistor equation
		// T = (1/(A + B*ln(ohms) + C*ln^3(ohms)) - 273.15
		double temp_K = 0; // Kelvin
		double temp_C = 0; // Celcius

		//temp_K = FDIV(FASSIGN_INT(1),FADD(trA, FADD(FMUL(trB, lnohms), FMUL(trC, lnohms_cubed))));
		temp_K =  1.0/(trA + trB * lnohms + trC * lnohms_cubed);
		
		temp_C = temp_K - 273.15;
		//FLOATTOWSTR(temp_C, strout, 64);
		//DBGPRINTF("temp_c is %S", strout);

		return temp_C;		
		
	}
	
	private double doSPL(short [] data, int dataSize) {
		
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
		final double [] b = {0.44929985, -0.89859970, -0.44929985, 1.79719940, -0.44929985, -0.89859970, 0.44929985};
		final double [] a = {1.00000000, -3.22907881, 3.35449488, -0.73178437, -0.62716276, 0.17721420, 0.05631717};
		// For 16kHz data
		//const TReal64 b[] = {0.53148983, -1.06297966, -0.53148983,  2.12595932, -0.53148983, -1.06297966,  0.53148983};
		//const TReal64 a[] = {1.0, -2.86783257,  2.22114441,  0.45526833, -0.98338686, 0.05592994,  0.1188781};
		
		final int bsize = 7;
		final int asize = 7;	
		
		// The integer data from the sound device
//		TInt16* dataarr = (TInt16 *) iStreamBuffer.Ptr();
		
		// The temporary location of the filtered data ... zero it out first
//		ArrayList<Double> filtered = new ArrayList<Double>();
		double [] filtered = new double [dataSize + 8];
//		iFilterBuffer.FillZ((4096/2) * iFrameCount * sizeof(TReal64));
//		TReal64* filtered = (TReal64 *) iFilterBuffer.Ptr();
		
		double finalanswer = 0.0;
		double printanswer = 0.0;
		int i = 0;
		int j = 0;
		int k = 0;
//		int numsamps = (iFrameSize/2) * iFrameCount;
		double myzero = 0.0;
		double divmeby = (dataSize + myzero);
		
		// Filter
		for (i = 0; i < dataSize; i++) {
			for (j = i, k = 0; (j >= 0 && k < bsize); j--, k++) {
				filtered[i] += b[k] * (data[j] + myzero);
			}
			for (j = i-1, k = 1; (j >= 0 && k < asize); j--, k++) {
				filtered[i] -= a[k] * filtered[j];
			}
		}
		
		// LEQ calculation approximatly from here:
		// http://digital.ni.com/public.nsf/allkb/FCE0EC0A6B193A028625722E006DE298
		//
		// square, sum, divide all in double
		for (i = 0; i < dataSize; i++) {
			finalanswer = finalanswer + (((filtered[i] * filtered[i]))); // divmeby);
		}
		
		finalanswer = finalanswer / divmeby;
		
		
		if (finalanswer != 0.0 && !Double.isNaN(finalanswer) && !Double.isInfinite(finalanswer)) {
			printanswer = Math.log10(finalanswer);
		}
		
		return printanswer * 10.0;
	}
	
	
	private int doPeakFinder(short [] data, int dataSize) {
	
		 // all three queues/lists store the indecies, not values
		ArrayList<Integer> peak_list = new ArrayList<Integer>();
		int peak_size = 10;
		int up_in_row_last_idx = 0;
		int up_in_row = 0;
		int down_in_row_last_idx = 0;
		int down_in_row = 0;
		
		for (int i = 1; i < dataSize; i++) {
			 
			if (data[i] >= data[i-1]) {
				
				if (up_in_row == peak_size) {
					// do nothing
				} else {
					up_in_row += 1;
				}
				
				up_in_row_last_idx = i;
				down_in_row = 0;
				
			} else {
				if (down_in_row == peak_size) {		// have seen at least peak_size up and peak_size down, so store and restart!
					
					peak_list.add(up_in_row_last_idx);
					down_in_row = 0;
					up_in_row = 0;
					
				} else { // going down but not seen peak_size yet
					
					if (up_in_row != peak_size) {
					//restart! since going down after not peak_size up
				 	   	up_in_row = 0;
				 	   	down_in_row = 0;
				 	   	
					} else {
						down_in_row += 1;
					}
				}
			}
		}

		if (peak_list.size() == 0) {
			return 0;
		}
		
		double avg_peak = 0;
		
		for (int i = 0; i < peak_list.size(); i++) {
			avg_peak += data[peak_list.get(i)];
		}
		
		avg_peak = avg_peak / peak_list.size();
		 
		// avg_peak is the answer!
		
		return (int) avg_peak;
	}
}
