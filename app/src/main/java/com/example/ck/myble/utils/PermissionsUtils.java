package com.example.ck.myble.utils;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.NonNull;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

/**
 * Created by Daemon on 2017/4/10 13:58.
 */

public class PermissionsUtils {
    public interface PermissinCallBack {
        /**
         * 成功回调
         */
        void callBackOk();

        /**
         * 失败回调
         */
        void callBackFial();
    }

    /**
     * 通讯录 权限
     *
     * @param activity
     * @param callBack
     */
    public static void handleCONTACTS(Activity activity, final PermissinCallBack callBack) {
        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("注意:", "防问通讯录", "不让看", "打开权限");
        if (PermissionsUtil.hasPermission(activity, Manifest.permission.READ_CONTACTS)) {
//            Toast.makeText(activity, "访问联系人", Toast.LENGTH_LONG).show();
            callBack.callBackOk();
        } else {
            PermissionsUtil.requestPermission(activity, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户授予读取通讯录权限", Toast.LENGTH_LONG).show();
                    callBack.callBackOk();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户拒绝了读取通信录", Toast.LENGTH_LONG).show();
                    callBack.callBackFial();
                }
            }, new String[]{Manifest.permission.READ_CONTACTS}, false, tip);
        }
    }

    /**
     * 短信权限
     *
     * @param activity
     * @param callBack
     */
    public static void handleSMS(Activity activity, final PermissinCallBack callBack) {
        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("注意:", "功能需要获取短信权限", "不让看", "打开权限");
        if (PermissionsUtil.hasPermission(activity, Manifest.permission.READ_SMS)) {
//            Toast.makeText(activity, "访问短信", Toast.LENGTH_LONG).show();
            callBack.callBackOk();
        } else {
            PermissionsUtil.requestPermission(activity, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户授予读取短信权限", Toast.LENGTH_LONG).show();
                    callBack.callBackOk();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户拒绝了读取短信", Toast.LENGTH_LONG).show();
                    callBack.callBackFial();
                }
            }, new String[]{Manifest.permission.READ_SMS}, false, tip);
        }
    }

    /**
     * 位置 权限
     *
     * @param activity
     * @param callBack
     */
    public static void handleLOCATION(Activity activity, final PermissinCallBack callBack) {
        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("注意:", "功能需要获取地理位置权限", "不让看", "打开权限");
        if (PermissionsUtil.hasPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
//            Toast.makeText(activity, "访问地理位置", Toast.LENGTH_LONG).show();
            callBack.callBackOk();
        } else {
            PermissionsUtil.requestPermission(activity, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户授予地理位置权限", Toast.LENGTH_LONG).show();
                    callBack.callBackOk();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户拒绝了地理位置短信", Toast.LENGTH_LONG).show();
                    callBack.callBackFial();
                }
            }, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, false, tip);
        }
    }

    public static void handleLOCATION(Activity activity, final PermissinCallBack callBack, boolean showTip) {
        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("注意:", "功能需要获取地理位置权限", "不让看", "打开权限");
        if (PermissionsUtil.hasPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)) {
//            Toast.makeText(activity, "访问地理位置", Toast.LENGTH_LONG).show();
            callBack.callBackOk();
        } else {
            PermissionsUtil.requestPermission(activity, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户授予地理位置权限", Toast.LENGTH_LONG).show();
                    callBack.callBackOk();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户拒绝了地理位置短信", Toast.LENGTH_LONG).show();
                    callBack.callBackFial();
                }
            }, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, false, tip);
        }
    }


    /**
     * STORAGE 存储权限
     *
     * @param activity
     * @param callBack
     */
    public static void handleSTORAGE(Activity activity, final PermissinCallBack callBack) {
        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("注意:", "功能需要获取存储权限", "不让看", "打开权限");
        if (PermissionsUtil.hasPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            Toast.makeText(activity, "访问存储", Toast.LENGTH_LONG).show();
            callBack.callBackOk();
        } else {
            PermissionsUtil.requestPermission(activity, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户授予存储权限", Toast.LENGTH_LONG).show();
                    callBack.callBackOk();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户拒绝了存储短信", Toast.LENGTH_LONG).show();
                    callBack.callBackFial();
                }
            }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, false, tip);
        }
    }

    /**
     * STORAGE 存储权限
     *
     * @param activity
     * @param callBack
     */
    public static void handleSTORAGEWrite(Activity activity, final PermissinCallBack callBack) {
        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("注意:", "功能需要获取存储权限", "不让看", "打开权限");
        if (PermissionsUtil.hasPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            Toast.makeText(activity, "访问存储", Toast.LENGTH_LONG).show();
            callBack.callBackOk();
        } else {
            PermissionsUtil.requestPermission(activity, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户授予存储权限", Toast.LENGTH_LONG).show();
                    callBack.callBackOk();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户拒绝了存储短信", Toast.LENGTH_LONG).show();
                    callBack.callBackFial();
                }
            }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, false, tip);
        }
    }

    /**
     * 摄像头
     *
     * @param activity
     * @param callBack
     */
    public static void handleCAMER(Activity activity, final PermissinCallBack callBack) {
        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("注意:", "功能需要获取摄像头权限", "不让看", "打开权限");
        if (PermissionsUtil.hasPermission(activity, Manifest.permission.CAMERA)) {
            callBack.callBackOk();
        } else {
            PermissionsUtil.requestPermission(activity, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户授予摄像头权限", Toast.LENGTH_LONG).show();
                    callBack.callBackOk();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
//                    Toast.makeText(activity, "用户拒绝了摄像头短信", Toast.LENGTH_LONG).show();
                    callBack.callBackFial();
                }
            }, new String[]{Manifest.permission.CAMERA}, false, tip);
        }
    }

    /**
     * 手机状态 拨打号码
     * group:android.permission-group.PHONE
     * permission:android.permission.READ_CALL_LOG
     * permission:android.permission.READ_PHONE_STATE
     * permission:android.permission.CALL_PHONE
     * permission:android.permission.WRITE_CALL_LOG
     * permission:android.permission.USE_SIP
     * permission:android.permission.PROCESS_OUTGOING_CALLS
     * permission:com.android.voicemail.permission.ADD_VOICEMAIL
     *
     * @param activity
     * @param callBack
     */
    public static void handlePhone(Activity activity, final PermissinCallBack callBack) {
        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("注意:", "功能需要获取手机状态权限", "不让看", "打开权限");
        if (PermissionsUtil.hasPermission(activity, Manifest.permission.READ_PHONE_STATE)) {
            callBack.callBackOk();
        } else {
            PermissionsUtil.requestPermission(activity, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permissions) {
                    callBack.callBackOk();
                }

                @Override
                public void permissionDenied(@NonNull String[] permissions) {
                    callBack.callBackFial();
                }
            }, new String[]{Manifest.permission.READ_PHONE_STATE}, false, tip);
        }
    }
}


/**
 * 危险权限
 * <p>
 * 这些权限是在开发6.0程序时，必须要注意的。
 * 这些权限处理不好，程序可能会直接被系统干掉。
 * 权限如下：
 * <p>
 * 权限组	权限
 * CALENDAR	READ_CALENDAR，WRITE_CALENDAR
 * CAMERA	CAMERA
 * CONTACTS	READ_CONTACTS，WRITE_CONTACTS，GET_ACCOUNTS
 * LOCATION	ACCESS_FINE_LOCATION，ACCESS_COARSE_LOCATION
 * MICROPHONE	RECORD_AUDIO
 * PHONE	READ_PHONE_STATE，CALL_PHONE，READ_CALL_LOG，WRITE_CALL_LOG，ADD_VOICEMAIL，USE_SIP，PROCESS_OUTGOING_CALLS
 * SENSORS	BODY_SENSORS
 * SMS	SEND_SMS，RECEIVE_SMS，READ_SMS，RECEIVE_WAP_PUSH，RECEIVE_MMS
 * STORAGE	READ_EXTERNAL_STORAGE，WRITE_EXTERNAL_STORAGE
 * 我们会发现这些权限被分成了组。每个组里面包含了一些相近的权限。
 * <p>
 * 一些权限组
 * group:android.permission-group.CONTACTS
 * permission:android.permission.WRITE_CONTACTS
 * permission:android.permission.GET_ACCOUNTS
 * permission:android.permission.READ_CONTACTS
 * <p>
 * group:android.permission-group.PHONE
 * permission:android.permission.READ_CALL_LOG
 * permission:android.permission.READ_PHONE_STATE
 * permission:android.permission.CALL_PHONE
 * permission:android.permission.WRITE_CALL_LOG
 * permission:android.permission.USE_SIP
 * permission:android.permission.PROCESS_OUTGOING_CALLS
 * permission:com.android.voicemail.permission.ADD_VOICEMAIL
 * <p>
 * group:android.permission-group.CALENDAR
 * permission:android.permission.READ_CALENDAR
 * permission:android.permission.WRITE_CALENDAR
 * <p>
 * group:android.permission-group.CAMERA
 * permission:android.permission.CAMERA
 * <p>
 * group:android.permission-group.SENSORS
 * permission:android.permission.BODY_SENSORS
 * <p>
 * group:android.permission-group.LOCATION
 * permission:android.permission.ACCESS_FINE_LOCATION
 * permission:android.permission.ACCESS_COARSE_LOCATION
 * <p>
 * group:android.permission-group.STORAGE
 * permission:android.permission.READ_EXTERNAL_STORAGE
 * permission:android.permission.WRITE_EXTERNAL_STORAGE
 * <p>
 * group:android.permission-group.MICROPHONE
 * permission:android.permission.RECORD_AUDIO
 * <p>
 * group:android.permission-group.SMS
 * permission:android.permission.READ_SMS
 * permission:android.permission.RECEIVE_WAP_PUSH
 * permission:android.permission.RECEIVE_MMS
 * permission:android.permission.RECEIVE_SMS
 * permission:android.permission.SEND_SMS
 * permission:android.permission.READ_CELL_BROADCASTS
 **/