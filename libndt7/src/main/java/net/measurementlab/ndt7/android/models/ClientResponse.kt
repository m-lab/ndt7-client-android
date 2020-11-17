package net.measurementlab.ndt7.android.models

import com.google.gson.annotations.SerializedName

data class ClientResponse(
    @SerializedName("AppInfo") val appInfo: AppInfo,
    @SerializedName("Origin") val origin: String = "client",
    @SerializedName("Test") val test: String
)

data class AppInfo(
    @SerializedName("ElapsedTime") val elapsedTime: Long,
    @SerializedName("NumBytes") val numBytes: Double
)
