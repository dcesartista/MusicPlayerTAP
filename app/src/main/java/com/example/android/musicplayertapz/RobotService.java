package com.example.android.musicplayertapz;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.example.android.musicplayertapz.PocketDetector.IInPocketListener;
import com.example.android.musicplayertapz.leg.LegMovementDetector;
import com.example.android.musicplayertapz.leg.LegMovementDetector.ILegMovementListener;

/**
 * @author �����
 *
 */
public class RobotService extends Service {	
    private static final int NOTIFICATION 	= R.string.robot_service_label;
    private static final String WAKELOCK 	= "WL_TAG";    

    private static boolean sIsRunning = false;

    private final IBinder mBinder = new RobotBinder();
    private NotificationManager mNotificationManager;
    private SensorManager mSensorManager;    
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private LegMovementDetector mLegMovementDetector;    
    private com.example.android.musicplayertapz.LegMovementPlayer mPlayer;
    private com.example.android.musicplayertapz.PocketDetector mPocket;
    private boolean mIsStarted = false;
    private MediaPlayer player;
    Thread thread = new Thread(new Counter());

    public class RobotBinder extends Binder {
	RobotService getService() {
	    return RobotService.this;
	}
    }

    /**
     * Used for receiving notifications from the LegMovementDetector when leg state have changed
     */
    private ILegMovementListener mLegMovementListener = new ILegMovementListener() {
	@Override
	public void onLegActivity(int activity) {
	    if (!mIsStarted) return;
	    switch (activity) {
	    case LegMovementDetector.LEG_MOVEMENT_BACKWARD:
		//mPlayer.playBackward();
            //thread.stop();
            //thread.start();
			//player.start();
            player.pause();
		break;
	    case LegMovementDetector.LEG_MOVEMENT_FORWARD:
		//mPlayer.playForward();
            //thread.stop();
            //thread.start();
            //player.start();
            player.pause();
		break;
	    }									
	}   	
    };

    /********************* Service *************************************/

    @Override
    public IBinder onBind(Intent intent) {
	return mBinder;
    }

    @Override
    public void onCreate() {
	sIsRunning = true;

	// initialize class fields
	mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);		
	mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
	mPlayer = new com.example.android.musicplayertapz.LegMovementPlayer(getApplicationContext());

	// initialize wakelock
	mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKELOCK);
	mWakeLock.acquire();    	

	// initialize movement detector
	mLegMovementDetector = new LegMovementDetector(mSensorManager);
	mLegMovementDetector.addListener(mLegMovementListener);

	// initialize pocket detector
	mPocket = new com.example.android.musicplayertapz.PocketDetector((SensorManager) getSystemService(SENSOR_SERVICE));
	mPocket.registerListener(mPocketDetectorListener);
	mPocket.start();	

    }

    @Override
    public void onDestroy() {
	sIsRunning = false;
	mLegMovementDetector.stopDetector();
	mNotificationManager.cancel(NOTIFICATION);    		
	mPocket.release();
	mPlayer.release(); 
	mWakeLock.release();
    }        

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
	return START_STICKY;
    }

    /********************* Public methods*******************************/

    public void start() {
	mPocket.start();
	mIsStarted = true;	
    }	

    public void stop() {
	mPocket.stop();			
	mLegMovementDetector.stopDetector();
	mIsStarted = false;	
    }

    public boolean isStarted() {
	return mIsStarted;
    }



    public static boolean isRunning() {
	return sIsRunning;
    }

    /******************* Working with Pocket detector *****************/

    private IInPocketListener mPocketDetectorListener = new IInPocketListener() {

	/**
	 * Called when you put the phone in pocket
	 */
	public void phoneInPocket() {
	    if (mLegMovementDetector != null) { // just to be on safe side
		mLegMovementDetector.startDetector();
	    }
	}

	/**
	 * Called when you take the phone out of pocket
	 */
	public void phoneOutOfPocket() {
	    if (mLegMovementDetector != null) {
		mLegMovementDetector.stopDetector();			
	    }
	}
    };

    /********************* Private methods *****************************/

    /**
     * Show a notification while this service is running.
     */

}
