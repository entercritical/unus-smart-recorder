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

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.artifex.mupdfdemo.MuPDFCore;
import com.artifex.mupdfdemo.MuPDFPageAdapter;
import com.artifex.mupdfdemo.MuPDFReaderView;

public class SRVoiceView extends RelativeLayout implements SRVoice.SRVoiceObserver {

    private Context mContext;
    private SRVoiceControllerInterface mController;

    private ListView mTagListView;
    private ArrayAdapter<String> mTagListViewAdapter;
    private ImageButton mTextTagBtn, mPhotoTagBtn, mRecordBtn, mStopBtn;
    
    private ImageView mDummyView;
    private FrameLayout mDocFrame;
    private MuPDFReaderView mDocView;
    private MuPDFCore mCore;

    // {{TESTCODE
    private String[] mStrings = Cheeses.sCheeseStrings;

    // }}TESTCODE

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
        mTagListView = (ListView) findViewById(R.id.tagListView);
        mTagListView.setAdapter(mTagListViewAdapter = new ArrayAdapter<String>(

        mContext, android.R.layout.simple_list_item_1, Cheeses.sCheeseStrings));
        // }}TESTCODE

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

        mStopBtn = (ImageButton) findViewById(R.id.stopBtn);
        mStopBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SRDebugUtil.SRLog("Stop Click");
                if (mController != null)
                    mController.recordStop();
            }
        });
        
        //mSRDocView = new SRDocView(getContext());
        //addView(mSRDocView);
        mDocFrame = (FrameLayout)findViewById(R.id.docFrame);
        mDummyView = (ImageView)findViewById(R.id.dummyView);

        
        mDocView = new MuPDFReaderView(getContext());
        mDocView.setMode(MuPDFReaderView.Mode.Viewing);

    }
    
    public void setDocPath(String docPath) {
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
    public void updateTags() {
        
    }

    @Override
    public void updateTime() {
        
    }
}
