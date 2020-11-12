@file:JvmName("DataConverter")
package net.measurementlab.ndt7.android.utils

import net.measurementlab.ndt7.android.NDTTest
import net.measurementlab.ndt7.android.models.AppInfo
import net.measurementlab.ndt7.android.models.ClientResponse

object DataConverter {

    @JvmStatic fun generateResponse(startTime: Long, numBytes: Double, testType: NDTTest.TestType): ClientResponse {
        return ClientResponse(AppInfo(currentTimeInMicroseconds() - startTime, numBytes), test = testType.value)
    }

    @JvmStatic fun convertToMbps(clientResponse: ClientResponse): String {
        val time = clientResponse.appInfo.elapsedTime / 1e6
        var speed = clientResponse.appInfo.numBytes / time
        speed *= 8
        speed /= 1e6
        return String.format("%.1f", speed)
    }

    fun currentTimeInMicroseconds(): Long {
        return System.nanoTime() / 1000
    }
}
