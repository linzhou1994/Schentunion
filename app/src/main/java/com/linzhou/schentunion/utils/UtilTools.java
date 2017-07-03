package com.linzhou.schentunion.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;
import com.linzhou.schentunion.R;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
 *项目名： Schentunion
 *包名：   com.linzhou.schentunion.application
 *创建者:  linzhou
 *创建时间:17/04/20
 *描述:   工具统一类
 */
public class UtilTools {

    //设置字体
    public static void setFont(Context mContext, TextView textview) {
        Typeface fontType = Typeface.createFromAsset(mContext.getAssets(), "fonts/FONT.TTF");
        textview.setTypeface(fontType);
    }

    //保存图片到shareutils
    public static void putImageToShare(Context mContext, ImageView imageView) {
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        //第一步：将Bitmap压缩成字节数组输出流
        ByteArrayOutputStream byStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byStream);
        //第二步：利用Base64将我们的字节数组输出流转换成String
        byte[] byteArray = byStream.toByteArray();
        String imgString = new String(Base64.encodeToString(byteArray, Base64.DEFAULT));
        //第三步：将String保存shareUtils
        ShareUtils.putString(mContext, "image_title", imgString);
    }

    //读取图片
    public static void getImageToShare(Context mContext, ImageView imageView) {
        //1.拿到string
        String imgString = ShareUtils.getString(mContext, "image_title", "");
        if (!imgString.equals("")) {
            //2.利用Base64将我们string转换
            byte[] byteArray = Base64.decode(imgString, Base64.DEFAULT);
            ByteArrayInputStream byStream = new ByteArrayInputStream(byteArray);
            //3.生成bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(byStream);
            imageView.setImageBitmap(bitmap);
        }
    }

    //获取版本号
    public static String getVersion(Context mContext){
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(),0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return mContext.getString(R.string.text_unknown);
        }
    }

    //Date转String
    public static String dateToString(Date registerTime) {
        if (registerTime==null)
            return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(registerTime);
    }

    //Date转String
    public static String dateToString(Date registerTime, String time) {
        if (registerTime==null)
            return "";
        return new SimpleDateFormat(time).format(registerTime);
    }
    //根据年月生成Date
    public static Date stringToDate(String year ,String month){
        String time=year+"-"+month+"-01 00:00:00";
        Date date=null;
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date=formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    //获取今天的起始时间
    public static Date getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.setTime(new Date());
        todayStart.set(Calendar.HOUR, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }


    //上传Bitmap
    public static void uploadBitmap(String url,int sid, Bitmap bitmap) {
        String filePath = "/tmp.png";
        File f = new File(Environment.getExternalStorageDirectory(), filePath);
        if (f.exists()) f.delete();

        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {

        }
        upload(url,sid, f,null);

    }

    //上传Bitmap
    public static void uploadBitmap(String url,int sid, Bitmap bitmap,final UploadListener uploadListener) {
        String filePath = "/tmp.png";
        File f = new File(Environment.getExternalStorageDirectory(), filePath);
        if (f.exists()) f.delete();

        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {

        }
        upload(url,sid, f,uploadListener);


    }

    //上传文件
    public static void upload(final String url,final int sid, final File file,final UploadListener uploadListener) {
        new Thread() {
            @Override
            public void run() {
                try {

                    String urlstr = url + "?sid=" + sid;
                    URL url = new URL(urlstr);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    // 发送POST请求必须设置如下两行

                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "text/html");
                    conn.setRequestProperty("Cache-Control", "no-cache");
                    conn.setRequestProperty("Charsert", "UTF-8");
                    conn.connect();
                    conn.setConnectTimeout(10000);
                    OutputStream out = conn.getOutputStream();

                    if (!file.exists()) {
                        uploadListener.uploadFailure("文件不存在");
                        return;
                    }
                    DataInputStream in = new DataInputStream(new FileInputStream(file));
                    int bytes = 0;
                    byte[] buffer = new byte[1024];
                    while ((bytes = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytes);
                        L.d("发送文件...");
                    }


                    in.close();
                    out.flush();
                    out.close();

                    conn.getInputStream();
                    conn.disconnect();
                    if (uploadListener!=null){
                        uploadListener.uploadSuccess();
                        file.delete();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (uploadListener!=null)
                    uploadListener.uploadFailure(e.toString());
                }
            }
        }.start();
    }

    public interface UploadListener{
        //上传成功
        public void uploadSuccess();
        //上传失败
        public void uploadFailure(String e);
    }

    /**
     * 通知栏展示消息
     * @param context  上下文
     * @param title   标题（好友名、群组名）
     * @param content 内容
     */
    public static void showNotification(Context context ,String title, String content) {
        //LogUtils.d("通知栏提示", content);

        final int NOTIFICATION_ID = 1993;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.logo) // Needed for the notification to work/show!!
                .setContentTitle(title)
                .setContentText(content)
                .setDefaults(Notification.DEFAULT_ALL);
        Notification notification = builder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

    }
    //分享
    public static void shareImage(Context context, Uri uri, String title) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, title));
    }


}
