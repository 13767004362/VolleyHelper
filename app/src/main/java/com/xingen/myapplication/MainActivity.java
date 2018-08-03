package com.xingen.myapplication;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.android.volley.VolleyError;
import com.xingen.myapplication.bean.Movie;
import com.xingen.myapplication.bean.MovieList;
import com.xingen.volleyhelper.VolleyHelper;
import com.xingen.volleyhelper.listener.DownloadListener;
import com.xingen.volleyhelper.listener.FileProgressListener;
import com.xingen.volleyhelper.listener.GsonResultListener;
import com.xingen.volleyhelper.request.DownloadRequest;

import org.json.JSONObject;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.main_download_btn).setOnClickListener(this);
        findViewById(R.id.main_json_btn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_download_btn:
                download();
                break;
            case R.id.main_json_btn:
                json();
                break;
        }
    }
    private void json() {
        String url = "https://api.douban.com/v2/movie/search";
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("q", "张艺谋");
        } catch (Exception e) {
            e.printStackTrace();
        }
        VolleyHelper.getInstance().sendGsonRequest(url, jsonObject, new GsonResultListener<MovieList<Movie>>() {
            @Override
            public void success(MovieList<Movie> movieMovieList) {
                Log.i("JsonRequest", "响应结果 " + movieMovieList.toString() + " " + movieMovieList.getSubjects().get(0).getTitle());
            }

            @Override
            public void error(VolleyError volleyError) {
                Log.i("JsonRequest", "异常结果" + volleyError.toString());
            }
        });
    }
    private void download() {
        String url2 = "http://yun.aiwan.hk/1441972507.apk";
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "BaiHeWang.apk";
        VolleyHelper.getInstance().sendRequest(new DownloadRequest(url2, filePath, new FileProgressListener() {
            @Override
            public void progress(int progress) {
                Log.i("DownloadRequest", " 下载进度 " + progress);
            }
        }, new DownloadListener() {
            @Override
            public void downloadFinish(String downloadUrl, String filePath) {
                Log.i("DownloadRequest", " 文件下载完成,路径是 " + filePath);
            }

            @Override
            public void downloadError(String downloadUrl, VolleyError volleyError) {
                Log.i("DownloadRequest", " 文件下载异常 " + downloadUrl + " 异常是 " + volleyError.toString());
            }
        }));
    }
}
