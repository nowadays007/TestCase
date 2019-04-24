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

public class CaseListData1 {

    private int ID;
    private String Creator;
    private String Name;
    private String Desc;
    private int Protocol;
    private String DeviceModel;
    private String CreateDate;
    private String FileUrl;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCreator() {
        return Creator;
    }

    public void setCreator(String creator) {
        Creator = creator;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public int getProtocol() {
        return Protocol;
    }

    public void setProtocol(int protocol) {
        Protocol = protocol;
    }

    public String getDeviceModel() {
        return DeviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        DeviceModel = deviceModel;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }
}
