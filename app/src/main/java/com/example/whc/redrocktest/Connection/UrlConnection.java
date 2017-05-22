package com.example.whc.redrocktest.Connection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by WHC on 2017/5/20.
 */

public class UrlConnection extends Thread {

    private Handler handler;
    private String response;
    private List<String> list_text=new ArrayList<>();
    private List<String> list_name=new ArrayList<>();
    private List<String> list_videourl=new ArrayList<>();
    private List<String> list_createtime=new ArrayList<>();
    private List<Bitmap> picture=new ArrayList<>();

    public UrlConnection(Handler handler, String response, List<String> list_text,List<String> list_name
    ,List<String> list_videourl,List<String> list_createtime,List<Bitmap> picture) {
        this.handler = handler;
        this.list_text = list_text;
        this.list_name=list_name;
        this.picture=picture;
        this.list_videourl=list_videourl;
        this.list_createtime=list_createtime;
        this.response = response;
    }

    @Override
    public void run() {
        int timeOut = 30 * 1000;
        HttpParams param = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(param, timeOut);
        HttpConnectionParams.setSoTimeout(param, timeOut);
        HttpConnectionParams.setTcpNoDelay(param, true);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", TrustAllSSLSocketFactory.getDefault(), 443));
        ClientConnectionManager manager = new ThreadSafeClientConnManager(param, registry);

        HttpClient httpClient = new DefaultHttpClient(manager, param);
        HttpGet httpGet = new HttpGet(response);
        try {
            Log.d("TAG---", "开始请求数据：" + response);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                String response = EntityUtils.toString(entity, "utf-8");
                resolver(response);
            } else {
                Log.d("TAG---", "HTTP返回状态错误:" + httpResponse.getStatusLine().getStatusCode() + ", " + EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
            }
        } catch (IOException e) {
            Log.e("TAG---", "Exception : ", e);
        }
    }

    public void resolver(String jsonresouse) {
        try {
            Log.d("TAG---", "处理Json数据：" + jsonresouse);
            JSONObject jsonObject = new JSONObject(jsonresouse);
            JSONObject showapi_res_body = jsonObject.optJSONObject("showapi_res_body");
            JSONObject pagebean = showapi_res_body.optJSONObject("pagebean");
            JSONArray jsonArray = pagebean.optJSONArray("contentlist");
            Log.d("TAG---", "length : " + jsonArray.length());
            for (int i = 0; i <jsonArray.length(); i++) {
                JSONObject object3 = jsonArray.getJSONObject(i);
                String text = object3.getString("text");
                Log.d("TAG----", "text:"+text);
                String profile_image=object3.getString("profile_image");
                String video_uri=object3.getString("video_uri");
                String name=object3.getString("name");
                String create_time=object3.getString("create_time");
                list_text.add(text);
                list_videourl.add(video_uri);
                list_name.add(name);
                list_createtime.add(create_time);
//                donwpicture(profile_image,picture);
            }
            handler.sendEmptyMessage(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void donwpicture(String donwpath, List<Bitmap> picture) {
        try {
            URL url = new URL(donwpath);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            Log.d("11", "length : ok");
            if (connection.getResponseCode() == 200) {
                Log.d("11", "length : hehe");
                InputStream is = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                picture.add(bitmap);
                is.close();
            } else {
                Log.d("11", "Http Status : " + connection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
