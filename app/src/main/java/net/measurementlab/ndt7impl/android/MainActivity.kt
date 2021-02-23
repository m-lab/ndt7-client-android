package net.measurementlab.ndt7impl.android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import net.measurementlab.ndt7.android.NDTTest
import net.measurementlab.ndt7.android.models.ClientResponse
import net.measurementlab.ndt7.android.utils.DataConverter
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val client = NDTTestImpl(createHttpClient())

        button.setOnClickListener {
            client.startTest(NDTTest.TestType.DOWNLOAD)
        }

        button2.setOnClickListener {
            client.startTest(NDTTest.TestType.UPLOAD)
        }

        button3.setOnClickListener {
            client.startTest(NDTTest.TestType.DOWNLOAD_AND_UPLOAD)
        }

        stopTestButton.setOnClickListener {
            client.stopTest()
        }
    }

    private fun createHttpClient(connectTimeout: Long = 10, readTimeout: Long = 10, writeTimeout: Long = 10): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.NONE
        return OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

    private inner class NDTTestImpl constructor(okHttpClient: OkHttpClient) : NDTTest(okHttpClient) {

        override fun onDownloadProgress(clientResponse: ClientResponse) {
            super.onDownloadProgress(clientResponse)
            Log.d(TAG, "download progress: $clientResponse")

            val speed = DataConverter.convertToMbps(clientResponse)

            runOnUiThread {
                textView.text = speed
            }

            Log.d(TAG, "download speed: $speed")
        }

        override fun onUploadProgress(clientResponse: ClientResponse) {
            super.onUploadProgress(clientResponse)
            Log.d(TAG, "upload stuff: $clientResponse")

            val speed = DataConverter.convertToMbps(clientResponse)
            runOnUiThread {
                textView2.text = speed
            }
            Log.d(TAG, "upload speed: $speed")
        }

        override fun onFinished(clientResponse: ClientResponse?, error: Throwable?, testType: TestType) {
            super.onFinished(clientResponse, error, testType)
            val speed = clientResponse?.let { DataConverter.convertToMbps(it) }
            println(error)
            error?.printStackTrace()
            Log.d(TAG, "ALL DONE: $speed")
        }
    }
}
