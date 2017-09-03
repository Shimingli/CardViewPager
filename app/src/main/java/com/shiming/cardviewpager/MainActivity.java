package com.shiming.cardviewpager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private CardViewPager mCardViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
    }

    private void findView() {
        mCardViewPager = (CardViewPager) findViewById(R.id.cardViewPager);
        mCardViewPager.setAllowScroll(false);
        ArrayList<View> viewSparseArray = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            View inflate = LayoutInflater.from(this).inflate(R.layout.text_layout, null);
            viewSparseArray.add(inflate);
            mCardViewPager.addView(inflate);
        }
    }
}
