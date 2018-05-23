package com.wifitalk.phone;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

public class NetPhone implements Runnable {

	private final static int Sample_Rate = 8000;
	private final static int Channel_In_Configuration = AudioFormat.CHANNEL_IN_MONO;
	private final static int Channel_Out_Configuration = AudioFormat.CHANNEL_OUT_MONO;
	private final static int AudioEncoding = AudioFormat.ENCODING_PCM_16BIT;

	private AudioRecord phoneMIC;
	private AudioTrack phoneSPK;

	private boolean stoped = true;

	private CallLink curCallLink;

	private int recBufferSize;
	private int playBufferSize;

	private Thread inThread, outThread;

	public void startPhone(String inIP) throws Exception {
		initAudioHardware();

		initCallLink(inIP);

		stoped = false;

		outThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					startPhoneMIC();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		outThread.start();

		curCallLink.listen();
		inThread = new Thread(this);
		inThread.start();

	}

	private void initAudioHardware() throws Exception {
		recBufferSize = 4 * 1024;// 4k bytes
		playBufferSize = 4 * 1024;

		phoneMIC = new AudioRecord(MediaRecorder.AudioSource.MIC, Sample_Rate,
				Channel_In_Configuration, AudioEncoding, recBufferSize);
		phoneSPK = new AudioTrack(AudioManager.STREAM_MUSIC, Sample_Rate,
				Channel_Out_Configuration, AudioEncoding, playBufferSize,
				AudioTrack.MODE_STREAM);
		phoneSPK.setStereoVolume(0.7f, 0.7f);

	}

	private void initCallLink(String inIP) {
		curCallLink = new CallLink(inIP);
	}

	@Override
	public void run() {
		startPhoneSPK();
	}

	private void startPhoneMIC() throws Exception {
		curCallLink.open();
		phoneMIC.startRecording();
		while ((!Thread.interrupted()) && !stoped) {
			byte[] compressedVoice = new byte[recBufferSize];
			int b = phoneMIC.read(compressedVoice, 0, recBufferSize);
			curCallLink.getOutputStream().write(compressedVoice, 0, b);
		}

	}

	private void startPhoneSPK() {
		byte[] gsmdata = new byte[recBufferSize];
		int numBytesRead = 0;
		phoneSPK.play();
		try {

			while ((!Thread.interrupted()) && !stoped) {
				numBytesRead = curCallLink.getInputStream().read(gsmdata);
				if (numBytesRead == -1) {
					break;
				}
				phoneSPK.write(gsmdata, 0, numBytesRead);

			}
		} catch (Exception e) {
			Log.e("phone", e.getMessage(), e);
		}
	}

	public void stopPhone() throws Exception {
		stoped = true;
		while (inThread.isAlive() || outThread.isAlive()) {
			Thread.sleep(100);
		}
		phoneMIC.stop();
		phoneSPK.stop();
		curCallLink.close();
	}

}
