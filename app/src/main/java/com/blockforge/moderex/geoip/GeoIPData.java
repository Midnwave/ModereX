package com.blockforge.moderex.geoip;

/**
 * Simple data container for GeoIP lookup results.
 */
public class GeoIPData {

    private final String ipAddress;
    private final String country;
    private final String countryCode;
    private final String region;
    private final String city;

    public GeoIPData(String ipAddress, String country, String countryCode, String region, String city) {
        this.ipAddress = ipAddress;
        this.country = country;
        this.countryCode = countryCode;
        this.region = region;
        this.city = city;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getCountry() {
        return country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getFlagEmoji() {
        return GeoIPManager.getFlagEmoji(countryCode);
    }
}
