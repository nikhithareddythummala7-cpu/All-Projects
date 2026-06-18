package com.streamy.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/api/stream")
public class VideoStreamController {
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> streamVideo(@PathVariable String filename, @RequestHeader(value = "Range", required = false) String rangeHeader) {
        try {
            File file = new File("/path/to/videos/" + filename); // Update path as needed
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            Resource resource = new UrlResource(file.toURI());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_TYPE, "video/mp4");
            // Add range support and premium restriction logic here
            return new ResponseEntity<>(resource, headers, HttpStatus.PARTIAL_CONTENT);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
