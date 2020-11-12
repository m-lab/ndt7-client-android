@file:JvmName("HostnameResponse")
package net.measurementlab.ndt7.android.models

import com.google.gson.annotations.SerializedName

data class HostnameResponse(
    @SerializedName("results")
    val results: List<Result>?
)

data class Result(
    @SerializedName("location")
    val location: Location,
    @SerializedName("machine")
    val machine: String,
    @SerializedName("urls")
    val urls: Urls
)

data class Location(
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String
)

data class Urls(
    @SerializedName("ws:///ndt/v7/download")
    val ndt7DownloadWS: String,
    @SerializedName("ws:///ndt/v7/upload")
    val ndt7UploadWS: String,
    @SerializedName("wss:///ndt/v7/download")
    val ndt7DownloadWSS: String,
    @SerializedName("wss:///ndt/v7/upload")
    val ndt7UploadWSS: String
)
