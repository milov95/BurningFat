package com.milov.fat.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.milov.fat.R;
import com.milov.fat.activity.HomeActivity;
import com.milov.fat.util.DataManager;

/**
 * Created by Milov on 2015/9/22.
 */
public class FirstSetFragment extends Fragment implements View.OnClickListener {

    RadioGroup genderRadioGroup;
    TextView height,weight,goal;
    NumberPicker heightPicker,weightPicker,goalPicker;
    AlertDialog pickerDialog;
    DataManager dataManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_set_fragment_layout,container,false);
        height = (TextView) view.findViewById(R.id.first_set_height);
        weight = (TextView) view.findViewById(R.id.first_set_weight);
        goal = (TextView) view.findViewById(R.id.first_set_goal);

        height.setOnClickListener(this);
        weight.setOnClickListener(this);
        goal.setOnClickListener(this);

        dataManager = dataManager.getInstance();
        return view;
    }

    @Override
    public void onClick(View v) {
        if( v.getId() == R.id.first_set_start && getActivity() instanceof FirstSelfInfoSetFragClickListener){
            ((FirstSelfInfoSetFragClickListener) getActivity()).onFirstSelfInfoSetFragClick(v);
        } else {
            showPickerDialog();
        }
    }

    private void showPickerDialog(){
        // 取得自定义NumberPickerDialog
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View pickerDialogLayout = layoutInflater.inflate(R.layout.info_picker_dialog_layout, null);
        initNumberPicker(pickerDialogLayout);

        // 配置自定义的AlertDialog
        pickerDialog = new AlertDialog.Builder(getActivity())
                .setTitle("设置用户信息与目标")
                .setView(pickerDialogLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),"height:"+heightPicker.getValue(),Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create();
        pickerDialog.show();
    }

    private void initNumberPicker(View view){
        heightPicker = (NumberPicker) view.findViewById(R.id.first_set_height_picker);
        weightPicker = (NumberPicker) view.findViewById(R.id.first_set_weight_picker);
        goalPicker = (NumberPicker) view.findViewById(R.id.first_set_goal_picker);



        heightPicker.setDisplayedValues(dataManager.heights);
        weightPicker.setDisplayedValues(dataManager.weights);
        goalPicker.setDisplayedValues(dataManager.goals);

        heightPicker.setMinValue(0);
        heightPicker.setMaxValue(dataManager.heights.length - 1);
        heightPicker.setValue(17);

        weightPicker.setMinValue(0);
        weightPicker.setMaxValue(dataManager.weights.length-1);
        weightPicker.setValue(10);

        goalPicker.setMinValue(0);
        goalPicker.setMaxValue(dataManager.goals.length-1);
        goalPicker.setValue(4);
    }

    public interface FirstSelfInfoSetFragClickListener{
        void onFirstSelfInfoSetFragClick(View view);
    }
}
