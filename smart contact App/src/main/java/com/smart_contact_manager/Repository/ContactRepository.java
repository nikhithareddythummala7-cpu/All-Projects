package com.smart_contact_manager.Repository;

import com.smart_contact_manager.Model.ContactModel;
import com.smart_contact_manager.Model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends MongoRepository<ContactModel, String> {

    List<ContactModel> findByUserId(String userId);

    Page<ContactModel> findByUser(UserModel user, Pageable pageable);

    Page<ContactModel> findByUserAndNameContaining(UserModel user, String nameKeyword, Pageable pageable);

    Page<ContactModel> findByUserAndEmailContaining(UserModel user, String emailKeyword, Pageable pageable);

    Page<ContactModel> findByUserAndPhoneNumberContaining(UserModel user, String phoneNumberKeyword, Pageable pageable);

}
