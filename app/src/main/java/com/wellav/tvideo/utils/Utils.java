package com.wellav.tvideo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    /**
     * 毫秒转换成时分秒
     * @return
     */
    public static String getHHMMSS(int mTime) {
        String timeStr = null;
        int time = mTime / 1000;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99) {
                    return "59:59:59";
                }
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    public static String getEpgPlayTimeFOrmat(String time) {
        if (time == null) {
            return "00:00";
        }
        String[] myTime;
        myTime = time.split("T");
        String value = myTime[1].substring(0, 5);
        return value;
    }

    public static long getEpgPlayTime2s(String stime, String etime) {
        if (stime == null || etime == null) {
            return 1l;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        long l = 0;
        try {
            Date date1 = sdf.parse(stime.substring(0, 19));
            Date date2 = sdf.parse(etime.substring(0, 19));
            l = date2.getTime() - date1.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return l;
    }

    public static long getEpgSeekbarPos(long currenttime, String stime) {
        if (currenttime == 0 || stime == null) {
            return 1l;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        long l = 0;
        try {

            Date date = sdf.parse(stime.substring(0, 19));
            l = currenttime - date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return l;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int dip2px(Context context, float px) {
        final float scale = getScreenDensity(context);
        return (int) (px * scale + 0.5);
    }

    private static Toast toast;

    public static void DisplayToast(Context context, String str) {
        if (toast == null) {
            // hesn 保持只显示最新的toast，其他的都取消
            toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        } else {
            toast.setText(str);
        }
        toast.show();
    }

    public static void DisplayToast(Context context, int strId) {

        if (toast == null) {
            toast = Toast.makeText(context, context.getString(strId), Toast.LENGTH_SHORT);
        } else {
            toast.setText(strId);
        }
        toast.show();
    }

    private static boolean logTag = true;

    public static void PrintLog(String tag, String txt) {
        if (logTag) {
            Log.e(tag, txt);
        }
    }

    public static boolean isConnByHttp(String stringurl) {
        boolean isConn = false;
        URL url;
        HttpURLConnection conn = null;
        if (stringurl.equals("")) {
            return false;
        }

        try {
            url = new URL(stringurl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(1000 * 5);
            if (conn.getResponseCode() == 200) {
                isConn = true;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }
        return isConn;
    }

    /**
     * 获取节目
     * @param path
     * @return
     */
    public static String httpGetChannels(String path) {

        StringBuffer sb = new StringBuffer();
        try {
            // 新建一个URL对象
            URL url = new URL(path);
            // 打开一个HttpURLConnection连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 设置请求方式get请求
            conn.setRequestMethod("GET");
            // 设置连接超时时间
            conn.setConnectTimeout(5000);
            // //再设置超时时间
            conn.setReadTimeout(5000);
            // 开始连接
            conn.connect();
            // 判断请求是否成功 成功码为200
            if (200 == conn.getResponseCode()) {
                InputStream inputStream = conn.getInputStream();
                // 把字节流转化成字符流 InputStreamReader
                InputStreamReader isr = new InputStreamReader(inputStream);
                // 把字符流转换成缓冲字符流
                BufferedReader br = new BufferedReader(isr);
                // 创建一个StringBuffer

                String str = "";
                while ((str = br.readLine()) != null) {
                    sb.append(str);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    // byte转化成MB
    public static String bytesToMb(int bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        if (returnValue > 1)
            return (returnValue + "M");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + "KB");
    }

    /**
     * 保存图片到指定文件夹
     * @param bmp
     * @param filename
     * @return
     */
    public static void saveCroppedImage(Bitmap bmp, String fileName, String url) {
        File dirFile = new File(url);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        try {
            File myCaptureFile = new File(url + fileName + ".png");
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
            bmp.compress(CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除一张图片
     * @param url
     */
    public static void deleteSDImage(String url) {
        File file = new File(url);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 获取本地图片
     * @param urladdr
     * @return
     */
    public static Drawable loadImageFromNetwork(String urladdr) {
        Drawable drawable = null;
        try {
            drawable = Drawable.createFromStream(new URL(urladdr).openStream(), "image.jpg");
        } catch (IOException e) {
        }
        return drawable;
    }
}
