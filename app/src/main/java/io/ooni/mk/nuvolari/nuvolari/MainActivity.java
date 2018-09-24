package io.ooni.mk.nuvolari.nuvolari;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.ooni.mk.nuvolari.libndt7.Client;
import io.ooni.mk.nuvolari.libndt7.Measurement;
import io.ooni.mk.nuvolari.libndt7.Settings;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Settings settings = new Settings();
        settings.hostname = "master.neubot.org";
        settings.port = 4444;
        settings.disableTLS = true;
        settings.download.duration = 1;
        settings.download.adaptive = true;
        Client client = new MyClient(settings);
        client.runDownload();
    }

    private class MyClient extends Client {
        public MyClient(Settings settings) {
            super(settings);
        }

        @Override
        public void onLogInfo(String message) {
            System.out.println(message);
        }

        @Override
        public void onError(String error) {
            System.out.println("Error: " + error);
        }

        @Override
        public void onServerDownloadMeasurement(Measurement measurement) {
            System.out.println("Server download measurement:");
            System.out.println("Elapsed: " + measurement.elapsed);
            System.out.println("Num bytes: " + measurement.numBytes);
            if (measurement.bbrInfo != null){
                System.out.println("Bandwidth: " + measurement.bbrInfo.bandwidth);
                System.out.println("RTT: " + measurement.bbrInfo.RTT);
            }
        }

        @Override
        public void onClientDownloadMeasurement(Measurement measurement) {
            System.out.println("Client download measurement:");
            System.out.println("Elapsed: " + measurement.elapsed);
            System.out.println("Num bytes: " + measurement.numBytes);
        }
    }
}
