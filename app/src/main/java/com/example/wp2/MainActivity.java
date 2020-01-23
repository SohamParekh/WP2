package com.example.wp2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    boolean isfirstRun;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences("PREFERENCE",MODE_PRIVATE);
        isfirstRun = preferences.getBoolean("isfirstRun",true   );
        if(isfirstRun) {
            onfirst();
        }
    }

    public void doSomething(View view) {
        Intent intent = new Intent(this,ActivityOne.class);
        startActivity(intent);
    }
    public void onfirst(){
       // boolean isfirstRun = getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("isfirstRun",true);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Terms & Conditions")
                    .setMessage("T&C")
                    .setNegativeButton("Decline",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .setPositiveButton("Accept",new DialogInterface.OnClickListener(){

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        SharedPreferences preferences = getSharedPreferences("PREFERENCE",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isfirstRun",false);
        editor.apply();

    }
}
