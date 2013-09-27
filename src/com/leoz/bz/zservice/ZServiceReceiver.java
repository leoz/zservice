package com.leoz.bz.zservice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.leoz.bz.zbase.ZBaseData;
import com.leoz.bz.zclient.ZCltReceiver;

public class ZServiceReceiver extends ZCltReceiver {

	private static final String TAG = "[z::local] ServiceReceiver"; /// TODO: FIX ME

	private static TextView mTime = null;  	
	private static TextView mDir  = null;
	private static TextView mFile = null;    	
	private static TextView mSize = null;    	
	private static TextView mMsg  = null;
	private static ImageView mPic = null;
	
	public ZServiceReceiver() {	
	}
	
	public static void setReceiver(final Activity a) {
		if (a != null) {
	    	mTime = (TextView)  a.findViewById(R.id.text_time);  	
	    	mDir  = (TextView)  a.findViewById(R.id.text_dir);
	    	mFile = (TextView)  a.findViewById(R.id.text_file);    	
	    	mSize = (TextView)  a.findViewById(R.id.text_size);    	
	    	mMsg  = (TextView)  a.findViewById(R.id.text_message);
	    	mPic  = (ImageView) a.findViewById(R.id.image_view);			
		}		
	}

	@Override
	public IntentFilter getFilter() {
    	IntentFilter filter = new IntentFilter();
    	filter.addAction(ZBaseData.ACTION);
    	filter.addAction(ZBaseData.MESSAGE);
		return filter;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		
        if(intent.getAction().equals(ZBaseData.ACTION)) {
 			receiveData(intent);
        }
        else if(intent.getAction().equals(ZBaseData.MESSAGE)) {
        	receiveMessage(intent);
        }
	}
	
    private void receiveData(Intent intent) {
    	
    	Log.v(TAG, "receiveData" );
    	
    	String time = intent.getStringExtra("time");
    	String dir = intent.getStringExtra("dir");
    	String file = intent.getStringExtra("file");
    	int size = intent.getIntExtra("size", 0);
    	String ssize = String.valueOf(size);
    	
    	Log.d(TAG, time);
    	Log.d(TAG, dir);
    	Log.d(TAG, file);
    	Log.d(TAG, ssize);
    	    	
    	mTime.setText(time);
    	mDir.setText(dir);
    	mFile.setText(file);
    	mSize.setText(ssize);
    	
    	byte[] bitmapdata = intent.getByteArrayExtra("image");
    	if (bitmapdata != null) {
        	Bitmap b = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
        	if (b != null) {
            	mPic.setImageBitmap(b);    		
        	}
    	}
    }
    
    private void receiveMessage(Intent intent) {
    	
    	Log.v(TAG, "receiveMessage" );
    	
    	String status = ZServiceStatus.INSTANCE.getText(intent);
    	mMsg.setText(status);		    		
    }
}
