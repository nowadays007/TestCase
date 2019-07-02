package com.example.ck.myble;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import static com.github.dfqin.grantor.PermissionsUtil.TAG;

public class MyApplication extends Application {
    private static MyApplication instance;
    private Context context;
    private String packgeName;

    public  BluetoothLeService mBluetoothLeService;



    public static  MyApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        context=this;
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        packgeName = context.getPackageName();
    }


    private final ServiceConnection mServiceConnection = new ServiceConnection() {  // 当与service的连接建立后被调用

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("123", "Unable to initialize Bluetooth");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean isWatch(){
        Log.e(TAG, "isWatch: "+packgeName.toString());
        if (packgeName.toString().equals("com.example.ck.watch")){
            return true;
        }else {
            return false;
        }
    }
}
