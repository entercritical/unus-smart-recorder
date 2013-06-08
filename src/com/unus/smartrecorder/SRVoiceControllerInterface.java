package com.unus.smartrecorder;

import android.app.Dialog;
import android.os.Bundle;

public interface SRVoiceControllerInterface {
    public Dialog createDialog(int id);
    
    public void prepareDialog(int id, Dialog dialog, Bundle args);
    
    public void record();
    
    public void recordStop();

    public void tagText();

    public void tagPhoto();
    
    public void playBySearchListPos(int position);
    
    public void playBySearchList(SRTagDb tagDb);

    public void setViewMode(int mode);

    public void playStop();

    public void playByPlayToggleBtn();
    
    public void jumpToggleBtn(Boolean rewind);
    
    public void playBySeekBar(int seekTime);
}
