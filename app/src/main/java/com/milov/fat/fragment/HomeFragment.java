package com.milov.fat.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.milov.fat.R;

/**
 * Created by Milov on 2015/8/22.
 */
public class HomeFragment extends Fragment implements OnClickListener {
    public TextView deviceStatusText,openAppText,openAppButton,calText,unitText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment_layout,container,false);
        TextView personalView = (TextView) view.findViewById(R.id.personal_TextView);
        TextView missionView = (TextView) view.findViewById(R.id.mission_TextView);
        deviceStatusText = (TextView) view.findViewById(R.id.test_device_state);
        openAppText = (TextView) view.findViewById(R.id.openApp_text);
        openAppButton = (Button) view.findViewById(R.id.openApp_button);
        calText = (TextView) view.findViewById(R.id.cal_text);
        unitText = (TextView) view .findViewById(R.id.unit_text);

        personalView.setOnClickListener(this);
        missionView.setOnClickListener(this);
        openAppButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(getActivity() instanceof HomeFragClickListener){
            ((HomeFragClickListener) getActivity()).onHomeFragClick(v);
        }
    }

    //统一由Activity负责响应操作
    public interface HomeFragClickListener{
        void onHomeFragClick(View view);
    }


}
