package com.example.android.musicplayertapz;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;

/**
 * @author Artem Tartakynov
 * Plays the sounds of leg movement
 */
public class LegMovementPlayer implements OnAudioFocusChangeListener {	
    private final static int PLAYER_FORWARD 	= 0;
    private final static int PLAYER_BACKWARD 	= 1;

    private final AudioManager mAudioManager;	
    private final Context mContext;
    private Object mSync = new Object();
    private boolean mCanPlay = false;
    private float mVolume;

    public LegMovementPlayer(Context context) {
	this.mContext = context;
	this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	final int  maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
	int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
	if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
	    init();
	}
    }

    /********************* Public methods ******************************/

    /**
     * Plays forward movement sound
     */
    public void playForward() {
	play(PLAYER_FORWARD);
    }

    /**
     * Plays backward movement sound
     */
    public void playBackward() {
	play(PLAYER_BACKWARD);		
    }


    /**
     * Call this method when you are done with this instance
     */ 
    public void release() {
	synchronized (mSync) {
		if (mCanPlay) {
			mCanPlay = false;
		}
	}
    }

    /**
     * Initializes current instance
     */ 
    public void init() {
	synchronized (mSync) {
	    mCanPlay = true;							
	}
    }

    /********************* OnAudioFocusChangeListener ******************/

    /**
     * Handles audio focus change for this listener
     */
    @Override
    public void onAudioFocusChange(int focusChange) {
	synchronized (mSync) {
	    switch (focusChange) {
	    case AudioManager.AUDIOFOCUS_GAIN:
		init();
		break;
	    case AudioManager.AUDIOFOCUS_LOSS:
		release();
		break;
	    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
	    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
		mCanPlay = false;				
		break;		
	    }
	}
    }

    /********************* Private methods *****************************/

    /**
     * Plays specified player if it's allowed
     */
    private void play(int player) {
	if (mCanPlay) {
	    //mPlayers[player].start();
	}
    }
}
