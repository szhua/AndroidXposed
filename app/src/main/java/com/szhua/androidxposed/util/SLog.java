package com.szhua.androidxposed.util;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
/**
 * AndroidXposed
 * Create   2017/1/3 9:54;
 * https://github.com/szhua
 *
 * @author sz.hua
 */
public class SLog {

    private  StringBuffer mLogBuffer = new StringBuffer();
    private  boolean mEnableSaveToFile = true; //是否允许保存文件
    private  static final  String mLogFilePath="/SLog"; //文件
    private  static final  String mLogFileName="slog.txt"; //日志
    private  WriteTask writeTask =new WriteTask();

    private SLog( ){}
    private static class  SLogSingleTon{
        private static  SLog instance(){return  new SLog();}
    }
    public static  SLog getInstance(){
        return  SLogSingleTon.instance();
    }

 public  void setmEnableSaveToFile(boolean enableSaveToFile){
     mEnableSaveToFile=enableSaveToFile;
 }

  public void writIntoFile( String weChatmsg)
    {
        if (!mEnableSaveToFile)
        {
            return;
        }

            mLogBuffer.append(weChatmsg);
            writeTask.execute();


    }

    private  void writeBufferToFile()
    {
        String tStr="";

            int len = mLogBuffer.length();
            if(len>0)
            {
                tStr = mLogBuffer.toString();
                mLogBuffer.delete(0,len-1);
            }

        if(tStr.length()>0)
        {
            if (Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState()))
            {
                File floder = new File(Environment.getExternalStorageDirectory()+mLogFilePath);
                FileOutputStream fileOutputStream = null;
                OutputStreamWriter outputStreamWriter = null;

                if (!floder.exists())   {  floder.mkdirs(); }
                File file = new File(floder, mLogFileName);
                if (!file.exists())
                {
                    try
                    {
                     file.createNewFile();
                    } catch (IOException e){
                        Log.e("MobileUtil", "创建文件失败");
                    }
                }

                try
                {
                    fileOutputStream = new FileOutputStream(file, true);
                    outputStreamWriter = new OutputStreamWriter(fileOutputStream,"UTF-8");
                    outputStreamWriter.write(tStr);
                    outputStreamWriter.flush();
                    outputStreamWriter.close();
                    outputStreamWriter=null;
                    fileOutputStream.close();
                    fileOutputStream=null;
                } catch (Exception e)
                {
                    Log.e("MobileUtil", "文件写入出错");
                } finally
                {
                    try
                    {
                        if (outputStreamWriter != null)
                        {
                            outputStreamWriter.close();
                        }
                        if (fileOutputStream != null)
                        {
                            fileOutputStream.close();
                        }
                    } catch (IOException e)
                    {
                        Log.e("MobileUtil", "关闭输出流出错");
                    }
                }
            }
        }
    }



    private    class  WriteTask extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] objects) {
            writeBufferToFile();
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }

}
