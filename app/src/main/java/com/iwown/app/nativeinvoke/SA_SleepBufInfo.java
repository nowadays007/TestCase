package com.iwown.app.nativeinvoke;

public class SA_SleepBufInfo {
    public int total;
    public SA_SleepDataInfo[] sleepdata;
    public SA_TimeInfo inSleepTime;
    public SA_TimeInfo outSleepTime;
    public int datastatus;
    public int completeFlag;

    public SA_SleepBufInfo(){
        sleepdata = new SA_SleepDataInfo[total];
        inSleepTime = new SA_TimeInfo();
        outSleepTime = new SA_TimeInfo();
    }
}

