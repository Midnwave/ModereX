package com.blockforge.moderex.geoip;

import com.blockforge.moderex.ModereX;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

/**
 * Manages GeoIP lookups using MaxMind's GeoLite2 database.
 *
 * Download the free GeoLite2-City.mmdb from:
 * https://dev.maxmind.com/geoip/geolite2-free-geolocation-data
 */
public class GeoIPManager {

    private final ModereX plugin;
    private DatabaseReader reader;
    private boolean enabled;

    public GeoIPManager(ModereX plugin) {
        this.plugin = plugin;
        this.enabled = false;
    }

    /**
     * Initializes the GeoIP database reader.
     * Call this during plugin startup.
     */
    public void initialize() {
        if (!plugin.getConfig().getBoolean("geoip.enabled", false)) {
            plugin.getLogger().info("GeoIP is disabled in config.yml");
            return;
        }

        String databasePath = plugin.getConfig().getString("geoip.database-path", "plugins/ModereX/GeoLite2-City.mmdb");
        File database = new File(databasePath);

        if (!database.exists()) {
            plugin.getLogger().warning("GeoIP database not found at: " + databasePath);
            plugin.getLogger().warning("Download GeoLite2-City.mmdb from https://dev.maxmind.com/geoip/geolite2-free-geolocation-data");
            return;
        }

        try {
            this.reader = new DatabaseReader.Builder(database).build();
            this.enabled = true;
            plugin.getLogger().info("GeoIP database loaded successfully");
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load GeoIP database: " + e.getMessage());
        }
    }

    /**
     * Looks up geographic information for an IP address.
     *
     * @param ipAddress the IP address to lookup
     * @return GeoIPData if found, empty optional otherwise
     */
    public Optional<GeoIPData> lookup(String ipAddress) {
        if (!enabled || reader == null) {
            return Optional.empty();
        }

        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);

            // Skip private/local IP addresses
            if (inetAddress.isSiteLocalAddress() || inetAddress.isLoopbackAddress()) {
                return Optional.empty();
            }

            CityResponse response = reader.city(inetAddress);

            Country country = response.getCountry();
            Subdivision subdivision = response.getMostSpecificSubdivision();
            City city = response.getCity();

            return Optional.of(new GeoIPData(
                    ipAddress,
                    country.getName() != null ? country.getName() : "Unknown",
                    country.getIsoCode() != null ? country.getIsoCode() : "",
                    subdivision.getName() != null ? subdivision.getName() : "Unknown",
                    city.getName() != null ? city.getName() : "Unknown"
            ));

        } catch (UnknownHostException e) {
            plugin.getLogger().warning("Invalid IP address: " + ipAddress);
            return Optional.empty();
        } catch (GeoIp2Exception e) {
            // IP not found in database or other GeoIP error
            return Optional.empty();
        } catch (IOException e) {
            plugin.getLogger().severe("GeoIP lookup error: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Checks if GeoIP lookups are available.
     *
     * @return true if the database is loaded and ready
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Closes the database reader.
     * Call this during plugin shutdown.
     */
    public void shutdown() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                plugin.getLogger().warning("Error closing GeoIP database: " + e.getMessage());
            }
        }
    }

    /**
     * Gets the Unicode flag emoji for a country code.
     *
     * @param countryCode ISO 3166-1 alpha-2 country code
     * @return flag emoji or empty string if invalid
     */
    public static String getFlagEmoji(String countryCode) {
        if (countryCode == null || countryCode.length() != 2) {
            return "";
        }

        countryCode = countryCode.toUpperCase();

        // Convert country code to regional indicator symbols
        int firstLetter = Character.codePointAt(countryCode, 0) - 0x41 + 0x1F1E6;
        int secondLetter = Character.codePointAt(countryCode, 1) - 0x41 + 0x1F1E6;

        return new String(Character.toChars(firstLetter)) + new String(Character.toChars(secondLetter));
    }
}
