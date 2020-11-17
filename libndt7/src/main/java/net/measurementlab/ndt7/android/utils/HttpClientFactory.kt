@file:JvmName("HttpClientFactory")
package net.measurementlab.ndt7.android.utils

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object HttpClientFactory {

    fun createHttpClient(connectTimeout: Long = 10, readTimeout: Long = 10, writeTimeout: Long = 10): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.SECONDS)
            .readTimeout(readTimeout, TimeUnit.SECONDS)
            .writeTimeout(writeTimeout, TimeUnit.SECONDS)
            .build()
    }
}
