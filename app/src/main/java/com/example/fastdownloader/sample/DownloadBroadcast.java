package com.example.fastdownloader.sample;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class DownloadBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("sssskkssk","Broadcast");
        Intent i=new Intent(context,DownloadService.class);
        switch (intent.getAction()){
            case "com.example.fastdownloader.sample.Strings.DOWNLOAD":
                i.putExtra(Strings.NAME,intent.getStringExtra(Strings.NAME));
                i.putExtra(Strings.URL,intent.getStringExtra(Strings.URL));
                i.setAction(Strings.DOWNLOAD);
                break;
             case "com.example.fastdownloader.sample.Strings.PAUSE":
                 i.putExtra(Strings.ID,intent.getStringExtra(Strings.ID));
                 i.setAction(Strings.PAUSE);
                break;
            case "com.example.fastdownloader.sample.Strings.RESUME":
                i.putExtra(Strings.ID,intent.getStringExtra(Strings.ID));
                i.setAction(Strings.RESUME);
                break;
            case "com.example.fastdownloader.sample.Strings.CANCEL":
                i.putExtra(Strings.ID,intent.getStringExtra(Strings.ID));
                i.setAction(Strings.CANCEL);
                break;
            case "com.example.fastdownloader.sample.Strings.CANCEL_ALL":
                i.setAction(Strings.CANCEL_ALL);
                break;
            case "com.example.fastdownloader.sample.Strings.CLEAN_UP":
                i.putExtra(Strings.DAYS,intent.getStringExtra(Strings.DAYS));
                i.setAction(Strings.CLEAN_UP);
                break;

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(i);
        }else {
            context.startService(i);
        }
    }
}