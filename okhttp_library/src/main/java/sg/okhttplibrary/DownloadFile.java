package sg.okhttplibrary;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;


public class DownloadFile {

    File file;
    Handler handler;
    private String newFileName;
    private DownloadListener listener;

    public void setNewFileName(String nameFileName) {
        this.newFileName = newFileName;
    }

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }

    public void execute(final String downloadUrl, final File location) {

        Log.e(getClass().getName(), downloadUrl);
        handler = new Handler(Looper.getMainLooper());
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(downloadUrl).build();
        client.newCall(request).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure", "onFailure");

                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        listener.onFailed();
                    }
                });
            }

            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    if (!location.exists())
                        location.mkdirs();

                    if (newFileName == null || newFileName.isEmpty())
                        newFileName = getFileName(downloadUrl);
                    file = new File(location, newFileName);

                    ResponseBody body = response.body();
                    long contentLength = body.contentLength();
                    Log.e("ContentLength", contentLength + "");
                    BufferedSource source = body.source();

                    BufferedSink sink = Okio.buffer(Okio.sink(file));
                    Buffer sinkBuffer = sink.buffer();

                    long totalBytesRead = 0;
                    int bufferSize = 8 * 1024;
                    for (long bytesRead; (bytesRead = source.read(sinkBuffer, bufferSize)) != -1; ) {
                        sink.emit();
                        totalBytesRead += bytesRead;
                        int progress = (int) ((totalBytesRead * 100) / contentLength);
                        listener.onProgressUpdate(progress);
                    }
                    sink.flush();
                    sink.close();
                    source.close();


                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            listener.onSuccess(file.getAbsolutePath());
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            listener.onFailed();
                        }
                    });
                }
            }
        });
    }

    public void download(String url, File destFile) throws IOException {
        Request request = new Request.Builder().url(url).build();
        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();
        ResponseBody body = response.body();
        long contentLength = body.contentLength();
        BufferedSource source = body.source();

        BufferedSink sink = Okio.buffer(Okio.sink(destFile));
        Buffer sinkBuffer = sink.buffer();

        long totalBytesRead = 0;
        int bufferSize = 8 * 1024;
        for (long bytesRead; (bytesRead = source.read(sinkBuffer, bufferSize)) != -1; ) {
            sink.emit();
            totalBytesRead += bytesRead;
            int progress = (int) ((totalBytesRead * 100) / contentLength);
            //publishProgress(progress);
        }
        sink.flush();
        sink.close();
        source.close();
    }

    private String getFileName(String url) {
        File file = new File(url);
        return file.getName();
    }

}
