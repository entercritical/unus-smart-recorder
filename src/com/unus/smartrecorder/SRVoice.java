//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : SRVoice.java
//  @ Date : 2013-05-30
//  @ Author : 
//
//
package com.unus.smartrecorder;

import java.io.File;
import java.io.IOException;



import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.sax.StartElementListener;
import android.util.Log;

public class SRVoice {
    private SRTag mTag;
    private SRDoc mDoc;
    public SRDbHandler mDB;
    public SRShare mShare;
    
    boolean isRecorder = false;
    //private MediaRecorder mRecorder = null;
	private int currentFormat = 0;
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
    
    /**
     * 음성녹음 파일과 Tag DB 읽는다
     * 
     * Tag DB는 음성녹음 파일 이름을 Key로 가진다
     * 
     * @param filePath
     **/
    public void open(String filePath) {

    }

    public void getDoc() {

    }

    public void setDoc(SRDoc doc) {

    }

    public void recordStart(Context mContext) {
    	DebugUtil.SRLog("recordStart -> isRecorder = " + isRecorder);
    	mContext.startService(new Intent("com.unus.smartrecorder.Recorder"));

    }
    /*
     *  recorder error handling
     */
//    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
//        @Override
//        public void onError(MediaRecorder mr, int what, int extra) {     
//        	DebugUtil.SRLog("Error = " +what);
//        }
//    };
//    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
//        @Override
//        public void onInfo(MediaRecorder mr, int what, int extra) {
//        	DebugUtil.SRLog("Error = " +what);
//        }
//    };


    public void recordStop(Context mContext) {
    	mContext.stopService(new Intent("com.unus.smartrecorder.Recorder"));
//    	SRVoiceView.mBtnRecorder.setText("recorder");
//		isRecorder = false;
//		mRecorder.stop();
//		mRecorder.release();
//		mRecorder = null;
    }

    public void save() {

    }

    public void play() {
    	DebugUtil.SRLog("play -> play = ");
    	MediaPlayer player;
    	player = new MediaPlayer();
//    	player = MediaPlayer.create(this, R.raw.man);
    	

		String filepath = Environment.getExternalStorageDirectory().getPath();
		
    	File file = new File(filepath, "BondRecorder");
	    if (!file.exists()) {
	        file.mkdirs();
	    }
	    //String filename = file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat];
	    String filename = file.getAbsolutePath() + "/" + "test" + file_exts[currentFormat];
	    DebugUtil.SRLog("filename = " +filename);
    	
	    try {
			player.setDataSource(filename);
			player.prepare();
			//player.seekTo(1);
			player.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    public void seekTo(int position) {

    }

    public void playPause() {

    }

    public void playStop() {

    }

    public void getCurrentPosition() {

    }

    public void getDuration() {

    }

    public void saveBasicInfo(Object voiceFilePath, Object docFilePath) {

    }

    public void share() {

    }
}
