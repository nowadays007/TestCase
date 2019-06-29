package com.example.ck.myble.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;
import static java.lang.Integer.toHexString;


public class Utils {

    public static byte SMS_TYPE = 2;
    public static byte FONT_TYPE = -1;
    public static final String sleepFile="/Zeroner/sleep/";
    public static final long uid=111222L;
    public static boolean isDataOver = true;
    public static int dataLength = -1;
    public static byte[] datas;

    public  static String dataStr;


    private static Utils INSTANCE;

    private Utils() {}



    public static void hideViews(View... views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.GONE);
        }
    }




    public static Utils getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Utils();
        }
        return INSTANCE;
    }

    public byte form_Header(int grp, int cmd) {
        return (byte) ((((byte) grp & 0x0f) << 4) | ((byte) cmd & 0x0f));
    }


    /**
     * Convert hex string to byte[]
     * @param hexString the hex string
     * @return byte[]
     */
    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }


    public static String bytesToString(byte[] bytes) {
        return bytesToString(bytes, true);
    }

    /**
     * CRC16校验
     * @param bytes
     * @return
     */
    public static int crc16Modem(byte[] bytes) {
        int crc = 0x00;          // initial value
        int polynomial = 0x1021;
        for (byte index : bytes) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((index >> (7 - i) & 1) == 1);
                boolean c15 = ((crc >> 15 & 1) == 1);
                crc <<= 1;
                if (c15 ^ bit) crc ^= polynomial;
            }
        }
        crc &= 0xffff;
        return crc;

    }
    public static String bytesToString(byte[] bytes, boolean needSpace) {
        if (bytes == null) return null;
        StringBuilder stringBuilder = new StringBuilder(bytes.length);
        String format;
        if (needSpace) {
            format = "%02X ";
        } else {
            format = "%02X";
        }

        for (byte byteChar : bytes)
            stringBuilder.append(String.format(format, byteChar));
        return stringBuilder.toString();
    }

    /**
     * 字符串转换成十六进制字符串
     //     * @param String str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str)
    {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++)
        {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
//            sb.append(' ');
        }
        return sb.toString().trim();
    }
    public byte[] writeWristBandDataByte(boolean prefix, byte header, ArrayList<Byte> datas) {
        byte[] commonData = new byte[4];
        if (prefix) {
            /** 写入 */
            commonData[0] = 0x21;
        } else {
            /** 读 数据 */
            commonData[0] = 0x23;
        }
        commonData[1] = (byte) 0xff;
        commonData[2] = header;
        if (datas != null) {
            commonData[3] = (byte) datas.size();
            byte[] data = new byte[datas.size()];
            for (int i = 0; i < datas.size(); i++) {
                data[i] = datas.get(i);
            }
            return concat(commonData, data);
        } else {
            commonData[3] = 0;
            return commonData;
        }
    }


    static public  Byte[] byteoByte(byte[] data){
        Byte[] Data = new Byte[data.length];
        for (int i =0;i<data.length;i++){
            Data[i] = data[i];
        }
        return Data;
    }


    /**
     * byte转换成16进制字符串
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 十六进制String转byte[]
     * @param str
     * @return
     */
    public static byte[] hexStrToByteArray(String str)
    {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return new byte[0];
        }
        byte[] byteArray = new byte[str.length() / 2];
        for (int i = 0; i < byteArray.length; i++){
            String subStr = str.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte)Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }


    /**
     * 将字符串转化为16进制的字节
     *
     * @param message
     *            需要被转换的字符
     * @return
     */
    public static byte[] getHexBytes(String message) {
        int len = message.length() / 2;
        char[] chars = message.toCharArray();

        String[] hexStr = new String[len];

        byte[] bytes = new byte[len];

        for (int i = 0, j = 0; j < len; i += 2, j++) {
            hexStr[j] = "" + chars[i] + chars[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
    }


    public static void addText(TextView textView, String content) {
        textView.append(content);
        textView.append("\n");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }



//    /**
//     *  普通字符转换成16进制字符串
//     * @param str
//     * @return
//     */
//    public static String str2HexStr(String str)
//    {
//        byte[] bytes = str.getBytes();
//        // 如果不是宽类型的可以用Integer
//        BigInteger bigInteger = new BigInteger(1, bytes);
//        return bigInteger.toString(16);
//    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    /**
     * 16进制 字符串转换为 2进制
     * @param s
     * @return
     */
    public static String HexToTwo(String s) {
//        String format = String.format("%017d", Long.parseLong(num));
        String a = Integer.toString(Integer.parseInt (s, 16),2);
        for (int i = 0; a.length() < 8; i++) {
            a = "0"+a;
        }
        return a;
    }
    public static String TenToTwo(int s) {
//        String format = String.format("%017d", Long.parseLong(num));
        String a = Integer.toBinaryString(s);
        for (int i = 0; a.length() < 8; i++) {
            a = "0"+a;
        }
        return a;
    }

    /**
     * int to byte[] 支持 1或者 4 个字节 小断模式
     *
     * @param i
     * @param len
     * @return
     */
    public static byte[] intToByte(int i, int len) {
        byte[] abyte = null;
        if (len == 1) {
            abyte = new byte[len];
            abyte[0] = (byte) (0xff & i);
        } else {
            abyte = new byte[len];
            abyte[0] = (byte) (0xff & i);
            abyte[1] = (byte) ((0xff00 & i) >> 8);
            abyte[2] = (byte) ((0xff0000 & i) >> 16);
            abyte[3] = (byte) ((0xff000000 & i) >> 24);
        }
        return abyte;
    }

/**
 *  int数字转换成16进制
 */
    public static String kk(int a){
        String B = String.valueOf(Integer.toHexString(a));
        if (B.length()<2){
            B = "0"+B;
        }
        return B;
    }




    /**
     * 保存连接的设备名 和 地址
     * @param context
     * @param address 地址
     * @param deviceName 设备名
     */
    public static void setShareAddress(Context context,String address,String deviceName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("address", address);
        editor.putString("deviceName", deviceName);
        editor.commit();
    }

    /**
     *
     * @param context
     * @return 设备地址
     */
    public static String getAddress(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString("address", "");
    }

    /**
     *
     * @param context
     * @return 设备名
     */
    public static String getDeviceName(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        return sharedPreferences.getString("deviceName", "");
    }




    /**
     * 接收数据  拼包 MTK
     * @param data
     * @return
     */
    public static boolean contactData(byte[] data){

        if (data != null && data.length != 0) {
//            Utils.bytesToString(data)
            Log.i(TAG, "接收原始数据 未处理 datas--->" + Utils.bytesToHexString(data));

            if (isDataOver && dataLength == -1) {
                if (data[0] == 0x23 || data[0] == 0x22) {
                    dataLength = data[3]&0xff;
                    datas = new byte[]{};
                }
            } else {
                if ((data[0] == 0x23 || data[0] == 0x22) && data.length > 3 && (data[1] == (byte) 0xFF)) {
                    datas = new byte[]{};
                    dataLength = data[3]&0xff;
                    Log.i(TAG, "发现掉包情况 --->" +bytesToHexString(data));
//                    MtkBroadcastSender.getInstance().onError(MTKBleError.BLE_NOTIFY_DATA_LOSE_PACKAGE);
                }
            }
            if (dataLength != -1) {
                datas = Utils.concat(datas, data);
                Log.e(TAG, "contactData: 正在拼接"+bytesToHexString(datas));
            }

            try {
                if (dataLength != -1 && datas.length - 4 >= dataLength) {
                    Log.i(TAG, "接收数据长度 datas--->" + (datas.length - 4) + "    ff = " + datas[1]);
                    Log.i(TAG, "接收原始数据 datas--->" + bytesToHexString(data));
                    dataStr = bytesToHexString(datas);
                    Log.e(TAG, "contactData: === == "+dataStr );
                    clearDataState();

                    //数据拼接完毕
                    return true;
                } else if (dataLength != -1) {
                    isDataOver = false;
                    return isDataOver;
                }
            } catch (Exception e) {
                clearDataState();
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }




    public static void clearDataState() {
        isDataOver = true;
        dataLength = -1;
    }

    /**
     * int16类型转换成 16进制 字符串（包括天气的负数值）
     * @param number
     * @return
     */
    public static String int16toHex(int number) {
        String result = Integer.toHexString(number & 0xffff);
        Log.i("debug",String.format("--*weather transform %d 2hex %s",number,result));
        boolean flag = false;
        while (result.length() < 2) {
            if(number>=0)
                result = "0"+result;
            else{
                result = "F"+result;
            }
        }
        if(result.length() == 2){
            while (result.length() < 4) {
                    if(number>=0)
                        result = result+"0";
                    else {
                        result = result+"F";
                    }
            }
        }
        else {
            flag = true;
            while (result.length() < 4) {
                    if(number>=0) {
                        result = "0"+result;
                    }
                    else {
                        result = "F"+result;
                    }

            }
        }
        if(flag){
            String ss = result.substring(2,4)+result.substring(0,2);
            result = ss;
        }

        return result.toUpperCase();
    }


    private static String SDPATH = Environment.getExternalStorageDirectory() + "/";
    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public static File creatSDDir(String dirName) {
        File dir = new File(SDPATH + dirName);
        dir.mkdirs();
        return dir;
    }

    /**
     * 在SD卡上创建文件
     *
     * @throws IOException
     */
    public static File creatSDFile(String fileName) throws IOException {
        File file = new File(SDPATH + fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }






    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public static File wirteData2File(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            File file1 = creatSDDir(path);
            file = new File(file1, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            output = new FileOutputStream(file, true);
            byte buffer[] = new byte[128];
            while ((input.read(buffer)) != -1) {
                output.write(buffer);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    public static InputStream StringTOInputStream(String in) throws Exception {

        ByteArrayInputStream is = new ByteArrayInputStream(in.getBytes("utf-8"));
        return is;
    }

    /**
     * 合并两数组
     *
     * @param a
     * @param b
     * @return
     */
    public static byte[] concat(byte[] a, byte[] b) {
        if (a == null) {
            return b;
        } else if (b == null) {
            return a;
        } else {
            byte[] c = new byte[a.length + b.length];
            System.arraycopy(a, 0, c, 0, a.length);
            System.arraycopy(b, 0, c, a.length, b.length);
            return c;
        }
    }

}
