@file:JvmName("SocketFactory")
package net.measurementlab.ndt7.android.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.lang.Error

internal object SocketFactory {

    // establishes a websocket with an ndt server
    fun establishSocketConnection(url: String, httpClient: OkHttpClient?, listener: WebSocketListener): WebSocket {

        val request = Request.Builder()
            .url(url)
            .addHeader("Sec-WebSocket-Protocol", "net.measurementlab.ndt.v7")
            .build()

        return httpClient?.newWebSocket(request, listener) ?: throw Error("socket unable to be created")
    }
}
