package io.ooni.mk.nuvolari.libndt7;

import android.support.annotation.Nullable;

public class Measurement {
    public double elapsed = 0.0;

    @Nullable
    public TcpInfo tcpInfo = null;

    public double numBytes = 0.0;

    @Nullable
    public BBRInfo bbrInfo = null;

    @Nullable
    public AppInfo appInfo = null;

    @Override
    public String toString() {
        return "Measurement{" +
                "elapsed=" + elapsed +
                ", tcpInfo=" + tcpInfo +
                ", numBytes=" + numBytes +
                ", bbrInfo=" + bbrInfo +
                ", appInfo=" + appInfo +
                '}';
    }

    public static class TcpInfo {
        public double smoothedRtt = 0.0;
        public double rttVar = 0.0;

        @Override
        public String toString() {
            return "TcpInfo{" +
                    "smoothedRtt=" + smoothedRtt +
                    ", rttVar=" + rttVar +
                    '}';
        }
    }

    public static class AppInfo {
        public long numBytes = 0;

        @Override
        public String toString() {
            return "AppInfo{" +
                    "numBytes=" + numBytes +
                    '}';
        }
    }

    public static class BBRInfo {
        public long bandwidth = 0;
        public double minRtt = 0.0;

        @Override
        public String toString() {
            return "BBRInfo{" +
                    "bandwidth=" + bandwidth +
                    ", minRtt=" + minRtt +
                    '}';
        }
    }
}
