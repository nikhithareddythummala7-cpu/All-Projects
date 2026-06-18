package com.smart_contact_manager.Controller;

import com.smart_contact_manager.Enum.MessageType;
import com.smart_contact_manager.Form.ContactForm;
import com.smart_contact_manager.Form.ContactSearchForm;
import com.smart_contact_manager.Helper.AppConstants;
import com.smart_contact_manager.Helper.Helper;
import com.smart_contact_manager.Helper.Message;
import com.smart_contact_manager.Model.ContactModel;
import com.smart_contact_manager.Model.UserModel;
import com.smart_contact_manager.Service.ContactService;
import com.smart_contact_manager.Service.ImageService;
import com.smart_contact_manager.Service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "/add")
    public String addContactViewPage(Model model){
        ContactForm contactForm = new ContactForm();
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult, Authentication authentication, HttpSession session){
        // Validate form data
        if(bindingResult.hasErrors()){
            Message errorMessage = new Message();
            errorMessage.setContent("Please correct the following errors!");
            errorMessage.setType(MessageType.danger);
            session.setAttribute("message", errorMessage);
            return "user/add_contact";
        }
        // Fetch the user
        String username = Helper.getEmailOfLoggedInUser(authentication);
        UserModel user = userService.getUserByEmail(username);
        // Save to database
        ContactModel contact = new ContactModel();
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavorite(contactForm.isFavorite());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setUser(user);
        // Process the contact picture
        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()){
            logger.info("File information : {}", contactForm.getContactImage().getOriginalFilename());
            String filename = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
            contact.setPicture(fileURL);
            contact.setCloudinaryImagePublicId(filename);
        }
        contactService.saveContact(contact);
        // Show message : Successfully added a new contact
        Message successMessage = new Message();
        successMessage.setContent("You are successfully added a new contact!");
        successMessage.setType(MessageType.success);
        session.setAttribute("message", successMessage);
        // Redirect
        return "redirect:/user/contacts/add";
    }


    @RequestMapping
    public String viewContactsPage(@RequestParam(value = "page", defaultValue = "0") int page,
                                   @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
                                   @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
                                   @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                   Model model,
                                   Authentication authentication){
        String username = Helper.getEmailOfLoggedInUser(authentication);
        UserModel user = userService.getUserByEmail(username);
        Page<ContactModel> pageContacts = contactService.getByUser(user, page, size, sortBy, direction);
        model.addAttribute("pageContacts", pageContacts);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        model.addAttribute("currentPage", "contactsView");
        model.addAttribute("contactSearchForm", new ContactSearchForm());
        return "user/contacts";
    }


    @RequestMapping("/search")
    public String searchHandler(@ModelAttribute ContactSearchForm contactSearchForm,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
                                @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
                                @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                Model model,
                                Authentication authentication){

        logger.info("field {} keyword {}", contactSearchForm.getField(), contactSearchForm.getValue());

        // Fetch the user
        String username = Helper.getEmailOfLoggedInUser(authentication);
        UserModel user = userService.getUserByEmail(username);

        Page<ContactModel> pageContacts = null;
        if(contactSearchForm.getField().equalsIgnoreCase("name")){
            pageContacts = contactService.searchByName(contactSearchForm.getValue(), page, size, sortBy, direction, user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContacts = contactService.searchByEmail(contactSearchForm.getValue(), page, size, sortBy, direction, user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            pageContacts = contactService.searchByPhoneNumber(contactSearchForm.getValue(), page, size, sortBy, direction, user);
        }

        logger.info("pageContact {}", pageContacts);

        model.addAttribute("pageContacts", pageContacts);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);
        model.addAttribute("currentPage", "contactsSearch");
        model.addAttribute("contactSearchForm", contactSearchForm);

        return "user/search";
    }


    @RequestMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable("contactId") String contactId, HttpSession session){
        contactService.deleteContact(contactId);
        logger.info("contactId{} deleted", contactId);

        // Show message : Contact is deleted successfully
        Message successMessage = new Message();
        successMessage.setContent("Contact is deleted successfully!");
        successMessage.setType(MessageType.success);
        session.setAttribute("message", successMessage);

        return "redirect:/user/contacts";
    }


    @RequestMapping("/view/{contactId}")
    public String updateContactFormView(@PathVariable("contactId") String contactId, Model model){
        ContactModel contact = contactService.getContactById(contactId);
        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setLinkedInLink(contact.getLinkedInLink());
        contactForm.setPicture(contact.getPicture());
        model.addAttribute("contactForm", contactForm);
        model.addAttribute("contactId", contactId);
        return "/user/update_contact_view";
    }


    @RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
    public String updateContact(@PathVariable("contactId") String contactId, @Valid @ModelAttribute ContactForm contactForm, BindingResult bindingResult, Model model, HttpSession session){
        // Validate form data
        if(bindingResult.hasErrors()){
            Message errorMessage = new Message();
            errorMessage.setContent("Please correct the following errors!");
            errorMessage.setType(MessageType.danger);
            session.setAttribute("message", errorMessage);
            return "user/update_contact_view";
        }
        // Update the contact
        ContactModel contact = contactService.getContactById(contactId);
        contact.setId(contactId);
        contact.setName(contactForm.getName());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setFavorite(contactForm.isFavorite());
        contact.setWebsiteLink(contactForm.getWebsiteLink());
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        // Process the contact picture
        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String filename = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
            contact.setCloudinaryImagePublicId(filename);
            contact.setPicture(fileURL);
            contactForm.setPicture(fileURL);
        }
        ContactModel updatedContact = contactService.updateContact(contact);
        // Show message : Successfully updated the contact
        Message successMessage = new Message();
        successMessage.setContent("You are successfully updated the contact!");
        successMessage.setType(MessageType.success);
        session.setAttribute("message", successMessage);
        // Redirect
        return "redirect:/user/contacts/view/" + contactId;
    }
}
