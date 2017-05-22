package com.example.testvideo.DonwLoad;

/**
 * Created by WHC on 2017/4/17.
 */

public interface DownloadListener {

    void onProgress(int progress);//通知当前进度

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();
}
