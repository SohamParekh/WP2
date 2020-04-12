package com.termux;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.termux.app.TermuxActivity;

public class ActivityOne extends AppCompatActivity {
    boolean v=false;
    TabLayout mytab;
    ViewPager mypage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_one);

        mytab = (TabLayout)findViewById(R.id.tab);
        mypage = (ViewPager)findViewById(R.id.page);

        mypage.setAdapter(new MyOwnPagerAdapter(getSupportFragmentManager()));
        mytab.setupWithViewPager(mypage);
        mytab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mypage.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    class MyOwnPagerAdapter extends FragmentPagerAdapter{

        String data[] = {"Payload","Phishing"};
        public MyOwnPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return new Payload();
            if(position == 1)
                return new Phishing();
            /*if (position == 2)
                return new wifiaccess();*/
            return null;
        }

        @Override
        public int getCount() {
            return data.length;
        }
        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return data[position];
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
            .setMessage("Are you sure you want to exit?")
            .setCancelable(false)
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityOne.this.finishAffinity();
                }
            })
            .setNegativeButton("No", null)
            .show();
    }

    public void startTermuxActivity(View view) {

        Intent intent = new Intent(ActivityOne.this, TermuxActivity.class);
        startActivity(intent);
    }
    public void startWifiActivity(View view){
        Intent intent = new Intent(ActivityOne.this, wifiactivity.class);
        startActivity(intent);
    }
}
