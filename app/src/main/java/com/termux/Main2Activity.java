package com.termux;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Main2Activity extends AppCompatActivity {
    String data[] = {"212101","212102","212103"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main2);
        Bundle b1 = getIntent().getExtras();
        String val = b1.getString("detail");
        Bundle b2 = getIntent().getExtras();
        String val2 = b2.getString("detail1");
        TextView tv = (TextView)findViewById(R.id.textView2);
        tv.setText(val);
        TextView t = (TextView)findViewById(R.id.textView1);
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        Button btn = (Button)findViewById(R.id.button);
        if(val2.equals("true")){
            t.setText(val2);
            ArrayAdapter<String> myadapter = new ArrayAdapter<String>(Main2Activity.this,
                android.R.layout.simple_list_item_1, data);
            myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(myadapter);
        }
        else{
            t.setText("Is not a WPA");
            spinner.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.INVISIBLE);
        }


    }

    public void doSomething(View view) {
        Toast.makeText(this,"Button Clicked", Toast.LENGTH_SHORT).show();
    }
}
