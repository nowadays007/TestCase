package com.example.ck.myble;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.example.ck.myble.Sendhttp.CaseListData;
import com.example.ck.myble.Sendhttp.CaseListData1;
import com.example.ck.myble.Sendhttp.HttpLogUtils;
import com.example.ck.myble.Sendhttp.fileApi;
import com.example.ck.myble.dataFile.CaseStr;
import com.example.ck.myble.dataFile.TestCaseListActivity;
import com.example.ck.myble.utils.FileUtils;
import com.example.ck.myble.utils.MyQuen;
import com.example.ck.myble.utils.Utils;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;

public class showActivity extends Activity {

    private final static String TAG = showActivity.class.getSimpleName();
    private TextView textView2;
    private Button uploadbtn;

    public static int sendLength;
    public static int sendtabLength;
    public static byte[] a;
    private boolean dataisOver =false;
    private String fileName;
    private String nowCase;
    private String oldCase = "";
    private String zgallData = null;
    private String receveStr;

    MyQuen queue = new MyQuen();
    BpCllback callback;
    public BpCllback getCallback() {
        return callback;
    }
    public void setCallback(BpCllback callback) {
        this.callback = callback;
    }

    private static final MediaType FROM_DATA = MediaType.parse("multipart/form-data");

    public interface BpCllback{
        void bpData(int a);
    }

    Handler handler=new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if (receveStr == ""){
                Log.e("---l808- Handler:","发送的指令 没有返回的字符串");
                receveStr = "返回为null";

                addText(textView2, "发送的字符串："+'\n'+nowCase);
//                if (receveStr.length()>40){
//                    addText(textView2, "返回的字符串：");
//                    while (receveStr.length()>40){
//                        addText(textView2, receveStr.substring(0,40));
//                        receveStr = receveStr.substring(40);
//                    }
//                    if (receveStr.length()<40){
                        addText(textView2, receveStr);
//                    }
//                }else {
                    addText(textView2, "返回的字符串："+'\n'+receveStr);
                    Log.e(TAG, "run: 呵呵呵：" + receveStr);
//                }

                addText(textView2, "------------------------------------------------------------------");
                textView2.setMovementMethod(ScrollingMovementMethod.getInstance());

                if (TestCaseListActivity.probNumber == 4){
                    //转换json字符串 保存到文件{"id":"112","name":"down","sentence":"1801"}
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd  HH:mm:ss");
                    String timestr = df.format(new Date());
                    if (oldCase != nowCase){
                        nowCase = nowCase.replace(" ","");
                        int idstrH = Integer.valueOf(nowCase.substring(12,14),16);
                        int idstrL = Integer.valueOf(nowCase.substring(14,16),16);
                        Log.e(TAG, "run: 0000000000"+idstrH +" "+idstrL);
                        int idstr = idstrH+idstrL*256;
                        String sentence = nowCase.substring(16);

                        FileUtils.writeTxtToFile(timestr+":{\"id\":\""+idstr+"\",\"name\":\"down\",\"sentence\":\""+sentence+"\"}", "/sdcard/IwownTest/", fileName);
                        oldCase = nowCase;
                    }
                    receveStr = "0";
                    FileUtils.writeTxtToFile(timestr+":{\"id\":\""+receveStr+"\",\"name\":\"up\",\"sentence\":\""+receveStr+"\"}", "/sdcard/IwownTest/", fileName);
                }else {
                    FileUtils.writeTxtToFile("发送的字符串："+'\n'+nowCase, "/sdcard/IwownTest/", fileName);
                    FileUtils.writeTxtToFile("返回的字符串："+'\n'+receveStr, "/sdcard/IwownTest/", fileName);
                }


                //发布消息
                EventBus.getDefault().postSticky(new MessageEvent(1));
                Log.e("---l808- EventBus:","");
            }
        }
    };

    showActivity.MyBroadcastReceiver receiver=new showActivity.MyBroadcastReceiver();
    private class MyBroadcastReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(final Context context, Intent intent) {
            if(intent.getAction().equals(BluetoothLeService.ACTION_DATA_AVAILABLE)){
                String str = intent.getStringExtra(BluetoothLeService.EXTRA_DATA).toString();

                if (TestCaseListActivity.probNumber == 3){
                        dataisOver=true;
                }else if (TestCaseListActivity.probNumber == 4){
                    dataisOver = true;
                }else if (TestCaseListActivity.probNumber == 1 || TestCaseListActivity.probNumber == 2){
                    str = str.replace(" ", "");
                    if (Utils.contactData(Utils.hexStringToBytes(str))) {
                        str = Utils.dataStr;
                        dataisOver = true;
                    }
                }

                if (null!=str&&dataisOver == true){
                    receveStr = str;
                    receveStr = receveStr.replace(" ","");
                    addText(textView2, "发送的字符串："+'\n'+nowCase);
//                    if (receveStr.length()>40){
//                        addText(textView2, "返回的字符串：");
//                        while (receveStr.length()>40){
//                            addText(textView2, receveStr.substring(0,40));
//                            receveStr = receveStr.substring(40);
//                        }
//                        if (receveStr.length()<40){
//                            addText(textView2, receveStr);
//                        }
//                    }else {
                        addText(textView2, "返回的字符串："+'\n'+receveStr);
//                    }
                    addText(textView2, "------------------------------------------------------------------");
                    textView2.setMovementMethod(ScrollingMovementMethod.getInstance());

                    if(TestCaseListActivity.probNumber == 4){
                        //转换json字符串 保存到文件{"id":"112","name":"down","sentence":"1801"}
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String timestr = df.format(new Date());
                        if (oldCase != nowCase){
                            nowCase = nowCase.replace(" ","");
                            int idstrH = Integer.valueOf(nowCase.substring(12,14),16);
                            int idstrL = Integer.valueOf(nowCase.substring(14,16),16);
                            int idstr = idstrH+idstrL*256;
                            String sentence = nowCase.substring(16);
                            FileUtils.writeTxtToFile(timestr+":{\"id\":\""+idstr+"\",\"name\":\"down\",\"sentence\":\""+sentence+"\"}", "/sdcard/IwownTest/", fileName);
                            oldCase = nowCase;
                        }
                        Log.e("---l808- receveStr:",receveStr);
                        int reidstrH = Integer.valueOf(receveStr.substring(12,14),16);
                        int reidstrL = Integer.valueOf(receveStr.substring(14,16),16);
                        int reidstr = reidstrH+reidstrL*256;
                        Log.e("---l808- EventBus:",receveStr.substring(12,14)+"  "+receveStr.substring(14,16));
                        String resentence = receveStr.substring(16);
                        FileUtils.writeTxtToFile(timestr+":{\"id\":\""+reidstr+"\",\"name\":\"up\",\"sentence\":\""+resentence+"\"}", "/sdcard/IwownTest/", fileName);
                    }else {
                        FileUtils.writeTxtToFile("发送的字符串："+'\n'+nowCase, "/sdcard/IwownTest/", fileName);
                        FileUtils.writeTxtToFile("返回的字符串："+'\n'+receveStr, "/sdcard/IwownTest/", fileName);
                    }

                    //发布消息
                    EventBus.getDefault().postSticky(new MessageEvent(1));
                    Log.e("---l808- EventBus:","");
                    dataisOver = false;
                }else {
                    Log.e("---l808- 需要拼接字符串","");
                }

            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        // 申请并获得权限
        if (ContextCompat.checkSelfPermission(showActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(showActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,1);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        textView2 = findViewById(R.id.tv_showRecive);
        uploadbtn = findViewById(R.id.uploadbtn);

        IntentFilter filter=new IntentFilter();
        filter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        this.registerReceiver(receiver,filter);

        //注册订阅者
        EventBus.getDefault().register(this);
        new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void run() {
                readurlfile();
            }
        }).start();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        fileName = df.format(new Date())+TestCaseListActivity.personName+".txt";
        FileUtils.deleteLocal(new File("/sdcard/IwownTest/"+fileName));


        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        String filepath = "/sdcard/IwownTest/"+fileName;
                        try{
                            String responseData= TestSavepost("http://api1.iwown.com:8088/response/upload",filepath,fileName);
                            Log.e("---l808- responseData:",responseData);
                            if(responseData.toString().equals("{\"ReturnCode\":0}")){
                                Looper.prepare();
                                Toast.makeText(showActivity.this,"上传成功",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }else {
                                Looper.prepare();
                                Toast.makeText(showActivity.this,"上传失败",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }catch (IOException e){
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(receiver);
        //注销事件注册
        EventBus.getDefault().unregister(this);
    }

    public class MessageEvent {
        public int msg;

        public MessageEvent(int msg) {
            this.msg = msg;
        }
    }

    private void addText(TextView textView, String content) {
        textView.append(content);
        textView.append("\n");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void readurlfile(){
        try {
            URL url = new URL(TestCaseListActivity.fileUrl);
            Log.e("---l808- 2222", url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");

            conn.connect();
            InputStream stream = conn.getInputStream();

            String str = new BufferedReader(new InputStreamReader(stream))
                    .lines().parallel().collect(Collectors.joining(System.lineSeparator()));

            conn.disconnect();
            stream.close();

            if (TestCaseListActivity.probNumber == 3) {
                queue.enQueue("010181080000000000000000");
            }
            //指令入队
//            synchronized (this) {
                String[] ss = str.split("\n");
                if (null!=ss) {
                for (int i = 0; i < ss.length; i++) {
                    queue.enQueue(ss[i]);
                    Log.e("---队列长度 数据", "==：" + queue.QueueLength());
                }
                //读取队列的第一条case并发送
                Log.e("---l808- 2222", queue.QueuePeek().toString());
                SendCase(queue.QueuePeek().toString());
                receveStr = "";
                queue.deQueue();
                handler.postDelayed(runnable, 5000);//每5秒执行一次runnable.
//            }
            }


            } catch(MalformedURLException e){
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(MessageEvent event){
        handler.removeCallbacks(runnable);
        Log.e("---接收 EventBus 的数据", "========= :" + event.msg);
        if(!queue.QueueEmpty()){
            SendCase(queue.QueuePeek().toString());
            receveStr = "";
            queue.deQueue();
            handler.postDelayed(runnable, 7000);//每5秒执行一次runnable.
        }else {
            Log.e("---队列 为空", "指令文件已读取完毕并发送成功");
            Toast.makeText(this,"指令文件已读取完毕并发送成功",Toast.LENGTH_SHORT);

        }

    }

    public String TestSavepost(String url, String filePath, String fileName) throws Exception {
            OkHttpClient client = new OkHttpClient();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName,
                            RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath)))
                    .build();

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();

            okhttp3.Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return String.valueOf(response.body().string());
    }


    public void SendCase(String str){
        BluetoothGattCharacteristic characteristic1 = null;

        str = str.replace(" ","");
        sendLength = str.length()/2;
        sendtabLength = 20;
        Log.e("---l808- probNumber发数据", "=========" + str);
        if (TestCaseListActivity.probNumber == 2) {
            characteristic1 = MyApplication.getInstance().mBluetoothLeService.characteristicByUUID();
            characteristic1.setValue(Utils.getHexBytes(str));
            BluetoothLeService.mBluetoothGatt.writeCharacteristic(characteristic1);
            nowCase = str;
        } else if (TestCaseListActivity.probNumber == 4) {
            characteristic1 = MyApplication.getInstance().mBluetoothLeService.characteristiByUUIDProb();
            characteristic1.setValue(Utils.getHexBytes(str));
            BluetoothLeService.mBluetoothGatt.writeCharacteristic(characteristic1);
            nowCase = str;
        } else if (TestCaseListActivity.probNumber == 1) {
            characteristic1 = MyApplication.getInstance().mBluetoothLeService.characteristiByUUIDIwown();
            characteristic1.setValue(Utils.getHexBytes(str));
            BluetoothLeService.mBluetoothGatt.writeCharacteristic(characteristic1);
            nowCase = str;
        } else if (TestCaseListActivity.probNumber == 3) {
            characteristic1 = MyApplication.getInstance().mBluetoothLeService.characteristiByUUIDZG();
            characteristic1.setValue(Utils.getHexBytes(str));
            BluetoothLeService.mBluetoothGatt.writeCharacteristic(characteristic1);
            nowCase = str;
        }
    }

}
