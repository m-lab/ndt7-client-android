@file:JvmName("Uploader")
package net.measurementlab.ndt7.android

import com.google.gson.Gson
import net.measurementlab.ndt7.android.utils.DataConverter.currentTimeInMicroseconds
import net.measurementlab.ndt7.android.utils.DataConverter.generateResponse
import net.measurementlab.ndt7.android.utils.NDT7Constants.MAX_MESSAGE_SIZE
import net.measurementlab.ndt7.android.utils.NDT7Constants.MAX_RUN_TIME
import net.measurementlab.ndt7.android.utils.NDT7Constants.MEASUREMENT_INTERVAL
import net.measurementlab.ndt7.android.utils.NDT7Constants.MIN_MESSAGE_SIZE
import net.measurementlab.ndt7.android.NDTTest.TestType.*
import net.measurementlab.ndt7.android.models.CallbackRegistry
import net.measurementlab.ndt7.android.models.Measurement
import net.measurementlab.ndt7.android.utils.SocketFactory
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.net.URI
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
            //we don't care that much if a single measurement has trouble
            return
        }
    }


override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {

    val clientResponse = generateResponse(startTime, totalBytesSent, UPLOAD)
    when (code) {
        1000 -> {
            cbRegistry.onFinishedCbk(clientResponse, null, UPLOAD)
        }
        else -> {
            cbRegistry.onFinishedCbk(clientResponse, Error(reason), UPLOAD)
        }
    }

    releaseResources()
    webSocket.close(1000, null)
}

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
        cbRegistry.onFinishedCbk(generateResponse(startTime, totalBytesSent, UPLOAD), t, UPLOAD)

        releaseResources()
        webSocket.close(1001, null)
    }

    fun beginUpload(uri: URI, httpClient: OkHttpClient?) {
        val ws: WebSocket = SocketFactory.establishSocketConnection(uri, httpClient, this)
        startTime = currentTimeInMicroseconds()
        previous = startTime

        createBytePayloads(ws)
    }

    private fun createBytePayloads(ws: WebSocket) {

        val timerStart = currentTimeInMicroseconds()
        var elapsedTime = currentTimeInMicroseconds() - timerStart

        //only allow this to run for 10 seconds, then stop
        while (elapsedTime < MAX_RUN_TIME) {
            val bytes = ByteString.of(*ByteArray(MIN_MESSAGE_SIZE))/* (1<<13) */
            sendToWebSocket(bytes, ws)
            elapsedTime = currentTimeInMicroseconds() - timerStart
        }
    }

    private fun sendToWebSocket(data: ByteString, ws: WebSocket) {
        val byteString = performDynamicTuning(data, ws)

        val underBuffered = byteString.size * 7

        while (ws.queueSize() < underBuffered) {
            ws.send(byteString)
            totalBytesSent += byteString.size
        }
        tryToUpdateUpload(totalBytesSent, ws)
    }


    //this is gonna let higher speed clients saturate their pipes better
    //it will gradually increase the size of data if the websocket queue isn't filling up
    private fun performDynamicTuning(data: ByteString, ws: WebSocket): ByteString {

        return if ( (data.size * 2 < MAX_MESSAGE_SIZE) && queueIsUnsaturated(data, ws)) {
            ByteString.of(*ByteArray(data.size)) //double the size of data
        }
        else {
            data
        }
    }

    private fun queueIsUnsaturated(data:ByteString, ws: WebSocket): Boolean {
        return data.size < (totalBytesSent-ws.queueSize()) / 16
    }

    private fun tryToUpdateUpload(total: Double, ws: WebSocket) {
        val now = currentTimeInMicroseconds()

        //if we haven't sent an update in 250ms, lets send one
        if (now - previous > MEASUREMENT_INTERVAL) {
            previous = now
            cbRegistry.speedtestProgressCbk(generateResponse(startTime,total - ws.queueSize(), UPLOAD))
        }
    }

    private fun releaseResources() {
        speedtestLock.release()
        executorService.shutdown()
    }
}