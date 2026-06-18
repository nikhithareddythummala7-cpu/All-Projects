package com.SmartBridge.Task_Management.repository;


import com.SmartBridge.Task_Management.model.Task;
import com.SmartBridge.Task_Management.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String> {
    List<Task> findByUser(User user);

    Optional<Task> findByIdAndUser(String id, User user);
}

