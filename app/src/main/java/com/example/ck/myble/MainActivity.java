package com.example.ck.myble;


import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ck.myble.adapters.LeDeviceListAdapter;
import com.example.ck.myble.utils.PermissionsUtils;
import com.example.ck.myble.utils.Utils;

import java.util.UUID;


public class MainActivity extends ListActivity {

    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private String TAG = "MainActivty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();

        PermissionsUtils.handleLOCATION(this, new PermissionsUtils.PermissinCallBack() {
            @Override
            public void callBackOk() {

            }

            @Override
            public void callBackFial() {

            }
        });

        // 是否支持BLE
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
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);//刷新按钮效果
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
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
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        setListAdapter(mLeDeviceListAdapter);
//        listView2.setAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
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
        mLeDeviceListAdapter.clear();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        final Intent intent = new Intent(this, ConnectActivity.class);
        intent.putExtra(ConnectActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(ConnectActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        startActivity(intent);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {     //handler发送消息
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);

            Log.e(TAG, "scanLeDevice: "+mLeDeviceListAdapter.getCount() );

//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            }, 500);

//            }else{
//                UUID[] scanUUID=new UUID[]{UUID.fromString(strScanUUID)};
//                mBluetoothAdapter.startLeScan(scanUUID,mLeScanCallback);
//            }

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();  //自定义菜单
    }


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {       //当前线程切换到主线程
//                            Log.e(TAG, "scanLeDevice55555555: "+mLeDeviceListAdapter.getCount() );
                            if(mLeDeviceListAdapter.getCount()>80){
                                mScanning = false;
                                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                invalidateOptionsMenu();
                            }else {
                                Utils.setShareAddress(MainActivity.this,device.getAddress(),device.getName());
                                if(device.getName()!=null) {
                                    if (device.getName().substring(0, 2).equals("le") ) {
                                        Log.e("哈哈哈哈：", device.getName());
                                        Log.e("哈哈哈哈：", device.getName().substring(0,2));
                                        mLeDeviceListAdapter.addDevice(device);
                                        mLeDeviceListAdapter.notifyDataSetChanged();

                                        if (mScanning) {
                                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                                            mScanning = false;
                                        }
                                    }
                                }
                            }

                        }
                    });
                }
            };

}
