package net.measurementlab.ndt7.android

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import java.net.URL
import com.google.gson.Gson
import net.measurementlab.ndt7.android.model.Server
import java.util.Locale
import kotlin.math.pow


private const val TAG = "MainActivity"

private val LOCATESERVER = URL("https://locate.measurementlab.net/ndt7")


class MainActivity : AppCompatActivity() {
    @BindView(R.id.server)
    lateinit var tv_server: TextView

    @BindView(R.id.downloadspeed)
    lateinit var tv_downloadspeed: TextView

    @BindView(R.id.maxbw)
    lateinit var tv_maxbw: TextView

    @BindView(R.id.minrtt)
    lateinit var tv_minrtt: TextView

    @BindView(R.id.smoothedrtt)
    lateinit var tv_smoothedrtt: TextView

    @BindView(R.id.rttvariance)
    lateinit var tv_rttvariance: TextView

    @BindView(R.id.uploadspeed)
    lateinit var tv_uploadspeed: TextView

    val settings = Settings()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ButterKnife.bind(this)

        val ndt7ServerSearch = Ndt7ServerSearch(LOCATESERVER)
        val rawJsonServer = Gson().fromJson(ndt7ServerSearch.server, Server::class.java)


        val fqdn    = rawJsonServer.fqdn.replace("\"","")
        val country = rawJsonServer.country.replace("\"","")
        val ipv4    = rawJsonServer.ip[0].replace("\"","")
        val site    = rawJsonServer.site.replace("\"","")
        val city    = rawJsonServer.city.replace("\"","")

        Log.d(TAG, "SERVER FOUND:$fqdn")

        tv_server.text = String.format(Locale.ENGLISH, "IPv4: %s\nCity: %s\nCountry: %s\n" +
                "Site: %s",ipv4,city,country,site)

        settings.hostname = fqdn
        settings.port = 443
        settings.skipTlsCertificateVerification = true



        val clientKt = MyClientKt(settings)

        if (!clientKt.runDownload())
            Toast.makeText(this, "runDownload failed", Toast.LENGTH_SHORT).show()

    }

    inner class MyClientJ constructor(settings: Settings) : ClientUpload(settings) {

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
            Log.d(TAG,""+measurement.elapsed)

            if(measurement.elapsed > 0) {
                val rate: Double = (measurement.appInfo!!.numBytes).toDouble() / 2.0.pow(20.0) / (measurement.elapsed/ 1_000_000_000.0)

                Thread(Runnable {
                    this@MainActivity.runOnUiThread(java.lang.Runnable {
                        val mb = measurement.appInfo!!.numBytes.toDouble() / 125000.0 / 10.0
                        val upload = (10f - measurement.elapsed) * mb / (10f - measurement.elapsed)
                        val uploadSpeedText = String.format(Locale.getDefault(), "%.2f MBit/s", upload)
                        tv_uploadspeed.text = uploadSpeedText

                    })
                }).start()
            }
        }
    }
    private inner class MyClientKt constructor(settings: Settings) : Client(settings) {

        override fun onLogInfo(message: String?) {
            Log.i(TAG, "onLogInfo: $message")
        }

        override fun onError(error: String?) {
            Log.e(TAG, "onError: $error")

            Handler(Looper.getMainLooper()).post { Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show() }
        }

        override fun onDownloadClose() {
                val client = MyClientJ(settings)
                if(!client.runUpload())
                    Log.e(TAG, "runUpload failed")
        }
        override fun onServerDownloadMeasurement(measurement: Measurement) {
            Log.d(TAG, "server measurement: $measurement")
            Thread(Runnable {
                this@MainActivity.runOnUiThread(java.lang.Runnable {
                    tv_rttvariance.text = String.format(Locale.ENGLISH, "%.3f ms",
                            measurement.tcpInfo!!.rttVar)

                    tv_maxbw.text = String.format(Locale.ENGLISH, "%.3f Mbit/s",
                            (measurement.bbrInfo!!.bandwidth/1000000).toDouble())
                    tv_minrtt.text = String.format(Locale.ENGLISH, "%.3f ms",
                            measurement.bbrInfo!!.minRtt)
                    tv_smoothedrtt.text = String.format(Locale.ENGLISH, "%.3f ms",
                            measurement.tcpInfo!!.smoothedRtt)
                })
            }).start()
        }

        override fun onClientDownloadMeasurement(measurement: Measurement) {
            Log.d(TAG, "client measurement: $measurement")
            Thread(Runnable {
                this@MainActivity.runOnUiThread(java.lang.Runnable {

                    Log.d(TAG, ""+measurement.elapsed)
                    if (measurement.elapsed > 0) {
                        val mb = measurement.appInfo!!.numBytes.toDouble() / 125000.0 / 10.0
                        val download = (10f - measurement.elapsed) * mb / (10f - measurement.elapsed)
                        val downloadSpeedText = String.format(Locale.getDefault(), "%.2f MBit/s", download)
                        tv_downloadspeed.text = downloadSpeedText
                    }

                })
            }).start()

        }
    }
}
