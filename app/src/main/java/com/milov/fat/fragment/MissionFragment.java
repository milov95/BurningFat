package com.milov.fat.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.milov.fat.R;

/**
 * Created by Milov on 2015/8/23.
 */
public class MissionFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mission_fragment_layout,container,false);
        ImageView back = (ImageView) view.findViewById(R.id.back_mission_image);
        Button deleteMission = (Button) view.findViewById(R.id.delete_mission_button);
        Button resetMission = (Button) view.findViewById(R.id.reset_mission_button);

        deleteMission.setOnClickListener(this);
        resetMission.setOnClickListener(this);
        back.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.delete_mission_button){
            return;
        } else if (v.getId()==R.id.reset_mission_button){
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
