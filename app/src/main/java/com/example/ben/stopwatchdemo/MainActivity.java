package com.example.ben.stopwatchdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView savedValueText;
    private TextView timerText;
    private int counter = 0;
    private String TAG = "MAIN";


    private BroadcastReceiver br;
    private IntentFilter intentFilter;

    public Handler handler;

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MAIN" + Thread.currentThread());

//        thread = new Thread(new SecondThread(handler));
//        thread.start();

        timerText = (TextView)findViewById(R.id.timerText);
        savedValueText = (TextView)findViewById(R.id.savedValueText);

        // final variables cannot be assigned to reference a different object
        final ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
        final ProgressBar progressBar2 = (ProgressBar)findViewById(R.id.progressBar2);

        // Get a handle to Shared Preferences
        SharedPreferences sharedPreferences = getSharedPreferences("namneIdentifier", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit(); // SharedPreferences editor

        //Read from Shared Preferences
        int retrievedValue = sharedPreferences.getInt("Counter", 0);

        // Update TextView with the retreieved save value, 0 is default if key is not found
        savedValueText.setText("" + retrievedValue);
        Log.d(TAG, "onCreate: ");


        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.getData().containsKey("count")) {
                    progressBar.setProgress(msg.getData().getInt("count"));
                    timerText.setText("" + msg.getData().getInt("count"));
                }
                else if(msg.getData().containsKey("countService")){
                    progressBar2.setProgress(msg.getData().getInt("countService"));
                    // Write data to shared preferences, Key,Value
                    editor.putInt("Counter",msg.getData().getInt("countService"));
                    editor.commit(); // Commit this
                }

            }
        };

        thread = new Thread(new SecondThread(handler));
        thread.start();


        //  = new Handler(getMainLooper());




        Button startServiceButton = (Button) findViewById(R.id.startServiceButton);
        Button stopServiceButton = (Button) findViewById(R.id.endServiceButton);


        startServiceButton.setOnClickListener(this);
        stopServiceButton.setOnClickListener(this);


        intentFilter = new IntentFilter("StartProgressBar");
        br = new myReciever(); // makes a new broadcast reciever
        this.registerReceiver(br, intentFilter);



    }






    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.startServiceButton) {
            startService();
            Log.d(TAG, "onClick: TESTETSET");
        }
        else if(v.getId() == R.id.endServiceButton)
            stopService();


    }



    public void startService() {

        Messenger myMessenger = new Messenger(handler);

        Intent serviceIntent = new Intent(this, TimeService.class);
        serviceIntent.putExtra("handlerRef", myMessenger);
        startService(serviceIntent);

        //startService(new Intent(this, TimeService.class));

    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    public void stopService() {

        stopService(new Intent(this, TimeService.class));
    }

    public class SecondThread implements Runnable{

        Handler mainHandler;
        Message msg;
        Bundle bundle = new Bundle();
        public SecondThread(Handler mainHandler){
            this.mainHandler = mainHandler;
        }


        @Override
        public void run() {
            for(int i = 0; i<100; i++){
                Log.d(TAG, "" + i);
                msg = mainHandler.obtainMessage();
                bundle.putInt("count", i);
                msg.setData(bundle);
                mainHandler.sendMessage(msg);
                try {
                    Thread.sleep(50);
                }catch(InterruptedException e){}

            }
        }
    }



    public class myReciever extends BroadcastReceiver{

        Bundle extras;
        @Override
        public void onReceive(Context context, Intent intent) {
            extras = intent.getExtras();
            if(extras.containsKey("countService")){
                Log.d(TAG, "onReceive: test");
                Toast.makeText(context, "" + extras.getInt("countService"), Toast.LENGTH_SHORT).show();
            }
        }
    }





}
