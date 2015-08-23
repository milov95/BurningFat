package com.milov.fat.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.milov.fat.R;

/**
 * Created by Milov on 2015/8/23.
 */
public class PersonalFragment extends Fragment implements OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal_fragment_layout,container,false);
        ImageView back = (ImageView) view.findViewById(R.id.back_personal_image);
        back.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if(getActivity() instanceof PersonFragClickListener){
            ((PersonFragClickListener) getActivity()).onPersonFragClick(v);
        }
    }

    public interface PersonFragClickListener{
        void onPersonFragClick(View view);
    }
}
