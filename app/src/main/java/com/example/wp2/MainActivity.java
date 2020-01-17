package com.example.wp2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import termux_helper.app.TermuxInstaller;
import termux_helper.app.TermuxService;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startTermuxService();
        installTermuxDependecies();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onfirst();
    }

    private void startTermuxService() {
        Intent serviceIntent = new Intent(this, TermuxService.class);
        startService(serviceIntent);

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
