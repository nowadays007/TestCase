package com.example.ck.myble.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ck.myble.R;

import java.util.ArrayList;

public class LeDeviceListAdapter extends BaseAdapter {
    private ArrayList<BluetoothDevice> mLeDevices;

    private LayoutInflater mInflater;
    public LeDeviceListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        mLeDevices = new ArrayList<BluetoothDevice>();
    }

    public void addDevice(BluetoothDevice device) {
        if(!mLeDevices.contains(device)) {
            mLeDevices.add(device);
        }
    }

    public BluetoothDevice getDevice(int position) {
        return mLeDevices.get(position);
    }

    public void clear() {
        mLeDevices.clear();
    }

    @Override
    public int getCount() {
        return mLeDevices.size();
    }

    @Override
    public Object getItem(int i) {
        return mLeDevices.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override   //确定列表项
    public View getView(int i, View view, ViewGroup viewGroup) {
        deviceView viewHo = new deviceView();
        if (view == null){
            view = mInflater.inflate(R.layout.device_list, null,false);

        viewHo.deviceAddress = (TextView) view.findViewById(R.id.device_address);
        viewHo.deviceName = (TextView) view.findViewById(R.id.device_name);
        viewHo.deviceName.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线
                view.setTag(viewHo);
        //View中的setTag(Onbect)表示给View添加一个格外的数据，以后可以用getTag()将这个数据取出来
            }
            else {
            viewHo = (deviceView) view.getTag();
            }

        BluetoothDevice device = mLeDevices.get(i);
        final String deviceName = device.getName();
        if (deviceName != null && deviceName.length() > 0)
            viewHo.deviceName.setText(deviceName);
        else
            viewHo.deviceName.setText(R.string.unknown_device);
        viewHo.deviceAddress.setText(device.getAddress());

        return view;
    }


    public static class deviceView {
        TextView deviceName;
        TextView deviceAddress;

        public TextView getDeviceAddress() {
            return deviceAddress;
        }

        public TextView getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(TextView deviceName) {
            this.deviceName = deviceName;
        }

        public void setDeviceAddress(TextView deviceAddress) {
            this.deviceAddress = deviceAddress;
        }
    }
}
