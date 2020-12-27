package com.example.funcenter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.example.funserveraidl.AIDLFunServer;

public class MyService extends Service {

    private Bitmap bmp;                                   // variable for storing a bitmap

    private int image;
    private int audio_clip;

    private MediaPlayer mPlayer;                        // Using the inbuilt Media Player
    private static final int NOTIFICATION_ID = 1;       // Notification ID for starting Foreground
    private Notification notification ;                 // Using inbuilt notification function
    private static String CHANNEL_ID = "Notification channel" ;

    private int newNumber;
    private final String flag1 = "flag1";
    private final String flag2 = "flag2";
    private final String flag3 = "flag3";
    private final String flag4 = "flag4";

    @Override
    public void onCreate() {
        super.onCreate();

        //this.createNotificationChannel();
        Log.i("MyService:","Entered create");

        final Intent notificationIntent = new Intent();                                                                              // Base intent to start FunClient
        notificationIntent.setComponent(new ComponentName("com.example.funclient","com.example.funclient.MainActivity"));
        notificationIntent.setAction(Intent.ACTION_MAIN);

        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,                                    // Pending intent for notification that starts the FunClient activity
                notificationIntent, 0) ;
        notification =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.arrow_down_float)
                        .setFullScreenIntent(pendingIntent, false)
                        .setOngoing(true).setContentTitle("FunClient Playing")
                        .setContentText("Click to Access FunClient")
                        .setTicker("Music is playing!")
                        .build();
        Log.i("MyService:","creating notification completed");
        startForeground(NOTIFICATION_ID, notification); // starting Foreground
        Log.i("MyService:","started service");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {                 //creation of notification
            CharSequence name = "FunClient notification";
            String description = "The channel for FunClient notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private final AIDLFunServer.Stub mBinder = new AIDLFunServer.Stub() {                     // AIDL interface methods that define the functionality
        @Override
        public Bitmap getImage(int number) throws RemoteException {
            Resources red = getApplicationContext().getResources();
            synchronized (flag1){
                if(number == 1){
                    image = R.drawable.one;
                }else if (number == 2){
                    image = R.drawable.two;
                }else {
                    image = R.drawable.three;
                }

            }
            bmp = BitmapFactory.decodeResource(red,image);
            return bmp;
        }

        @Override
        public void playSong(int number) throws RemoteException {             //Play the song with song number


            Log.i("MyService:","entered play and song number is:"+number);
            if(null != mPlayer){
                newNumber = number;
                if(newNumber != audio_clip){
                    if(mPlayer.isPlaying()){
                        mPlayer.stop();
                    }
                    mPlayer.reset();
                    synchronized (flag2){
                        if (number == 1){
                            mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.one);
                        }else if(number == 2){
                            mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.two);
                        }else{
                            mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.three);
                        }
                    }
                    if(!mPlayer.isPlaying()){
                        mPlayer.start();
                    }

                }else{
                    synchronized (flag3){
                        if (number == 1){
                            mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.one);
                        }else if(number == 2){
                            mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.two);
                        }else{
                            mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.three);
                        }
                    }
                    if(!mPlayer.isPlaying()){
                        mPlayer.start();
                    }
                }

            }else{
                audio_clip = number;
                synchronized (flag4){
                    if (number == 1){
                        mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.one);
                    }else if(number == 2){
                        mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.two);
                    }else{
                        mPlayer = MediaPlayer.create(getApplicationContext(),R.raw.three);
                    }
                }
                mPlayer.start();
            }

        }

        @Override
        public void pauseSong(int number) throws RemoteException {  // Pause the song with song number
            Log.i("MyService","Entered pause");
            if(null != mPlayer){
                Log.i("MyService","mPlayer is not null");
                if(mPlayer.isPlaying()){
                    Log.i("MyService","mPlayer is not null");
                    mPlayer.pause();
                }
            }else{
                Log.i("MyService","mPlayer is null");
            }

        }

        @Override
        public void resumeSong(int number) throws RemoteException {     // resume the song with song number
            if (null != mPlayer) {
                mPlayer.start();
            }
        }

        @Override
        public void stopSong(int number) throws RemoteException {             // stop the song with song number
            if (null != mPlayer) {
                if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                    mPlayer.reset();

                }
            }
        }

        @Override
        public void stopServ() throws RemoteException {
            Log.i("MyService:","Entered stopServ");
            stopSelf();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;                                                    // return the proxy to the client
    }
    public void onDestroy() {

        if (null != mPlayer) {

            mPlayer.stop();                                                // stopping the Media Player
            mPlayer.reset();

        }
    }
}


