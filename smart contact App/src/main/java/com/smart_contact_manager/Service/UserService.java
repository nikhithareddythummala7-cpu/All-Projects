package com.smart_contact_manager.Service;

import com.smart_contact_manager.Model.UserModel;
import java.util.List;

public interface UserService {
    UserModel saveUser(UserModel userModel);
    UserModel getUserById(String id);
    UserModel updateUser(UserModel userModel);
    void deleteUser(String id);
    boolean isUserExist(String id);
    boolean isUserExistByEmail(String email);
    List<UserModel> getAllUsers();
    UserModel getUserByEmail(String email);
}
