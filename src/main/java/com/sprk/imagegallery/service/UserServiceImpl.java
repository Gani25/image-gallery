package com.sprk.imagegallery.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sprk.imagegallery.configuration.ImageUtil;
import com.sprk.imagegallery.model.RoleModel;
import com.sprk.imagegallery.model.UserDTO;
import com.sprk.imagegallery.model.UserModel;
import com.sprk.imagegallery.repository.RoleRepository;
import com.sprk.imagegallery.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserModel saveUser(UserModel userModel) {

        String encodedPassword = bCryptPasswordEncoder.encode(userModel.getPassword());
        userModel.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER").getFirst()));
        userModel.setPassword(encodedPassword);
        UserModel savedUser = userRepository.save(userModel);
        return savedUser;
    }

    @Override
    @Transactional
    public void updateUser(UserModel userModel) {
        int userId = userModel.getUserId();
        String userName = userModel.getUserName();
        String email = userModel.getEmail();
        byte[] profilePic = userModel.getProfilePic();
        userRepository.updateNameAndEmailAndProfilePicByUserId(userId, userName, email, profilePic);

    }

    @Override
    public UserModel updateRole(UserModel userModel) {
        UserModel savedUser = userRepository.save(userModel);
        return savedUser;
    }

    @Override
    public UserModel findUserById(int userId) {
        // TODO Auto-generated method stub
        Optional<UserModel> userModel = userRepository.findById(userId);
        if (userModel.isEmpty()) {

            return null;
        }
        UserModel dbModel = userModel.get();
        return dbModel;
    }

    @Override
    public void deleteUser(UserModel userModel) {
        userRepository.delete(userModel);
    }

    @Override
    public UserDTO getUserByUserId(int userId) {
        // TODO Auto-generated method stub
        Optional<UserModel> userModel = userRepository.findById(userId);
        if (userModel.isEmpty()) {

            return null;
        }
        UserModel dbModel = userModel.get();
        byte[] decompressedImage = ImageUtil.decompressImage(dbModel.getProfilePic());
        String base64Image = Base64.getEncoder().encodeToString(decompressedImage);
        UserDTO userDTO = new UserDTO(userId, dbModel.getUserName(), dbModel.getEmail(), dbModel.getPassword(),
                base64Image, dbModel.getImageType());
        // dbModel.setProfilePic(decompressedImage);
        return userDTO;
    }

    @Override
    public UserModel getUserByEmail(String userEmail) {

        UserModel dbUserModel = userRepository.findByEmail(userEmail);
        return dbUserModel;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserModel dbUserModel = userRepository.findByEmail(username);
        if (dbUserModel != null) {
            Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(dbUserModel.getRoles());
            return new User(dbUserModel.getEmail(), dbUserModel.getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("Invalid username or password");
        }
    }

    private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<RoleModel> roles) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (RoleModel tempRole : roles) {
            SimpleGrantedAuthority tempAuthority = new SimpleGrantedAuthority(tempRole.getName());
            authorities.add(tempAuthority);
        }
        return authorities;
    }

    // Methods For Admin
    @Override
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void removeSessionMsg() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpServletRequest request = attr.getRequest();
        HttpSession session = request.getSession();

        session.removeAttribute("msg");

    }

}
