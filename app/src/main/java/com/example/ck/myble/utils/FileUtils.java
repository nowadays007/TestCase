package com.example.ck.myble.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import static android.content.ContentValues.TAG;

public class FileUtils {
    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public static File write2SDFromString_1(String path, String fileName, String string) {
        File file = null;
        FileWriter output = null;
        try {
            creatSDDir(path);
            file = createSDFile(path + fileName);
            output = new FileWriter(file, true);
            output.write(string);
            output.write("\r\n");
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null)
                    output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }
    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public static File creatSDDir(String dirName) {
        File dir = new File(Environment.getExternalStorageDirectory()+"/" + dirName);
        if(!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }
    /**
     * 在SD卡上创建文件
     *
     * @throws IOException
     */
    public static File createSDFile(String fileName) throws IOException {
        File file = new File( Environment.getExternalStorageDirectory()+"/" + fileName);
        if(!file.exists()){
            file.createNewFile();
//            KLog.e("---create file : " + Environment.getExternalStorageDirectory()+"/" + fileName);
            Log.e(TAG, "--- create SDFile ---: "+Environment.getExternalStorageDirectory()+"/"+fileName);
        }
        return file;
    }
    /**
     * 读取SD卡文件里面的内容
     */
    public static void readSDfile(String pat)
            throws IOException {
        FileReader fr = new FileReader(pat);
        BufferedReader r = new BufferedReader(fr);
        String result = r.readLine();
        while (result != null) {
            Log.d(TAG, "SD卡文件里面的内容:" + result);
            result = r.readLine();
        }

    }

    public static void deleteLocal(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();//如果为文件，直接删除
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File file1 : files) {
                    deleteLocal(file1);//如果为文件夹，递归调用
                }
            }
            file.delete();
        }
    }

    /**
     * 删除SD卡文件里面的内容
     * 调用时，deleteAllFiles(new File(Environment.getExternalStorageDirectory()+"/myimage/"));
     * 删除sd卡myimage下的所有文件
     */
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }


    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        makeFilePath(filePath, fileName);

        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    //生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    //读取指定目录下的所有TXT文件的文件内容
    public static String getFileContent(File file) {
        String content = "";
        if (!file.isDirectory()) {  //检查此路径名的文件是否是一个目录(文件夹)
            if (file.getName().endsWith("txt")) {//文件格式为""文件
                try {
                    InputStream instream = new FileInputStream(file);
                    if (instream != null) {
                        InputStreamReader inputreader
                                = new InputStreamReader(instream, "UTF-8");
                        BufferedReader buffreader = new BufferedReader(inputreader);
                        String line = "";
                        //分行读取
                        while ((line = buffreader.readLine()) != null) {
                            content += line + "\n";
                        }
                        instream.close();//关闭输入流
                    }
                } catch (java.io.FileNotFoundException e) {
                    Log.d("TestFile", "The File doesn't not exist.");
                } catch (IOException e) {
                    Log.d("TestFile", e.getMessage());
                }
            }
        }
        return content;
    }

    /**
     * 判断文件是否存在
     */
    public void FileExite(){
        final File file = new File(Environment.getExternalStorageDirectory()+"/sdcard/Gyt");
            if (!file.exists()) {
                Log.e(TAG,"***  存在****  ");
            } else {
                Log.e(TAG,"*** 不存在 ****  ");
            }
    }
}
