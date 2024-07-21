package com.sprk.imagegallery.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sprk.imagegallery.configuration.ImageUtil;
import com.sprk.imagegallery.model.ImageModel;
import com.sprk.imagegallery.model.RoleModel;
import com.sprk.imagegallery.model.UserDTO;
import com.sprk.imagegallery.model.UserModel;
import com.sprk.imagegallery.repository.ImageRepository;
import com.sprk.imagegallery.repository.RoleRepository;
import com.sprk.imagegallery.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final int PAGE_SIZE = 3;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private S3Service s3Service;

    @Override
    public UserModel saveUser(UserModel userModel) {

        userModel.setRoles(Arrays.asList(roleRepository.findByName("ROLE_USER").get(0)));
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
        UserModel saved = userRepository.save(userModel);
        return saved;
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
        // Delete all images in S3 bucket also implementd in service

        List<ImageModel> images = imageRepository.findByUserModel(userModel);
        for (ImageModel image : images) {
            s3Service.deleteFile(image.getUrl());
        }

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
            log.info("User found: {}", dbUserModel);
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
    public Page<UserModel> getAllUsers(int pageNum, String sortField, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortField).ascending()
                : Sort.by(
                        sortField).descending();
        Pageable pageable = PageRequest.of(pageNum - 1, PAGE_SIZE, sort);
        return userRepository.findAll(pageable);
    }

    @Override
    public void removeSessionMsg() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpServletRequest request = attr.getRequest();
        HttpSession session = request.getSession();

        session.removeAttribute("msg");

    }

}
