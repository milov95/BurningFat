package com.milov.fat.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.milov.fat.R;

/**
 * Created by Administrator on 2015/8/23.
 */
public class MissionFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mission_fragment_layout,container,false);
        ImageView back = (ImageView) view.findViewById(R.id.back_mission_image);
        back.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(getActivity() instanceof MissionFragClickListener){
            ((MissionFragClickListener) getActivity()).onMissionFragClick(v);
        }
    }

    public interface MissionFragClickListener{
        void onMissionFragClick(View view);
    }
}
