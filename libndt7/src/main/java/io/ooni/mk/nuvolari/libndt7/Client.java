package io.ooni.mk.nuvolari.libndt7;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocketListener;
import okhttp3.WebSocket;
import okhttp3.Response;
import okio.ByteString;

import org.json.JSONException;
import org.json.JSONObject;

// TODO(bassosimone): do we need locking for this class?

public class Client extends WebSocketListener {
    public Client(Settings settings) {
        this.settings = settings;
    }

    public void onLogInfo(String message) {
        // NOTHING
    }

    public void onError(String error) {
        // NOTHING
    }

    public void onServerDownloadMeasurement(Measurement measurement) {
        // NOTHING
    }

    public void onClientDownloadMeasurement(Measurement measurement){
        // NOTHING
    }

    @Override
    public final void onOpen(WebSocket ws, Response resp) {
        onLogInfo("WebSocket connection established");
    }

    @Override
    public final void onMessage(WebSocket ws, String text) {
        count += text.length();
        periodic();

        Measurement measurement = new Measurement();
        JSONObject doc;
        try {
            doc = new JSONObject(text);
            measurement.elapsed = doc.getDouble("elapsed");
            measurement.numBytes = doc.getDouble("num_bytes");
        } catch (JSONException exc) {
            // TODO(bassosimone): how to handle this failure? This is a case
            // where the JSON has unexpected fields.
            return;
        }
        try {
            JSONObject bbrInfo = doc.getJSONObject("bbr_info");
            measurement.bbrInfo = new BBRInfo();
            try {
                measurement.bbrInfo.bandwidth = bbrInfo.getDouble("bandwidth");
                measurement.bbrInfo.RTT = bbrInfo.getDouble("rtt");
            } catch (JSONException exc) {
                // TODO(bassosimone): how to handle this failure? Like above
                // this is a case where the JSON has unexpected fields.
                return;
            }
        } catch (JSONException exc) {
            // NOTHING: bbrInfo is optional
        }
        onServerDownloadMeasurement(measurement);
    }

    @Override
    public final void onMessage(WebSocket ws, ByteString bytes) {
        count += bytes.size();
        periodic();
    }

    @Override
    public final void onClosing(WebSocket ws, int code, String reason) {
        // TODO(bassosimone): make sure code has the correct value otherwise
        // we must return an error to the caller.
        ws.close(1000, null);
    }

    @Override
    public final void onFailure(WebSocket ws, Throwable t, Response r) {
        onError(t.getMessage());
        rv = false;
    }

    public boolean runDownload() {
        URI uri;
        try {
            uri = new URI(
                (settings.disableTLS) ? "ws" : "wss",
                null, // userInfo
                settings.hostname,
                (settings.port >= 0 && settings.port < 65536) ? settings.port : -1,
                "/ndt/v7/download",
                makeQuery(),
                null
            );
        } catch (URISyntaxException e) {
            onError(e.getMessage());
            return false;
        }

        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build();

        Request request = new Request.Builder()
            .url(uri.toString())
            .addHeader("Sec-WebSocket-Protocol", "net.measurementlab.ndt.v7")
            .build();
        client.newWebSocket(request, this);

        t0 = tLast = System.nanoTime();

        // Basically make the code synchronous here:
        ExecutorService svc = client.dispatcher().executorService();
        svc.shutdown();
        try {
            svc.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException exc) {
            // TODO(bassosimone): how to handle this error condition?
        }

        return rv;
    }

    private void periodic() {
        long now = System.nanoTime();
        if (now - tLast > measurementInterval) {
            Measurement measurement = new Measurement();
            measurement.elapsed = now - t0;
            measurement.numBytes = count;
            tLast = now;
            onClientDownloadMeasurement(measurement);
        }
    }

    private String makeQuery() {
        String s = "";
        if (settings.download.adaptive) {
            s += "adaptive=true";
        }
        if (settings.download.duration > 0) {
            if (!s.isEmpty()) {
                s += "&";
            }
            s += "duration=";
            s += Integer.toString(settings.download.duration);
        }
        return s;
    }

    private Settings settings;
    private double count = 0.0;
    private boolean rv = true;
    private long t0 = 0;
    private long tLast = 0;
    private final long measurementInterval = TimeUnit.NANOSECONDS.convert(
        250, TimeUnit.MILLISECONDS);
}
