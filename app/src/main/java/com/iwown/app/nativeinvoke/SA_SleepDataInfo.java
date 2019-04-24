package com.iwown.app.nativeinvoke;

public class SA_SleepDataInfo {
    public SA_TimeInfo startTime;
    public SA_TimeInfo stopTime;
    public int sleepMode;

    public SA_SleepDataInfo(){
        startTime = new SA_TimeInfo();
        stopTime = new SA_TimeInfo();
    }
}
