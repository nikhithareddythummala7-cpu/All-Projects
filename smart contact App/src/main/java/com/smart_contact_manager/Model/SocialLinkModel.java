package com.smart_contact_manager.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "social_links")
public class SocialLinkModel {
    @Id
    private String id;

    private String link;

    private String title;

    @DBRef  // Reference to the contact document
    private ContactModel contact;
}
