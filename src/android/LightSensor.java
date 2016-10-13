package org.apache.cordova.lightsensor; 

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;


import java.lang.Math;
import java.util.List;
import java.util.ArrayList;


import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;


import android.os.Handler;
import android.os.Looper;


public class LightSensor extends CordovaPlugin implements SensorEventListener{

	//to get the sensor manager running status

	public static int STOPPED = 0;
    public static int STARTING = 1;
    public static int RUNNING = 2;
    public static int FAILED_TO_START = 3;

    public long TIMEOUT = 30000;		//shutdown the listner


    int status;					        //running status listner
    float intensityRead;					//intensity value
    long timeStamp;				        //time of most recent value
    long lastAccessTime;			    //time the value was last requested


    private SensorManager mSensorManager; 	//sensor manager
    Sensor mSensor;							//Acceleration sensor

    private CallbackContext callbackContext;
    List<CallbackContext> watchContexts;

    public LightSensor(){
    	this.intensityRead= 0;
    	this.timeStamp = 0;
    	this.watchContexts = new ArrayList<CallbackContext>();
    	this.setStatus(LightSensor.STOPPED);
    }

    public void onDestroy(){
    	this.stop();
    }

    public void onReset(){
    	this.stop();
    }


    //--------------------------------------------------------------
    // Corodova Plugin Methods
    //--------------------------------------------------------------

    public void initialize(CordovaInterface cordova, CordovaWebView webView){
    	super.initialize(cordova,webView);
        this.mSensorManager = (SensorManager) cordova.getActivity().getSystemService(Context.SENSOR_SERVICE);

    }


    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException{
    	if (action.equals("start")){
    		this.start();
    	}

    	else if (action.equals("stop")){
    		this.stop();
    	}

    	else if (action.equals("getStatus")) {
    		int i = this.getStatus();
    		callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,i));
    	}

    	else if (action.equals("getReading")){
    		//this should only call when listner is running
    		if (this.status != LightSensor.RUNNING){
    			int val = this.start();
    			Log.i("getReading","In the function");

    			if(val == LightSensor.FAILED_TO_START){
    				Log.e("getReading","Failed to start");
    				callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.IO_EXCEPTION, LightSensor.FAILED_TO_START));
    				return false;
    			}

    			//set timeout call back on main thread if failed to start
    			Handler handler = new Handler(Looper.getMainLooper());
    			handler.postDelayed(new Runnable() {
    				public void run() {
    					LightSensor.this.timeout();
    				}
    			},2000);
    		}
    		callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK,getReading()));
            Log.i("getReading","Successssfuly read");
    		
    	}
    	else{
    		// action not defined
    		return false;
    	}

    	return true;
    }



	//--------------------------------------------------------------
	// Local Methods
	//--------------------------------------------------------------


	/*****************************
	 * start to listening to sensor
	 *****************************
	 */

	/**
	 * @return
	 */

	public int start(){

		// if accelerometer is already running
		if((this.status == LightSensor.RUNNING) || (this.status == LightSensor.STARTING)){
			return this.status;
		}

		// get LightSensor from sensor manager
		@SuppressWarnings("deprecation")
		List<Sensor> list = this.mSensorManager.getSensorList(Sensor.TYPE_LIGHT);

		// if sensor found, register as listner
		if (list != null && list.size()>0){
			Log.i("Sensor","Sensor found");
			this.mSensor = list.get(0);
			this.mSensorManager.registerListener(this, this.mSensor, SensorManager.SENSOR_DELAY_NORMAL);
			this.lastAccessTime = System.currentTimeMillis();
			this.setStatus(LightSensor.STARTING);
		}

		else{
			Log.e("Sensor","Sensor not found");
			this.setStatus(LightSensor.FAILED_TO_START);
		}

		return this.status;
	}


	/**************************************
	 * stop listing to sensor
	 **************************************
	 */

	public void stop(){
		if (this.status != LightSensor.STOPPED){
			this.mSensorManager.unregisterListener(this);
		}
		this.setStatus(LightSensor.STOPPED);
	}



    /**
     * Called after a delay to time out if the listener has not attached fast enough.
     */

	private void timeout() {
        if (this.status == LightSensor.STARTING) {
            this.setStatus(LightSensor.FAILED_TO_START);
            if (this.callbackContext != null) {
                this.callbackContext.error("LightSensor listener failed to start.");
            }
        }
    }


	//--------------------------------------------------------------
	// SensorEventListner interface
	//--------------------------------------------------------------

   /**
     * Sensor listener event.
     * @param event
     */

	public void onSensorChanged(SensorEvent event){

		this.timeStamp = System.currentTimeMillis();

		intensityRead = event.values[0];
		

	       

		// If heading hasn't been read for TIMEOUT time, then turn off compass sensor to save power
        if ((this.timeStamp - this.lastAccessTime) > this.TIMEOUT) {
            this.stop();
        }

	}

	/**
     * Required by SensorEventListener
     * @param sensor
     * @param accuracy
     */

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // DO NOTHING
    }

    



	//--------------------------------------------------------------
	// JavaScript Interacion
	//--------------------------------------------------------------


    /**
     * get status from accelorometer
     * @return status
     *
     */

    public int getStatus(){
    	return this.status;
    }

    /**
     * set status and send 
     * @param status
     */

    private void setStatus(int status){
    	this.status = status;
    }




    /**
     * generate JSON objecet to return JS
     * @return a accelorometer sensor reading
	 */

    private JSONObject getReading() throws JSONException {
    	JSONObject obj = new JSONObject();

    	obj.put("intensity", this.intensityRead);

    	return obj;

    }
}





