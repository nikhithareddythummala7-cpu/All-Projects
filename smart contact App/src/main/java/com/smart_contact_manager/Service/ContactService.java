package com.smart_contact_manager.Service;

import com.smart_contact_manager.Model.ContactModel;
import com.smart_contact_manager.Model.UserModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ContactService {
    ContactModel saveContact(ContactModel contactModel);
    ContactModel updateContact(ContactModel contactModel);
    List<ContactModel> getAllContacts();
    ContactModel getContactById(String id);
    void deleteContact(String id);
    Page<ContactModel> searchByName(String nameKeyword, int page, int size, String sortBy, String direction, UserModel user);
    Page<ContactModel> searchByEmail(String emailKeyword, int page, int size, String sortBy, String direction, UserModel user);
    Page<ContactModel> searchByPhoneNumber(String phoneNumberKeyword, int page, int size, String sortBy, String direction, UserModel user);
    List<ContactModel> getContactsByUserId(String userId);
    Page<ContactModel> getByUser(UserModel user, int page, int size, String sortBy, String direction);
}
