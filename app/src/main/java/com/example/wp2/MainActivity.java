package com.example.wp2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import termux_helper.app.TermuxInstaller;
import termux_helper.app.TermuxService;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    public static final String ACTION_EXECUTE = "com.example.wp2.service_execute";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        installTermuxDependecies();
        startTermuxService();
//        onfirst();

    }

    private void startTermuxService() {
        Uri command = Uri.parse("pkg");
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
        Intent intent = new Intent(this,ActivityOne.class);
        startActivity(intent);
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
