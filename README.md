# Android Networking Lib

Network library which helps you to make http reqeust in android. It uses OkHttp as base library.

# Feature
```
Easy to use
Response Caching option
Http, Https Supported
Support Multipart
```

# Sample Get Request
```
  HashMap<String, String> headers = new HashMap<>();
  headers.put("device_id", "<NO-DEVICE-ID>");
  HashMap<String, String> param = new HashMap<>();
  param.put("userId", "1");
  sendDataOnServer(headers, param, Method.GET, AppUrl.URL, true, GET_REQ);
```
# Sample Post Request
```
  HashMap<String, String> headers = new HashMap<>();
  headers.put("device_id", "<NO-DEVICE-ID>");
  HashMap<String, String> param = new HashMap<>();
  param.put("userId", "1");
  sendDataOnServer(headers, param, Method.POST, AppUrl.URL, true, POST_REQ);
```
# Hit Request
```
  private void sendDataOnServer(HashMap<String, String> headers, final HashMap<String, String> param, int method, final String url,
                                  boolean showProgressDialog, int requestCode) {


        WebServiceExecutor executor = new WebServiceExecutor(activity);
        executor.setRequestParam(param);
        executor.setHeader(headers);
        executor.isProgressDialogShow(showProgressDialog);
        executor.setUrl(url);
        executor.setRequestMethod(method);
        executor.setRequestCode(requestCode);
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
```


# Advance Getting Cached Response
```
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
```

## License

This library is licensed under the [Apache Software License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).

```
    Copyright (C) 2015 [Hanks](https://github.com/hanks-zyh)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
```
If you have any new idea about this project, feel free to [contact me](mailto:developer.ashish01@gmail.com).


