package sg.okhttplibrary;
/**
 * Author       :   Ashish Khandelwal
 * Designation  :   Android Developer
 * E-mail       :   developer.ashish01@gmail.com
 * Date         :   16/02/2017
 * Purpose      :   Purpose of this class is Calling Rest Apis Exchange Data with Server. this is
 * :   Universal Web Service Class For our Work.
 */


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class WebServiceExecutor {

    private final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    private final String PROGRESS_MSG = "Please Wait...";
    CacheCallback cacheCallback;
    private String url;
    private boolean isProgressDialogShow = true;
    private int requestCode;
    private OkHttpClient client;
    private ResponseListener listener;
    private CustomProgress progressDialog;
    private Call call;
    private int requestMethod = Method.POST;
    private Context context;
    private HashMap<String, String> queryParam;
    private HashMap<String, String> header;
    private String baseUrl = "";


    public WebServiceExecutor(Context context) {
        this.context = context;
        progressDialog = new CustomProgress(context);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .sslSocketFactory(createSSLSocketFactory())
                .hostnameVerifier(new TrustAllHostnameVerifier())
                .build();

        queryParam = new HashMap<>();
        header = new HashMap<>();
    }

    private static SSLSocketFactory createSSLSocketFactory() {

        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return sSLSocketFactory;
    }

    public void setHeader(HashMap<String, String> header) {
        this.header = header;
    }

    public Headers getHeaders() {
        Headers.Builder builder = new Headers.Builder();
        if (header != null) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    public void setUrl(String url) {
        this.baseUrl = url;
        this.url = url;
    }

    public void isProgressDialogShow(boolean isProgressDialogShow) {
        this.isProgressDialogShow = isProgressDialogShow;
    }

    public void setRequestMethod(int requestMethod) {
        this.requestMethod = requestMethod;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public void loadCacheData(CacheCallback cacheCallback) {
        this.cacheCallback = cacheCallback;
    }

    public void setResponseListener(ResponseListener listener) {
        this.listener = listener;
    }

    public void cancelRequest() {
        if (call != null)
            call.cancel();
    }

    public void setRequestParam(HashMap<String, String> queryParam) {
        this.queryParam = queryParam;
    }

    public void execute() {

        if (url == null || url.isEmpty()) {
            showMessage("Invalid URL");
            return;
        }

        if (cacheCallback != null) {

            if (queryParam == null)
                queryParam = new HashMap<>();
            if (header == null)
                header = new HashMap<>();

            RealmController controller = RealmController.with((Activity) context);
            //Realm realm = controller.getRealm();
            //realm.beginTransaction();

            final CacheResponse response = controller.getCachedResponse(calculateHash());
            if (response != null) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        cacheCallback.isCacheAvailable(requestCode, response.getResponse());
                    }
                });
            }
        }

        showProgressDialog(true);

        Request request = null;

        if (requestMethod == Method.POST) {

            FormBody body = setPostParam(queryParam);
            request = new Request.Builder()
                    .url(url)
                    .headers(getHeaders())
                    .post(body)
                    .build();
            Log.e("WebRequest", url + "{ " + queryParam + "} ");
        } else if (requestMethod == Method.GET) {

            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            for (Map.Entry<String, String> entry : queryParam.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }

            /*String query = hashMapToQuery(queryParam);
            url = url + "?" + query;*/
            url = urlBuilder.build().toString();

            Log.e("WebRequest", url);
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .headers(getHeaders())
                    .build();
        } else if (requestMethod == Method.PUT) {

            FormBody body = setPostParam(queryParam);
            request = new Request.Builder()
                    .url(url)
                    .headers(getHeaders())
                    .put(body)
                    .build();
        } else {
            showMessage("Invalid Method Type");
            return;
        }

        try {
            call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    String msg = e.getMessage();
                    if (msg == null || msg.isEmpty())
                        msg = "null";
                    Log.e("onFailure", msg);

                    showProgressDialog(false);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFailed(requestCode);
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    showProgressDialog(false);
                    final String responseStr = response.body().string();
                    final int responseCode = response.code();
                    Log.e("RESPONSE:" + responseCode, responseStr);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            Realm realm = RealmController.getInstance().getRealm();
                            realm.beginTransaction();
                            RealmController controller = RealmController.with((Activity) context);
                            CacheResponse cacheResponse = controller.getCachedResponse(calculateHash());

                            if (cacheResponse != null) {
                                cacheResponse.setResponse(responseStr);
                                cacheResponse.setTime(System.currentTimeMillis());
                            } else {
                                cacheResponse = new CacheResponse();
                                cacheResponse.setHash(calculateHash());
                                cacheResponse.setTime(System.currentTimeMillis());
                                cacheResponse.setResponse(responseStr);
                            }

                            /*CacheResponse cacheResponse = new CacheResponse();
                            cacheResponse.setHash(calculateHash());
                            cacheResponse.setTime(System.currentTimeMillis());
                            cacheResponse.setResponse(responseStr);*/
                            realm.copyToRealmOrUpdate(cacheResponse);
                            realm.commitTransaction();
                            controller.size();

                            listener.onResponse(requestCode, responseCode, responseStr);
                        }
                    });
                }
            });

        } catch (Exception e) {
            showProgressDialog(false);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    listener.onFailed(requestCode);
                }
            });
            e.printStackTrace();
        }
    }

    private void showProgressDialog(boolean status) {
        if (isProgressDialogShow && status) {
            progressDialog.show();
        } else if (isProgressDialogShow && !status)
            progressDialog.dismiss();
    }

    private void showMessage(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private FormBody setPostParam(HashMap<String, String> queryParam) {
        FormBody.Builder builder = new FormBody.Builder();
        if (queryParam != null) {
            for (Map.Entry<String, String> entry : queryParam.entrySet()) {
                builder.add(entry.getKey(), entry.getValue());
            }

        }
        return builder.build();
    }

    public String calculateHash() {
        return crc32(baseUrl + queryParam.toString() + header.toString());
    }

    public String crc32(String str) {
        Log.e("crc32", str);
        CRC32 crc = new CRC32();
        crc.update(str.trim().getBytes());
        return String.format("%08X", crc.getValue());
    }

    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)

                throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}




