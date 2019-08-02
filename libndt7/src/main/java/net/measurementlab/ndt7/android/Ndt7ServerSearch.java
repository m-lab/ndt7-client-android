package net.measurementlab.ndt7.android;

import android.util.Log;


import net.measurementlab.ndt7.android.exceptions.NoCapacityException;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Ndt7ServerSearch {

    private String server;
    private URL locateUrl;
    private final String TAG = "NDT7SERVERSEARCH";
    private final OkHttpClient client = new OkHttpClient();
    private final int NOCAPACITY = 204;
    public Ndt7ServerSearch(URL locateUrl) {
        this.locateUrl = locateUrl;
    }

    private String setServer() {

        Request request = new Request.Builder()
                .url(locateUrl)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override public void onResponse(Call call, Response response) throws IOException {

                if (response.networkResponse() != null) {
                    Log.d(TAG,"Response Code"+ response.networkResponse().code());
                    //Handling 204 as specs
                    if(response.networkResponse().code() != NOCAPACITY) {
                        try (ResponseBody responseBody = response.body()) {
                            if (!response.isSuccessful())
                                throw new IOException("Unexpected code " + response);
                            server = responseBody != null ? responseBody.string() : null;
                            Log.d(TAG, server);
                        }
                    }else {
                        try {
                            throw new NoCapacityException("NO Capacity");
                        } catch (NoCapacityException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        ExecutorService svc = client.dispatcher().executorService();
        svc.shutdown();
        try {
            svc.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // TODO(bassosimone): how to handle this error condition?
            Log.e(TAG, " awaitTermination encountered exception", e);
        }
        return server;
    }
    public String getServer(){
        return setServer();
    }
}
