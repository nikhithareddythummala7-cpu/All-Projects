package com.groceryapp.freshcart.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;

@RestController
public class ImageController {

	private static final Set<String> ALLOWED_HOSTS = Set.of(
			"images.pexels.com",
			"images.unsplash.com",
			"picsum.photos"
	);

	@GetMapping("/img")
	public ResponseEntity<byte[]> proxy(@RequestParam("src") String src) {
		try {
			URL url = new URL(src);
			String host = url.getHost();
			if (!ALLOWED_HOSTS.contains(host)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(("Host not allowed: " + host).getBytes());
			}

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");
			conn.setRequestProperty("Accept", "image/*,*/*;q=0.8");
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(10000);
			int code = conn.getResponseCode();
			if (code >= 200 && code < 300) {
				try (InputStream is = conn.getInputStream()) {
					byte[] bytes = StreamUtils.copyToByteArray(is);
					String contentType = conn.getContentType();
					HttpHeaders headers = new HttpHeaders();
					if (contentType != null && contentType.startsWith("image/")) {
						headers.setContentType(MediaType.parseMediaType(contentType));
					} else {
						headers.setContentType(MediaType.IMAGE_JPEG);
					}
					headers.setCacheControl("public, max-age=86400");
					return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
				}
			}
			return ResponseEntity.status(code).body(("Upstream response: " + code).getBytes());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
					.body(("Failed to load image: " + e.getMessage()).getBytes());
		}
	}
}


