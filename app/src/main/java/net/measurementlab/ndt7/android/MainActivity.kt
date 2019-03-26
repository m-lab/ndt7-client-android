package net.measurementlab.ndt7.android

import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settings = Settings()
        settings.hostname = "35.235.104.27"
        settings.port = 443
        settings.skipTlsCertificateVerification = true
        val client = MyClient(settings)

        if (!client.runDownload()) {
            Toast.makeText(this, "runDownload failed", Toast.LENGTH_SHORT).show()
        }
    }

    private inner class MyClient constructor(settings: Settings) : Client(settings) {

        override fun onLogInfo(message: String?) {
            Log.i(TAG, "onLogInfo: $message")
        }

        override fun onError(error: String?) {
            Log.e(TAG, "onError: $error")

            Handler(Looper.getMainLooper()).post { Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show() }
        }

        override fun onServerDownloadMeasurement(measurement: Measurement) {
            Log.d(TAG, "server measurement: $measurement")
        }

        override fun onClientDownloadMeasurement(measurement: Measurement) {
            Log.d(TAG, "client measurement: $measurement")
        }
    }
}
