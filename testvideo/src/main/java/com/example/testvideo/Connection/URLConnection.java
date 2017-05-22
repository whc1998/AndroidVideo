package com.example.testvideo.Connection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by WHC on 2017/5/22.
 */

public class URLConnection extends Thread {

    private String path;
    private Handler handler;
    private List<String> list_text=new ArrayList<>();
    private List<String> list_name=new ArrayList<>();
    private List<String> list_videourl=new ArrayList<>();
    private List<String> list_createtime=new ArrayList<>();
    private List<Bitmap> picture,video=new ArrayList<>();

    public URLConnection(Handler handler, String path, List<String> list_text,List<String> list_name
            ,List<String> list_videourl,List<String> list_createtime,List<Bitmap> picture,List<Bitmap> video){
        this.handler = handler;
        this.list_text = list_text;
        this.list_name=list_name;
        this.picture=picture;
        this.list_videourl=list_videourl;
        this.list_createtime=list_createtime;
        this.path = path;
        this.video=video;
    }

    @Override
    public void run() {
        StringBuilder resultData = new StringBuilder();
        try {
            URL url=new URL(path);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            int code = connection.getResponseCode();
            if (code==200){
                InputStreamReader isr = new InputStreamReader(connection.getInputStream());
                BufferedReader buffer = new BufferedReader(isr);
                String inputLine = null;
                while((inputLine = buffer.readLine()) != null){
                    resultData.append(inputLine);
                }
                resolver(resultData.toString());
                buffer.close();
                isr.close();
                connection.disconnect();
            }else{
                Log.d("TAG", "Http Status : " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resolver(String jsonresouse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonresouse);
            JSONObject showapi_res_body = jsonObject.optJSONObject("showapi_res_body");
            JSONObject pagebean = showapi_res_body.optJSONObject("pagebean");
            JSONArray jsonArray = pagebean.optJSONArray("contentlist");
            for (int i = 0; i <jsonArray.length(); i++) {
                JSONObject object3 = jsonArray.getJSONObject(i);
                String text = object3.getString("text");
                text = text.replaceAll("\r|\n", "");
                String profile_image=object3.getString("profile_image");
                String video_uri=object3.getString("video_uri");
                String name=object3.getString("name");
                String create_time=object3.getString("create_time");
                video.add(createVideoThumbnail(video_uri,100,100));
                list_text.add(text);
                list_videourl.add(video_uri);
                list_name.add(name);
                list_createtime.add(create_time);
                getImageBitmap(picture,profile_image);
            }
            handler.sendEmptyMessage(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getImageBitmap(List<Bitmap> picture,String url){
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imgUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            picture.add(bitmap);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap createVideoThumbnail(String url, int width, int height) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        int kind = MediaStore.Video.Thumbnails.MINI_KIND;
        try {
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
        if (kind == MediaStore.Images.Thumbnails.MICRO_KIND && bitmap != null) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        return bitmap;
    }

}
