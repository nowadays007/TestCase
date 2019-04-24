package com.example.ck.myble.dataFile;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ck.myble.ConnectActivity;
import com.example.ck.myble.MyApplication;
import com.example.ck.myble.R;
import com.example.ck.myble.Sendhttp.CaseListData;
import com.example.ck.myble.Sendhttp.CaseListData1;
import com.example.ck.myble.Sendhttp.HttpLogUtils;
import com.example.ck.myble.Sendhttp.fileApi;
import com.example.ck.myble.adapters.CaseAdapter;
import com.example.ck.myble.showActivity;
import com.example.ck.myble.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.support.constraint.Constraints.TAG;

public class TestCaseListActivity extends Activity implements View.OnClickListener{
    private List<CaseListData1> caselist = new ArrayList<>();
    private ListView cslist_view;
    public static String fileUrl;
    public static int probNumber;
    public static String personName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_case_list);

        // 创建一个子线程处理http请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownCaseList();
            }
        }).start();
    }

    public void Inivete(){
        cslist_view = (ListView)findViewById(R.id.tsCaseList) ;
        if (null!=caselist && caselist.size()>0){
            CaseAdapter caseAdapter = new CaseAdapter(this,R.layout.testcaselistlayout,caselist);
            cslist_view.setAdapter(caseAdapter);
            cslist_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView listView = (ListView) parent;
                    CaseListData1 indexdata = (CaseListData1) listView.getItemAtPosition(position);
                    fileUrl = indexdata.getFileUrl();
                    probNumber = indexdata.getProtocol();
                    personName = indexdata.getName();

                    Intent intent = new Intent(TestCaseListActivity.this,
                            showActivity.class);
                    startActivity(intent);
                }
            });
        }

    }




    public void DownCaseList(){
        HttpLogUtils httpLogUtils = new HttpLogUtils();
        httpLogUtils.setLevel(HttpLogUtils.Level.CUSTOM);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(300, TimeUnit.SECONDS).addInterceptor(httpLogUtils).build();
//            Retrofit retrofit = new Retrofit.Builder().baseUrl(ConstantsLive.NEW_API)
        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api1.iwown.com:8088/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();

        fileApi api = retrofit.create(fileApi.class);
        Call<CaseListData> call1 = api.downloadTestcase();
        call1.enqueue(new Callback<CaseListData>() {
//            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<CaseListData> call, Response<CaseListData> response) {

                Log.e(TAG,"DownCaseList--------成功-------------");
                CaseListData caseListData = response.body();
                if (null!=caseListData && caseListData.getReturnCode() ==0){
                    CaseListData1[] caseListData1s = new CaseListData1[caseListData.getData().size()];

                    for (int i=0;i<caseListData.getData().size();i++){
                       caseListData1s[i] = new CaseListData1();
                       caseListData1s[i].setName(caseListData.getData().get(i).getName());
                       caseListData1s[i].setCreator(caseListData.getData().get(i).getCreator());
                       caseListData1s[i].setDeviceModel(caseListData.getData().get(i).getDeviceModel());
                       caseListData1s[i].setDesc(caseListData.getData().get(i).getDesc());
                       caseListData1s[i].setProtocol(caseListData.getData().get(i).getProtocol());
                       caseListData1s[i].setFileUrl(caseListData.getData().get(i).getFileUrl());

                       caselist.add(caseListData1s[i]);
                   }
                    Inivete();
                }else {
                    Log.e(TAG,caseListData.getReturnCode()+"");
                }
            }

            @Override
            public void onFailure(Call<CaseListData> call, Throwable t) {
                Log.e(TAG,"DownCaseList------失败-----"+t.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
