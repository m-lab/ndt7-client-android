package net.measurementlab.ndt.android;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @NonNull
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Settings settings = new Settings();
        settings.hostname = "35.235.104.27";
        settings.port = 443;
        settings.disableTLS = false;
        settings.skipTLSVerify = true;
        settings.download.duration = 1;
        settings.download.adaptive = true;
        Client client = new MyClient(settings);

        if (!client.runDownload()) {
            Toast.makeText(this, "runDownload failed", Toast.LENGTH_SHORT).show();
        }
    }

    private class MyClient extends Client {
        private MyClient(@NonNull Settings settings) {
            super(settings);
        }

        @Override
        public void onLogInfo(@Nullable String message) {
            System.out.println(message);
        }

        @Override
        public void onError(@Nullable String error) {
            System.out.println("Error: " + error);

            new Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show());
        }

        @Override
        public void onServerDownloadMeasurement(@NonNull Measurement measurement) {
            Log.d(TAG, "server measurement: " + measurement);
        }

        @Override
        public void onClientDownloadMeasurement(@NonNull Measurement measurement) {
            Log.d(TAG, "client measurement: " + measurement);
        }
    }
}
