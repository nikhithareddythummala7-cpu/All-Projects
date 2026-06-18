package com.smart_contact_manager.Service.Impl;

import com.smart_contact_manager.Exception.ResourceNotFoundException;
import com.smart_contact_manager.Model.ContactModel;
import com.smart_contact_manager.Model.UserModel;
import com.smart_contact_manager.Repository.ContactRepository;
import com.smart_contact_manager.Service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Override
    public ContactModel saveContact(ContactModel contactModel) {
        return contactRepository.save(contactModel);
    }

    @Override
    public ContactModel updateContact(ContactModel contactModel) {
        ContactModel contact = contactRepository.findById(contactModel.getId()).orElse(null);
        if(contact == null){
            throw new ResourceNotFoundException("Contact not found with given ID " + contactModel.getId());
        }
        contact.setName(contactModel.getName());
        contact.setEmail(contactModel.getEmail());
        contact.setPhoneNumber(contactModel.getPhoneNumber());
        contact.setAddress(contactModel.getAddress());
        contact.setPicture(contactModel.getPicture());
        contact.setCloudinaryImagePublicId(contactModel.getCloudinaryImagePublicId());
        contact.setDescription(contactModel.getDescription());
        contact.setFavorite(contactModel.isFavorite());
        contact.setWebsiteLink(contactModel.getWebsiteLink());
        contact.setLinkedInLink(contactModel.getLinkedInLink());
        return contactRepository.save(contact);
    }

    @Override
    public List<ContactModel> getAllContacts() {
        return contactRepository.findAll();
    }

    @Override
    public ContactModel getContactById(String id) {
        ContactModel contact = contactRepository.findById(id).orElse(null);
        if(contact == null){
            throw new ResourceNotFoundException("Contact not found with given ID " + id);
        }
        return contact;
    }

    @Override
    public void deleteContact(String id) {
        ContactModel contact = contactRepository.findById(id).orElse(null);
        if(contact == null){
            throw new ResourceNotFoundException("Contact not found with given ID " + id);
        }
        contactRepository.delete(contact);
    }

    @Override
    public Page<ContactModel> searchByName(String nameKeyword, int page, int size, String sortBy, String direction, UserModel user) {
        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return contactRepository.findByUserAndNameContaining(user, nameKeyword, pageable);
    }

    @Override
    public Page<ContactModel> searchByEmail(String emailKeyword, int page, int size, String sortBy, String direction, UserModel user) {
        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return contactRepository.findByUserAndEmailContaining(user, emailKeyword, pageable);
    }

    @Override
    public Page<ContactModel> searchByPhoneNumber(String phoneNumberKeyword, int page, int size, String sortBy, String direction, UserModel user) {
        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return contactRepository.findByUserAndPhoneNumberContaining(user, phoneNumberKeyword, pageable);
    }

    @Override
    public List<ContactModel> getContactsByUserId(String userId) {
        return contactRepository.findByUserId(userId);
    }

    @Override
    public Page<ContactModel> getByUser(UserModel user, int page, int size, String sortBy, String direction) {
        Sort sort = direction.equals("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageable = PageRequest.of(page, size, sort);
        return contactRepository.findByUser(user, pageable);
    }
}
