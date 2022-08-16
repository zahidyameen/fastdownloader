package com.example.fastdownloader.sample;

import static com.example.fastdownloader.utils.Utils.ConvertSize;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.fastdownloader.Error;
import com.example.fastdownloader.OnCancelListener;
import com.example.fastdownloader.OnDownloadListener;
import com.example.fastdownloader.OnPauseListener;
import com.example.fastdownloader.OnProgressListener;
import com.example.fastdownloader.OnStartOrResumeListener;
import com.example.fastdownloader.PRDownloader;
import com.example.fastdownloader.Progress;
import com.example.fastdownloader.R;
import com.example.fastdownloader.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

public class DownloadService extends Service {
    private static final int NOTIFICATION_ID = 121;
    private int FLAG=START_STICKY;
    private boolean isBind=true;
    private static final String NOTIFICATION_CHANNEL_ID ="notification_channel_id";
    private static final String NOTIFICATION_Service_CHANNEL_ID = "service_channel";
    private int i=0;
    @Override
    public void onCreate() {
        super.onCreate();
        //Start download process
        downloadFile();
    }
    private final IBinder binder = new LocalBinder();
    // Registered callbacks
    private DownloadListener serviceCallbacks;
    // Class used for the client Binder.
    public class LocalBinder extends Binder {
        DownloadService getService() {
            // Return this instance of MyService so clients can call public methods
            return DownloadService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    public void setCallbacks(DownloadListener callbacks) {
        serviceCallbacks = callbacks;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String intentFilter=intent.getAction();
        if(intentFilter!=null){
            switch (intentFilter){
                case Strings.DOWNLOAD:
                    String url=intent.getStringExtra(Strings.URL);
                    String name=intent.getStringExtra(Strings.NAME);
                    Log.e("sssskkssk",url+"\n"+name);
                    addToDownload(url,name);
                    break;
                case Strings.PAUSE:
                    pauseDownload(intent.getIntExtra(Strings.ID,0));
                    break;
                case Strings.RESUME:
                    resumeDownload(intent.getIntExtra(Strings.ID,0));
                    break;
                case Strings.CANCEL:
                    cancelDownload(intent.getIntExtra(Strings.ID,0));
                    break;
                case Strings.CANCEL_ALL:
                    cancelAll();
                    break;
                case Strings.CLEAN_UP:
                    cleanUP(intent.getIntExtra(Strings.DAYS,0));
                    break;
            }
        }
        return FLAG;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable

    private void downloadFile() {
        startInForeground();
        //Logic to download the file.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(i==10){
                    isBind=false;
                    stopForeground(true);
                }
                i++;
            }
        },100);
    }
    private void addToDownload(String url,String name){
        String filePath=  Utils.SetFolderForGreaterThanLollipop(this).getAbsolutePath()+"/"+name;
        PRDownloader.download(this,url,name)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                       // EventBus.getDefault().post(new MessageEvent());
                        if (serviceCallbacks != null) {
                            serviceCallbacks.refresh();
                        }
                        Log.e("dkkdkkdkkkd","Started");
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {
                        Log.e("dkkdkkdkkkd","onPause");
                        if (serviceCallbacks != null) {
                            serviceCallbacks.refresh();
                        }
                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {
                        Log.e("dkkdkkdkkkd","onCancel");
                        if (serviceCallbacks != null) {
                            serviceCallbacks.refresh();
                        }
                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    long lastDownload=0;
                    @Override
                    public void onProgress(Progress progress) {
                        Log.e("dkkdkkdkkkd","Total:"+ConvertSize(progress.totalBytes)+
                                "\t"+"Downloaded:"+ConvertSize(progress.currentBytes)
                                +"\t"+"speed:"+ConvertSize(progress.receivedBytes));
                        if (serviceCallbacks != null) {
                            serviceCallbacks.refresh();
                        }

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.e("dkkdkkdkkkd","download completed ");
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
                        if (serviceCallbacks != null) {
                            serviceCallbacks.refresh();
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        Log.e("dkkdkkdkkkd","error:"+error.getServerErrorMessage());
                        if (serviceCallbacks != null) {
                            serviceCallbacks.refresh();
                        }
                    }
                });
    }
    private void pauseDownload(int id){
        PRDownloader.pause(id);
    }
    private void resumeDownload(int id){
        PRDownloader.pause(id);
    }
    private void cancelDownload(int id){
        PRDownloader.cancel(id);
    }
    private void cancelAll(){
        PRDownloader.cancelAll();
    }
    private void cleanUP(int day){
        PRDownloader.cleanUp(day);
    }
    private void startInForeground() {
        int icon = R.mipmap.ic_launcher;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,notificationIntent,0);
        Intent buttonIntent = new Intent( this, DownloadBroadcast. class ) ;
        buttonIntent.putExtra( "notificationId" , NOTIFICATION_ID) ;
        PendingIntent btPendingIntent = PendingIntent. getBroadcast ( this, 0 , buttonIntent , 0 ) ;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentIntent(pendingIntent)
                .setContentTitle("FastDownloading ")
                .setContentText("Downloading...")
                .addAction(0, getString(R.string.cancel),
                        btPendingIntent);;
        Notification notification=builder.build();
        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_Service_CHANNEL_ID,
                    "Fast Downloading", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Service Name");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);

            notification = new Notification.Builder(this,NOTIFICATION_Service_CHANNEL_ID)
                    .setContentTitle("Service")
                    .setContentText("Running...")
                    .setSmallIcon(icon)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        startForeground(NOTIFICATION_ID, notification);
    }
    @Override
    public boolean onUnbind(Intent intent) {
        return isBind;
    }
}