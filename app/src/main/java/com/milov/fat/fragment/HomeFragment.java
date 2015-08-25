package com.milov.fat.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.milov.fat.Animation.HomeAnim;
import com.milov.fat.R;

/**
 * Created by Milov on 2015/8/22.
 */
public class HomeFragment extends Fragment implements OnClickListener,View.OnTouchListener {
    public TextView circleText,deviceStatusText,openAppText,openAppButton,calStillText,unitText,failedText,calText,stepText;
    public LinearLayout manBar;
    public Handler handler;
    public HomeAnim homeAnim;
    private float startX,startY;
    private boolean hasCancle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout,container,false);
        TextView personalView = (TextView) view.findViewById(R.id.personal_TextView);
        TextView missionView = (TextView) view.findViewById(R.id.mission_TextView);

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

        personalView.setOnClickListener(this);
        missionView.setOnClickListener(this);
        openAppButton.setOnClickListener(this);

        circleText.setOnTouchListener(this);

        handler = new Handler(Looper.getMainLooper());
        homeAnim = new HomeAnim(circleText);
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
                hasCancle = false ;
                startX = event.getRawX();
                startY = event.getRawY();
                scale(v, false);
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(event.getRawX() - startX)>100 || Math.abs(event.getRawY() - startY)>100 ){
                    if(!hasCancle){
                        scale(v, true);
                        hasCancle = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /**
     * 放大或缩小控件的动画
     * @param view
     * @param gonnaLargen 要放大还是缩小
     */
    private void scale(View view ,boolean gonnaLargen){
        float rate = view.getScaleX();
        if(!gonnaLargen){
            homeAnim.startCircleForward();
        } else {
            homeAnim.startCircleBack();
        }
    }


    /**
     * 统一由Activity负责响应操作
     */
    public interface HomeFragClickListener{
        void onHomeFragClick(View view);
    }


}
