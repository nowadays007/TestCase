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
import com.zeroner.blemidautumn.bluetooth.cmdimpl.ProtoBufSendBluetoothCmdImpl;

public class WriteActivity extends Activity {
    private final static String TAG = WriteActivity.class.getSimpleName();

    private TextView reciveText;
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
//                addText(reciveText, ""+str);
//                addText(reciveText, "-----------------------");
//                reciveText.setMovementMethod(ScrollingMovementMethod.getInstance());
//                }


            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        IntentFilter filter=new IntentFilter();
        filter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        this.registerReceiver(receiver,filter);

//        reciveText = (TextView)findViewById(R.id.recive_tv);

        byte[] bytes = ProtoBufSendBluetoothCmdImpl.getInstance().setTime(1561447365);
        BluetoothGattCharacteristic characteristic1 = MyApplication.getInstance().mBluetoothLeService.characteristiByUUIDProb();
        characteristic1.setValue(bytes);
//        ProtoBle.getInstance().writeDataToWristBand();
        BluetoothLeService.mBluetoothGatt.writeCharacteristic(characteristic1);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
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

