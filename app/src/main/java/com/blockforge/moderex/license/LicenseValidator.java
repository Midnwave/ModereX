package com.blockforge.moderex.license;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * HTTP client for Cloudflare Workers license API with RSA signature verification.
 */
public class LicenseValidator {
    private static final String LICENSE_API_URL = "https://license.moderex.net";
    private static final int CONNECT_TIMEOUT = 5000; // 5 seconds
    private static final int READ_TIMEOUT = 10000;   // 10 seconds
    private static final int MAX_RETRIES = 3;

    private final Logger logger;
    private final Gson gson;
    private final PublicKey publicKey;

    public LicenseValidator(Logger logger) {
        this.logger = logger;
        this.gson = new GsonBuilder().serializeNulls().create();
        this.publicKey = loadPublicKey();
    }

    /**
     * Load RSA public key from embedded resource
     */
    private PublicKey loadPublicKey() {
        try {
            // Read public key PEM from resources
            var inputStream = getClass().getClassLoader().getResourceAsStream("license-public-key.pem");
            if (inputStream == null) {
                logger.warning("[License] Public key not found in resources - signature verification will fail");
                return null;
            }

            StringBuilder pemContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.contains("BEGIN") && !line.contains("END")) {
                        pemContent.append(line.trim());
                    }
                }
            }

            // Decode base64 PEM content
            byte[] keyBytes = Base64.getDecoder().decode(pemContent.toString());

            // Create public key
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            logger.severe("[License] Failed to load public key: " + e.getMessage());
            return null;
        }
    }

    /**
     * Validate license token with Cloudflare Workers API
     */
    public ValidationResult validate(String token, String serverId, String serverName, String version) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                URI uri = URI.create(LICENSE_API_URL + "/validate");
                HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setDoOutput(true);

                // Build request body
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("token", token);
                requestBody.addProperty("serverId", serverId);
                requestBody.addProperty("serverName", serverName);
                requestBody.addProperty("version", version);

                // Send request
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = gson.toJson(requestBody).getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Read response
                int responseCode = conn.getResponseCode();
                StringBuilder response = new StringBuilder();

                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream(),
                                StandardCharsets.UTF_8
                        ))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }

                JsonObject jsonResponse = gson.fromJson(response.toString(), JsonObject.class);

                // Check if validation succeeded
                if (responseCode == 200 && jsonResponse.has("valid") && jsonResponse.get("valid").getAsBoolean()) {
                    String signature = jsonResponse.has("signature") ? jsonResponse.get("signature").getAsString() : null;

                    // Verify signature
                    if (!verifySignature(jsonResponse, signature)) {
                        logger.warning("[License] Signature verification failed - API response may be tampered");
                        return new ValidationResult(false, "Signature verification failed", null);
                    }

                    Long expiresAt = jsonResponse.has("expiresAt") && !jsonResponse.get("expiresAt").isJsonNull()
                            ? jsonResponse.get("expiresAt").getAsLong()
                            : null;

                    logger.info("[License] Validation successful");
                    return new ValidationResult(true, null, expiresAt);
                } else {
                    String error = jsonResponse.has("error") ? jsonResponse.get("error").getAsString() : "Unknown error";
                    logger.warning("[License] Validation failed: " + error);
                    return new ValidationResult(false, error, null);
                }
            } catch (Exception e) {
                logger.warning("[License] Validation attempt " + attempt + " failed: " + e.getMessage());
                if (attempt < MAX_RETRIES) {
                    try {
                        // Exponential backoff
                        Thread.sleep((long) Math.pow(2, attempt) * 1000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        return new ValidationResult(false, "Failed after " + MAX_RETRIES + " attempts", null);
    }

    /**
     * Send heartbeat to license API
     */
    public boolean sendHeartbeat(String token, String serverId, int players, long uptime) {
        try {
            URI uri = URI.create(LICENSE_API_URL + "/heartbeat");
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            conn.setDoOutput(true);

            // Build request body
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("token", token);
            requestBody.addProperty("serverId", serverId);
            requestBody.addProperty("players", players);
            requestBody.addProperty("uptime", uptime);

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = gson.toJson(requestBody).getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read response
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                logger.fine("[License] Heartbeat sent successfully");
                return true;
            } else {
                logger.warning("[License] Heartbeat failed with code: " + responseCode);
                return false;
            }
        } catch (Exception e) {
            logger.warning("[License] Heartbeat error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verify RSA signature on API response using canonical (sorted-key) JSON.
     */
    private boolean verifySignature(JsonObject response, String signatureBase64) {
        if (publicKey == null) {
            logger.warning("[License] Cannot verify signature - public key not loaded");
            return false;
        }

        if (signatureBase64 == null) {
            logger.warning("[License] No signature provided in response");
            return false;
        }

        try {
            // Remove signature field from response for verification
            JsonObject dataToVerify = response.deepCopy();
            dataToVerify.remove("signature");

            // Build canonical JSON with alphabetically sorted keys (matches Worker)
            JsonObject sorted = new JsonObject();
            dataToVerify.entrySet().stream()
                    .sorted(java.util.Map.Entry.comparingByKey())
                    .forEach(e -> sorted.add(e.getKey(), e.getValue()));

            String jsonData = gson.toJson(sorted);

            // Decode signature
            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

            // Verify signature
            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(jsonData.getBytes(StandardCharsets.UTF_8));

            boolean verified = sig.verify(signatureBytes);

            if (!verified) {
                logger.warning("[License] Signature mismatch - canonical JSON: " + jsonData);
            }

            return verified;
        } catch (Exception e) {
            logger.severe("[License] Signature verification error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validation result container
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String error;
        private final Long expiresAt;

        public ValidationResult(boolean valid, String error, Long expiresAt) {
            this.valid = valid;
            this.error = error;
            this.expiresAt = expiresAt;
        }

        public boolean isValid() {
            return valid;
        }

        public String getError() {
            return error;
        }

        public Long getExpiresAt() {
            return expiresAt;
        }
    }
}
