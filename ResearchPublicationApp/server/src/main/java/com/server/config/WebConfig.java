package com.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /pdfs/** to the physical upload directory so uploaded files are accessible
        String path = Paths.get(uploadDir).toAbsolutePath().toString().replace("\\", "/");
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        String location = "file:///" + path; // e.g., file:///C:/path/to/dir/
        registry.addResourceHandler("/pdfs/**")
                .addResourceLocations(location);
    }
}
