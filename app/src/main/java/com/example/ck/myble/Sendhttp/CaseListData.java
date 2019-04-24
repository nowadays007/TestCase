package com.example.ck.myble.Sendhttp;

import java.util.List;

/**
 *  {"ReturnCode":0,
 "Data":[{
 "ID":6,
 "Creator":"LJ",
 "Name":"testL",
 "Desc":"testCase",
 "Protocol":4,
 "DeviceModel":"34",
 "CreateDate":"2018-12-19",
 "FileUrl":"http://devapi.iwown.com/fmtest/testL2018-12-19"
 }]
 }
 */
public class CaseListData {
    private int ReturnCode;
    private List<CaseListData1> Data;

    public int getReturnCode() {
        return ReturnCode;
    }

    public void setReturnCode(int returnCode) {
        ReturnCode = returnCode;
    }

    public List<CaseListData1> getData() {
        return Data;
    }

    public void setData(List<CaseListData1> data) {
        Data = data;
    }
}
