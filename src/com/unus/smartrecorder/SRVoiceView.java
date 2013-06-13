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

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

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
    private ToggleButton mAutoTagToggleBtn;
    
    private MuPDFReaderView mDocView;
    private MuPDFCore mCore;

    private static final int UPDATE_TAGS = 1;
    private static final int UPDATE_TIME = 2;
    private static final int UPDATE_PAGE = 3;
    private static final int UPDATE_DURATION = 4;
    private static final int UPDATE_RECORDER_BTN = 5;
    private static final int UPDATE_PLAYER_BTN = 6;
    private static final int JUMP_DELAYED_TIME = 250;
    
    private Handler mffHandler;
    
    private static class UpdateHandler extends Handler {
        WeakReference<SRVoiceView> mRef;

        UpdateHandler(SRVoiceView voice) {
            mRef = new WeakReference<SRVoiceView>(voice);
        }

        @Override
        public void handleMessage(Message msg) {
            SRVoiceView v = mRef.get();
            switch(msg.what) {
            case UPDATE_TAGS:
                v.tagListAdapter.add((SRTagDb)msg.obj);
                v.focusToLastItem();
                break;
            case UPDATE_TIME:
                v.setTime((Integer)msg.obj);
                break;
            case UPDATE_PAGE:
                v.setDocPage((Integer)msg.obj);
                break;
            case UPDATE_DURATION:
                v.setDuration((Integer)msg.obj);
                break;                
            case UPDATE_RECORDER_BTN:
                v.setRecorderBtnState((Boolean)msg.obj);
                break;
            case UPDATE_PLAYER_BTN:
                v.setPlayerBtnState((Integer)msg.obj);
                break;                
            }
        }
    }
    private UpdateHandler mHandler = new UpdateHandler(this);    
    
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
        
        mFFBtn.setOnTouchListener(new View.OnTouchListener(){
        	
        	
        	
        	@Override 
        	public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mffHandler != null) return true;
                    mffHandler = new Handler();
                    mffHandler.postDelayed(mffAction, JUMP_DELAYED_TIME);
                    break;
                case MotionEvent.ACTION_UP:
                    if (mffHandler == null) return true;
                    mffHandler.removeCallbacks(mffAction);
                    mffHandler = null;
                    break;
                }
                return false;
            }
        	
        	
        	
        });
        
        mFFBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SRDebugUtil.SRLog("FF Click");
                mController.jumpToggleBtn(false);
            }
        });
        
        
        
        
        mRewindBtn = (ImageButton) findViewById(R.id.rewindBtn);
        
        mRewindBtn.setOnTouchListener(new View.OnTouchListener(){
        	
        	private Handler mRevindHandler;
        	
        	@Override 
        	public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mRevindHandler != null) return true;
                    mRevindHandler = new Handler();
                    mRevindHandler.postDelayed(mAction, JUMP_DELAYED_TIME);
                    break;
                case MotionEvent.ACTION_UP:
                    if (mRevindHandler == null) return true;
                    mRevindHandler.removeCallbacks(mAction);
                    mRevindHandler = null;
                    break;
                }
                return false;
            }
        	
        	Runnable mAction = new Runnable() {
                @Override public void run() {
                	//SRDebugUtil.SRLog("setOnTouchListener setOnTouchListener run");
                	mController.jumpToggleBtn(true);
                	mRevindHandler.postDelayed(this, JUMP_DELAYED_TIME);
                }
            };
        	
        });
        
        mRewindBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SRDebugUtil.SRLog("Rewind Click");
                if (mController != null){
                	mController.jumpToggleBtn(true);
                }

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
        
        mSeekBarView.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				SRDebugUtil.SRLog("setOnSeekBarChangeListener onStopTrackingTouch");
				mController.playBySeekTime(seekBar.getProgress());
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				//SRDebugUtil.SRLog("setOnSeekBarChangeListener onStartTrackingTouch");
			    if (mController != null) {
			        mController.startSeekBarTracking();
			    }
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				//SRDebugUtil.SRLog("setOnSeekBarChangeListener onProgressChanged " + progress + " fromUser " + fromUser);
			    // 시간을 변경해야한다. 
			    if (fromUser == true) {
			        setTime(seekBar.getProgress());
			    }
			}
		});
        
        mTimeView = (TextView)findViewById(R.id.timeView);
        
        
        //mSRDocView = new SRDocView(getContext());
        //addView(mSRDocView);
        mDocFrame = (FrameLayout)findViewById(R.id.docFrame);
        mDummyView = (ImageView)findViewById(R.id.dummyView);

        
//        mDocView = new MuPDFReaderView(getContext());
//        mDocView.setMode(MuPDFReaderView.Mode.Viewing);
//        mDocView.setOnPageChangedListener(new MuPDFReaderView.onPageChagedListener() {
//            
//            @Override
//            public void onPageChanged(int page) {
//                if (mController != null) {
//                    mController.docPageChanged(page);
//                }
//            }
//        });
        
        mAutoTagToggleBtn = (ToggleButton) findViewById(R.id.autoTagToggleBtn);
        mAutoTagToggleBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mController != null) {
                    mController.changeAutoTag(isChecked);
                }
            }
        });
    }
    
    private Runnable mffAction = new Runnable() {
        @Override public void run() {
        	//SRDebugUtil.SRLog("setOnTouchListener setOnTouchListener run");
        	mController.jumpToggleBtn(false);
        	mffHandler.postDelayed(this, JUMP_DELAYED_TIME);
        }
    };
    
    public void removeFfHandler(){
    	//mffHandler.removeCallbacksAndMessages(null);
        //mffHandler = null;
//    	if (mffHandler == null){
//	        mffHandler.removeCallbacksAndMessages(null);
//	        mffHandler = null;
//    	}
    	if (mffHandler != null) {
    		mffHandler.removeCallbacks(mffAction);
            mffHandler = null;
    	}
        
    }
    
    public void setTagList(){
    	
    }
    
    /**
     * Doc의 해당 Page로 이동 
     * @param page
     */
    public void setDocPage(int page) {
        if (mDocView != null) {
            if (mDocView.getDisplayedViewIndex() != page)
                mDocView.setDisplayedViewIndex(page);
        }
    }
    
    /**
     * Doc 설정 
     * @param docPath
     */
    public void setDocPath(String docPath) {
        if (docPath == null || docPath.length() ==0) {
            if (mDocView != null) {
                mDocFrame.removeView(mDocView);
                mDocView = null;
            }
            if (mCore != null) {
                mCore.onDestroy();
                mCore = null;
            }
            mDummyView.setVisibility(View.VISIBLE);
            return;
        }
 
        // 삭제하고 재생성해야 제대로 출력된다. 
        if (mDocFrame != null)
            mDocFrame.removeView(mDocView);
        if (mCore != null)
            mCore.onDestroy();
        
        mDocView = new MuPDFReaderView(getContext());
        mDocView.setMode(MuPDFReaderView.Mode.Viewing);
        mDocView.setOnPageChangedListener(new MuPDFReaderView.onPageChagedListener() {
            
            @Override
            public void onPageChanged(int page) {
                if (mController != null) {
                    mController.docPageChanged(page);
                }
            }
        });
        
        try {
    		mCore = new MuPDFCore(getContext(), docPath);
    	} catch (Exception e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
        mDocView.setAdapter(new MuPDFPageAdapter(getContext(),mCore));
        
        mDocFrame.addView(mDocView);
        mDummyView.setVisibility(View.INVISIBLE);
    }
    public void focusToLastItem() {
    	SRDebugUtil.SRLog("mTagListView.getCount()-1 = " + mTagListView.getCount());
    	mTagListView.setSelection(mTagListView.getCount()-1);
    	mTagListView.requestFocus(mTagListView.getCount()-1);
	}
    @Override
    public void updateTags(SRTagDb tag) {
    	//tagListAdapter.add(tag);
        Message m = new Message();
        m.what = UPDATE_TAGS;
        m.obj = tag;
        
//        mTagListView.get
//        mTagListView.setSelection();
//        mTagListView.requestFocus();
        mHandler.sendMessage(m);
    }
    
    @Override
    public void updateTime(int time) {
        Message m = new Message();
        m.what = UPDATE_TIME;
        m.obj = Integer.valueOf(time);
        
        mHandler.sendMessage(m);        
    }
    
    @Override
    public void updatePage(int page) {
        Message m = new Message();
        m.what = UPDATE_PAGE;
        m.obj = Integer.valueOf(page);
        
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
    public void updateDuration(int duration) {
        Message m = new Message();
        m.what = UPDATE_DURATION;
        m.obj = duration;
        
        mHandler.sendMessage(m); 
    }

    @Override
    public void updatePlayerBtnState(int playerState) {
        Message m = new Message();
        m.what = UPDATE_PLAYER_BTN;
        m.obj = playerState;
        
        mHandler.sendMessage(m);
    }
    
    @Override
    public void updatePlayerTagList(ArrayList<SRTagDb> tags) {
    	// TODO Auto-generated method stub
    	//mTagListView.set
    	
    	SRDebugUtil.SRLog("updateTagList");
    	

    	
    	mTagListView = (ListView)findViewById(R.id.tagListView);
        
        //ArrayList<SRTagDb> tags = datasource.getAllTag();
        
        tagListAdapter = new SRTagListAdapter(mContext, R.layout.sr_tag_list, tags);
        
        mTagListView.setAdapter(tagListAdapter);
        
        mTagListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
//                SRDebugUtil.SRLog("mTagListView : onItemClick() pos = " + position);
//                SRDebugUtil.SRLog("tagListAdapter.getTagDb(position) = " + tagListAdapter.getTagDb(position));
//                SRDebugUtil.SRLog("tagListAdapter.getTagDb(position) = " + tagListAdapter.getTagDb(position));
                //mController.playBySearchList(tagListAdapter.getTagDb(position));
                SRTagDb tag = tagListAdapter.getTagDb(position);
//                SRDebugUtil.SRLog("tag.getTag_time()" + tag.getTag_time());
                mController.playBySeekTime(Integer.parseInt(tag.getTag_time()) );
            }
            
        });
        
    }
    
    /**
     * set Recording or Playing time text
     * 
     * @param t
     */
    private void setTime(int t) {
        int sec = t / 1000;
        int h, m, s, tmp;

        if (sec < 3600) {
            h = 0;
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
        if (mSeekBarView != null && mSeekBarView.getVisibility() == View.VISIBLE) {
            mSeekBarView.setProgress(t);
        }
    }
    
    /**
     * set Recording or Playing time text
     * 
     * @param t
     */
    private void setDuration(int t) {
        if (mSeekBarView != null) {
            mSeekBarView.setMax(t);
        }
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
    private void setPlayerBtnState(int playerState) {
        if (playerState == SRVoice.PLAYER_PLAY_STATE) {
            mFFBtn.setEnabled(true);
            mRewindBtn.setEnabled(true);
            //mPlayToggleBtn.setEnabled(false);
            mPlayToggleBtn.setImageResource(R.drawable.av_pause);
            mStopPlayBtn.setEnabled(true);
        } else if (playerState == SRVoice.PLAYER_STOP_STATE
                || playerState == SRVoice.PLAYER_COMPLETE_STATE) {
        	removeFfHandler();
            mFFBtn.setEnabled(false);
            mRewindBtn.setEnabled(false);
            //mPlayToggleBtn.setEnabled(true);
            mPlayToggleBtn.setImageResource(R.drawable.av_play);
            mStopPlayBtn.setEnabled(false);
        } else if (playerState == SRVoice.PLAYER_PAUSE_STATE) {
            mFFBtn.setEnabled(false);
            mRewindBtn.setEnabled(false);
            //mPlayToggleBtn.setEnabled(true);
            mPlayToggleBtn.setImageResource(R.drawable.av_play);
            mStopPlayBtn.setEnabled(true);
        }
    }
    
    /**
     * Recorder view or Player view
     * 
     * @param mode
     */
    public void setVoiceViewMode(int mode) {
        if (SRVoice.RECORDER_MODE == mode) {
        	//if(!SRVoice.isRecordering){ // TODO: Why?
        		initRecorderTagListView();
        		mTimeView.setText(R.string.zero_time);
                mVolumeView.setVisibility(View.VISIBLE);
                mSeekBarView.setVisibility(View.INVISIBLE);
                mRecorderBtnsLayout.setVisibility(View.VISIBLE);
                mPlayerBtnsLayout.setVisibility(View.INVISIBLE);
        	//}
            
        } else if (SRVoice.PLAYER_MODE == mode) {
            mTimeView.setText(R.string.zero_time);
            mVolumeView.setVisibility(View.INVISIBLE);
            mSeekBarView.setVisibility(View.VISIBLE);
            
            mRecorderBtnsLayout.setVisibility(View.INVISIBLE);
            mPlayerBtnsLayout.setVisibility(View.VISIBLE);
        }
    }
    
    public void initRecorderTagListView(){
    	SRDataSource datasource = new SRDataSource(mContext);
        datasource.open();
        // {{TESTCODE
        
        //mContext.bindService(service, conn, flags)
        
        mTagListView = (ListView)findViewById(R.id.tagListView);
        
        //ArrayList<SRTagDb> tags = datasource.getAllTag();
        
        tagListAdapter = new SRTagListAdapter(mContext, R.layout.sr_tag_list, new ArrayList<SRTagDb>());
        
        mTagListView.setAdapter(tagListAdapter);
        
        datasource.close();
    }
    
    public ProgressBar getProgressBar() {
        return mVolumeView;
    }

    public ToggleButton getAutoTagToggleBtn() {
        return mAutoTagToggleBtn;
    }
    
}
