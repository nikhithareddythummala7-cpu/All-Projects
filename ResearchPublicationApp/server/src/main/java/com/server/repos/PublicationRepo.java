package com.server.repos;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.server.models.PublicationModel;
import java.util.List;

public interface PublicationRepo extends MongoRepository<PublicationModel, String>{
    List<PublicationModel> findByAuthorId(String authorId);
}
