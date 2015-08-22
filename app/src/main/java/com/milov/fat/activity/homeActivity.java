package com.milov.fat.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import com.milov.fat.R;
import com.milov.fat.fragment.HomeFragment;

public class HomeActivity extends Activity {

    HomeFragment personalFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);

        FragmentManager fragmentManager= getFragmentManager();
        personalFragment = (HomeFragment) fragmentManager.findFragmentById(R.id.homeFragment);
    }
}
