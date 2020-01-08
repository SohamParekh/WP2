package com.example.wp2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onfirst();
    }

    public void doSomething(View view) {
        Intent intent = new Intent(this,ActivityOne.class);
        startActivity(intent);
    }
    public void onfirst(){
        boolean isfirstRun = getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("isfirstRun",true);
        if(isfirstRun){
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
                            getSharedPreferences("PREFERENCE",MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("isfirstRun ",false)
                                    .apply();
                        }
                    }).show();
        }

    }
}
