package com.smart_contact_manager.Config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {

    @Value("${cloudinary.cloud.name}")
    private String cloudinaryCloudName;

    @Value("${cloudinary.api.key}")
    private String cloudinaryApiKey;

    @Value("${cloudinary.api.secret}")
    private String cloudinaryApiSecret;

//    @Bean
//    public Cloudinary cloudinary(){
//        return new Cloudinary(
//                ObjectUtils.asMap(
//                        "cloud_name", cloudinaryCloudName,
//                        "api_key", cloudinaryApiKey,
//                        "api_secret", cloudinaryApiSecret
//                )
//        );
//    }

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> cloudinaryConfig = new HashMap<>();
        cloudinaryConfig.put("cloud_name", cloudinaryCloudName);
        cloudinaryConfig.put("api_key", cloudinaryApiKey);
        cloudinaryConfig.put("api_secret", cloudinaryApiSecret);
        return new Cloudinary(cloudinaryConfig);
    }
}
