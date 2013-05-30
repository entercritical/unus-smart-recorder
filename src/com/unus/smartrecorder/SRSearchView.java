//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : SRSearchView.java
//  @ Date : 2013-05-30
//  @ Author : 
//
//
package com.unus.smartrecorder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

public class SRSearchView extends FrameLayout {
    Context mContext;
    ListView mListView;
    
    //{{TESTCODE
    private String[] mStrings = Cheeses.sCheeseStrings;
    //}}TESTCODE

    public SRSearchView(Context context) {
        super(context);
        initView(context);
    }

    public SRSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sr_searchview_layout, this, true);
        
        mListView = (ListView)findViewById(R.id.SRSearchListView);
        mListView.setAdapter(new ArrayAdapter<String>(mContext,
                android.R.layout.simple_list_item_1, mStrings));
    }
}