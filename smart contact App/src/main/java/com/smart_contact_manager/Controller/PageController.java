package com.smart_contact_manager.Controller;

import com.smart_contact_manager.Enum.MessageType;
import com.smart_contact_manager.Form.UserForm;
import com.smart_contact_manager.Helper.Message;
import com.smart_contact_manager.Model.UserModel;
import com.smart_contact_manager.Service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String indexPage(){
        return "redirect:/home";
    }


    @GetMapping("/home")
    public String homePage(Model model) {
        model.addAttribute("currentPage", "home");
        return "home";
    }


    @GetMapping("/register")
    public String registerPage(Model model) {
        UserForm userForm = new UserForm();
        model.addAttribute("currentPage", "register");
        model.addAttribute("userForm", userForm);
        return "register";
    }


    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("currentPage", "login");
        return "login";
    }


    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(@Valid @ModelAttribute UserForm userForm, BindingResult bindingResult, HttpSession session){
        // Fetch form data
        System.out.println(userForm);

        // Validate form data
        if(bindingResult.hasErrors()){
            return "register";
        }

        // Save to database
        UserModel user = new UserModel();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
        user.setPassword(userForm.getPassword());
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());
        user.setProfilePic("https://img.freepik.com/premium-vector/silver-membership-icon-default-avatar-profile-icon-membership-icon-social-media-user-image-vector-illustration_561158-4215.jpg");
        userService.saveUser(user);
        System.out.println("User saved!");

        // Show message : Registration Successful
        Message message = new Message();
        message.setContent("Registration Successful!");
        message.setType(MessageType.success);
        session.setAttribute("message", message);

        // Redirect
        return "redirect:/register";
    }
}
