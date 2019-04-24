/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.ck.myble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.ck.myble.dataFile.TestCaseListActivity;
import com.example.ck.myble.utils.SampleGattAttributes;
import com.example.ck.myble.utils.Utils;

import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.internal.Util;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    public static BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private Map<String, BluetoothGatt> mBluetoothGatts;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static String EXTRA_DATA1 =
            "com.example.bluetooth.le.EXTRA_DATA1";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    private byte[] datas =new byte[]{};
    private int dataLength = -1;
    private boolean isDataOver = false;
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {

                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);

//                String connet_way = "015A81080102030405060708";
//                BluetoothGattCharacteristic connect_android = MyApplication.getInstance().mBluetoothLeService.characteristiByUUIDZG();
//                connect_android.setValue(Utils.getHexBytes(connet_way));
//                MyApplication.getInstance().mBluetoothLeService.mBluetoothGatt.writeCharacteristic(connect_android);

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.e(TAG, " service onCharacteristicWrite---: "+ Utils.bytesToHexString(characteristic.getValue()));

            try {
                int s = showActivity.sendLength - showActivity.sendtabLength;
                if (!ProtoBufUUID.BAND_CHARACT_NOTIFY_UUID.equals(characteristic.getUuid()) &&s > 0) {
                    byte[] c;
                    if (s > 20) {
                        c = new byte[20];
                        for (int i = 0; i < 20; i++) {
                            c[i] = showActivity.a[i + showActivity.sendtabLength];
                        }
                        showActivity.sendtabLength += 20;
                        Log.e(TAG, "onCharacteristicWrite: c  %%%% %%%% nnnn"+c);
                    } else {
                        c = new byte[s];
                        for (int i = 0; i < s; i++) {
                            c[i] = showActivity.a[i + showActivity.sendtabLength];
                        }
                        showActivity.sendtabLength += s;
                    }

                    BluetoothGattCharacteristic ztic1 = MyApplication.getInstance().mBluetoothLeService.characteristiByUUIDZG();
                    ztic1.setValue(c);
                    MyApplication.getInstance().mBluetoothLeService.mBluetoothGatt.writeCharacteristic(ztic1);

                } else {
                    showActivity.sendLength = -1;
                    showActivity.sendtabLength = -1;
//                    LogUtil.i(TAG, "所有包已发完！");
//                    Log.e(TAG, "onCharacteristicWrite: 所有包已发完" );
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }


        int length = 0;
        byte[] newByte = new byte[]{};
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
//            Log.e(TAG," 808UUID:"+characteristic.getUuid());
            byte[] receiverData = characteristic.getValue();
            if (ProtoBufUUID.BAND_CHARACT_NOTIFY_UUID.equals(characteristic.getUuid())) {
                //接收
                Log.e("0808", "onCharacteristicChanged"+Utils.bytesToString(receiverData));
                if (receiverData[0] == 0x44 && receiverData[1] == 0x54) {
                    length = ((receiverData[3] & 0xff) << 8) | (receiverData[2] & 0xff);
//                    KLog.i("yanxi111", "length:" + length + "");
                    if (receiverData.length - 8 >= length) {
                        isDataOver = true;
                        newByte = Arrays.copyOfRange(receiverData, 0, receiverData.length);
                        Log.d("999999", "不用拆分---" + Utils.bytesToString(newByte, false));
                        //数据不用分开收
                        //校验
                        int a = Utils.crc16Modem(Arrays.copyOfRange(receiverData, 8, receiverData.length));
                        byte high = (byte) ((a & 0xff00) >> 8);
                        byte low = (byte) (a & 0xff);
                        Log.d("808", String.format("%02d", high) + "---" + String.format("%02d", low));
                        if (high == newByte[5] && low == newByte[4]) {
                            //校验成功
//                            parseData(characteristic.getUuid(), newByte);
                            broadcastUpdatePro(ACTION_DATA_AVAILABLE, characteristic,newByte);
                            Log.e(TAG,"Service onCharacteristChanged %%%% %%%"+newByte);
                            super.onCharacteristicChanged(gatt, characteristic);
                        }
                        newByte = new byte[]{};
                    } else {
                        isDataOver = false;
                        //数据需要分开收
                        newByte = Arrays.copyOfRange(receiverData, 0, receiverData.length);
                    }
                } else {
                    if (!isDataOver) {
                        //非头部
                        Log.i("newbyte", newByte.length + "");
                        newByte = Utils.concat(newByte, receiverData);

                        if (newByte.length - 8 == length) {
                            Log.d("80899", "拆分---" + Utils.bytesToString(newByte, false));
                            //结束
                            //数据不用分开收
                            //校验
                            int a = Utils.crc16Modem(Arrays.copyOfRange(newByte, 8, newByte.length));
                            byte high = (byte) ((a & 0xff00) >> 8);
                            byte low = (byte) (a & 0xff);
                            Log.d("808", String.format("%02X", high) + "---" + String.format("%02X", low));
                            if (high == newByte[5] && low == newByte[4]) {
                                Log.i("808", "校验成功");
//                                parseData(characteristic.getUuid(), newByte);

                                broadcastUpdatePro(ACTION_DATA_AVAILABLE, characteristic,newByte);
                            Log.e(TAG,"Service onCharacteristChanged %%%% %%%"+newByte);
                            super.onCharacteristicChanged(gatt, characteristic);
                            }
                            newByte = new byte[]{};
                        }

                    } else {
                        newByte = new byte[]{};
                    }
                }
            }else if(ZG_NOTIFY_UUID.equals(characteristic.getUuid())){
                //ZG 协议
                Log.e("ZG   Changed: " , Utils.bytesToString(receiverData));
                int packNo = receiverData[2];
                if (packNo < 0 || (receiverData[0] == 0x01 && packNo == 0x5a) || (receiverData[2] == (byte) 0x81 && receiverData[4] == 0x02)) {
                    boolean isSendOver=true;
                    newByte = Utils.concat(newByte, receiverData);
                    if(receiverData[0]==(byte)0x8C && receiverData[1]!=0){
                        if(receiverData[1]==0x5A){
                            isSendOver=true;
                        }else if(!(receiverData[2]==(byte)0x81 && receiverData[4]==(byte)0xFF)){
                            isSendOver=false;
                            //一个包接收完毕 ，防止超时自动发送下一个指令
                            if(receiverData[2]==(byte)0x8F){
//                                KLog.d("no2s 收到一条256的包，重置超时: ");
//                                BleHandler.getInstance().setSendStatusNotOver();
                                Log.e(TAG,"哈哈哈哈哈哈哈");
                            }
                        }
                    }
                    if(isSendOver) {
                        Log.e(TAG,"ZG 蓝牙数据回调 开始下一个 onCharacteristChanged"+Utils.bytesToHexString(newByte));
                        broadcastUpdatePro(ACTION_DATA_AVAILABLE, characteristic,newByte);
                        newByte = new byte[]{};
                    }
                }else{
                    Log.e(TAG,"=======0000000000:");
                    newByte = Utils.concat(newByte, receiverData);
                }
            } else {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                Log.e(TAG,"Service onCharacteristChanged %%%% %%%"+Utils.bytesToHexString(characteristic.getValue()));
                super.onCharacteristicChanged(gatt, characteristic);
            }

        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,              //广播更新
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar)); //以16进制补位输出
                intent.putExtra(EXTRA_DATA, stringBuilder.toString());
                intent.putExtra(EXTRA_DATA1, data);
                Log.e(TAG, "broadcastUpdate: EXRA_DATAD"+ stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    private void broadcastUpdatePro(final String action,              //广播更新
                                 final BluetoothGattCharacteristic characteristic,byte[] newByte) {
        final Intent intent = new Intent(action);
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("Received heart rate: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
            // For all other profiles, writes the data formatted in HEX.
            final byte[] data = newByte;
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar)); //以16进制补位输出
                intent.putExtra(EXTRA_DATA, stringBuilder.toString());
                intent.putExtra(EXTRA_DATA1, data);
                Log.e(TAG, "broadcastUpdate: EXRA_DATAD"+ stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();


    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }



    public boolean connect(final String address) {
//        clearDataState();
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
//        clearDataState();
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }


    public boolean writeCharacteristic(String address,
                                       BluetoothGattCharacteristic characteristic) {
        BluetoothGatt gatt = mBluetoothGatts.get(address);
        if (gatt == null) {
            return false;
        }
        return gatt.writeCharacteristic(characteristic);
    }


    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);

        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    /**
     * I7B uuid
     */
    public static class ProtoBufUUID {
        public static final UUID BAND_SERVICE_MAIN_UUID = UUID.fromString("2E8C0001-2D91-5533-3117-59380A40AF8F");//GATT服务
        public static final UUID BAND_CHARACT_NOTIFY_UUID = UUID.fromString("2e8c0002-2d91-5533-3117-59380a40af8f");//GATT通知服务
        public static final UUID BAND_CHARACT_WRITE_UUID = UUID.fromString("2e8c0003-2d91-5533-3117-59380a40af8f");//写
        public static final UUID BAND_DES_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
        public static final UUID BAND_PROTO_DFU_SERVICE_UUID = UUID.fromString("0000fe59-0000-1000-8000-00805f9b34fb");
        public static final UUID BAND_PROTO_DFU_CHARACT_UUID = UUID.fromString("8ec90003-f315-4f60-9fb8-838830daea50");
    }

    /**
     *     mtk
     */
    public static final class ZeronerUUID{
        public static final UUID BAND_DES_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"); //
        public static final UUID BAND_SERVICE_MAIN = UUID.fromString("0000ff20-0000-1000-8000-00805f9b34fb"); // 新协议手环主GATT服务
        public static final UUID BAND_CHARACTERISTIC_NEW_WRITE = UUID.fromString("0000ff21-0000-1000-8000-00805f9b34fb"); // 新协议写
        public static final UUID BAND_CHARACTERISTIC_NEW_INDICATE = UUID.fromString("0000ff23-0000-1000-8000-00805f9b34fb"); // 3.0新协议notify
    }

    /**
     *   iwown
     */
     UUID uuid=  UUID.fromString("0000ff20-0000-1000-8000-00805f9b34fb");
     UUID write_uuid=  UUID.fromString("0000ff21-0000-1000-8000-00805f9b34fb");

    /**
     *   ZG
     */
    UUID ZG_uuid = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    UUID ZGwrite_uuid = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    UUID ZG_NOTIFY_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");


    public   BluetoothGattCharacteristic characteristicByUUID(){
        if (mBluetoothGatt == null) return null;
        BluetoothGattService service = mBluetoothGatt.getService(uuid);
        return service.getCharacteristic(write_uuid);
    }

    public  BluetoothGattCharacteristic characteristiByUUIDZG(){
        if (mBluetoothGatt == null) return null;
        BluetoothGattService service = mBluetoothGatt.getService(ZG_uuid);
        return service.getCharacteristic(ZGwrite_uuid);
    }

    public  BluetoothGattCharacteristic characteristiByUUIDProb() {
        if (mBluetoothGatt == null) return null;
        BluetoothGattService service = mBluetoothGatt.getService(ProtoBufUUID.BAND_SERVICE_MAIN_UUID);
        return service.getCharacteristic(ProtoBufUUID.BAND_CHARACT_WRITE_UUID);
    }

    public  BluetoothGattCharacteristic characteristiByUUIDIwown() {
        if (mBluetoothGatt == null) return null;
        BluetoothGattService service = mBluetoothGatt.getService(ProtoBufUUID.BAND_SERVICE_MAIN_UUID);
        return service.getCharacteristic(ProtoBufUUID.BAND_CHARACT_WRITE_UUID);
    }


    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification. False otherwise.
     */
    public boolean setCharacteristicNotification1(BluetoothGattCharacteristic characteristic, boolean enabled) {
        boolean rst = true;
        if (mBluetoothGatt == null || characteristic == null) {
            return false;
        }

        boolean b = mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if(!b){
            Log.e(TAG,"Enabling notification failed!");
            return false;
        }
        // This is specific to Heart Rate Measurement.
        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
        int properties = characteristic.getProperties();
        Log.d(TAG,"properties = " + properties);
        if ((properties & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
            descriptor.setValue(enabled ? BluetoothGattDescriptor.ENABLE_INDICATION_VALUE : new byte[]{0x00, 0x00});
        } else if ((properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
            descriptor.setValue(enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : new byte[]{0x00, 0x00});
        } else {
            descriptor.setValue(enabled ? BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE : new byte[]{0x00, 0x00});
        }
        rst = mBluetoothGatt.writeDescriptor(descriptor);
        if (rst) {

        }
        return rst;
    }
}

