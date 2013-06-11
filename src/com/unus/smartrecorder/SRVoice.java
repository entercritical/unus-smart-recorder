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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class SRVoice implements SRVoiceInterface, OnCompletionListener {
    public static final int RECORDER_MODE = 1;
    public static final int PLAYER_MODE = 2;
    public static final int SEARCH_MODE = 3;
    
    public static final int PLAYER_PLAY_STATE = 1;
    public static final int PLAYER_STOP_STATE = 2;      // 재생중에 사용자가 버튼을 눌러 종료 
    public static final int PLAYER_COMPLETE_STATE = 3;  // 재생이 끝까지 되서 종료 
    public static final int PLAYER_PAUSE_STATE = 4;
    
    public static final int UPDATE_PLAYER_TIMER = 1;
    public static final int UPDATE_RECORDER_TIMER = 2;
    
    public static final int RECORDER_TIMER_PERIOD = 1000;
    public static final int PLAYER_TIMER_PERIOD = 100;
    
    private int mMode = 0;
    private int mPrevMode = 0;
    
    public SRShare mShare;
    
    private Context mContext;
    
    private String mTitle;
    private String mVoiceFilePath;
    private String mDocFilePath;
    
    private SRRecorderService mSRRecorderService;
    private boolean mIsSRRecorderServiceBound = false;
    
    public static int JUMP_TIME = 5000; // 녹음중에 사용자가 음성을 점프할때의 시간 (ms)
    
    public boolean isRecordering = false;
    //private MediaRecorder mRecorder = null;
	//private int currentFormat = 0;
	private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
	private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
	
	//private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP };
	//private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
    
	private SRDataSource mDataSource;
	private SRVoiceDb mVoiceDb;
	private ArrayList<SRTagDb> mTagList = new ArrayList<SRTagDb>();
	private SRTagDb mTempTagForDelete = new SRTagDb();
	private SRVoiceDb mTempVoiceForDelete = new SRVoiceDb();
	private MediaPlayer mPlayer;
	private int mTagNumbering = 0;
	private int mPlayerState;
	
	private boolean mIsAutoTag = true;
	
	private long mVoiceId;
	
	ArrayList<SRTagDb> mDocPageTagList;
	
	private long mRecordStartTime;
	
	private int mPlayerPausePosition;

	private static final int MSG_UPDATE_DOC_PAGE_START = 1; // SeekBar, FF, Rewind로 이동할 경우 페이지 이동 간격을 조정할 필요가 있어 추가
	private static final int MSG_UPDATE_DOC_PAGE = 2;
	private Timer mTimer;
	private int mDocPageTagListIdx, mDocPageAdjustTime;
	
	public int getmTagNumbering() {
		return mTagNumbering;
	}

	public void setmTagNumbering(int mTagNumbering) {
		this.mTagNumbering = mTagNumbering;
	}

	public SRTagDb getmTempTagForDelete() {
		return mTempTagForDelete;
	}

	public void setmTempTagForDelete(SRTagDb mTempTagForDelete) {
		this.mTempTagForDelete = mTempTagForDelete;
	}
	
	public SRVoiceDb getmTempVoiceForDelete() {
		return this.mTempVoiceForDelete;
	}
	
	public void setmTempVoiceForDelete(SRVoiceDb mTempVoiceForDelete) {
		this.mTempVoiceForDelete = mTempVoiceForDelete;
	}
	
    private static class UpdatePageHandler extends Handler {
        WeakReference<SRVoice> mRef;

        UpdatePageHandler(SRVoice voice) {
            mRef = new WeakReference<SRVoice>(voice);
        }

        @Override
        public void handleMessage(Message msg) {
            SRVoice voice = mRef.get();
            switch (msg.what) {
            case MSG_UPDATE_DOC_PAGE_START: // for adjust time
                voice.notifyPageObservers(msg.obj != null ? (Integer)msg.obj : 0);    
                
                voice.sendUpdateDocPageMsg(voice.mDocPageAdjustTime);
                break;
            case MSG_UPDATE_DOC_PAGE:
                voice.notifyPageObservers((Integer)msg.obj);  
                
                voice.sendUpdateDocPageMsg(0);            
                break;
            }
        }
    }
    private UpdatePageHandler mHandler = new UpdatePageHandler(this);

	/*
	private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_UPDATE_DOC_PAGE_START: // for adjust time
                notifyPageObservers(msg.obj != null ? (Integer)msg.obj : 0);    
                
                sendUpdateDocPageMsg(mDocPageAdjustTime);
                break;
            case MSG_UPDATE_DOC_PAGE:
                notifyPageObservers((Integer)msg.obj);  
                
                sendUpdateDocPageMsg(0);            
                break;
            }
        }
	    
	};
	*/
	
	private void sendUpdateDocPageMsg(int adjustTime) {
        if (mDocPageTagListIdx < mDocPageTagList.size()) {
            SRTagDb tag = mDocPageTagList.get(mDocPageTagListIdx);
            int tagTime = Integer.parseInt(tag.getTag_time());
            int page = Integer.parseInt(tag.getContent());

            SRDebugUtil.SRLog("sendMessage: idx=" + mDocPageTagListIdx
                    + " page = " + page + " tagTime = " + tagTime);

            Message m = new Message();
            m.what = MSG_UPDATE_DOC_PAGE;
            m.obj = Integer.valueOf(page);

            if (mDocPageTagListIdx == 0) {
                mHandler.sendMessageDelayed(m, tagTime - adjustTime);
            } else {
                SRTagDb prevTag = mDocPageTagList.get(mDocPageTagListIdx - 1);
                int prevTagTime = Integer.parseInt(prevTag.getTag_time());

                mHandler.sendMessageDelayed(m, tagTime - prevTagTime - adjustTime);
            }
            mDocPageTagListIdx++;
        }
	}

    private void stopUpdateDocPage() {
        if (mHandler != null) {
            mHandler.removeMessages(MSG_UPDATE_DOC_PAGE_START);
            mHandler.removeMessages(MSG_UPDATE_DOC_PAGE);
        }           
    }
    
    private void startUpdateDocPage(int startSeekTime) {
        if (startSeekTime == 0) {
            mDocPageTagListIdx = 0;
            mDocPageAdjustTime = 0;
            
            mHandler.sendEmptyMessage(MSG_UPDATE_DOC_PAGE_START);
        } else {
            if (mDocPageTagList != null && mDocPageTagList.size() > 0) {
                int i;
                Message m = new Message();
                for (i = mDocPageTagList.size() - 1; i >= 0; i--) {
                    SRTagDb pageTag = mDocPageTagList.get(i);
                    int pageTagTime = Integer.parseInt(pageTag.getTag_time());
                    if (pageTagTime <= startSeekTime) {
                        mDocPageAdjustTime = startSeekTime - pageTagTime;
                        
                        m.what = MSG_UPDATE_DOC_PAGE_START;
                        m.obj = Integer.parseInt(pageTag.getContent());
                            
                        mDocPageTagListIdx = i + 1;
                        mHandler.sendMessage(m);
                        break;
                    }
                }
                if (i < 0) {
                    m.what = MSG_UPDATE_DOC_PAGE_START;
                    m.obj = 0;
                        
                    mDocPageTagListIdx = 0;
                    mDocPageAdjustTime = startSeekTime;
                            
                    mHandler.sendMessage(m);
                }
            }                
        }
    }    
 	
	private class TimeTimerTask extends TimerTask {
        
        @Override
        public void run() {
            int time = getCurrentTime();
            notifyTimeObservers(time);
        }
    };
    
    private int getCurrentTime() {
        if (mMode == RECORDER_MODE) {
            return getCurrentRecordTime();
        } else if (mMode == PLAYER_MODE) {
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }
    
    private void setTimeTimer(int msec) {
        SRDebugUtil.SRLog("setTimeTimer() : " + msec);
        if (mTimer != null) {
            mTimer.cancel();
        }
        
        mTimer = new Timer();
        mTimer.schedule(new TimeTimerTask(), 0, msec);
    }
    
    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
	
    @Override
    public boolean isRecordering() {
        return isRecordering;
    }

    public void setRecordering(boolean isRecordering) {
        this.isRecordering = isRecordering;
    }
    
	@Override
    public void initialize(Context context) {
	    mContext = context;
	    
        //DB Open
	    mDataSource = new SRDataSource(context);
	    mDataSource.open();
	    
	    mPlayer = new MediaPlayer();
	    mPlayer.setOnCompletionListener(this);
    }
	
    @Override
    public void finalize() {
        // TODO: DB Close
        if (mDataSource != null) {
            mDataSource.close();
        }
        
        // Timer
        stopTimer();
        
        // remove Update Page Msg
        stopUpdateDocPage();
        
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
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
        
        if (mMode == PLAYER_MODE) {
            //mPlayerState = PLAYER_STOP_STATE;
            notifyPlayerBtnStateObservers(mPlayerState);
        } else if (mMode == RECORDER_MODE) {
            notifyRecorderBtnStateObservers(isRecordering());
        } else if (mMode == SEARCH_MODE) {
            
        }
    }
    
    @Override
    public int getPrevMode() {
        return mPrevMode;
    }

    @Override
    public int getCurrentRecordTime() {
        return (int)(System.currentTimeMillis() - mRecordStartTime);
    }
    
    @Override
    public int getCurrentPlayTime() {
        return mPlayer.getCurrentPosition();
    }

    public void recordStart() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            playStop();
        }
        
    	SRDebugUtil.SRLog("SRVoice.recordStart() : voice = " + mVoiceFilePath + " doc = " + mDocFilePath);
    	
//    	Intent recorderIntent = new Intent("com.unus.smartrecorder.Recorder");
//    	recorderIntent.putExtra(SRConfig.VOICE_PATH_KEY, mVoiceFilePath);
//    	mContext.startService(recorderIntent);
    	if (mIsSRRecorderServiceBound) {
    	    mSRRecorderService.record(mVoiceFilePath);
    	} else { 
    	    SRDebugUtil.SRLogError("SRRecorderService is not bound");
    	    return;
    	}
    	
    	mRecordStartTime = System.currentTimeMillis();
    	
    	// Time Timer
    	setTimeTimer(RECORDER_TIMER_PERIOD);
    	
    	// Add Voice ddd
    	mVoiceDb = mDataSource.createVoice(mVoiceFilePath, mDocFilePath);
    	addTag(SRDbHelper.TEXT_TAG_TYPE, mTitle, "0");
    	
//    	ServiceConnection mConnection = null;
//        Boolean woo = mContext.bindService(recorderIntent, mConnection, 0);
//        SRDebugUtil.SRLog("woo = "+woo);
    	
    	//mView.setTagList();
    	
    	// SRVoiceView Button State
    	isRecordering = true;
    	notifyRecorderBtnStateObservers(true);
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
        SRDebugUtil.SRLog("SRVoice.recordStop()");
        
//    	mContext.stopService(new Intent("com.unus.smartrecorder.Recorder"));
        if (mIsSRRecorderServiceBound) {
            mSRRecorderService.recordStop();
        } else {
            SRDebugUtil.SRLogError("SRRecorderService is not bound");
        }
//    	SRVoiceView.mBtnRecorder.setText("recorder");
    	isRecordering = false;
//		mRecorder.stop();
//		mRecorder.release();
//		mRecorder = null;
    	
    	// Time Timer
    	stopTimer();
    	
    	mRecordStartTime = 0;
    	mVoiceDb = null;
    	
        // SRVoiceView Button State
        notifyRecorderBtnStateObservers(false);
    }

    @Override
    public int getPlayerState() {
        return mPlayerState;
    }
    

    @Override
    public void play(String voicePath, int seekTime) {
        SRDebugUtil.SRLog("SRVoice.play(): filePath = " + voicePath
                + " seekTime = " + Integer.toString(seekTime));

        try {
            mPlayer.reset();
            mPlayer.setDataSource(voicePath);
            mPlayer.prepare();
            mPlayer.seekTo(seekTime);
            mPlayer.start();

            // Duration
            notifyDurationObservers(getDuration());
            
            // Play Timer Start
            setTimeTimer(PLAYER_TIMER_PERIOD);
            
            // Doc Page Update
            startUpdateDocPage(seekTime);

            // Change state
            mPlayerState = PLAYER_PLAY_STATE;

            notifyPlayerBtnStateObservers(mPlayerState);

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
    
    /**
     * 재생 동작을 한다. (Search List에서 선택된 경우)
     */
//    @Override
//    public void play(long voiceId, int position) {
//        if (mDataSource == null) {
//            SRDebugUtil.SRLogError("SRVoice.play() : mDataSource is null");
//            return;
//        }
//        
//        SRVoiceDb voiceDb =  mDataSource.getVoiceByVoiceId(voiceId);
//        
//        ArrayList<SRTagDb> tagsDb = mDataSource.getTagByVoiceId(voiceId);
//        
//        setTagList(tagsDb);
//        
//        SRDebugUtil.SRLog("SRVoice.play(): mTagList = " + getTagList());
//        
//        if (voiceDb == null) {
//            SRDebugUtil.SRLogError("SRVoice.play() : voiceId is not valid");
//            return;
//        }
//        
//        if (getPrevMode() == RECORDER_MODE) {
//            recordStop();
//        }
//        
//        mVoiceFilePath = voiceDb.getVoice_path();
//        mTitle = makeVoicePathToTitle(mVoiceFilePath);
// 
//        play(mVoiceFilePath, position);
//    }

    @Override
    public ArrayList<SRTagDb> getTagList() {
        return mTagList;
    }

    @Override
    public void setTagList(ArrayList<SRTagDb> tagList) {
        this.mTagList = tagList;
        notifyPlayerTagListUpObservers(mTagList);
    }

    /**
     * 재생중 상태에 따라서 재생, 일시정지 동작을 한다. (Play/Pause 버튼을 누른 경우)
     */
    @Override
    public void playToggle() {
        SRDebugUtil.SRLog("SRVoice.playToggle()");
        
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                playPause();
            } else {
                // Play
                if (mPlayerState == PLAYER_PAUSE_STATE) {
                    playResume();
                }else if (mPlayerState == PLAYER_STOP_STATE
                        || mPlayerState == PLAYER_COMPLETE_STATE ) {
                    try {
                        if (mPlayerState == PLAYER_STOP_STATE) {
                            mPlayer.prepare();
                            mPlayer.seekTo(0);
                        }
                        playResume();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }       
    }
    
    /**
     * 재생 종료되었을 때 콜백, 버튼의 상태를 바꿔준다. 
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        SRDebugUtil.SRLog("SRVoice.onCompletion(): Playing completed");
        
        // Play Time End
        stopTimer();
        
        // remove Update Page Msg
        stopUpdateDocPage();
        mPlayerPausePosition = 0;
        
        // SeekBar Time Text to Zero
        notifyTimeObservers(0);
        
        mPlayerState = PLAYER_COMPLETE_STATE;
        notifyPlayerBtnStateObservers(mPlayerState);
    } 
    
    /**
     * 재생 재시작 
     */
    @Override
    public void playResume() {
        SRDebugUtil.SRLog("SRVoice.playResume()");
        
        if (mPlayer != null) {
            mPlayer.start();
            
            // Play Timer Start
            setTimeTimer(PLAYER_TIMER_PERIOD);
            
            // Doc Page Update
            startUpdateDocPage(mPlayerPausePosition);
            
            mPlayerState = PLAYER_PLAY_STATE;
            notifyPlayerBtnStateObservers(mPlayerState);
        }
    }

    /**
     * 재생 일시 정지 
     */
    @Override
    public void playPause() {
        SRDebugUtil.SRLog("SRVoice.playPause()");
        
        if (mPlayer != null && mPlayer.isPlaying()) {
            stopTimer();
            
            // remove Update Page Msg
            stopUpdateDocPage();            
            mPlayerPausePosition = mPlayer.getCurrentPosition();
            
            mPlayer.pause();
            mPlayerState = PLAYER_PAUSE_STATE;
            notifyPlayerBtnStateObservers(mPlayerState);
        }
    }

    /**
     * 재생 정지 
     */
    @Override
    public void playStop() {
        SRDebugUtil.SRLog("SRVoice.playStop()");
        
        if (mPlayer != null) {
            mPlayer.stop();
            
            // Play Time End
            stopTimer();
            
            // remove Update Page Msg
            stopUpdateDocPage();            
            mPlayerPausePosition = 0;
            
            // SeekBar Time Text to Zero
            notifyTimeObservers(0);
            
            mPlayerState = PLAYER_STOP_STATE;
            notifyPlayerBtnStateObservers(mPlayerState);
        }
    }
    
    @Override
    public boolean isPlaying() {
        if (mPlayer != null) {
            return mPlayer.isPlaying();
        }
        return false;
    }

    /**
     * 재생 파일의 전체 재생시간을 리턴한다 
     * 
     * @return
     */
    public int getDuration() {
        SRDebugUtil.SRLog("SRVoice.getDuration()");
        
        if (mPlayer != null) {
            return mPlayer.getDuration();
        }
        return 0;
    }

    public void share() {
    	
    }
    
    /**
     * 기본 Title을 시간정보를 이용해 자동 생성해준다.
     */
    @Override
    public String makeDefaultTitle() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
        
        return new String("Audio" + "-" + sdf.format(new Date()));
    }
    
    /**
     * Voice File 경로에서 Title에 해당하는 String을 리
     * @param voicePath
     * @return
     */
    @Override
    public String makeVoicePathToTitle(String voicePath) {
        int i = voicePath.lastIndexOf("/");
        if (i == -1) {
            return null;
        } else {
            mTitle = voicePath.substring(i + 1);
            return mTitle;
        }
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
        SRTagDb tag = mDataSource.createTag(mVoiceDb.getVoice_id(), getChangeNumbering(),type, data, position);
        mTagList.add(tag);
        notifyTagsObservers(tag);
    }
    
	private String getChangeNumbering(){
		return String.format("%03d",mTagNumbering++);
	}

    @Override
    public void seekTo(int seekTime) {
    	SRDebugUtil.SRLog("SRVoice.seekTo() : " + seekTime);
        if (mPlayer != null) {
            mPlayer.seekTo(seekTime);
            
            mPlayerPausePosition = seekTime;
            if (mPlayer.isPlaying()) {
                stopUpdateDocPage();
                startUpdateDocPage(seekTime);
            }
        }
    }
    

    public interface SRVoiceObserver {
        public void updateTags(SRTagDb tag);

        public void updateTime(int time);

        public void updatePage(int page);

        public void updateDuration(int duration);

        public void updateRecorderBtnState(boolean isRecording);

        public void updatePlayerTagList(ArrayList<SRTagDb> tags);

        public void updatePlayerBtnState(int playerState);

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
    
    public void notifyTimeObservers(int time) {
        for (int i = 0; i < mSRVoiceObserver.size(); i++) {
            SRVoiceObserver observer = mSRVoiceObserver.get(i);
            observer.updateTime(time);
        }
    }
    
    public void notifyPageObservers(int page) {
        for (int i = 0; i < mSRVoiceObserver.size(); i++) {
            SRVoiceObserver observer = mSRVoiceObserver.get(i);
            observer.updatePage(page);
        }
    }
    
    public void notifyDurationObservers(int duration) {
        for (int i = 0; i < mSRVoiceObserver.size(); i++) {
            SRVoiceObserver observer = mSRVoiceObserver.get(i);
            observer.updateDuration(duration);
        }
    }
    
    public void notifyRecorderBtnStateObservers(boolean isRecording) {
        for (int i = 0; i < mSRVoiceObserver.size(); i++) {
            SRVoiceObserver observer = mSRVoiceObserver.get(i);
            observer.updateRecorderBtnState(isRecording);
        }
    }
    
    public void notifyPlayerBtnStateObservers(int playerState) {
        for (int i = 0; i < mSRVoiceObserver.size(); i++) {
            SRVoiceObserver observer = mSRVoiceObserver.get(i);
            observer.updatePlayerBtnState(playerState);
        }
    } 
    public void notifyPlayerTagListUpObservers(ArrayList<SRTagDb> tagsDb){
    	for (int i = 0; i < mSRVoiceObserver.size(); i++) {
            SRVoiceObserver observer = mSRVoiceObserver.get(i);
            observer.updatePlayerTagList(tagsDb);
        }
    }
    
    public void removeObserver(SRVoiceObserver o) {
        int i = mSRVoiceObserver.indexOf(o);
        if (i >= 0) {
            mSRVoiceObserver.remove(i);
        }
    }

    /**
     * Auto tag 상태인지 확인 (PDF페이지 넘길때 자동으로 태그 추가)
     */
    @Override
    public boolean isAutoTag() {
        return mIsAutoTag;
    }
    
    @Override
    public void setAutoTag(boolean isAutoTag) {
        mIsAutoTag = isAutoTag;
    }
    
    @Override
    public SRDataSource getDataSource() {
        return mDataSource;
    }
    
    @Override
    public long getVoiceId() {
        return mVoiceId;
    }
    
    @Override
    public void setVoiceId(long voiceId) {
        mVoiceId = voiceId;
    }

    @Override
    public void setPageTagList(ArrayList<SRTagDb> docTagByVoiceId) {
        mDocPageTagList = docTagByVoiceId;
        
        if (mDocPageTagList != null && mDocPageTagList.size() > 0) {
            
        }
    }
    
    @Override
    public ArrayList<SRTagDb> getPageTagList() {
        return mDocPageTagList;
    }

    @Override
    public void setSRRecorderService(SRRecorderService service) {
        mSRRecorderService = service;
    }
    
    @Override
    public SRRecorderService getSRRecorderService() {
        return mSRRecorderService;
    }
    
    @Override
    public void setSRRecorderServiceBound(boolean isBound) {
        mIsSRRecorderServiceBound = isBound;
    }
    
    @Override
    public boolean isSRRecorderServiceBound() {
        return mIsSRRecorderServiceBound;
    }
    
    
    
}

