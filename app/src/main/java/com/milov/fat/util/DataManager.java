package com.milov.fat.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import jxl.*;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Milov on 2015/9/25.
 */
public class DataManager {

    public static final int MALE = 1, FEMALE = 2;
    public static final int GENDER = 10, HEIGHT = 11, WEIGHT = 12, GOAL = 13,PERIOD = 14,COMPLETE_DAYS = 15,
            DAILY_GOAL = 16,REACH_DAYS = 17,TOTAL_CAL = 18,TOTAL_DAYS = 19,START_TIME = 20,AGE = 21,
            BREAKFAST = 22,LUNCH = 23,SUPPER = 24;
    private Context context;
    /**
     * 用于获取应用数据
     */
    private static DataManager dataManager;
    /**
     * 设置用户信息及任务时的选择区间
     */
    public String[] heights,weights,goals,ages,breakfasts,lunches,suppers;
    /**
     * 月健康数据SP,用户信息SP（含任务目标），任务数据SP
     */
    private SharedPreferences monthDataSP,selfDataSP,missionDataSP;
    /**
     * SharedPreferences.Editor
     */
    private SharedPreferences.Editor monthDataEditor,selfDataEditor,missionDataEditor;

    private DataManager(Context context){
        monthDataSP = context.getSharedPreferences("monthData", Activity.MODE_PRIVATE);
        monthDataEditor = monthDataSP.edit();
        selfDataSP = context.getSharedPreferences("selfInfo", Activity.MODE_PRIVATE);
        selfDataEditor = selfDataSP.edit();
        missionDataSP = context.getSharedPreferences("missionData", Activity.MODE_PRIVATE);
        missionDataEditor = missionDataSP.edit();
        this.context=context;

        initSelfInfoInterval();
    }

    /**
     * 获取单一实例
     */
    public static DataManager getInstance(Context context){
        if(dataManager == null )
            dataManager = new DataManager(context);
        return dataManager;
    }

    /**
     * 初始化用户信息以及任务的选择区间
     */
    private void initSelfInfoInterval(){
        ages = new String[61];
        for(int i=0;i<=60;i++){
            ages[i]=i+10+"岁";
        }
        heights = new String[51];
        for(int i = 0; i <= 50;i++){
            heights[i]=i+150+"cm";
            if(i==0)
                heights[0] = "≤150cm";
            else if(i==50)
                heights[50] = "≥200cm";
        }
        weights = new String[61];
        for(int i = 0; i <= 60;i++){
            weights[i]=i+40+"kg";
            if(i==0)
                weights[0] = "≤40kg";
            else if(i==60)
                weights[60] = "≥100kg";
        }
        goals = new String[20];
        for(int i = 0; i <= 19;i++){
            goals[i]="-"+ (i+1) + "kg";
        }
    }

    /**
     * 初始化食谱信息
     */
    private void initRecipeInfo(){

    }

    /**
     * 存储个人信息数据，包含任务目标
     * @param age 年龄[0,60]([10岁,70岁])，-1为不存储
     * @param gender 性别{1,2}({男，女})，-1为不存储
     * @param height 身高[0,50]([150cm,200cm])，-1为不存储
     * @param weight 体重[0,60]([40kg,100kg])，-1为不存储
     * @param goal 任务目标[0,19]([-1kg,-20kg])，-1为不存储
     */
    public void saveSelfData(int age,int gender,int height,int weight,int goal){
        if(age!=-1) selfDataEditor.putInt("age",age);
        if(gender!=-1) selfDataEditor.putInt("gender",gender);
        if(height!=-1) selfDataEditor.putInt("height",height);
        if(weight!=-1) selfDataEditor.putInt("weight",weight);
        if(goal!=-1) selfDataEditor.putInt("goal",goal);
        selfDataEditor.commit();
    }

    /**
     * 获取个人信息数据，通过类型获取对应数据
     * @param type 信息类型
     * @return 对应的信息数据
     */
    public int getSelfData(int type){
        switch (type){
            case AGE:
                return selfDataSP.getInt("age",0);
            case GENDER:
                //0代表第一次启动应用程序或者自从启动应用程序后还没有设置过个人信息
                return selfDataSP.getInt("gender",0);
            case HEIGHT:
                return selfDataSP.getInt("height",0);
            case WEIGHT:
                return selfDataSP.getInt("weight",0);
            case GOAL:
                return selfDataSP.getInt("goal",0);
            default:
                return -1;
        }
    }
    
    /**
     * 保存月健康数据
     * @param date 日期
     * @param cal 卡路里消耗值
     */
    public void saveMonthData(String date,int cal){
        monthDataEditor.putInt(date,cal);
        monthDataEditor.commit();
    }

    /**
     * 获取月健康数据
     * @param date 日期
     * @return 卡路里消耗值
     */
    public int getMonthData(String date){
        return monthDataSP.getInt(date,-1);
    }

    /**
     * 设置任务
     */
    public void startMisson(){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        missionDataEditor.putInt("reachDays",0);
        missionDataEditor.putInt("startDayOfYear",c.get(Calendar.DAY_OF_YEAR));
        int goal = selfDataSP.getInt("goal",0);
        int period;
        int daily;
        if(selfDataSP.getInt("gender",0)==DataManager.MALE){
            period = (goal+1)*10;
            missionDataEditor.putInt("period",period);
        } else {
            period = (goal+1)*15;
            missionDataEditor.putInt("period",period);
        }
        daily = calculateDailyGoal(period,goal,selfDataSP.getInt("age",0));
        missionDataEditor.putInt("dailyGoal",daily);
        missionDataEditor.commit();
    }


    private int calculateDailyGoal(int period,int goal,int age){

        return 620;
    }

    public int getBaseConsumptionValue(int age,int gender,int height,int weight){
        int value = 0,delta = 0;
        try {
            Workbook book = Workbook.getWorkbook(new File(context.getFilesDir().getPath()+"/consumption_sheet.xls"));
            System.out.println(">>>>>>number of sheet " + book.getNumberOfSheets());
            //根据性别获取表格
            Sheet sheet = gender==DataManager.MALE ? book.getSheet(0) : book.getSheet(1);
            delta = gender==DataManager.MALE ? 82 : 56 ;
            int row = (weight-23)/5+1;
            int col = (height-50)/10+1;
            System.out.println("当前工作表的名字:" + sheet.getName());
            System.out.println("行:" + (row+1));
            System.out.println("列:" + (col+1));
            value = Integer.parseInt(sheet.getCell(col, row).getContents())-((age-10)/10)*delta;
            System.out.print((sheet.getCell(row, col)).getContents() + "\t");
            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        return value;
    }

    /**
     * 获取任务信息
     * @param type 信息类型
     * @return 对应的信息数据
     */
    public int getMissonData(int type){
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());

        switch (type){
            case START_TIME:
                return missionDataSP.getInt("startDayOfYear",0);
            case GOAL:
                return selfDataSP.getInt("goal",0);
            case PERIOD:
                return missionDataSP.getInt("period",0);
            case COMPLETE_DAYS:
                return c.get(Calendar.DAY_OF_YEAR)-missionDataSP.getInt("startDayOfYear",0);
            case REACH_DAYS:
                return missionDataSP.getInt("reachDays",0);
            case DAILY_GOAL:
                return missionDataSP.getInt("dailyGoal",620);
            default:
                return -1;
        }
    }



    public void addReachDays(){
        missionDataEditor.putInt("reachDays",missionDataSP.getInt("reachDays",0)+1);
        missionDataEditor.commit();
    }

    /**
     * 获取用户的日均卡路里消耗值
     */
    public int getAverageCal(){
        if(selfDataSP.getInt("totalDays",0)==0) return 0;
        return selfDataSP.getInt("totalCal",0)/selfDataSP.getInt("totalDays",0);
    }

    public int getTotalCal(){
        return selfDataSP.getInt("totalCal",0);
    }

    public int getTotalDays(){
        return selfDataSP.getInt("totalDays",0);
    }

    /**
     * 存储日均卡路里数据
     * @param totalCal 总共的卡路里值
     * @param totalDays 有卡路里消耗值的总天数
     */
    public void saveAverageCal(int totalCal,int totalDays){
        selfDataEditor.putInt("totalCal",totalCal);
        selfDataEditor.putInt("totalDays",totalDays);
        selfDataEditor.commit();
    }

    /**
     * 获取食谱
     * @param type BREAKFAST,LUNCH,SUPPER
     * @return 食谱信息
     */
    public String getRecipe(int type){
        return "";
    }
}

