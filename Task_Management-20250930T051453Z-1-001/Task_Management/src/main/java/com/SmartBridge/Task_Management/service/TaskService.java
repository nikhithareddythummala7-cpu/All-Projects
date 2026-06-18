package com.SmartBridge.Task_Management.service;


import com.SmartBridge.Task_Management.model.Task;
import com.SmartBridge.Task_Management.model.User;
import com.SmartBridge.Task_Management.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks(User user) {
        return taskRepository.findByUser(user);
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(String id, User user) {
        return taskRepository.findByIdAndUser(id, user);
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public void saveTask(Task task, User user) {
        task.setUser(user);
        taskRepository.save(task);
    }

    public void saveTaskAsIs(Task task) {
        // Do not change owner; assume caller has set intended fields except owner
        taskRepository.save(task);
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }

    public boolean deleteTask(String id, User user) {
        Optional<Task> t = taskRepository.findByIdAndUser(id, user);
        if (t.isPresent()) {
            taskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean reassignOwner(String taskId, User newOwner) {
        Optional<Task> t = taskRepository.findById(taskId);
        if (t.isPresent()) {
            Task task = t.get();
            task.setUser(newOwner);
            taskRepository.save(task);
            return true;
        }
        return false;
    }
}
