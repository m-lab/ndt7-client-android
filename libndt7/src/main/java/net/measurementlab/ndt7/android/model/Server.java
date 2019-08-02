package net.measurementlab.ndt7.android.model;

import com.google.gson.annotations.SerializedName;


public class Server {
    @SerializedName("ip")
    public String[] ip;

    @SerializedName("country")
    public String country;

    @SerializedName("city")
    public String city;

    @SerializedName("fqdn")
    public String fqdn;

    @SerializedName("site")
    public String site;

}
