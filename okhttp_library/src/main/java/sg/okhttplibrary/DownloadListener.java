package sg.okhttplibrary;

public interface DownloadListener {
     void onFailed();

     void onSuccess(String path);

     void onProgressUpdate(int progress);
}
