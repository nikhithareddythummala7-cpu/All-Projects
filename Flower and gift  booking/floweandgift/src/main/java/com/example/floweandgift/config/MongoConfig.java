package com.example.floweandgift.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Override
    protected String getDatabaseName() {
        // Extract database name from URI or define explicitly
        ConnectionString connectionString = new ConnectionString(mongoUri);
        String dbName = connectionString.getDatabase();
        return dbName != null ? dbName : "floweandgift";
    }

    @Override
    @Bean
    public com.mongodb.client.MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(mongoUri);

        MongoClientSettings.Builder builder = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .applyToSocketSettings(socketBuilder -> {
                socketBuilder.connectTimeout(5000, TimeUnit.MILLISECONDS);
                socketBuilder.readTimeout(5000, TimeUnit.MILLISECONDS);
            })
            .applyToConnectionPoolSettings(poolBuilder -> {
                poolBuilder.maxWaitTime(5000, TimeUnit.MILLISECONDS);
            })
            .applyToServerSettings(serverBuilder -> {
                serverBuilder.heartbeatFrequency(10000, TimeUnit.MILLISECONDS);
            });
        
        return com.mongodb.client.MongoClients.create(builder.build());
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}
