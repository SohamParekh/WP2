package com.example.wp2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

import termux_helper.app.BackgroundJob;
import termux_helper.app.TermuxInstaller;
import termux_helper.app.TermuxService;

import static termux_helper.app.BackgroundJob.getPid;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private static final String LOG_TAG = "wp2-task";
    Process mProcess;
    public static final String ACTION_EXECUTE = "com.example.wp2.service_execute";
    String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
    Integer[] requestCodes = new Integer[]{101,102};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for(int i = 0; i < permissions.length; i++ ){
            checkPermissions(permissions[i],requestCodes[i]);
        }

        installTermuxDependecies();
//        onfirst();

    }

    private void checkPermissions(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {
           ActivityCompat.requestPermissions(this,new String[]{permission},requestCode);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch(requestCode) {
//            case 101:
//                if(grantResults.length > 0){
//                    boolean externalStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    System.out.println(externalStorageAccepted);
//                }
//            case 102:
//                if(grantResults.length > 0){
//                    boolean externalStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                    System.out.println(externalStorageAccepted);
//                }
//        }
//
//    }

    private void startProcess() {
        String command = "pwd";
        String[] env = BackgroundJob.getEnvironment();
        Process process;
        try{
            process = Runtime.getRuntime().exec(command,env);
        } catch (Exception e) {
            Log.e("wp2-task","Exception occurred: " + command,e);
            return;
        }

        mProcess = process;
        final int pid = getPid(mProcess);
        final Bundle result = new Bundle();
        final StringBuilder outResult = new StringBuilder();
        final StringBuilder errResult = new StringBuilder();

        final Thread errThread = new Thread() {
            @Override
            public void run() {
                InputStream stderr = mProcess.getErrorStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stderr, StandardCharsets.UTF_8));
                String line;
                try {
                    // FIXME: Long lines.
                    while ((line = reader.readLine()) != null) {
                        errResult.append(line).append('\n');
                        Log.i(LOG_TAG, "[" + pid + "] stderr: " + line);
                    }
                } catch (IOException e) {
                    // Ignore.
                }
            }
        };
        errThread.start();

        new Thread() {
            @Override
            public void run() {
                Log.i(LOG_TAG, "[" + pid + "] starting: " + command);
                InputStream stdout = mProcess.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stdout, StandardCharsets.UTF_8));

                String line;
                try {
                    // FIXME: Long lines.
                    while ((line = reader.readLine()) != null) {
                        Log.i(LOG_TAG, "[" + pid + "] stdout: " + line);
                        outResult.append(line).append('\n');
                    }
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error reading output", e);
                }

//                try {
//                    int exitCode = mProcess.waitFor();
//                    service.onBackgroundJobExited(BackgroundJob.this);
//                    if (exitCode == 0) {
//                        Log.i(LOG_TAG, "[" + pid + "] exited normally");
//                    } else {
//                        Log.w(LOG_TAG, "[" + pid + "] exited with code: " + exitCode);
//                    }
//
//                    result.putString("stdout", outResult.toString());
//                    result.putInt("exitCode", exitCode);
//
//                    errThread.join();
//                    result.putString("stderr", errResult.toString());
//
//                    Intent data = new Intent();
//                    data.putExtra("result", result);
//
//                    if(pendingIntent != null) {
//                        try {
//                            pendingIntent.send(service.getApplicationContext(), Activity.RESULT_OK, data);
//                        } catch (PendingIntent.CanceledException e) {
//                            // The caller doesn't want the result? That's fine, just ignore
//                        }
//                    }
//                } catch (InterruptedException e) {
//                    // Ignore
//                }
            }
        }.start();


    }

    private void startTermuxService() {
        Uri command = Uri.parse("echo 'HELLO WORLD'");
        Intent serviceIntent = new Intent(ACTION_EXECUTE,command,this, TermuxService.class);
        startService(serviceIntent);
//        try{
//            java.lang.Process p=Runtime.getRuntime().exec();
//            BufferedReader reader=new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String Process_ip;
//            StringBuffer b=new StringBuffer();
//            while((Process_ip=reader.readLine())!=null){
//                b.append("\n"+Process_ip);
//            }
//            System.out.println(b.toString());
//            reader.close();
//        }catch (IOException e){
//            Toast.makeText(this, "Unable to execute the command", Toast.LENGTH_SHORT).show();
//
//        }


        if (!bindService(serviceIntent, this, 0))
            throw new RuntimeException("bindService() failed");
    }

    private void installTermuxDependecies() {
        TermuxInstaller.setupIfNeeded(this, () -> {

        });

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    public void doSomething(View view) {
        startProcess();
//        Intent intent = new Intent(this,ActivityOne.class);
//        startActivity(intent);
    }
    public void onfirst(){
        boolean isfirstRun = getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("isfirstRun",true);
        if(isfirstRun){
           AlertDialog alertDialog =  new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Terms & Conditions")
                    .setMessage("T&C")
                    .setNegativeButton("Decline",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            finish();
                            System.exit(0);
                        }
                    })
                    .setPositiveButton("Accept",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            getSharedPreferences("PREFERENCE",MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("isfirstRun ",false)
                                    .apply();
                        }
                    }).create();
           alertDialog.show();
        }

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
}
