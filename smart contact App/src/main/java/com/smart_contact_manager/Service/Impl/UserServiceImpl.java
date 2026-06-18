package com.smart_contact_manager.Service.Impl;

import com.smart_contact_manager.Exception.ResourceNotFoundException;
import com.smart_contact_manager.Helper.AppConstants;
import com.smart_contact_manager.Model.UserModel;
import com.smart_contact_manager.Repository.UserRepository;
import com.smart_contact_manager.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserModel saveUser(UserModel user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoleList(List.of(AppConstants.ROLE_USER));
        return userRepository.save(user);
    }

    @Override
    public UserModel getUserById(String id) {
        UserModel user = userRepository.findById(id).orElse(null);
        if(user == null){
            throw new ResourceNotFoundException("User not found!");
        }
        return user;
    }

    @Override
    public UserModel updateUser(UserModel userModel) {
        UserModel user = userRepository.findById(userModel.getId()).orElse(null);
        if(user == null){
            throw new ResourceNotFoundException("User not found!");
        }
        user.setName(userModel.getName());
        user.setEmail(userModel.getEmail());
        user.setPassword(userModel.getPassword());
        user.setAbout(userModel.getAbout());
        user.setPhoneNumber(userModel.getPhoneNumber());
        user.setProfilePic(userModel.getProfilePic());
        user.setEnabled(userModel.isEnabled());
        user.setEmailVerified(userModel.isEmailVerified());
        user.setPhoneVerified(userModel.isPhoneVerified());
        user.setProvider(userModel.getProvider());
        user.setProviderUserId(userModel.getProviderUserId());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(String id) {
        UserModel user = userRepository.findById(id).orElse(null);
        if(user == null){
            throw new ResourceNotFoundException("User not found!");
        }
        userRepository.delete(user);
    }

    @Override
    public boolean isUserExist(String id) {
        UserModel user = userRepository.findById(id).orElse(null);
        return user != null ? true : false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        UserModel user = userRepository.findByEmail(email).orElse(null);
        return user != null ? true : false;
    }

    @Override
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserModel getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
