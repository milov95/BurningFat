package com.milov.fat.activity;

import android.app.Activity;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.huawei.huaweiwearable.data.DataTodayTotalMotion;
import com.huawei.huaweiwearable.data.DataTotalMotion;
import com.milov.fat.R;
import com.milov.fat.fragment.HomeFragment;
import com.milov.fat.fragment.MissionFragment;
import com.milov.fat.fragment.PersonalFragment;
import com.milov.fat.util.HuaweiWearableHelper;

import java.util.List;

public class HomeActivity extends Activity implements HomeFragment.HomeFragClickListener,
        PersonalFragment.PersonFragClickListener,MissionFragment.MissionFragClickListener{

    /**
     * 首页
     */
    private HomeFragment homeFragment;
    /**
     * 个人中心
     */
    private PersonalFragment personalFragment;
    /**
     * 任务中心
     */
    private MissionFragment missionFragment;
    /**
     * 用于管理Fragment
     */
    private FragmentManager fragmentManager;
    /**
     * 华为工具类的实例，用于获取可穿戴数据
     */
    private HuaweiWearableHelper huawei;
    /**
     * 用于响应HuaweiWearableHelper状态回调的Handler
     */
    private static MyHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity_layout);

        handler = new MyHandler();

        //加载HomeFragment
        //Activity在旋屏或从后台切回时有时会重新启动，这时原有的Fragment也会重启，同时又会执行一遍onCreat
        //原来的Fragment会存储在savedInstanceState里，为了避免过多的Fragment产生，这里执行一次判断
        if(savedInstanceState == null){
            homeFragment = new HomeFragment();
            fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.home_content,homeFragment,null);
            fragmentTransaction.commit();
        }
        //实例化HuaweiWearleHelper工具类
        if(huawei == null )
            huawei = new HuaweiWearableHelper();
        //向HuaweiWearableHelper类传递Context
        huawei.onActivityCreated(this);

    }

    @Override
    public void onStart(){
        super.onStart();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                homeFragment.deviceStatusText.setText(huawei.getDeviceStatus()+"");
                huawei.getTodayHealthData();
            }
        }, 500);
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
            //回到HomeFragment
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
            //回到HomeFragment
            case R.id.back_mission_image:
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(missionFragment);
                fragmentTransaction.show(homeFragment);
                fragmentTransaction.commit();
                break;
        }
    }

    /**
     * 处理HuawerWearableHelper状态回调数据
     */
    public class MyHandler extends Handler{
        public MyHandler(){
            super();
        }

        @Override
        public void handleMessage(Message msg){
            Object obj = msg.obj;
            switch (msg.what){
                case HuaweiWearableHelper.TODAY_HEALTH:
                    int steps = 0,calorie = 0;
                    if(obj!=null){
                        calorie = ((DataTodayTotalMotion) obj).getTotalCalorie();
                        List<DataTotalMotion> list = ((DataTodayTotalMotion) obj).getDataTotalMotions();
                        for(DataTotalMotion motion:list){
                            steps+=motion.getStep();
                        }
                    }
                    final int innerCal = calorie;
                    final int innerSteps = steps;
                    //我也不知道这TM是什么鬼导致我的handler成了非主线程的了！！！
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            homeFragment.stepsText.setText(innerSteps +"");
                            homeFragment.calText.setText(innerCal+"");
                        }
                    });
                    break;

                case HuaweiWearableHelper.USER_INFO:
                    break;
            }
        }
    }

    public static Handler getHandler(){
        return handler;
    }
    /**
     * 用于传递HomeActivity的Context的回调接口
     */
    public interface ActivitiCallback{
        void onActivityCreated(Context context);
    }
}
