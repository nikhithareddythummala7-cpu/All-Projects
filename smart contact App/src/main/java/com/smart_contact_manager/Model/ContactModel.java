package com.smart_contact_manager.Model;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "contacts")
public class ContactModel {
    @Id
    private String id;

    private String name;

    private String email;

    private String phoneNumber;

    private String address;

    private String picture;

    private String cloudinaryImagePublicId;

    @Field("description")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    private boolean favorite = false;

    private String websiteLink;

    private String linkedInLink;

    @DBRef  // Reference to the user document
    private UserModel user;

    @DBRef  // Reference to a list of social links
    private List<SocialLinkModel> links = new ArrayList<>();

}
