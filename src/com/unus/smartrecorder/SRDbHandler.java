//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : Untitled
//  @ File Name : SRDB.java
//  @ Date : 2013-05-30
//  @ Author : 
//
//
package com.unus.smartrecorder;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;



public class SRDbHandler{
	
    public SRSearch mSearch;
    private SRTag mTag;
    
    private Context mContext;
    private SRDbHelper mDbhelper;
    private SQLiteDatabase mDatabase;
    
    public SRDbHandler(Context context) {
		// TODO Auto-generated constructor stub
    	this.mContext = context;
    	mDbhelper = new SRDbHelper(context);
    	this.mDatabase = mDbhelper.getWritableDatabase();
	}
    
    public static SRDbHandler open(Context context) throws SQLException{
    	SRDbHandler handler = new SRDbHandler(context);
    	return handler;
    }

    public void close() {

    }

    public long insertInfo(String voiceFilePath, String docFilePath) {
    	ContentValues values = new ContentValues();
    	values.put("voice_path", voiceFilePath);
    	values.put("document_path", docFilePath);
    	long result = mDatabase.insert("voice", null, values);
    	return result;
    }

    public void deleteInfo(String voiceFilePath) {

    }

    public void updateInfo(String voiceFilePath, String docFilePath) {

    }

    public void insertTag(String voiceFilePath, SRTagItem tagItem) {

    }

    public void deleteTag(String voiceFilePath, SRTagItem tagItem) {

    }

    public void updateTag(String voiceFilePath, SRTagItem tagItem) {

    }


	public Cursor selectAll() {
		
		Cursor cursor = mDatabase.query(true, "voice", new String[] {"voice_id","created_time","voice_path","document_path"}, null, null, null, null, null, null);
		
		DebugUtil.SRLog("selectAll cursor = " + cursor);
		return cursor;

    }

    public void selectByText(String text) {

    }

    public void selectByText(String voiceFileName, String text) {

    }


}
