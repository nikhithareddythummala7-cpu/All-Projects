package com.server.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.server.models.Domain;

public interface DomainRepo extends MongoRepository<Domain, String> {
}
