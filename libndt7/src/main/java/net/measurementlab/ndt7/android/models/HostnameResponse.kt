@file:JvmName("HostnameResponse")
package net.measurementlab.ndt7.android.models

import com.google.gson.annotations.SerializedName

data class HostnameResponse(
        @SerializedName("ip") val ip: ArrayList<String>,
        @SerializedName("country") val country: String,
        @SerializedName("city") val city: String,
        @SerializedName("fqdn") val fqdn: String,
        @SerializedName("site") val site: String
)