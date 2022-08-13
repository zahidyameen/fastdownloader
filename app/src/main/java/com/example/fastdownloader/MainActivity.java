package com.example.fastdownloader;

import static com.example.fastdownloader.utils.Utils.ConvertSize;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.fastdownloader.utils.Utils;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    public  final int STORAGE_PERMISSION_CODE = 20;
    private  String[] s={Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    String url="https://rr2---sn-uxaxovg23-aixl.googlevideo.com/videoplayback?expire=1660411515&ei=Gor3YujCO-v6xN8Pgdq9kAM&ip=103.149.33.18&id=o-ANd1MQHqw1b1GPSRB5_A9FRhuDWtR1JMqSEHF7pugT7O&itag=137&aitags=133%2C134%2C135%2C136%2C137%2C160%2C242%2C243%2C244%2C247%2C248%2C278%2C394%2C395%2C396%2C397%2C398%2C399&source=youtube&requiressl=yes&mh=Go&mm=31%2C29&mn=sn-uxaxovg23-aixl%2Csn-4wg7zne7&ms=au%2Crdu&mv=m&mvi=2&pl=24&initcwndbps=203750&spc=lT-KhipWk18-7WjEfUrto3BG9XjROHurh56q5HeJQXQA&vprv=1&mime=video%2Fmp4&ns=m0mkOmBGh5p6hfAzFpefSYMH&gir=yes&clen=29818073&dur=304.360&lmt=1660343365841371&mt=1660389660&fvip=5&keepalive=yes&fexp=24001373%2C24007246&c=WEB&rbqsm=fr&txp=5535434&n=E0UvUO1ZiObpOw&sparams=expire%2Cei%2Cip%2Cid%2Caitags%2Csource%2Crequiressl%2Cspc%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cdur%2Clmt&sig=AOq0QJ8wRQIgYldUo7Gvc94spUJK-cgdqAoACbuxeSHYtF7EF9pTn8ACIQDSlUk8psCj-gQFPt2cNmD4yvtpLaEPOJ1LL_BwNIBuig%3D%3D&lsparams=mh%2Cmm%2Cmn%2Cms%2Cmv%2Cmvi%2Cpl%2Cinitcwndbps&lsig=AG3C_xAwRAIgBdqKTKVzVL2g365QPS0ERf5XuIjvqRwWMNk2BEwnrRACIE2ZRRS9Ml_bXA052wCfU5JANgGOXDOtayyhVhiIsibU&pot=GpIBCmX4pvHN_P5wte6RsEavITvFwtReq-BxiwMWqXCIjtPBgt3QWUggY9KKqFDSV64ZPT4MOkAYUNnsbKUmVrGv63paiPqwVd15J6ADAcN22gRiulg79e0tH2It_eDuwFCbmBu6rbY6XxIpAX04kIirLINbOvScwGSX73wD6OYIBbqA0DMrzYXQEF_E_5z-XksImkc%3D";
    String filePath= "";
    String name=System.currentTimeMillis()+".mp4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        filePath=Utils.SetFolderForGreaterThanLollipop(this).getAbsolutePath()+"/"+name;
        // Enabling database for resume support even after the application is killed:
        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .setFolder(this)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);


    }

    private void download(){
        int downloadId = PRDownloader.download(this,url,name)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        Log.e("dkkdkkdkkkd","download started  ");

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    long lastDownload=0;
                    @Override
                    public void onProgress(Progress progress) {
                        Log.e("dkkdkkdkkkd","Total:"+ConvertSize(progress.totalBytes)+
                                "\t"+"Downloaded:"+ConvertSize(progress.currentBytes)
                        +"\t"+"speed:"+ConvertSize(progress.receivedBytes));

                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        Log.e("dkkdkkdkkkd","download completed ");
                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
    }
    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivity.this,  s , requestCode);
        }
        else {
            download();
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
                download();
            }
            else {
                Toast.makeText(MainActivity.this, "Camera Permission Denied", Toast.LENGTH_SHORT) .show();
            }
        }

    }
}