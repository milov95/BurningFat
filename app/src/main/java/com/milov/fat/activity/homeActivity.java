package com.milov.fat.activity;

import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.milov.fat.R;
import com.milov.fat.fragment.HomeFragment;
import com.milov.fat.fragment.MissionFragment;
import com.milov.fat.fragment.PersonalFragment;

public class HomeActivity extends AppCompatActivity implements HomeFragment.HomeFragClickListener,PersonalFragment.PersonFragClickListener,MissionFragment.MissionFragClickListener{

    private HomeFragment homeFragment;
    private PersonalFragment personalFragment;
    private MissionFragment missionFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);
        //加载HomeFragment
        //Activity在旋屏或从后台切回时有时会重新启动，这时原有的Fragment也会重启，同时又会执行一遍onCreat
        //原来的Fragment会存储在savedInstanceState里，为了避免过多的Fragment产生，这里执行一次判断
        if(savedInstanceState == null){
            homeFragment = new HomeFragment();
            fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.home_content,homeFragment,null);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onHomeFragClick(View view){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()){
            //打开PersonalFragment并加入回退栈
            case R.id.personal_TextView:
                if(personalFragment == null){
                    personalFragment = new PersonalFragment();
                }
                fragmentTransaction.hide(homeFragment);
                fragmentTransaction.add(R.id.home_content,personalFragment,null);
                fragmentTransaction.addToBackStack(null);
                break;
            //打开MissionFragment并加入回退栈
            case R.id.mission_TextView:
                if(missionFragment == null){
                    missionFragment = new MissionFragment();
                }
                fragmentTransaction.hide(homeFragment);
                fragmentTransaction.add(R.id.home_content,missionFragment,null);
                fragmentTransaction.addToBackStack(null);
                break;
        }
        fragmentTransaction.commit();

    }

    @Override
    public void onPersonFragClick(View view){
        switch (view.getId()){
            case R.id.back_personal_image:
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(personalFragment);
                fragmentTransaction.show(homeFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onMissionFragClick(View view){
        switch (view.getId()){
            case R.id.back_mission_image:
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(missionFragment);
                fragmentTransaction.show(homeFragment);
                fragmentTransaction.commit();
                break;
        }
    }


}
