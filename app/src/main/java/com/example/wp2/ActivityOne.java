package com.example.wp2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;

public class ActivityOne extends AppCompatActivity {

    GridLayout mainGrid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_one);
        mainGrid = (GridLayout) findViewById(R.id.mainGrid);

        //Set Event
        //setSingleEvent(mainGrid);
    }

    public void doSomething(View view) {
        Intent intent = new Intent(this,ActivityTwo.class);
        startActivity(intent);
    }

    private void setSingleEvent(GridLayout mainGrid) {
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(ActivityOne.this,ActivityTwo.class);
                    intent.putExtra("info","This is activity from card item index  "+finalI);
                    startActivity(intent);

                }
            });
        }
    }

    public void doSomething1(View view) {
        Intent intent = new Intent(this,ActivityThree.class);
        startActivity(intent);
    }

    public void doSomething2(View view) {
        Intent intent = new Intent(this,ActivityFour.class);
        startActivity(intent);
    }
}
