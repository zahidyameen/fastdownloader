package com.example.fastdownloader.sample;

import static com.example.fastdownloader.utils.Utils.ConvertSize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.fastdownloader.Error;
import com.example.fastdownloader.OnCancelListener;
import com.example.fastdownloader.OnDownloadListener;
import com.example.fastdownloader.OnPauseListener;
import com.example.fastdownloader.OnProgressListener;
import com.example.fastdownloader.OnStartOrResumeListener;
import com.example.fastdownloader.PRDownloader;
import com.example.fastdownloader.PRDownloaderConfig;
import com.example.fastdownloader.Progress;
import com.example.fastdownloader.R;
import com.example.fastdownloader.database.AppDbHelper;
import com.example.fastdownloader.database.DownloadModel;
import com.example.fastdownloader.database.NoOpsDbHelper;
import com.example.fastdownloader.internal.ComponentHolder;
import com.example.fastdownloader.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.net.URL;
import java.util.List;

public class MainActivity extends BaseClass  implements DownloadListener{
    private DownloadAdapter downloadAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton floating;
    private DownloadService myService;
    private boolean bound = false;
    private List<DownloadModel> list;
    public  final int STORAGE_PERMISSION_CODE = 20;
    private  String[] s={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    String url="https://rr1---sn-4wg7ln7e.googlevideo.com/videoplayback?expire=1660645886&ei=nh37Yuv8E_HYxN8Pmu-EyAw&ip=103.149.33.18&id=o-ADOxDn-NwRnAZ2EJds45T-B8P8XYfOjMqqfCHECGwhPi&itag=134&aitags=133%2C134%2C135%2C136%2C137%2C160%2C242%2C243%2C244%2C247%2C248%2C278&source=youtube&requiressl=yes&spc=lT-KhvaRY-jV8QtDxLMIMgYujj13vc8&vprv=1&mime=video%2Fmp4&ns=JIKyUb-ktOZGkyL4chCu5XsH&gir=yes&clen=25927362&dur=640.272&lmt=1658849633217989&keepalive=yes&fexp=24001373,24007246&c=WEB&rbqsm=fr&txp=5432434&n=MdIV7qI01hk8mw&sparams=expire%2Cei%2Cip%2Cid%2Caitags%2Csource%2Crequiressl%2Cspc%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cdur%2Clmt&sig=AOq0QJ8wRAIgdIN6mQBHDc04hQquBv584C6Qez-n7hXSfyo961REbGwCIGfQsiEdYFV9e_KHQPFm0OaCxXP0TAWHi1wOYEIH2wXV&pot=GpsBCm5JU1IM9ftWoEoP7Zz6gDHkYB2jW5GGKf9OWb31x-26JfEiE-HYaddiCVyr-GOWiPE2hxTvzy420mxcQqKua8TECRV_8hpn4gOr_uyEreqxTT1fZPndOYWTFTtIsLSC5jCianW0UKvp_o80gquuUhIpAX04kIjbV42x8mtvZfdXph-8lUARnTUffXM6uxGbdT2GIpcJ4DZmNGs%3D&redirect_counter=1&rm=sn-uxaxovg23-aixe7s&req_id=1827e0fff0e1a3ee&cms_redirect=yes&cmsv=e&mh=o4&mm=29&mn=sn-4wg7ln7e&ms=rdu&mt=1660624146&mv=m&mvi=1&pl=24&lsparams=mh,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRgIhAOJFXl7MhSd1qfqTQU3YYR7k2O7R4PpbGjjyaCMG5671AiEAhz-akrevmpPs6eHjIdwmZBTy-uBjzLwOJj5a9HeuCZM%3D";
    String name=System.currentTimeMillis()+".mp4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this,DownloadService.class); // Build the intent for the service
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.fastdownloader.sample.Strings.DOWNLOAD");
        filter.addAction("com.example.fastdownloader.sample.Strings.PAUSE");
        filter.addAction("com.example.fastdownloader.sample.Strings.RESUME");
        filter.addAction("com.example.fastdownloader.sample.Strings.CANCEL");
        filter.addAction("com.example.fastdownloader.sample.Strings.CANCEL_ALL");
        filter.addAction("com.example.fastdownloader.sample.Strings.CLEAN_UP");
        DownloadBroadcast myReceiver = new DownloadBroadcast();
        registerReceiver(myReceiver, filter);
        // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .setFolder(this)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        recyclerView=findViewById(R.id.rec);
        floating=findViewById(R.id.floating);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
         list= ComponentHolder.getInstance().getDbHelper().getAllModels();
        Log.e("sssskkssk","MainActivity:"+list.size());
        downloadAdapter=new DownloadAdapter(this,list);
        recyclerView.setAdapter(downloadAdapter);
        floating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
            }
        });


    }
    @Override
    protected void onStart() {
        super.onStart();
        // bind to Service
        Intent intent = new Intent(this, DownloadService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from service
        if (bound) {
            myService.setCallbacks(null); // unregister
            unbindService(serviceConnection);
            bound = false;
        }
    }

    /** Callbacks for service binding, passed to bindService() */
    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // cast the IBinder and get MyService instance
            DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
            myService = binder.getService();
            bound = true;
            myService.setCallbacks(MainActivity.this); // register
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };



    private void download(){
        Log.e("sssskkssk","MainActivity");
//        int downloadId = PRDownloader.download(this,url,name)
//                .build()
//                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
//                    @Override
//                    public void onStartOrResume() {
//                        Log.e("dkkdkkdkkkd","download started  ");
//
//                    }
//                })
//                .setOnPauseListener(new OnPauseListener() {
//                    @Override
//                    public void onPause() {
//
//                    }
//                })
//                .setOnCancelListener(new OnCancelListener() {
//                    @Override
//                    public void onCancel() {
//
//                    }
//                })
//                .setOnProgressListener(new OnProgressListener() {
//                    long lastDownload=0;
//                    @Override
//                    public void onProgress(Progress progress) {
//                        Log.e("dkkdkkdkkkd","Total:"+ConvertSize(progress.totalBytes)+
//                                "\t"+"Downloaded:"+ConvertSize(progress.currentBytes)
//                        +"\t"+"speed:"+ConvertSize(progress.receivedBytes));
//
//                    }
//                })
//                .start(new OnDownloadListener() {
//                    @Override
//                    public void onDownloadComplete() {
//                        Log.e("dkkdkkdkkkd","download completed ");
//                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
//                    }
//
//                    @Override
//                    public void onError(Error error) {
//
//                    }
//                });


        Intent intent1 = new Intent();
        intent1.putExtra(Strings.URL,url);
        intent1.putExtra(Strings.NAME,name);
        intent1.setAction("com.example.fastdownloader.sample.Strings.DOWNLOAD");
        intent1.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent1);
        Log.e("sssskkssk","MainActivity");
    }
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,  s , requestCode);
        }
        else {
           // download();
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);


        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Camera Permission Granted", Toast.LENGTH_SHORT) .show();
               // download();
            }
            else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }

    }
    @Override
    public void refresh() {
        if(downloadAdapter!=null)
            list= ComponentHolder.getInstance().getDbHelper().getAllModels();
            downloadAdapter.refresh(list);
    }
}