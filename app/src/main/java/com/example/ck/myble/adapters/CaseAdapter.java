package com.example.ck.myble.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ck.myble.R;
import com.example.ck.myble.Sendhttp.CaseListData1;

import java.util.List;

public class CaseAdapter extends ArrayAdapter<CaseListData1> {

    public CaseAdapter(Context context,int resource,List<CaseListData1> items){
        super(context,resource,items);
    }



    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View v, ViewGroup parent) {

        if (v == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            v = inflater.inflate(R.layout.testcaselistlayout,null);
        }
        final CaseListData1 caseListData1 = getItem(position);
        if (caseListData1 != null) {
//            View backgrounbp = (View)v.findViewById(R.id.tsCaseList);
            TextView tv_case_name = (TextView) v.findViewById(R.id.case_name);
            TextView tv_case_creator = (TextView) v.findViewById(R.id.case_creator);
            TextView tv_case_protobuf = (TextView) v.findViewById(R.id.case_protobuf);
            TextView tv_case_device_model = (TextView) v.findViewById(R.id.case_device_model);
            TextView tv_case_desc = (TextView) v.findViewById(R.id.case_desc);

            tv_case_name.setText(caseListData1.getName());
            tv_case_creator.setText(caseListData1.getCreator());
            tv_case_protobuf.setText(caseListData1.getProtocol()+"");
            tv_case_device_model.setText(caseListData1.getDeviceModel()+"");
            tv_case_desc.setText(caseListData1.getDesc());
        }
        return  v;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

}
