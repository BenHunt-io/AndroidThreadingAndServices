package com.example.ben.stopwatchdemo;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

/**
 * Created by Ben on 6/11/2017.
 */

public class TimeService extends Service {

    private Handler handler;
    Handler mainHandler;
    Message msg;
    Bundle bundle = new Bundle();

    Messenger mainMessenger;


//    public TimeService(Handler handler){
//        // Get a reference to the Main Threads handler.
//        this.handler = handler;
//    }
//
//    // No constructor?
//    public TimeService(){
//        handler = new Handler(getMainLooper());
//
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mainMessenger = intent.getParcelableExtra("handlerRef");

        new Thread(new myServiceThread()).start(); // start the new thread

        return START_STICKY;
    }

    public class myServiceThread implements Runnable {

        @Override
        public void run() {
            Log.d(TAG, "doSomethingRepeatedly: " + Thread.currentThread());
            for (int i = 0; i < 1000; i++) {
                if (i % 10 == 0) {
                    Log.d(TAG, "" + i);
                    msg = Message.obtain();
                    bundle.putInt("countService", i / 10);
                    msg.setData(bundle);
                    try {
                        mainMessenger.send(msg);
                    } catch (RemoteException e) {
                    }
//                Intent myIntent = new Intent();
//                myIntent.putExtra("countService", i);
//                myIntent.setAction("StartProgressBar");
//                sendBroadcast(myIntent);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }


        }
    }

