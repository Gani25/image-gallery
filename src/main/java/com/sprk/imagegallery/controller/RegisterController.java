package com.sprk.imagegallery.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sprk.imagegallery.configuration.ImageUtil;
import com.sprk.imagegallery.model.ImageModel;
import com.sprk.imagegallery.model.RoleModel;
import com.sprk.imagegallery.model.UserDTO;
import com.sprk.imagegallery.model.UserModel;
import com.sprk.imagegallery.service.ProfilePictureGenerator;
import com.sprk.imagegallery.service.RoleService;
import com.sprk.imagegallery.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("imagegallery")
public class RegisterController {

    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public RegisterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("userObj", new UserModel());
        return "registration-form";
    }

    @PostMapping("/user/signup")
    public String processRegistrationForm(@Valid @ModelAttribute("userObj") UserModel userModel,
            BindingResult bindingResult, HttpSession session, @RequestParam("profile-pic") MultipartFile profilePic)
            throws Exception {

        if (bindingResult.hasErrors()) {
            System.out.println("Inside Error method" + bindingResult.getAllErrors());
            return "registration-form";
        }
        UserModel existingModel = userService.getUserByEmail(userModel.getEmail());
        System.out.println("Inside processform method");
        if (existingModel != null && userModel.getUserId() == 0) {

            System.out.println("Inside if");
            session.setAttribute("msg", "user with email: " + userModel.getEmail() + " already exists");
            return "registration-form";
        } else {
            System.out.println("inside registration method, ");
            if (!profilePic.isEmpty()) {

                byte[] profilePicByte = profilePic.getBytes();
                userModel.setProfilePic(ImageUtil.compressImage(profilePicByte));
                String imageType = profilePic.getContentType();
                userModel.setImageType(imageType);
            } else {
                byte[] generatedImage = ProfilePictureGenerator.generateProfilePicture(userModel.getUserName());
                userModel.setProfilePic(ImageUtil.compressImage(generatedImage));
                userModel.setImageType("image/png");
            }
            String encodedPassword = bCryptPasswordEncoder.encode(userModel.getPassword());
            userModel.setPassword(encodedPassword);

            userService.saveUser(userModel);
            session.setAttribute("msg", "user saved succesffully");

            return "redirect:/imagegallery/user/showLoginForm";
        }
    }

    @GetMapping("user/profile")
    public String showProfilePage(Model model, HttpSession session) {

        UserModel sessionUser = (UserModel) session.getAttribute("user");
        UserDTO userDTO = userService.getUserByUserId(sessionUser.getUserId());
        if (userDTO == null) {
            session.setAttribute("msg", "User not found!!");
            return "redirect:/imagegallery/user/showLoginForm";
        }
        model.addAttribute("userObj", userDTO);

        return "profile-page";

    }

    @PostMapping("/user/update")
    public String processUpdateForm(@Valid @ModelAttribute("userObj") UserModel userModel,
            BindingResult bindingResult, HttpSession session, @RequestParam("profile-pic") MultipartFile profilePic,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        boolean userNameError = bindingResult.getFieldErrors("userName").size() != 0;
        boolean emailError = bindingResult.getFieldErrors("email").size() != 0;

        if (userNameError || emailError) {
            System.out.println("Inside Error method" + bindingResult.getAllErrors());
            return "update-form";
        }
        UserModel sessionUser = (UserModel) session.getAttribute("user");
        // UserModel dbUser = userService.findUserById(userModel.getUserId());

        if (!profilePic.isEmpty()) {

            byte[] profilePicByte = profilePic.getBytes();
            userModel.setProfilePic(ImageUtil.compressImage(profilePicByte));
            String imageType = profilePic.getContentType();
            userModel.setImageType(imageType);
        } else if (profilePic.isEmpty()) {
            // System.out.println("Inside elseif");
            byte[] profilePicByte = sessionUser.getProfilePic();
            String imageType = sessionUser.getImageType();
            userModel.setImageType(imageType);
            userModel.setProfilePic(profilePicByte);
        }
        int userId = (int) session.getAttribute("userId");
        userModel.setUserId(userId);
        userService.updateUser(userModel);

        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // if (auth != null) {
        // new SecurityContextLogoutHandler().logout(request, response, auth);
        // }
        session.setAttribute("msg", "user updated succesffully");
        // return "redirect:/imagegallery/user/showLoginForm";
        return "redirect:/logout";

    }

    @GetMapping("/user/updateform")
    public String showUpdateForm(Model model, HttpSession session) {

        // UserModel userModel = userService.findUserById(userId);
        UserModel sessionUser = (UserModel) session.getAttribute("user");

        model.addAttribute("userObj", sessionUser);
        session.setAttribute("userId", sessionUser.getUserId());

        return "update-form";

    }

    @GetMapping("/user/delete")
    public String deleteAccount(Model model, HttpSession session, HttpServletRequest request,
            HttpServletResponse response) {

        UserModel sessionUser = (UserModel) session.getAttribute("user");

        userService.deleteUser(sessionUser);
        model.addAttribute("msg", "User Deleted succesffully");
        session.removeAttribute("user");
        return "redirect:/logout";

    }

}
