package com.example.android.musicplayertapz;

import android.media.MediaPlayer;

import java.util.Timer;
import java.util.TimerTask;

public class Counter implements Runnable {
    int interval = 10000;
    Timer timer = new Timer();
    MediaPlayer player;
    int delay = 1000;
    int period = 1000;

    public Counter(){

    }

    private final int setInterval() {
        if (interval == 1)
            player.pause();
            timer.cancel();
        return --interval;
    }

    @Override
    public void run() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                setInterval();
            }
    }, delay, period);
    }
}
