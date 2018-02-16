package sg.okhttplibrary;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MultipartRequest {

    private UploadListener listener;
    private int requestCode;
    private MultipartBody.Builder builder;
    private ProgressDialog dialog;
    private OkHttpClient client;
    private HashMap<String, String> header;
    private HashMap<String, String> queryParam;

    public MultipartRequest(Context caller) {
        this.builder = new MultipartBody.Builder();
        this.client = new OkHttpClient();
        dialog = new ProgressDialog(caller);
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
    }

    public void setListener(UploadListener listener) {
        this.listener = listener;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }


    public void addImage(String name, String filePath, String fileName) {
        MediaType type = MediaType.parse("image/jpeg");
        this.builder.addFormDataPart(name, fileName, RequestBody.create(type, new File(filePath)));
    }

    public void addVideo(String name, String filePath, String fileName) {
        MediaType type = MediaType.parse("video/mp4");
        this.builder.addFormDataPart(name, fileName, RequestBody.create(type, new File(filePath)));
    }

    public void setHeader(HashMap<String, String> header) {
        this.header = header;
        Log.e("REQUEST_Header", header.toString());
    }

    public void setRequestParam(HashMap<String, String> queryParam) {
        Log.e("REQUEST_PARAM", queryParam.toString());
        this.queryParam = queryParam;
    }

    public Headers getHeaders() {
        Headers.Builder builder = new Headers.Builder();
        for (Map.Entry<String, String> entry : header.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    public void execute(String url) {

        Log.e("URL", url);
        dialog.show();
        Request request = null;
        try {
            for (Map.Entry<String, String> entry : queryParam.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
            RequestBody requestBody = builder
                    .setType(MultipartBody.FORM)
                    .build();
            request = new Request
                    .Builder()
                    .headers(getHeaders())
                    .url(url)
                    .post(requestBody)
                    .build();
            Log.e("Reeq_ContentLength", request.body().contentLength() + "");

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    dialog.dismiss();
                    if (listener != null)
                        listener.onFailed();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    String strResponse = "";
                    if (response.isSuccessful()) {
                        strResponse = response.body().string();
                        Log.e("::::::: response :: ", strResponse);
                        dialog.dismiss();

                        if (listener != null)
                            listener.onSuccess(requestCode, response.code(), strResponse);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}