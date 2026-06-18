package com.smart_contact_manager.Form;

import com.smart_contact_manager.Validator.ValidFile;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ContactForm {
    @NotBlank(message = "Name is required!")
    private String name;

    @NotBlank(message = "Email Address is required!")
    @Email(message = "Invalid Email Address! [ example@gmail.com ]")
    private String email;

    @NotBlank(message = "Phone Number is required!")
    @Pattern(regexp = "^[0-9]{10}$", message = "Invalid Phone Number!")
    private String phoneNumber;

    @NotBlank(message = "Address is required!")
    private String address;

    private String description;

    private boolean favorite;

    private String websiteLink;

    private String linkedInLink;

    // Validate file size
    @ValidFile(message = "Invalid file!")
    private MultipartFile contactImage;

    private String picture;
}
