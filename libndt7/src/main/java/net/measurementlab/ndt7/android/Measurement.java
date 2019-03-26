package net.measurementlab.ndt7.android;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Measurement {
    public final double elapsed;

    @Nullable
    public final TcpInfo tcpInfo;

    @Nullable
    public final BBRInfo bbrInfo;

    @Nullable
    public final AppInfo appInfo;

    public Measurement(double elapsed,
                       @Nullable TcpInfo tcpInfo,
                       @Nullable BBRInfo bbrInfo,
                       @Nullable AppInfo appInfo) {
        this.elapsed = elapsed;
        this.tcpInfo = tcpInfo;
        this.bbrInfo = bbrInfo;
        this.appInfo = appInfo;
    }

    @NonNull
    @Override
    public String toString() {
        return "Measurement{" +
                "elapsed=" + elapsed +
                ", tcpInfo=" + tcpInfo +
                ", bbrInfo=" + bbrInfo +
                ", appInfo=" + appInfo +
                '}';
    }

    public static class TcpInfo {
        public final double smoothedRtt;
        public final double rttVar;

        public TcpInfo(double smoothedRtt,
                       double rttVar) {
            this.smoothedRtt = smoothedRtt;
            this.rttVar = rttVar;
        }

        @NonNull
        @Override
        public String toString() {
            return "TcpInfo{" +
                    "smoothedRtt=" + smoothedRtt +
                    ", rttVar=" + rttVar +
                    '}';
        }
    }

    public static class AppInfo {
        public final long numBytes;

        public AppInfo(long numBytes) {
            this.numBytes = numBytes;
        }

        @NonNull
        @Override
        public String toString() {
            return "AppInfo{" +
                    "numBytes=" + numBytes +
                    '}';
        }
    }

    public static class BBRInfo {
        public final long bandwidth;
        public final double minRtt;

        public BBRInfo(long bandwidth,
                       double minRtt) {
            this.bandwidth = bandwidth;
            this.minRtt = minRtt;
        }

        @NonNull
        @Override
        public String toString() {
            return "BBRInfo{" +
                    "bandwidth=" + bandwidth +
                    ", minRtt=" + minRtt +
                    '}';
        }
    }
}
