package com.smart_contact_manager.Form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserForm {
    @NotBlank(message = "Username is required!")
    @Size(min = 3, message = "Minimum 3 characters are required!")
    private String name;

    @NotBlank(message = "Email Address is required!")
    @Email(message = "Invalid Email Address!")
    private String email;

    @NotBlank(message = "Password is required!")
    @Size(min = 6, message = "Minimum 6 characters are required!")
    private String password;

    @NotBlank(message = "Write something about yourself is required!")
    private String about;

    @Size(min = 8, max = 12, message = "Invalid Phone Number")
    private String phoneNumber;
}
