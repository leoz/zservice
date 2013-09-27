package com.leoz.bz.zservice;

import com.leoz.bz.zbase.ZBaseMessage;

import android.content.Intent;

public enum ZServiceStatus {
	INSTANCE;

	private int mCurrent = 0;
    private int mCount = 0;
    private boolean mProgress = false;
    private boolean mAborted = false;
    private long mT1 = 0;
    private long mT2 = 0;
    
    private void setFlags() {
    	mProgress = true;    	
		mCurrent = 0;
		mCount = 0;
		// Log Time
		mT1 = System.currentTimeMillis();    	
		mT2 = mT1;    	
    }
    
    private void clearFlags() {
       	mProgress = false;    	
		mCurrent = 0;
    	mCount = 0;
		// Clear Time
 		mT1 = 0;
    	mT2 = mT1;
    }
    
    private String finishLoad() {
    	String status;
		mT2 = System.currentTimeMillis(); // Log time    	    	
		status = "Loaded "  + String.valueOf(mCurrent) +
				 " out of " + String.valueOf(mCount) +
				 " file(s). Load complete in " + ((mT2 - mT1)/1000L) + " seconds.";
    	clearFlags();
    	return status;
    }
    
    public String getTextByMessage(ZBaseMessage m) {
    	
    	String status;
		
    	switch (m) {
    		case BM_SCAN_START:
	    		status = "Start scan";
	    		mAborted = false;
	    		setFlags();
    			break;
    		case BM_SCAN_STOP:
	    		status = "Stop scan";
	    		if (mProgress) {
	    			mAborted = true;
	    		}
    			break;
	    	case BM_SET_DIR:
	    		status = "Dir is set";
	    		mAborted = false;
	    		break;
	    	case BM_SET_SIZE:
	    		status = "Size is set";
	    		mAborted = false;
	    		break;
	    	case BM_SET_THREADS:
	    		status = "Thread count is set";
	    		mAborted = false;
	    		break;
	    	case BM_DIR_LOAD_BEGIN:
	    		status = "Dir load: begin";
	    		break;
	    	case BM_DIR_LOAD_END:
	    		status = "Dir load: end";
	    		break;
	    	case BM_FILE_LOAD_BEGIN:
	    		status = "File load: begin";
	    		break;
	    	case BM_FILE_LOAD_END:
	    		status = "File load: end";
	    		mCurrent++;
	    		break;
	    	case BM_CLEAR_CACHE_BEGIN:
	    		status = "Cache clear: begin";
	    		break;
	    	case BM_CLEAR_CACHE_END:
	    		status = "Cache clear: end";
	    		mAborted = false;
	    		break;
	    	default:
	    		status = "unknown";
    	}
    	
    	if (mAborted == true) {
    		status = "Scan aborted. " + finishLoad();
    	}
    	else {
        	if (mProgress == true) {
            	if (mCount != 0 && mCount == mCurrent) {
            		status = "Scan finished. " + finishLoad();
            	}
            	else if (mCount != mCurrent) {
            		status = "Loaded " + String.valueOf(mCurrent) + " file(s). Continuing...";    		
            	}    		
        	}    		
    	}
    	
    	return status;   
    }
    
    public String getText(Intent intent) {
    	
    	int i = intent.getIntExtra("msg", 0);

    	ZBaseMessage m = ZBaseMessage.values()[i];
    	
    	String status;

    	if (m == ZBaseMessage.BM_FILE_COUNT) {
    		mCount = intent.getIntExtra("count", 0);
    		status = "File count is " + String.valueOf(mCount);    		
    	}
    	else {
    		status = getTextByMessage(m);
    	}
    	
    	return status;
    }
    
    public void setCount(int count) {
    	mCount = count;
    }
}
