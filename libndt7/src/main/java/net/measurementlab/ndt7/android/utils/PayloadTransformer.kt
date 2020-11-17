package net.measurementlab.ndt7.android.utils

import okio.ByteString

internal object PayloadTransformer {

    // this is gonna let higher speed clients saturate their pipes better
    // it will gradually increase the size of data if the websocket queue isn't filling up
    fun performDynamicTuning(data: ByteString, queueSize: Long, bytesEnqueued: Double): ByteString {
        val totalBytesTransmitted = bytesEnqueued - queueSize

        return if (data.size * 2 < NDT7Constants.MAX_MESSAGE_SIZE && data.size < totalBytesTransmitted / 16) {
            ByteString.of(*ByteArray(data.size * 2)) // double the size of data
        } else {
            data
        }
    }
}
