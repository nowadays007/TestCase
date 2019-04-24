package com.example.ck.myble.Sendhttp;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseClient {

//        HttpLogUtils httpLogUtils = new HttpLogUtils();
//        httpLogUtils.setel1(HttpLogUtils.Level.CUSTOM);
//        OkHttpClient okHttpClient;
//        if(LOG_DEBUG){
//            okHttpClient = new OkHttpClient.Builder()
//                    .connectTimeout(60, TimeUnit.SECONDS)
//                    .addInterceptor(new LogInterceptor())
//                    .build();
//        }else {
//            okHttpClient = new OkHttpClient.Builder()
//                    .connectTimeout(60, TimeUnit.SECONDS)
//                    .build();
//        }
//        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api1.iwown.com/nggservice/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .client(okHttpClient)
//                .build();
//        fileApi api = retrofit.create(fileApi.class);
//
//        Call<CaseListData> call1 = api.downloadTestcase();
//
//        call1.enqueue(new Callback<CaseListData>() {
//            @Override
//            public void onResponse(Call<CaseListData> call, Response<CaseListData> response) {
//
//            }
//
//            @Override
//            public void onFailure(Call<CaseListData> call, Throwable t) {
//
//            }
//        });


//        HttpLogUtils httpLogUtils = new HttpLogUtils();
//        httpLogUtils.setLevel(HttpLogUtils.Level.CUSTOM);
//        OkHttpClient okHttpClient;
//        if(LOG_DEBUG){
//            okHttpClient = new OkHttpClient.Builder()
//                    .connectTimeout(60, TimeUnit.SECONDS)
//                    .addInterceptor(new LogInterceptor())
//                    .build();
//        }else {
//            okHttpClient = new OkHttpClient.Builder()
//                    .connectTimeout(60, TimeUnit.SECONDS)
//                    .build();
//        }
//        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://api1.iwown.com/nggservice/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .client(okHttpClient)
//                .build();
//        ProjectAPI api = retrofit.create(ProjectAPI.class);
//        Call<ResponseBody> call = api.uploadBlood(bul);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                try {
//                    KLog.e(TAG, "initData: 血压上传   7777  "+response.body().string());
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                KLog.e("血压上传失败");
//            }
//        });
//    }
}
