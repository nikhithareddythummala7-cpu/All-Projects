package com.example.floweandgift;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
@SpringBootApplication
@EnableWebMvc
public class FloweandgiftApplication {
    public static void main(String[] args) {
        SpringApplication.run(FloweandgiftApplication.class, args);
    }
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Configure uploads directory
                registry
                    .addResourceHandler("/uploads/**")
                    .addResourceLocations("file:uploads/")
                    .setCachePeriod(3600);
                
                // Configure static resources (CSS, JS, images)
                registry
                    .addResourceHandler("/css/**", "/js/**", "/images/**")
                    .addResourceLocations("classpath:/static/css/", "classpath:/static/js/", "classpath:/static/images/")
                    .setCachePeriod(3600);
            }
        };
    }
}
