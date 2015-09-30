package com.milov.fat.fragment;

import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.milov.fat.R;
import com.milov.fat.util.DataManager;
import com.milov.fat.view.LineChartView;


/**
 * Created by Milov on 2015/8/23.
 */
public class PersonalFragment extends Fragment implements OnClickListener{
    public TextView averageCal,assess;
    private TextView height,weight,gender;
    private HorizontalScrollView scrollView;
    private ImageView back;
    public LineChartView chartView;
    private DataManager dataManager;
    public ImageView status;
    public AnimationDrawable statusAnim;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        dataManager = DataManager.getInstance(getActivity());

        View view = inflater.inflate(R.layout.personal_fragment_layout,container,false);
        initView(view);
        showInfo();
        status.setVisibility(View.VISIBLE);
        statusAnim.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(5000,0);
            }
        },300);

        back.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(getActivity() instanceof PersonFragClickListener){
            ((PersonFragClickListener) getActivity()).onPersonFragClick(v);
        }
    }

    private void initView(View view){
        scrollView = (HorizontalScrollView) view.findViewById(R.id.lineChartScrollView);
        chartView = (LineChartView) view.findViewById(R.id.lineChartView);
        back = (ImageView) view.findViewById(R.id.back_personal_image);
        height = (TextView) view.findViewById(R.id.person_height);
        weight = (TextView) view.findViewById(R.id.person_weight);
        gender = (TextView) view.findViewById(R.id.person_gender);
        averageCal = (TextView) view.findViewById(R.id.average_cal);
        assess = (TextView) view.findViewById(R.id.person_assess);
        status = (ImageView) view.findViewById(R.id.status_image);
        status.setImageResource(R.drawable.status_anim);
        statusAnim =(AnimationDrawable) status.getDrawable();
    }

    private void showInfo(){
        height.setText(dataManager.heights[dataManager.getSelfData(DataManager.HEIGHT)]);
        weight.setText(dataManager.weights[dataManager.getSelfData(DataManager.WEIGHT)]);
        gender.setText(dataManager.getSelfData(DataManager.GENDER)==1?"男":"女");
    }

    public interface PersonFragClickListener{
        void onPersonFragClick(View view);
    }
}
