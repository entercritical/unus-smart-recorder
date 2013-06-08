//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : SRVoiceView.java
//  @ Date : 2013-05-30
//  @ Author : 
//
//
package com.unus.smartrecorder;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;

public class SRVoiceView extends RelativeLayout implements SRVoice.SRVoiceObserver {

    private Context mContext;
    private SRVoiceControllerInterface mController;

    private ListView mTagListView;
    private SRTagListAdapter tagListAdapter;
    private ImageButton mTextTagBtn, mPhotoTagBtn, mRecordBtn, mStopRecordBtn;
    private TextView mTimeView;
    
    private ImageView mDummyView;
    private FrameLayout mDocFrame;
    private ProgressBar mVolumeView;
    private SeekBar mSeekBarView;
    private LinearLayout mRecorderBtnsLayout, mPlayerBtnsLayout;
    private ImageButton mFFBtn, mRewindBtn, mPlayToggleBtn, mStopPlayBtn; 
    private MuPDFReaderView mDocView;
    private MuPDFCore mCore;

    private static final int UPDATE_TAGS = 1;
    private static final int UPDATE_TIME = 2;
    private static final int UPDATE_RECORDER_BTN = 3;
    private static final int UPDATE_PLAYER_BTN = 4;
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
            case UPDATE_TAGS:
                tagListAdapter.add((SRTagDb)msg.obj);
                break;
            case UPDATE_TIME:
                setTime((Long)msg.obj);
                break;
            case UPDATE_RECORDER_BTN:
                setRecorderBtnState((Boolean)msg.obj);
                break;
            case UPDATE_PLAYER_BTN:
                setPlayerBtnState((Boolean)msg.obj);
                break;                
            }
        }
        
    };

    public SRVoiceView(Context context) {
        super(context);
        initView(context);
    }

    public SRVoiceView(Context context, SRVoiceControllerInterface controller) {
        super(context);
        mController = controller;
        initView(context);
    }

    public SRVoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    /**
     * View initialization
     * 
     * @param context
     */
    private void initView(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.sr_voiceview_layout, this, true);

        SRDataSource datasource = new SRDataSource(mContext);
        datasource.open();
        // {{TESTCODE
        
        mTagListView = (ListView)findViewById(R.id.tagListView);
        
        //ArrayList<SRTagDb> tags = datasource.getAllTag();
        
        tagListAdapter = new SRTagListAdapter(mContext, R.layout.sr_tag_list, new ArrayList<SRTagDb>());
        
        mTagListView.setAdapter(tagListAdapter);
        
        datasource.close();
        
        
        
//        mTagListView.setAdapter(mTagListViewAdapter = new ArrayAdapter<String>(
//
//        mContext, android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings));
        // }}TESTCODE

        mRecorderBtnsLayout = (LinearLayout) findViewById(R.id.recorderBtnsLayout);
        
        mTextTagBtn = (ImageButton) findViewById(R.id.textTagBtn);
        mTextTagBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SRDebugUtil.SRLog("TextTag Click");
                if (mController != null)
                    mController.tagText();

            }
        });

        mPhotoTagBtn = (ImageButton) findViewById(R.id.photoTagBtn);
        mPhotoTagBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SRDebugUtil.SRLog("PhotoTag Click");
                if (mController != null)
                    mController.tagPhoto();                
            }
        });

        mRecordBtn = (ImageButton) findViewById(R.id.recordBtn);
        mRecordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SRDebugUtil.SRLog("Record Click");
                if (mController != null)
                    mController.record();
            }
        });

        mStopRecordBtn = (ImageButton) findViewById(R.id.stopRecordBtn);
        mStopRecordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SRDebugUtil.SRLog("Stop Click");
                if (mController != null)
                    mController.recordStop();
            }
        });
        
        mPlayerBtnsLayout = (LinearLayout) findViewById(R.id.playerBtnsLayout);
        mFFBtn = (ImageButton) findViewById(R.id.ffBtn);
        mFFBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SRDebugUtil.SRLog("FF Click");

            }
        });
        mRewindBtn = (ImageButton) findViewById(R.id.rewindBtn);
        mRewindBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SRDebugUtil.SRLog("Rewind Click");

            }
        });
        mPlayToggleBtn = (ImageButton) findViewById(R.id.playToggleBtn);
        mPlayToggleBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SRDebugUtil.SRLog("PlayToggle Click");
                
                if (mController != null) {
                    mController.playByPlayToggleBtn();
                }
            }
        });
        mStopPlayBtn = (ImageButton) findViewById(R.id.stopPlayBtn);
        mStopPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SRDebugUtil.SRLog("StopPlay Click");
                
                if (mController != null)
                    mController.playStop();
            }
        });
        
        mVolumeView = (ProgressBar)findViewById(R.id.volumeView);
        mSeekBarView = (SeekBar)findViewById(R.id.seekBarView);
        
        mTimeView = (TextView)findViewById(R.id.timeView);
        
        
        //mSRDocView = new SRDocView(getContext());
        //addView(mSRDocView);
        mDocFrame = (FrameLayout)findViewById(R.id.docFrame);
        mDummyView = (ImageView)findViewById(R.id.dummyView);

        
        mDocView = new MuPDFReaderView(getContext());
        mDocView.setMode(MuPDFReaderView.Mode.Viewing);

    }
    
    public void setDocPath(String docPath) {
        if (docPath == null || docPath.length() ==0) {
            if (mDocView != null)
                mDocFrame.removeView(mDocView);
            return;
        }
        
        try {
    		mCore = new MuPDFCore(getContext(), docPath);
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
        mDocView.setAdapter(new MuPDFPageAdapter(getContext(),mCore));
        
        for (int i = 0; i < mDocFrame.getChildCount(); i++) {
        	if (mDocFrame.getChildAt(i) == mDocView)
        		return;
        }
        //mDocFrame.removeView(mDocView);
        mDocFrame.addView(mDocView);    	
    }

    @Override
    public void updateTags(SRTagDb tag) {
    	//tagListAdapter.add(tag);
        Message m = new Message();
        m.what = UPDATE_TAGS;
        m.obj = tag;
        
        mHandler.sendMessage(m);
    }
    
    @Override
    public void updateTime(long time) {
        Message m = new Message();
        m.what = UPDATE_TIME;
        m.obj = Long.valueOf(time);
        
        mHandler.sendMessage(m);        
    }

    @Override
    public void updateRecorderBtnState(boolean isRecording) {
        Message m = new Message();
        m.what = UPDATE_RECORDER_BTN;
        m.obj = isRecording;
        
        mHandler.sendMessage(m);       
    }

    @Override
    public void updatePlayerBtnState(boolean isPlaying) {
        Message m = new Message();
        m.what = UPDATE_PLAYER_BTN;
        m.obj = isPlaying;
        
        mHandler.sendMessage(m);
    }
    
    /**
     * set Recording or Playing time text
     * 
     * @param t
     */
    private void setTime(long t) {
        long sec = t / 1000;
        long h, m, s, tmp;

        if (sec < 3600) {
            h = 0L;
            m = sec / 60;
            s = sec % 60;
        } else {
            h = sec / 3600;
            tmp = sec % 3600;
            m = tmp / 60;
            s = tmp % 60;
        }
        if (mTimeView != null)
            mTimeView.setText(String.format("%d:%02d:%02d", h, m, s));
    }
    
    /**
     * set Recorder Buttons enable/disable
     * 
     * @param isRecording
     */
    private void setRecorderBtnState(Boolean isRecording) {
        if (isRecording == true) {
            mTextTagBtn.setEnabled(true);
            mPhotoTagBtn.setEnabled(true);
            mRecordBtn.setEnabled(false);
            mStopRecordBtn.setEnabled(true);
        } else {
            mTextTagBtn.setEnabled(false);
            mPhotoTagBtn.setEnabled(false);
            mRecordBtn.setEnabled(true);
            mStopRecordBtn.setEnabled(false);
        }
    }
    
    /**
     * set Player buttons enable/disable
     * 
     * @param isPlaying
     */
    private void setPlayerBtnState(Boolean isPlaying) {
        if (isPlaying == true) {
            mFFBtn.setEnabled(true);
            mRewindBtn.setEnabled(true);
            //mPlayToggleBtn.setEnabled(false);
            mPlayToggleBtn.setImageResource(R.drawable.av_pause);
            mStopPlayBtn.setEnabled(true);
        } else {
            mFFBtn.setEnabled(false);
            mRewindBtn.setEnabled(false);
            //mPlayToggleBtn.setEnabled(true);
            mPlayToggleBtn.setImageResource(R.drawable.av_play);
            mStopPlayBtn.setEnabled(false);
        }
    }
    
    /**
     * Recorder view or Player view
     * 
     * @param mode
     */
    public void setVoiceViewMode(int mode) {
        if (SRVoice.RECORDER_MODE == mode) {
            mTimeView.setText(R.string.zero_time);
            mVolumeView.setVisibility(View.VISIBLE);
            mSeekBarView.setVisibility(View.INVISIBLE);
            
            mRecorderBtnsLayout.setVisibility(View.VISIBLE);
            mPlayerBtnsLayout.setVisibility(View.INVISIBLE);
        } else if (SRVoice.PLAYER_MODE == mode) {
            mTimeView.setText(R.string.zero_time);
            mVolumeView.setVisibility(View.INVISIBLE);
            mSeekBarView.setVisibility(View.VISIBLE);
            
            mRecorderBtnsLayout.setVisibility(View.INVISIBLE);
            mPlayerBtnsLayout.setVisibility(View.VISIBLE);
        }
    }
}
