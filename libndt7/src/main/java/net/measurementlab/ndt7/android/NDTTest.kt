@file:JvmName("NDTTest")
package net.measurementlab.ndt7.android

import com.google.gson.Gson
import net.measurementlab.ndt7.android.models.CallbackRegistry
import net.measurementlab.ndt7.android.models.HostnameResponse
import net.measurementlab.ndt7.android.models.Urls
import net.measurementlab.ndt7.android.utils.HttpClientFactory
import net.measurementlab.ndt7.android.utils.NDT7Constants.TEST_MAX_WAIT_TIME
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

// You MUST not instantiate more than one NDTTest instance
abstract class NDTTest(private var httpClient: OkHttpClient? = null) : DataPublisher {

    private var downloader: Downloader? = null
    private var executorService: ExecutorService? = null
    private var runLock: Semaphore = Semaphore(1)

    init {
        if (httpClient == null) {
            httpClient = HttpClientFactory.createHttpClient()
        }
    }

    fun startTest(testType: TestType) {

        if (!runLock.tryAcquire()) {
            return
        }
        val speedtestLock = Semaphore(1)
        executorService = Executors.newSingleThreadScheduledExecutor()

        getHostname()?.enqueue(
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFinished(null, e, testType)
                    executorService?.shutdown()
                    runLock.release()
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val hostInfo: HostnameResponse = Gson().fromJson(response.body?.string(), HostnameResponse::class.java)
                        val numUrls = hostInfo.results?.size!!
                        for (i in 0 until numUrls) {
                            try {
                                selectTestType(testType, hostInfo.results[i].urls, speedtestLock)
                                return
                            } catch (e: Exception) {
                                if (i == numUrls - 1) throw e
                            }
                        }
                    } catch (e: Exception) {

                        onFinished(null, e, testType)
                        executorService?.shutdown()
                        runLock.release()
                    }
                }
            }
        )
    }

    fun stopTest() {
        downloader?.cancel()
        executorService?.shutdown()
        runLock.release()
    }

    private fun selectTestType(testType: TestType, urls: Urls, speedtestLock: Semaphore) {
        when (testType) {
            TestType.DOWNLOAD -> {
                executorService?.submit { startDownload(urls.ndt7DownloadWSS, executorService!!, speedtestLock) }
            }
            TestType.UPLOAD -> {
                speedtestLock.release()
                executorService?.submit { startUpload(urls.ndt7UploadWSS, executorService!!, speedtestLock) }
            }
            TestType.DOWNLOAD_AND_UPLOAD -> {
                executorService?.submit { startDownload(urls.ndt7DownloadWSS, executorService!!, speedtestLock) }
                executorService?.submit { startUpload(urls.ndt7UploadWSS, executorService!!, speedtestLock) }
            }
        }
        executorService?.awaitTermination(TEST_MAX_WAIT_TIME * 2, TimeUnit.SECONDS)
        runLock.release()
    }

    private fun startUpload(url: String, executorService: ExecutorService, speedtestLock: Semaphore) {
        speedtestLock.tryAcquire(TEST_MAX_WAIT_TIME, TimeUnit.SECONDS)
        val cbRegistry = CallbackRegistry(this::onUploadProgress, this::onMeasurementUploadProgress, this::onFinished)

        Uploader(cbRegistry, executorService, speedtestLock).beginUpload(url, httpClient)
    }

    private fun startDownload(url: String, executorService: ExecutorService, speedtestLock: Semaphore) {
        speedtestLock.tryAcquire(TEST_MAX_WAIT_TIME, TimeUnit.SECONDS)
        val cbRegistry = CallbackRegistry(this::onDownloadProgress, this::onMeasurementDownloadProgress, this::onFinished)

        downloader = Downloader(cbRegistry, executorService, speedtestLock).apply {
            beginDownload(url, httpClient)
        }
    }

    private fun getHostname(): Call? {
        val locateServerUrl = "https://locate.measurementlab.net/v2/nearest/ndt/ndt7?client_name=ndt7-android&client_version=${BuildConfig.NDT7_ANDROID_VERSION_NAME}"
        val request = Request.Builder()
            .method("GET", null)
            .url(locateServerUrl)
            .build()

        return httpClient?.newCall(request)
    }

    enum class TestType(val value: String) {
        UPLOAD("upload"),
        DOWNLOAD("download"),
        DOWNLOAD_AND_UPLOAD("DownloadAndUpload"),
    }
}
