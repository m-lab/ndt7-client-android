package net.measurementlab.ndt.android;

public class Settings {
    public boolean disableTLS = false;
    public String hostname = "";
    public int port = 0;
    public boolean skipTLSVerify = false;  // TODO(bassosimone): implement
    public DownloadSettings download = new DownloadSettings();

    @Override
    public String toString() {
        return "Settings{" +
                "disableTLS=" + disableTLS +
                ", hostname='" + hostname + '\'' +
                ", port=" + port +
                ", skipTLSVerify=" + skipTLSVerify +
                ", download=" + download +
                '}';
    }
}
