package com.example.khandelwalsyoncom.networkingsample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

import sg.okhttplibrary.CacheCallback;
import sg.okhttplibrary.Method;
import sg.okhttplibrary.MultipartRequest;
import sg.okhttplibrary.ResponseListener;
import sg.okhttplibrary.UploadListener;
import sg.okhttplibrary.WebServiceExecutor;

public class MainActivity extends AppCompatActivity {

    final int GET_REQ = 101;
    final int POST_REQ = 102;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
    }

    public void getUser(View view) {

        HashMap<String, String> headers = new HashMap<>();
        headers.put("device_id", "<NO-DEVICE-ID>");

        HashMap<String, String> param = new HashMap<>();
        param.put("userId", "1");
        sendDataOnServer(headers, param, Method.GET, AppUrl.URL, true, GET_REQ);

    }

    private void sendDataOnServer(HashMap<String, String> headers, final HashMap<String, String> param, int method, final String url,
                                  boolean showProgressDialog, int requestCode) {


        WebServiceExecutor executor = new WebServiceExecutor(activity);
        executor.setRequestParam(param);
        executor.setHeader(headers);
        executor.isProgressDialogShow(showProgressDialog);
        executor.setUrl(url);
        executor.setRequestMethod(method);
        executor.setRequestCode(requestCode);
        executor.loadCacheData(new CacheCallback() {
            @Override
            public void isCacheAvailable(int requestCode, String response) {
                Log.e("isCacheAvailable", response);
                switch (requestCode) {
                    case GET_REQ:
                        manageGetResponse(response);
                        break;
                    case POST_REQ:
                        managePostResponse(response);
                        break;
                }
            }
        });
        executor.setResponseListener(new ResponseListener() {
            @Override
            public void onResponse(int requestCode, int responseCode, String response) {

                switch (requestCode) {
                    case GET_REQ:
                        manageGetResponse(response);
                        break;
                    case POST_REQ:
                        managePostResponse(response);
                        break;
                }
            }

            @Override
            public void onFailed(int requestCode) {
            }
        });
        executor.execute();
    }

    private void managePostResponse(String response) {
        Toast.makeText(activity, response, Toast.LENGTH_SHORT).show();
    }

    private void manageGetResponse(String response) {
        Toast.makeText(activity, response, Toast.LENGTH_SHORT).show();
    }

    public void postUser(View view) {

        HashMap<String, String> headers = new HashMap<>();
        headers.put("device_id", "<NO-DEVICE-ID>");

        HashMap<String, String> param = new HashMap<>();
        param.put("userId", "1");
        sendDataOnServer(headers, param, Method.POST, AppUrl.URL, true, POST_REQ);
    }

    public void uploadFile() {

        HashMap<String, String> headers = new HashMap<>();
        headers.put("device_id", "<NO-DEVICE-ID>");

        final HashMap<String, String> param = new HashMap<>();
        param.put("userId", "1");

        MultipartRequest request = new MultipartRequest(activity);
        request.setHeader(headers);
        request.setRequestParam(param);

        File filePath = new File(Environment.getExternalStorageDirectory().getPath(), "/test.jpg");
        request.addImage("key", filePath.getAbsolutePath(), filePath.getName());
        request.execute(AppUrl.URL);
        request.setListener(new UploadListener() {
            @Override
            public void onSuccess(int requestCode, int responseCode, String response) {

            }

            @Override
            public void onFailed() {

            }
        });
    }
}
