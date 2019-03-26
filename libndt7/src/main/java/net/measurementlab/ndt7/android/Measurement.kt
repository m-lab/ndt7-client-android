package net.measurementlab.ndt7.android

import com.google.gson.annotations.SerializedName

data class Measurement(@SerializedName("elapsed") val elapsed: Double,
                       @SerializedName("tcp_info") val tcpInfo: TcpInfo? = null,
                       @SerializedName("bbr_info") val bbrInfo: BBRInfo? = null,
                       @SerializedName("app_info") val appInfo: AppInfo? = null) {

    data class TcpInfo(@SerializedName("smoothed_rtt") val smoothedRtt: Double,
                       @SerializedName("rtt_var") val rttVar: Double)

    data class AppInfo(@SerializedName("num_bytes") val numBytes: Long)

    data class BBRInfo(@SerializedName("max_bandwidth") val bandwidth: Long,
                       @SerializedName("min_rtt") val minRtt: Double)
}
