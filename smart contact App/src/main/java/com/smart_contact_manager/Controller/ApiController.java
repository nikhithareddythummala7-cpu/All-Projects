package com.smart_contact_manager.Controller;

import com.smart_contact_manager.Model.ContactModel;
import com.smart_contact_manager.Service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private ContactService contactService;

    @GetMapping("/contacts/{contactId}")
    public ContactModel getContact(@PathVariable String contactId){
        return contactService.getContactById(contactId);
    }
}
