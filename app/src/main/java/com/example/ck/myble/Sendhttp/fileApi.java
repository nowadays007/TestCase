package com.example.ck.myble.Sendhttp;

import java.io.File;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface fileApi {

//    http://api1.iwown.com:8088/
//    http://api1.iwown.com:8088/testcase/list

//    http://api1.iwown.com:8088/response/upload
//    post, 文件form : file, 上传响应的数据文件

    @GET("testcase/list")
    Call<CaseListData> downloadTestcase();
}
