package com.milov.fat.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.milov.fat.R;

/**
 * Created by Milov on 2015/9/22.
 */
public class FirstSetFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mission_fragment_layout,container,false);

        return view;
    }

    @Override
    public void onClick(View v) {

        if(getActivity() instanceof FirstSelfInfoSetFragClickListener){
            ((FirstSelfInfoSetFragClickListener) getActivity()).onFirstSelfInfoSetFragClick(v);
        }
    }

    public interface FirstSelfInfoSetFragClickListener{
        void onFirstSelfInfoSetFragClick(View view);
    }
}
