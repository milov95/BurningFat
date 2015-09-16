package com.milov.fat.fragment;

import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import com.milov.fat.R;


/**
 * Created by Milov on 2015/8/23.
 */
public class PersonalFragment extends Fragment implements OnClickListener{

    private HorizontalScrollView scrollView;

    public PersonalFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal_fragment_layout,container,false);
        scrollView = (HorizontalScrollView) view.findViewById(R.id.lineChartScrollView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(5000,0);
            }
        },300);
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
