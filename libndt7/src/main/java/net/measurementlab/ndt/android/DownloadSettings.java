package net.measurementlab.ndt.android;

public class DownloadSettings {
    public boolean adaptive = false;
    public int duration = 10;

    @Override
    public String toString() {
        return "DownloadSettings{" +
                "adaptive=" + adaptive +
                ", duration=" + duration +
                '}';
    }
}
