package com.smart_contact_manager.Controller;

import com.smart_contact_manager.Helper.Helper;
import com.smart_contact_manager.Model.UserModel;
import com.smart_contact_manager.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // User dashboard page
    @RequestMapping(value = "/dashboard")
    public String userDashboardPage(){
        return "user/dashboard";
    }


    // User profile page
    @RequestMapping(value = "/profile")
    public String userProfilePage(Authentication authentication){
        return "user/profile";
    }
}
