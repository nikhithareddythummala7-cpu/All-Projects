package com.smart_contact_manager.Model;

import com.smart_contact_manager.Enum.Providers;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "users")
public class UserModel implements UserDetails {
    @Id
    private String id;

    @Field("user_name")
    @NotNull(message = "Username cannot be null")
    private String name;

    @Indexed(unique = true)
    @NotNull(message = "Email cannot be null")
    private String email;

    @Getter(AccessLevel.NONE)
    private String password;

    @Field("about")
    @Size(max = 1000, message = "About cannot exceed 1000 characters")
    private String about;

    @Field("profile_pic")
    @Size(max = 1000, message = "Profile picture URL cannot exceed 1000 characters")
    private String profilePic;

    private String phoneNumber;

    @Getter(AccessLevel.NONE)
    private boolean enabled = true;

    private boolean emailVerified = false;

    private boolean phoneVerified = false;

    private Providers provider = Providers.SELF;

    private String providerUserId;

    @DBRef(lazy = true)  // Reference to another document, lazy fetching in MongoDB
    private List<ContactModel> contacts = new ArrayList<>();

    private List<String> roleList = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> roles = roleList.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toList());
        return roles;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        // For this project username is email
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled(){
        return this.enabled;
    }
}

