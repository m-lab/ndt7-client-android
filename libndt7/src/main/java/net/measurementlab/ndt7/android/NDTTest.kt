@file:JvmName("NDTTest")
package net.measurementlab.ndt7.android

import com.google.gson.Gson
import net.measurementlab.ndt7.android.models.*
import net.measurementlab.ndt7.android.utils.NDT7Constants.TEST_MAX_WAIT_TIME
import net.measurementlab.ndt7.android.utils.HttpClientFactory
import okhttp3.*
import java.io.IOException
import java.net.URI
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

// You MUST not instantiate more than one NDTTest instance
abstract class NDTTest(private val settings: Settings, private var httpClient: OkHttpClient? = null): DataPublisher {

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

        //if specific hostname is provided, lets use it
        if (!settings.hostname.isBlank()) {
            selectTestType(testType, settings.hostname, speedtestLock)
        }
        else {
            getHostname()?.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFinished(null, e, testType)
                    executorService?.shutdown()
                    runLock.release()
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val hostInfo: HostnameResponse = Gson().fromJson(response.body()?.string(), HostnameResponse::class.java)
                        selectTestType(testType, hostInfo.fqdn, speedtestLock)
                    } catch (e: Exception) {
                        onFinished(null, e, testType)
                        executorService?.shutdown()
                        runLock.release()
                    }
                }
            })
        }
    }


    private fun selectTestType(testType: TestType, fqdn: String, speedtestLock: Semaphore) {
        when (testType) {
            TestType.DOWNLOAD -> {
                executorService?.submit { startDownload(fqdn, executorService!!, speedtestLock) }
            }
            TestType.UPLOAD -> {
                speedtestLock.release()
                executorService?.submit { startUpload(fqdn, executorService!!, speedtestLock) }
            }
            TestType.UPLOAD_AND_DOWNLOAD -> {
                executorService?.submit { startDownload(fqdn, executorService!!, speedtestLock) }
                executorService?.submit { startUpload(fqdn, executorService!!, speedtestLock) }
            }
        }
        executorService?.awaitTermination(TEST_MAX_WAIT_TIME * 2, TimeUnit.SECONDS)
        runLock.release()
    }

    private fun startUpload(fqdn: String, executorService: ExecutorService, speedtestLock: Semaphore) {
        speedtestLock.tryAcquire(TEST_MAX_WAIT_TIME, TimeUnit.SECONDS)
        val uri: URI = createUri(fqdn, TestType.UPLOAD)
        val cbRegistry = CallbackRegistry(this::onUploadProgress, this::onMeasurementUploadProgress, this::onFinished)

        Uploader(cbRegistry, executorService, speedtestLock).beginUpload(uri, httpClient)
    }

    private fun startDownload(fqdn: String, executorService: ExecutorService, speedtestLock: Semaphore) {
        speedtestLock.tryAcquire(TEST_MAX_WAIT_TIME, TimeUnit.SECONDS)
        val uri: URI = createUri(fqdn, TestType.DOWNLOAD)
        val cbRegistry = CallbackRegistry(this::onDownloadProgress, this::onMeasurementDownloadProgress, this::onFinished)

        Downloader(cbRegistry, executorService, speedtestLock).beginDownload(uri, httpClient)
    }

    //creates a uri for a websocket connection
    private fun createUri(fqdn: String, testType: TestType): URI {
        // TODO figure out when we should use ws vs wss (how do we tell if ssl compatible?)
        return URI(
                "wss", null,
                fqdn,
                if (settings.port in 0..65535) settings.port else -1,
                "/ndt/v7/" + testType.value,
                null, null
        )

    }

    private fun getHostname(): Call? {
        val locateServerUrl = "https://locate.measurementlab.net/ndt7"
        val request = Request.Builder()
                .method("GET", null)
                .url(locateServerUrl)
                .build()

        return httpClient?.newCall(request)
    }

    enum class TestType(val value: String) {
        UPLOAD("upload"),
        DOWNLOAD("download"),
        UPLOAD_AND_DOWNLOAD("uploadAndDownload"),
    }
}
