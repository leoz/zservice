package com.leoz.bz.zservice;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.leoz.bz.zbase.ZBaseMessage;
import com.leoz.bz.zbase.ZFile;
import com.leoz.bz.zclient.ZCltConnector;
import com.leoz.bz.zclient.ZCltManager;
import com.leoz.bz.zclient.ZCltService;
import com.leoz.bz.zclient.ZCltSettings;

public class ZServiceActivity extends Activity {
	
	private static final String TAG = "[z::local] ServiceFragment"; /// TODO: FIX ME
	
	private static int mSize = ZCltSettings.INSTANCE.getSize();
	private static int TCOUNT = 10;
	private boolean mSSelected = false;
	private boolean mTSelected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		
        ZCltService.INSTANCE.init(this);
        ZCltService.INSTANCE.addReceiver(new ZServiceReceiver());        

        setContentView(R.layout.service_view);
		
		ZServiceReceiver.setReceiver(this);
		
        Button start = (Button)findViewById(R.id.button_start);
        Button stop = (Button)findViewById(R.id.button_stop);
        Button bind = (Button)findViewById(R.id.button_bind);
        Button release = (Button)findViewById(R.id.button_release);
        
        Button sdata = (Button)findViewById(R.id.button_set_data);
        
        Button sscan = (Button)findViewById(R.id.button_server_scan);
        Button asscan = (Button)findViewById(R.id.button_all_server_scan);
        Button cscan = (Button)findViewById(R.id.button_client_scan);
        
        Button sstop = (Button)findViewById(R.id.button_stop_scan);
        Button cclear = (Button)findViewById(R.id.button_clear_cache);
        
        Spinner sSize = (Spinner)findViewById(R.id.spinner_size);
        Spinner tCount = (Spinner)findViewById(R.id.spinner_thread);
		
        start.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		ZCltService.INSTANCE.start();
        		showStatus();
        	}
        });
        
        stop.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){        		
        		ZCltConnector.INSTANCE.stop();
        		showStatus();
        	}
        });       
        
        bind.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		if (ZCltService.INSTANCE.started()) {
        			ZCltService.INSTANCE.bind();        			
            		showStatus();
        		}
        		else {
                	Log.v(TAG, "Service is not started"); /// TODO: FIX ME    		    		        			
        		}
        	}
        });  
        
        release.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		ZCltService.INSTANCE.release();
        		showStatus();
        	}
        });          
        
        sdata.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		ZCltManager.INSTANCE.invokeSetDir(ZCltSettings.INSTANCE.getDir());
        		ZCltManager.INSTANCE.invokeSetSize(mSize);
        		ZCltManager.INSTANCE.invokeSetCount(TCOUNT);
        	}
        });          
        
        sscan.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		scanOnServer();
        	}
        });          
        
        asscan.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		scanAllOnServer();
        	}
        });          
        
        cscan.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		scanOnClient();
        	}
        });          
        
        sstop.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		ZCltManager.INSTANCE.invokeScanStop();
        	}
        });
        
        cclear.setOnClickListener(new OnClickListener() {
        	public void onClick(View v){
        		ZCltManager.INSTANCE.invokeClearCache();
        	}
        });
                
        /// Size spinner
        sSize.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
            	
            	if (mSSelected == true) {
                	String selected = parent.getItemAtPosition(pos).toString();
                	mSize = Integer.valueOf(selected);
    				Toast.makeText(parent.getContext(), "Selected size: " + mSize, Toast.LENGTH_SHORT).show();							            		
            	}
            	else {
            		mSSelected = true;
            	}
            } 

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            } 
        });
        
        /// Thread count spinner
        tCount.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
            	
            	if (mTSelected == true) {
                	String selected = parent.getItemAtPosition(pos).toString();
                	TCOUNT = Integer.valueOf(selected);
    				Toast.makeText(parent.getContext(), "Selected thread count: " + TCOUNT, Toast.LENGTH_SHORT).show();							            		
            	}
            	else {
            		mTSelected = true;
            	}
            } 

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            } 
        });        
	}
	
    private void scanAllOnServer() {
		ZCltManager.INSTANCE.invokeScanStart();    
    }
    
    private void scanOnServer() {
		ZCltManager.INSTANCE.invokeScanDir(ZCltSettings.INSTANCE.getDir(), ZCltSettings.INSTANCE.getSizes());    
    }

    private void scanOnClient() {
    	ZServiceStatus.INSTANCE.getTextByMessage(ZBaseMessage.BM_SCAN_START);
		
		ZFile dir = ZCltSettings.INSTANCE.getAppDir();
		
		if (dir.isDirectory()) {
			
			ZFile[] files = dir.listFiles();
						
			if (files != null) {
				ZServiceStatus.INSTANCE.setCount(files.length);
			}

			for (ZFile file : files) {
				ZCltManager.INSTANCE.invokeScanFile(file.getPath(), mSize);
			}
		}    	
    }
    
	@Override
	public void onDestroy() {
		
    	Log.v(TAG, "onDestroy"); /// TODO: FIX ME
    	
    	ZCltConnector.INSTANCE.stop();
		
		super.onDestroy();
	}
            	
	private void showStatus(){
		String status = "";
		
    	if (ZCltService.INSTANCE.started()){
    		status = "Service is started";
    	}
    	else {
    		status = "Service is stopped";    		
    	}
    	
    	if (ZCltManager.INSTANCE.connected()){
    		status += " and bound";
    	}
    	else {
    		status += " and unbound";    		
    	}

    	TextView txtStatus = (TextView) findViewById(R.id.text_status);
    	txtStatus.setText(status);		
	}
}
