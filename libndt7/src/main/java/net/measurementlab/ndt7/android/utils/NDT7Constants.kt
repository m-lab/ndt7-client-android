@file:JvmName("NDT7Constants")
package net.measurementlab.ndt7.android.utils

import java.util.concurrent.TimeUnit

internal object NDT7Constants {
    val MEASUREMENT_INTERVAL = TimeUnit.MICROSECONDS.convert(250, TimeUnit.MILLISECONDS)
    val MAX_RUN_TIME = TimeUnit.MICROSECONDS.convert(10, TimeUnit.SECONDS) // 10 seconds
    const val MAX_MESSAGE_SIZE = 16777216 // (1<<24) = 16MB
    const val MIN_MESSAGE_SIZE = 8192 // (1<<13)
    const val TEST_MAX_WAIT_TIME = 20L // seconds
    const val MAX_QUEUE_SIZE = 16777216 // 16MB
}
