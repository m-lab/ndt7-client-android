package net.measurementlab.ndt7.android;

public class Settings {
    public String hostname = "";
    public int port = 0;
    public boolean skipTlsCertificateVerification = false;

    @Override
    public String toString() {
        return "Settings{" +
                ", hostname='" + hostname + '\'' +
                ", port=" + port +
                ", skipTlsCertificateVerification=" + skipTlsCertificateVerification +
                '}';
    }
}
