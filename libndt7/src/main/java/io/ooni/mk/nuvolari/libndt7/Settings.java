package io.ooni.mk.nuvolari.libndt7;

public class Settings {
    public boolean disableTLS = false;
    public String hostname = "";
    public int port = 0;
    public boolean skipTLSVerify = false;  // TODO(bassosimone): implement
    public DownloadSettings download = new DownloadSettings();
}
