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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;

public class SRVoice implements SRVoiceInterface {
    public static final int RECORDER_MODE = 1;
    public static final int PLAYER_MODE = 2;
    public static final int SEARCH_MODE = 3;
    private int mMode = RECORDER_MODE;
    private int mPrevMode = RECORDER_MODE;
    
    private SRTag mTag;
    public SRShare mShare;
    
    private Context mContext;
    
    private String mTitle;
    private String mVoiceFilePath;
    private String mDocFilePath;
    
    boolean isRecorder = false;
    //private MediaRecorder mRecorder = null;
	private int currentFormat = 0;
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	
	private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
	private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
    
	private SRDataSource mDataSource;
	private SRVoiceDb mVoiceDb;
	private ArrayList<SRTagDb> mTagList = new ArrayList<SRTagDb>();


	
	private long mRecordStartTime;
	private Handler mHandler = new Handler();
	private Timer mTimer;
	private class TimeTimerTask extends TimerTask {
        
        @Override
        public void run() {
            notifyTimeObservers(getCurrentRecordTime());
        }
    };
	
	@Override
    public void initialize(Context context) {
	    mContext = context;
	    
        //DB Open
	    mDataSource = new SRDataSource(context);
	    mDataSource.open();
    }
	
    @Override
    public void finalize() {
        // TODO: DB Close
        if (mDataSource != null) {
            mDataSource.close();
        }
        
        // Timer
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
    
    @Override
    public int getMode() {
        return mMode;
    }
    
    @Override
    public void setMode(int mode) {
        mPrevMode = mMode;
        mMode = mode;
    }
    
    @Override
    public int getPrevMode() {
        return mPrevMode;
    }

    @Override
    public long getCurrentRecordTime() {
        return System.currentTimeMillis() - mRecordStartTime;
    }
 
    public ArrayList<SRTagDb> getmTagList() {
		return mTagList;
	}

	public void setmTagList(ArrayList<SRTagDb> mTagList) {
		this.mTagList = mTagList;
	}

    public void recordStart() {
    	SRDebugUtil.SRLog("recordStart -> isRecorder = " + isRecorder);
    	Intent recorderIntent = new Intent("com.unus.smartrecorder.Recorder");
    	recorderIntent.putExtra(SRConfig.VOICE_PATH_KEY, mVoiceFilePath);
    	mContext.startService(recorderIntent);
    	mRecordStartTime = System.currentTimeMillis();
    	mTimer = new Timer();
    	mTimer.schedule(new TimeTimerTask(), 1000, 1000);
    	
    	// Add Voice ddd
    	mVoiceDb = mDataSource.createVoice(mVoiceFilePath, mDocFilePath);
    	addTag(SRDbHelper.TEXT_TAG_TYPE, "START TAG", "0");
    	//mView.setTagList();
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


    public void recordStop() {
    	mContext.stopService(new Intent("com.unus.smartrecorder.Recorder"));
//    	SRVoiceView.mBtnRecorder.setText("recorder");
//		isRecorder = false;
//		mRecorder.stop();
//		mRecorder.release();
//		mRecorder = null;
    	
    	mTimer.cancel();
    	mTimer = null;
    	mVoiceDb = null;
    }

    public void play(long voiceId, int position) {
        MediaPlayer player;
        String filePath = "/sdcard/aaa.mp4";
                
        player = new MediaPlayer();
 
        SRDebugUtil.SRLog("play(): filePath = " + filePath + " pos = " + Integer.toString(position));
        
        try {
            player.setDataSource(filePath);
            player.prepare();
            player.seekTo(position);
            player.start();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }
    
    public void play() {
    	SRDebugUtil.SRLog("play -> play = ");
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
	    SRDebugUtil.SRLog("filename = " +filename);
    	
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

    public void getDuration() {

    }

    public void share() {

    }
    
    @Override
    public String makeDefaultTitle() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        
        return new String("Audio" + "-" + sdf.format(new Date()));
    }

    @Override
    public void setTitle(String title) {
        SRDebugUtil.SRLog("setTitle(): " + title);
        mTitle = title;
        
        mVoiceFilePath = String.format("%s/%s/%s.mp4", 
                Environment.getExternalStorageDirectory().getPath(),
                SRConfig.AUDIO_RECORDER_FOLDER, mTitle);
        SRDebugUtil.SRLog("VoiceFilePath: " + mVoiceFilePath);
    }
    
    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public void setDocFilePath(String filePath) {
        SRDebugUtil.SRLog("setDocFilePath(): " + filePath);
        
        mDocFilePath = filePath;
    }
    
    @Override
    public void addTag(int type, String data, String position) {
        if (mDataSource == null || mVoiceDb == null) {
            SRDebugUtil.SRLogError("addTag(): DB is null");
            return;
        }
        
        SRDebugUtil.SRLog("addTag(): " + Integer.toString(type) + " [" + data + "] " +position);
        SRTagDb tag = mDataSource.createTag(mVoiceDb.getVoice_id(), type, data, position);
        mTagList.add(tag);
        notifyTagsObservers(tag);
    }

    public interface SRVoiceObserver {
        public void updateTags(SRTagDb tag);
        public void updateTime(long time);
    }
    
    ArrayList<SRVoiceObserver> mSRVoiceObserver = new ArrayList<SRVoiceObserver>();
    public void registerObserver(SRVoiceObserver observer) {
        mSRVoiceObserver.add(observer);
    }
        
    public void notifyTagsObservers(SRTagDb tag) {
        for (int i = 0; i < mSRVoiceObserver.size(); i++) {
            SRVoiceObserver observer = mSRVoiceObserver.get(i);
            observer.updateTags(tag);
        }
    }
    
    public void notifyTimeObservers(long time) {
        for (int i = 0; i < mSRVoiceObserver.size(); i++) {
            SRVoiceObserver observer = mSRVoiceObserver.get(i);
            observer.updateTime(time);
        }
    }    
    
    public void removeObserver(SRVoiceObserver o) {
        int i = mSRVoiceObserver.indexOf(o);
        if (i >= 0) {
            mSRVoiceObserver.remove(i);
        }
    }
}
