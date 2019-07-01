package com.example.ck.myble;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ck.myble.adapters.LeDeviceListAdapter;
import com.example.ck.myble.dataFile.TestCaseListActivity;
import com.example.ck.myble.utils.FileUtils;
import com.example.ck.myble.utils.JsonTool;
import com.example.ck.myble.utils.PermissionsUtils;
import com.example.ck.myble.utils.SampleGattAttributes;
import com.example.ck.myble.utils.Utils;
import com.zeroner.blemidautumn.bluetooth.cmdimpl.ProtoBufReceiverCmd;
import com.zeroner.blemidautumn.bluetooth.cmdimpl.ProtoBufSendBluetoothCmdImpl;
import com.zeroner.blemidautumn.bluetooth.impl.ProtoBle;
import com.zeroner.blemidautumn.bluetooth.model.ProtoBufHardwareInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.content.ContentValues.TAG;

//打包签名密码文件 factory 123456

public class MainActivity extends AppCompatActivity {
    private int number = 1;
    private String numberStr;
    private String writeStr = "";
    private String hexwriteStr = null;
    private int rss;

    private int a = 1;
    private boolean isConnect = false;
    private int b = 1;
    private boolean isOutTime = true;

    private Button beginBtn;
    private Button nextBtn;
    private TextView deviceName;
    private TextView write_text;
    private TextView device_RSSI;
    private TextView status_tv;
    private TextView result;
    private ImageView resultImg;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private String TAG = "MainActivty";

    Handler mainHandler = new Handler();
    Runnable r = new Runnable() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void run() {
            if (isOutTime){
                resultImg.setVisibility(View.VISIBLE);
                result.setVisibility(View.VISIBLE);
                result.setText("烧录失败(超时)");
                beginBtn.setText("开始烧录");
                beginBtn.setClickable(false);
                beginBtn.setBackgroundColor(R.color.color3);
                resultImg.setImageResource(R.drawable.wrong3x);
                if (isConnect == true){
                    mBluetoothLeService.disconnect();
                }

            }

        }
    };

    MyBroadcastReceiver receiver=new MyBroadcastReceiver();
    private class MyBroadcastReceiver extends BroadcastReceiver {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (intent.getAction().equals(BluetoothLeService.ACTION_DATA_AVAILABLE)) {
                String str = intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toString();
                str = str.replace(" ", "");
                if (str.substring(0, 8).equals("44547F00")) {
                    if (b == 1){
                            b = b+1;
                    }else {
                        if (str.indexOf(hexwriteStr) != -1) {
                            resultImg.setVisibility(View.VISIBLE);
                            result.setVisibility(View.VISIBLE);
                            result.setText("烧录成功");
                            resultImg.setImageResource(R.drawable.right3x);
                            beginBtn.setText("开始烧录");
                            writeLog();
                        } else {
                            resultImg.setVisibility(View.VISIBLE);
                            result.setVisibility(View.VISIBLE);
                            result.setText("烧录失败");
                            beginBtn.setText("开始烧录");
                            resultImg.setImageResource(R.drawable.wrong3x);
                        }
                        mBluetoothLeService.disconnect();
                        beginBtn.setClickable(false);
                        beginBtn.setBackgroundColor(R.color.color3);
                        isOutTime = false;
                    }


                }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 禁用横屏
        setContentView(R.layout.activity_main);

        mHandler = new Handler();

        PermissionsUtils.handleLOCATION(this, new PermissionsUtils.PermissinCallBack() {
            @Override
            public void callBackOk() {

            }

            @Override
            public void callBackFial() {

            }
        });
        PermissionsUtils.handleSTORAGEWrite(this, new PermissionsUtils.PermissinCallBack() {
            @Override
            public void callBackOk() {

            }

            @Override
            public void callBackFial() {

            }
        });

        // 是否支持BLE
//
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this,"BLE IS NOT SUPPORTED",Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        beginBtn = findViewById(R.id.begin_btn);
        nextBtn = findViewById(R.id.next_btn);
        deviceName = findViewById(R.id.deviceName);
        write_text = findViewById(R.id.write_text);
        device_RSSI = findViewById(R.id.deviceRSSI);
        result = findViewById(R.id.result_text);
        resultImg = findViewById(R.id.result_img);
        status_tv = findViewById(R.id.status);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        beginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginBtn.setText("操作进行中。。。");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scanLeDevice(true);
                    }
                }, 2000);
                mainHandler.postDelayed(r, 15000);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.disconnect();
                finish();
                mBluetoothLeService = null;
                startActivity(getIntent());
                a = 1;
                b = 1;
                Log.e(TAG, "onClick: 888888"+a+"   "+b );
            }
        });

        //注册收指令的广播
        IntentFilter filter=new IntentFilter();
        filter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        this.registerReceiver(receiver,filter);

    }


    @Override
    protected void onResume() {   //在onResume()方面里进行刷新操作
        super.onResume();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    //在一个主界面(主Activity)通过意图跳转至多个不同子Activity上去，当子模块的代码执行完毕后再次返回主
    // 页面，将子activity中得到的数据显示在主界面/完成的数据交给主Activity处理。这种带数据的意图跳转需要使用activity的onActivityResult()方法
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {     //handler发送消息
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            status_tv.setText("设备搜索中。。。。。");
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    rss = rssi;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {       //当前线程切换到主线程
                                Utils.setShareAddress(MainActivity.this,device.getAddress(),device.getName());
                                if(device.getName()!=null) {
                                    if (device.getName().substring(0, 5).equals("le B1") && rss>-50) {
                                        deviceName.setText(device.getName());
                                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                            mScanning = false;
                                            device_RSSI.setText(rss+"");

                                        //连接操作
                                        connectAct(device.getName(),device.getAddress());
                                    }
                                }
                        }
                    });
                }
            };





    private String mDeviceName;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private final ServiceConnection mServiceConnection = new ServiceConnection() {  // 当与service的连接建立后被调用

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "=========================== Unable to initialize Bluetooth");
                finish();
            }
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
            Log.e(TAG, "=========================== 断连了11111111");
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                isConnect = true;
                status_tv.setText("已连接");
                Log.e(TAG, "=========================== 连接");
                Handler sendhandler = new Handler();
                        sendhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sendnumber();
                                a = a + 1;
                            }
                        }, 2000);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                mConnected = false;
                status_tv.setText("设备已断开连接");
                Log.e(TAG, "=========================== 断连了");
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
                Log.e(TAG, "=========================== 显示服务displayGattServices");
//                sendnumber();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
//                Log.e(TAG, "=========================== 广播服务 ");
//                sendnumber();
            }
        }
    };

    //发送烧录指令
    void sendnumber(){
        Calendar c = Calendar.getInstance();//
        int mYear = c.get(Calendar.YEAR)-2000; // 获取当前年份
        int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期

        File file = new File("/sdcard/FactoryTest/"+"numberN.txt");
        if (file.exists()) {
            Log.e(TAG,"***  存在****  ");
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String readline = "";
                StringBuffer sb = new StringBuffer();
                while ((readline = br.readLine()) != null) {
                    sb.append(readline);
                }
                br.close();
                Log.e(TAG,"***  读取的字符串****  "+ sb.toString());
                SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd");
                String todaystr = today.format(new Date());
//                Log.e(TAG,"***  今天的年月日****  "+ todaystr);
//                Log.e(TAG,"***  今天的年月日****  "+ sb.substring(0,10));
                if (sb.substring(0,10).toString().equals(todaystr)){
                    String num = sb.substring(11);
//                    Log.e(TAG,"***  num ****  "+ num);
                    number = Integer.parseInt(num)+1;
                }else {
                    number = 1;
                }

            }catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG,"*** 不存在 ****  ");
        }

        numberStr = String.valueOf(number);
        String yearstr= String.valueOf(mYear);
        String Monthstr= String.valueOf(mMonth);
        String Daystr= String.valueOf(mDay);
        while (numberStr.length() < 5){
            numberStr = "0"+numberStr;
        }
        if (yearstr.length()<2){
            yearstr = "0"+yearstr;
        }
        if (Monthstr.length()<2){
            Monthstr = "0"+Monthstr;
        }
        if (Daystr.length()<2){
            Daystr = "0"+Daystr;
        }
        writeStr = "LBB01"+yearstr+Monthstr+Daystr+numberStr;
        hexwriteStr = Utils.str2HexStr(writeStr);
        Log.e("哈哈哈哈:",writeStr);
        write_text.setText(writeStr);

        byte[] bytes = ProtoBufSendBluetoothCmdImpl.getInstance().writeHardwareFeatures(writeStr);
        writeCharacteristicNewAPI(BluetoothLeService.ProtoBufUUID.BAND_SERVICE_MAIN_UUID,bytes);
        beginBtn.setText("指令已发送。。。");

        /**存数据**/
        SimpleDateFormat dfnum = new SimpleDateFormat("yyyy-MM-dd");
        String numtimestr = dfnum.format(new Date());
        String fileName = "numberN.txt";
        FileUtils.writeToFile(numtimestr+" "+number, "/sdcard/FactoryTest/", fileName);
    }

    void connectAct(String DeviceName,String DeviceAddress){

        mDeviceName = DeviceName;
        mDeviceAddress = DeviceAddress;
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
                if(gattCharacteristic.getUuid().equals(UUID.fromString("2e8c0002-2d91-5533-3117-59380a40af8f"))){
                    Log.e(TAG,"displayGattServices---------------------");
                    MyApplication.getInstance().mBluetoothLeService.setCharacteristicNotification1(gattCharacteristic,true);
                }
            }

            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    void writeLog(){
//        LBB0119060600007  EE-71-E1-9A-75-D2  2019-06-06  11:41:02
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestr = df.format(new Date());
        String logstr = writeStr+"  "+mDeviceAddress+"  "+timestr;
        String fileName = "log.txt";
        FileUtils.writeTxtToFile(logstr, "/sdcard/FactoryTest/", fileName);
    }

    /**
     * 将byte数组写入特征
     *
     * @param uuid 特征UUID
     * @param data 要写入的数据
     */
    public void writeCharacteristicNewAPI(UUID uuid, byte[] data) {
        Log.e(TAG,"发送指令=====================================");
        try {
            BluetoothGattCharacteristic characteristic = MyApplication.getInstance().mBluetoothLeService.characteristiByUUIDProb();
            BluetoothGatt gatt = MyApplication.getInstance().mBluetoothLeService.mBluetoothGatt;
            if (gatt == null || characteristic == null) {
                return;
            }
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            characteristic.setValue(data);
            gatt.writeCharacteristic(characteristic);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
