package net.measurementlab.ndt7.android

data class Measurement(val elapsed: Double,
                       val tcpInfo: TcpInfo?,
                       val bbrInfo: BBRInfo?,
                       val appInfo: AppInfo?) {

    data class TcpInfo(val smoothedRtt: Double,
                       val rttVar: Double)

    data class AppInfo(val numBytes: Long)

    data class BBRInfo(val bandwidth: Long,
                       val minRtt: Double)
}
