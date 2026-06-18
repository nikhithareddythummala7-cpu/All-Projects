package com.SmartBridge.Task_Management.controller;


import com.SmartBridge.Task_Management.model.Task;
import com.SmartBridge.Task_Management.model.User;
import com.SmartBridge.Task_Management.service.TaskService;
import com.SmartBridge.Task_Management.service.UserService;
import com.SmartBridge.Task_Management.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/tasks")
@PreAuthorize("isAuthenticated()")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;

    @GetMapping
    public String listTasks(Model model, Authentication authentication) {
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        boolean isViewer = hasRole(authentication, "ROLE_VIEWER");
        // current user for project list context
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        if (isAdmin || isViewer) {
            model.addAttribute("tasks", taskService.getAllTasks());
        } else {
            model.addAttribute("tasks", taskService.getAllTasks(user));
        }
        // include projects on dashboard
        model.addAttribute("projects", projectService.listForUser(user.getId(), isAdmin));
        return "index";
    }

    @GetMapping("/new")
    public String newTaskForm(Model model, Authentication authentication) {
        if (hasRole(authentication, "ROLE_VIEWER")) {
            return "redirect:/tasks?forbidden";
        }
        model.addAttribute("task", new Task());
        return "new-task";
    }

    @PostMapping("/save")
    public String saveTask(@ModelAttribute Task task, Authentication authentication) {
        if (hasRole(authentication, "ROLE_VIEWER")) {
            return "redirect:/tasks?forbidden";
        }
        String username = authentication.getName();
        User user = userService.getUserByUsername(username);
        taskService.saveTask(task, user);
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{id}")
    public String editTaskForm(@PathVariable String id, Model model, Authentication authentication) {
        if (hasRole(authentication, "ROLE_VIEWER")) {
            return "redirect:/tasks?forbidden";
        }
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        if (isAdmin) {
            model.addAttribute("task", taskService.getTaskById(id).orElse(new Task()));
        } else {
            String username = authentication.getName();
            User user = userService.getUserByUsername(username);
            model.addAttribute("task", taskService.getTaskById(id, user).orElse(new Task()));
        }
        return "edit-task";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable String id, @ModelAttribute Task task, Authentication authentication) {
        if (hasRole(authentication, "ROLE_VIEWER")) {
            return "redirect:/tasks?forbidden";
        }
        task.setId(id);
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        if (isAdmin) {
            // preserve existing owner
            Task existing = taskService.getTaskById(id).orElse(null);
            if (existing != null) {
                task.setUser(existing.getUser());
                taskService.saveTaskAsIs(task);
            }
        } else {
            String username = authentication.getName();
            User user = userService.getUserByUsername(username);
            taskService.saveTask(task, user);
        }
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable String id, Authentication authentication) {
        if (hasRole(authentication, "ROLE_VIEWER")) {
            return "redirect:/tasks?forbidden";
        }
        boolean isAdmin = hasRole(authentication, "ROLE_ADMIN");
        if (isAdmin) {
            taskService.deleteTask(id);
        } else {
            String username = authentication.getName();
            User user = userService.getUserByUsername(username);
            boolean deleted = taskService.deleteTask(id, user);
            if (!deleted) {
                return "redirect:/tasks?notfound";
            }
        }
        return "redirect:/tasks";
    }

    private boolean hasRole(Authentication authentication, String role) {
        for (GrantedAuthority a : authentication.getAuthorities()) {
            if (role.equals(a.getAuthority())) return true;
        }
        return false;
    }
}
