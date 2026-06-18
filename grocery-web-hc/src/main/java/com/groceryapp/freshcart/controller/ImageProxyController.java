package com.groceryapp.freshcart.controller;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@RestController
public class ImageProxyController {

    @GetMapping("/proxy/img")
    public ResponseEntity<byte[]> proxy(@RequestParam(name = "src") String src) {
        try {
            if (src == null || src.isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            // Basic allowlist: only http/https
            URI uri = URI.create(src);
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Some inputs might be Google redirector URLs; try to unwrap the real target if present
            String host = uri.getHost() == null ? "" : uri.getHost();
            if (host.contains("google.") && "/url".equalsIgnoreCase(uri.getPath())) {
                String query = uri.getRawQuery();
                if (query != null) {
                    for (String part : query.split("&")) {
                        int eq = part.indexOf('=');
                        if (eq > 0) {
                            String key = part.substring(0, eq);
                            String val = java.net.URLDecoder.decode(part.substring(eq + 1), StandardCharsets.UTF_8);
                            if ("url".equalsIgnoreCase(key)) {
                                try { uri = URI.create(val); } catch (Exception ignored) {}
                                break;
                            }
                        }
                    }
                }
            }

            HttpURLConnection conn = (HttpURLConnection) new URL(uri.toString()).openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(7000);
            conn.setRequestMethod("GET");
            // Set a browser-like UA to reduce anti-bot 403s
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36");
            conn.setRequestProperty("Accept", "image/avif,image/webp,image/apng,image/*,*/*;q=0.8");

            int code = conn.getResponseCode();
            if (code >= 300 && code < 400) {
                String loc = conn.getHeaderField("Location");
                if (loc != null) {
                    conn.disconnect();
                    conn = (HttpURLConnection) new URL(loc).openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(7000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36");
                    conn.setRequestProperty("Accept", "image/avif,image/webp,image/apng,image/*,*/*;q=0.8");
                    code = conn.getResponseCode();
                }
            }

            if (code >= 400) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(("blocked:" + code).getBytes(StandardCharsets.UTF_8));
            }

            String contentType = conn.getContentType();
            if (contentType == null || !contentType.toLowerCase().startsWith("image/")) {
                // not an image; block to avoid SSRF/exposing arbitrary content
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("not an image".getBytes(StandardCharsets.UTF_8));
            }

            try (InputStream in = conn.getInputStream(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buf = new byte[8192];
                int n;
                // Cap to ~10MB
                int total = 0;
                while ((n = in.read(buf)) > 0) {
                    out.write(buf, 0, n);
                    total += n;
                    if (total > (10 * 1024 * 1024)) break;
                }
                byte[] bytes = out.toByteArray();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.parseMediaType(contentType));
                headers.setCacheControl(CacheControl.maxAge(Duration.ofHours(12)).getHeaderValue());
                return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("blocked".getBytes(StandardCharsets.UTF_8));
        }
    }
}
