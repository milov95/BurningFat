package com.milov.fat.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.milov.fat.R;
import com.milov.fat.util.DataManager;

/**
 * Created by Milov on 2015/8/23.
 */
public class MissionFragment extends Fragment implements View.OnClickListener {

    private ImageView back;
    private TextView goal,period,completeDays,reachDays,reachRates,suggest;
    private Button deleteMission,resetMission;
    private DataManager dataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        dataManager = DataManager.getInstance(getActivity());

        View view = inflater.inflate(R.layout.mission_fragment_layout,container,false);
        initView(view);
        showInfo();

        deleteMission.setOnClickListener(this);
        resetMission.setOnClickListener(this);
        back.setOnClickListener(this);
        return view;
    }

    private void initView(View view){
        back = (ImageView) view.findViewById(R.id.back_mission_image);
        deleteMission = (Button) view.findViewById(R.id.delete_mission_button);
        resetMission = (Button) view.findViewById(R.id.reset_mission_button);
        goal = (TextView) view.findViewById(R.id.mission_goal);
        period = (TextView) view.findViewById(R.id.mission_period);
        completeDays = (TextView) view.findViewById(R.id.mission_completeDays);
        reachDays = (TextView) view.findViewById(R.id.mission_reachDays);
        reachRates = (TextView) view.findViewById(R.id.mission_reachRate);
        suggest = (TextView) view.findViewById(R.id.mission_suggest);
    }

    private void showInfo(){
        goal.setText(dataManager.goals[dataManager.getSelfData(DataManager.GOAL)]);
        period.setText(dataManager.getMissonData(DataManager.PERIOD)+"天");
        completeDays.setText(dataManager.getMissonData(DataManager.COMPLETE_DAYS)+"天");
        reachDays.setText(dataManager.getMissonData(DataManager.REACH_DAYS)+"天");
        reachRates.setText(0+"%");
        suggest.setText("根据您个人的身体素质以及任务要求，每日\n卡路里的建议消耗达标值为"+dataManager.getMissonData(DataManager.DAILY_GOAL)+"千卡");
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.delete_mission_button){
            AlertDialog deleteWarning = new AlertDialog.Builder(getActivity())
                    .setTitle("删除任务")
                    .setMessage("确定要删除任务？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
            deleteWarning.show();
            return;
        } else if (v.getId()==R.id.reset_mission_button){
            AlertDialog deleteWarning = new AlertDialog.Builder(getActivity())
                    .setTitle("重置任务")
                    .setMessage("确定要重置任务？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
            deleteWarning.show();
            return;
        }

        if(getActivity() instanceof MissionFragClickListener){
            ((MissionFragClickListener) getActivity()).onMissionFragClick(v);
        }
    }

    public interface MissionFragClickListener{
        void onMissionFragClick(View view);
    }
}
