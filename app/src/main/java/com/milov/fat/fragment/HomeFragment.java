package com.milov.fat.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.milov.fat.Animation.HomeAnim;
import com.milov.fat.R;
import com.milov.fat.view.HomeCurveView;

/**
 * Created by Milov on 2015/8/22.
 */
public class HomeFragment extends Fragment implements OnClickListener,View.OnTouchListener {
    private View view;
    private TextView personalText,missionText;
    public TextView circleText,deviceStatusText,openAppText,openAppButton,calStillText,unitText,failedText,calText,stepText;
    public LinearLayout manBar;
    public Handler handler;
    public HomeAnim homeAnim;
    public HomeCurveView curveView;
    private float startX,startY;
    private boolean hasCancle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment_layout,container,false);
        initView();
        initEvent();

        handler = new Handler(Looper.getMainLooper());
        homeAnim = new HomeAnim(circleText,curveView);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(getActivity() instanceof HomeFragClickListener ){
            ((HomeFragClickListener) getActivity()).onHomeFragClick(v);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                down(v,event);
                break;
            case MotionEvent.ACTION_MOVE:
                move(v,event);
                break;
            case MotionEvent.ACTION_UP:
                up(v,event);
                break;
        }
        return true;
    }
    /**
     * ACTION_DOWN触发的方法，记录起始坐标
     */
    private void down(View v,MotionEvent event){
        hasCancle = false ;
        startX = event.getRawX();
        startY = event.getRawY();
        circleSelected(v, true);
    }
    /**
     * ACTION_MOVE触发的方法，判断是否cancle
     */
    private void move(View v,MotionEvent event){
        if(Math.abs(event.getRawX() - startX)>100 || Math.abs(event.getRawY() - startY)>100 ){
            if(!hasCancle){
                circleSelected(v, false);
                hasCancle = true;
            }
        }
    }
    /**
     * ACTION_UP触发的方法，判断是否加载recipe视图
     */
    private void up(View v,MotionEvent event){
        if(!hasCancle){
            homeAnim.startCurveAnim2(true);

        }
    }

    /**
     * 播放主页跟随circle按钮变化的动画
     * @param view
     * @param isSelected
     */
    private void circleSelected(View view, boolean isSelected){
        if(isSelected){
            homeAnim.startCircleAnim(false);
            homeAnim.startCurveAnim1(true);
        } else {
            homeAnim.startCircleAnim(true);
            homeAnim.startCurveAnim1(false);
        }
    }

    /**
     * 初始化视图
     */
    private void initView(){
        personalText = (TextView) view.findViewById(R.id.personal_TextView);
        missionText = (TextView) view.findViewById(R.id.mission_TextView);
        circleText = (TextView) view.findViewById(R.id.circle_TextView);
        deviceStatusText = (TextView) view.findViewById(R.id.test_device_state);
        openAppText = (TextView) view.findViewById(R.id.openApp_text);
        openAppButton = (Button) view.findViewById(R.id.openApp_button);
        calStillText = (TextView) view.findViewById(R.id.cal_still_text);
        unitText = (TextView) view .findViewById(R.id.unit_text);
        failedText = (TextView) view.findViewById(R.id.failed_text);
        manBar = (LinearLayout) view.findViewById(R.id.man_bar);
        calText = (TextView) view.findViewById(R.id.cal_text);
        stepText = (TextView) view.findViewById(R.id.step_text);
        curveView = (HomeCurveView) view.findViewById(R.id.homeCurveView);
    }

    /**
     * 初始化事件
     */
    private void initEvent(){
        personalText.setOnClickListener(this);
        missionText.setOnClickListener(this);
        openAppButton.setOnClickListener(this);
        circleText.setOnTouchListener(this);
    }
    /**
     * 所有的onClick事件统一由Activity负责响应操作
     */
    public interface HomeFragClickListener{
        void onHomeFragClick(View view);
    }


}
