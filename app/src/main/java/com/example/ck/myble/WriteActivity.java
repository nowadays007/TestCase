package com.example.ck.myble;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import com.example.ck.myble.dataFile.TestCaseListActivity;
import com.example.ck.myble.utils.Utils;

public class WriteActivity extends Activity {
    private final static String TAG = WriteActivity.class.getSimpleName();
    private Button send_btn;
    private Button wri_btn;
    private EditText mMessageTextView;
    private TextView textView1;
    private TextView textView2;
    private String mDeviceAddress;
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    MyBroadcastReceiver receiver=new MyBroadcastReceiver();

    private boolean dataisOver =false;
    private String zgallData = "";

    private class MyBroadcastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(BluetoothLeService.ACTION_DATA_AVAILABLE)){
                String str = intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toString();
                    Log.e("--------",str);
                    addText(textView2, ""+str);
                    addText(textView2, "-----------------------");
                    textView2.setMovementMethod(ScrollingMovementMethod.getInstance());
//                }


            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        But_click();

        IntentFilter filter=new IntentFilter();
        filter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        this.registerReceiver(receiver,filter);

        String connet_way = "010181080000000000000000";
        BluetoothGattCharacteristic connect_android = MyApplication.getInstance().mBluetoothLeService.characteristiByUUIDZG();
        connect_android.setValue(Utils.getHexBytes(connet_way));
        MyApplication.getInstance().mBluetoothLeService.mBluetoothGatt.writeCharacteristic(connect_android);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
    }


    // 点击空白区域 自动隐藏软键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(null != this.getCurrentFocus()){
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super .onTouchEvent(event);
    }


    public void But_click() {
        final Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);
        textView1 = (TextView) findViewById(R.id.textView4);
        textView2 = (TextView) findViewById(R.id.textView5);
        send_btn = (Button) findViewById(R.id.button_send);
        mMessageTextView = (EditText) findViewById(R.id.edit_text);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    if (mMessageTextView.getText() == null || mMessageTextView.getText().toString().equals("")) {
                        Toast.makeText(v.getContext(), "You need input some message to send", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    ArrayList<Byte> byteList = new ArrayList<Byte>();
                    byte header = Utils.getInstance().form_Header(3, 1);
                    byteList.add(Utils.SMS_TYPE);
                    byteList.add(Utils.FONT_TYPE);
                    Byte[] wordBytes = Utils.getInstance().byteoByte(mMessageTextView.getText().toString().getBytes("UTF-8"));
                    ArrayList<Byte> arrayList = new ArrayList<Byte>(Arrays.asList(wordBytes));
                    byteList.addAll(arrayList);
                    byte[] data = Utils.getInstance().writeWristBandDataByte(true, header, byteList);
                    Log.w("WriteActivity", "add write request to queue");

                    BluetoothGattCharacteristic characteristic1 = MyApplication.getInstance().mBluetoothLeService.characteristiByUUIDZG();
                    characteristic1.setValue(data);
                    MyApplication.getInstance().mBluetoothLeService.mBluetoothGatt.writeCharacteristic(characteristic1);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        wri_btn = (Button) findViewById(R.id.button_write);
        wri_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    if (mMessageTextView.getText() == null || mMessageTextView.getText().toString().equals("")) {
                        Toast.makeText(v.getContext(), "You need input some message to write", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    BluetoothGattCharacteristic characteristic1 = MyApplication.getInstance().mBluetoothLeService.characteristiByUUIDZG();
//                    MyApplication.getInstance().mBluetoothLeService.mBluetoothGatt.setCharacteristicNotification(characteristic1,true);

                    String aa = mMessageTextView.getText().toString();
//                    characteristic1.setValue(Utils.hexStringToBytes(aa));
                    characteristic1.setValue(Utils.getHexBytes(aa));
                    addText(textView1, " "+aa);
                    addText(textView1, "---------------------");
                    MyApplication.getInstance().mBluetoothLeService.mBluetoothGatt.writeCharacteristic(characteristic1);
//                    Log.e(TAG, "TTTTTT" + Utils.bytesToHexString(characteristic1.getValue()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void addText(TextView textView, String content) {
        textView.append(content);
        textView.append("\n");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }
}

