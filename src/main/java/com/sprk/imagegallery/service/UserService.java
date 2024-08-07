package com.sprk.imagegallery.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.sprk.imagegallery.model.UserDTO;
import com.sprk.imagegallery.model.UserModel;

public interface UserService extends UserDetailsService {

    UserModel saveUser(UserModel userModel);

    void updateUser(UserModel userModel);

    UserModel updateRole(UserModel userModel);

    UserModel findUserById(int userId);

    UserDTO getUserByUserId(int userId);

    Page<UserModel> getAllUsers(int pageNum, String sortField, String sortDir);

    void removeSessionMsg();

    UserModel getUserByEmail(String userEmail);

    void deleteUser(UserModel userModel);
}
