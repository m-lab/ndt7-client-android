@file:JvmName("Uploader")
package net.measurementlab.ndt7.android

import com.google.gson.Gson
import net.measurementlab.ndt7.android.NDTTest.TestType
import net.measurementlab.ndt7.android.models.CallbackRegistry
import net.measurementlab.ndt7.android.models.Measurement
import net.measurementlab.ndt7.android.utils.DataConverter.currentTimeInMicroseconds
import net.measurementlab.ndt7.android.utils.DataConverter.generateResponse
import net.measurementlab.ndt7.android.utils.NDT7Constants.MAX_QUEUE_SIZE
import net.measurementlab.ndt7.android.utils.NDT7Constants.MAX_RUN_TIME
import net.measurementlab.ndt7.android.utils.NDT7Constants.MEASUREMENT_INTERVAL
import net.measurementlab.ndt7.android.utils.NDT7Constants.MIN_MESSAGE_SIZE
import net.measurementlab.ndt7.android.utils.PayloadTransformer
import net.measurementlab.ndt7.android.utils.SocketFactory
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.util.concurrent.ExecutorService
import java.util.concurrent.Semaphore

class Uploader(
    private val cbRegistry: CallbackRegistry,
    private val executorService: ExecutorService,
    private val speedtestLock: Semaphore
) : WebSocketListener() {

    private var startTime: Long = 0
    private var previous: Long = 0
    private var totalBytesSent = 0.0
    private val gson = Gson()

    override fun onMessage(webSocket: WebSocket, text: String) {

        try {
            val measurement = gson.fromJson(text, Measurement::class.java)
            cbRegistry.measurementProgressCbk(measurement)
        } catch (e: Exception) {
            // we don't care that much if a single measurement has trouble
            return
        }
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        val clientResponse = generateResponse(startTime, totalBytesSent - webSocket.queueSize(), TestType.UPLOAD)
        when (code) {
            1000 -> {
                cbRegistry.onFinishedCbk(clientResponse, null, TestType.UPLOAD)
            }
            else -> {
                cbRegistry.onFinishedCbk(clientResponse, Error(reason), TestType.UPLOAD)
            }
        }

        releaseResources()
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
        cbRegistry.onFinishedCbk(generateResponse(startTime, totalBytesSent - webSocket.queueSize(), TestType.UPLOAD), t, TestType.UPLOAD)

        releaseResources()
        webSocket.close(1001, null)
    }

    fun beginUpload(url: String, httpClient: OkHttpClient?) {
        val ws: WebSocket = SocketFactory.establishSocketConnection(url, httpClient, this)
        startTime = currentTimeInMicroseconds()
        previous = startTime

        createBytePayloads(ws)
    }

    private fun createBytePayloads(ws: WebSocket) {

        val timerStart = currentTimeInMicroseconds()
        var elapsedTime = currentTimeInMicroseconds() - timerStart
        var bytes = ByteString.of(*ByteArray(MIN_MESSAGE_SIZE))/* (1<<13) */

        // only allow this to run for 10 seconds, then stop
        while (elapsedTime < MAX_RUN_TIME) {
            bytes = PayloadTransformer.performDynamicTuning(bytes, ws.queueSize(), totalBytesSent)
            sendToWebSocket(bytes, ws)
            elapsedTime = currentTimeInMicroseconds() - timerStart
        }
    }

    private fun sendToWebSocket(data: ByteString, ws: WebSocket) {

        while (ws.queueSize() + data.size < MAX_QUEUE_SIZE) {
            ws.send(data)
            totalBytesSent += data.size
        }
        tryToUpdateUpload(totalBytesSent, ws)
    }

    private fun tryToUpdateUpload(total: Double, ws: WebSocket) {
        val now = currentTimeInMicroseconds()

        // if we haven't sent an update in 250ms, lets send one
        if (now - previous > MEASUREMENT_INTERVAL) {
            previous = now
            cbRegistry.speedtestProgressCbk(generateResponse(startTime, total - ws.queueSize(), TestType.UPLOAD))
        }
    }

    private fun releaseResources() {
        speedtestLock.release()
        executorService.shutdown()
    }
}
