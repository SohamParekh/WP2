package com.termux;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class wifiactivity extends AppCompatActivity {
    String mTitle[] = {"Wifi-1", "Wifi-2", "Wifi-3", "Wifi-4"};
    String mDescription[] = {"mac: AA:BB:CC", "mac: AA:CC:BB", "mac: AA:BB:CC", "mac: AA:BB:CC"};
    int images[] = {R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground};

    String mIsWPA[] = {"true","false","true","false"};
    //Boolean mIsWPA[] = {true,false,true,false};
    // String data[] = {"Test 1","Test 2","Test 3"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_wifiactivity);


        ListView  lv = (ListView)findViewById(R.id.lv);
        MyAdapter adapter = new MyAdapter(this, mTitle, mDescription, images, mIsWPA);
        lv.setAdapter(adapter);
        /*ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        lv.setAdapter(arrayAdapter);*/
        /*for (String s: mIsWPA) {
            if(s.equals("true")){
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
                        intent.putExtra("detail",mTitle[position]);
                        intent.putExtra("detail1",mDescription[position]);
                        startActivity(intent);
                    }
                });
            }
        }*/
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
                intent.putExtra("detail",mTitle[position]);
                intent.putExtra("detail1",mIsWPA[position]);

                startActivity(intent);
            }
        });
    }
    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        String rTitle[];
        String rDescription[];
        int rImgs[];
        String mIsWPA[];

        MyAdapter(Context c, String[] title, String[] description, int[] imgs,String[] mIsWPA) {
            super(c, R.layout.row, R.id.textView1, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImgs = imgs;
            this.mIsWPA = mIsWPA;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View row = layoutInflater.inflate(R.layout.row, parent, false);
            ImageView images = row.findViewById(R.id.image);
            TextView myTitle = row.findViewById(R.id.textView1);
            TextView myDescription = row.findViewById(R.id.textView2);
            TextView isWpa = row.findViewById(R.id.textView3);
            isWpa.setVisibility(View.INVISIBLE);
            // now set our resources on views
            images.setImageResource(rImgs[position]);
            myTitle.setText(rTitle[position]);
            myDescription.setText(rDescription[position]);
            //isWpa.setText("IsWPA");
            //isWpa.setText("IsWPA : " + mIsWPA[position]);
            //String temp = isWpa.getText().toString();
            /*String temp = mIsWPA[position];
            CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox1);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        if(temp.equals("true")){
                            //Toast.makeText(wifiactivity.this,"Item true"+mIsWPA,Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            });*/

            if(mIsWPA[position] == "true"){
                row.setBackgroundColor(Color.parseColor("#bada55"));
                isWpa.setVisibility(View.VISIBLE);
                isWpa.setText("IsWpa");
            }

            return row;
        }
    }
}
