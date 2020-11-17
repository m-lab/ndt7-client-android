@file:JvmName("Measurement")
package net.measurementlab.ndt7.android.models

import com.google.gson.annotations.SerializedName

data class Measurement(
    @SerializedName("ConnectionInfo") val connectionInfo: ConnectionInfo,
    @SerializedName("BBRInfo") val bbrInfo: BBRInfo?,
    @SerializedName("TCPInfo") val tcpInfo: TCPInfo?
)

data class ConnectionInfo(
    @SerializedName("Client") val client: String,
    @SerializedName("Server") val server: String,
    @SerializedName("UUID") val uuid: String
)

data class BBRInfo(
    @SerializedName("BW") val bw: Long?,
    @SerializedName("MinRTT") val minRtt: Long?,
    @SerializedName("PacingGain") val pacingGain: Long?,
    @SerializedName("CwndGain") val cwndGain: Long?,
    @SerializedName("ElapsedTime") val elapsedTime: Long?
)

data class TCPInfo(
    @SerializedName("State") var state: Long?,
    @SerializedName("CAState") val CaState: Long?,
    @SerializedName("Retransmits") val retransmits: Long?,
    @SerializedName("Probes") val probes: Long?,
    @SerializedName("Backoff") val backoff: Long?,
    @SerializedName("Options") val options: Long?,
    @SerializedName("WScale") val wScale: Long?,
    @SerializedName("AppLimited") val appLimited: Long?,
    @SerializedName("RTO") val rto: Long?,
    @SerializedName("ATO") val ato: Long?,
    @SerializedName("SndMSS") val sndMss: Long?,
    @SerializedName("RcvMSS") val rcvMss: Long?,
    @SerializedName("Unacked") val unacked: Long?,
    @SerializedName("Sacked") val sacked: Long?,
    @SerializedName("Lost") val lost: Long?,
    @SerializedName("Retrans") val retrans: Long?,
    @SerializedName("Fackets") val fackets: Long?,
    @SerializedName("LastDataSent") val lastDataSent: Long?,
    @SerializedName("LastAckSent") val lastAckSent: Long?,
    @SerializedName("LastDataRecv") val lastDataRecv: Long?,
    @SerializedName("LastAckRecv") val lastAckRecv: Long?,
    @SerializedName("PMTU") val pmtu: Long?,
    @SerializedName("RcvSsThresh") val rcvSsThresh: Long?,
    @SerializedName("RTT") val rtt: Long?,
    @SerializedName("RTTVar") val rttVar: Long?,
    @SerializedName("SndSsThresh") val sndSsThresth: Long?,
    @SerializedName("SndCwnd") val sndCwnd: Long?,
    @SerializedName("AdvMSS") val advMss: Long?,
    @SerializedName("Reordering") val reordering: Long?,
    @SerializedName("RcvRTT") val rcvRtt: Long?,
    @SerializedName("RcvSpace") val rcvSpace: Long?,
    @SerializedName("TotalRetrans") val totalRetrans: Long?,
    @SerializedName("PacingRate") val pacingRate: Long?,
    @SerializedName("MaxPacingRate") val maxPacingRate: Long?,
    @SerializedName("BytesAcked") val bytesAcked: Long?,
    @SerializedName("BytesReceived") val bytesReceived: Long?,
    @SerializedName("SegsOut") val segsOut: Long?,
    @SerializedName("SegsIn") val segsIn: Long?,
    @SerializedName("NotsentBytes") val notSentBytes: Long?,
    @SerializedName("MinRTT") val minRtt: Long?,
    @SerializedName("DataSegsIn") val dataSegsIn: Long?,
    @SerializedName("DataSegsOut") val dataSegsOut: Long?,
    @SerializedName("DeliveryRate") val deliveryRate: Long?,
    @SerializedName("BusyTime") val busyTime: Long?,
    @SerializedName("RWndLimited") val rWndLimited: Long?,
    @SerializedName("SndBufLimited") val sndBufLimited: Long?,
    @SerializedName("Delivered") val delivered: Long?,
    @SerializedName("DeliveredCE") val deliveredCE: Long?,
    @SerializedName("BytesSent") val bytesSent: Long?,
    @SerializedName("BytesRetrans") val bytesRetrans: Long?,
    @SerializedName("DSackDups") val dSackDups: Long?,
    @SerializedName("ReordSeen") val reordSeen: Long?,
    @SerializedName("ElapsedTime") val elapsedTime: Long?
)
