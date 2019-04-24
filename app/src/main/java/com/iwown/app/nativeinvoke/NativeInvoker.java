package com.iwown.app.nativeinvoke;

/**
 * Created by scow on 2017/9/22.
 */

public class NativeInvoker {
    static{
        System.loadLibrary("algoSleep");
    }

    public native int calculateSleep(String datapath, long uid, String recordDate,
                                     String source, int heartmodeon, com.iwown.app.nativeinvoke.SA_SleepBufInfo sleepData);
}
