package com.sprk.imagegallery.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sprk.imagegallery.model.RoleModel;
import com.sprk.imagegallery.model.UserModel;
import com.sprk.imagegallery.repository.RoleRepository;
import com.sprk.imagegallery.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("imagegallery")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("/admin/listusers")
    public String getAllUsers(Model model) {
        List<UserModel> allUsers = userService.getAllUsers();
        // System.out.println(allUsers);
        model.addAttribute("users", allUsers);
        return "admin-dashboard";
    }

    @GetMapping("/admin/addrole/{userId}")
    public String showRoleForm(@PathVariable int userId, Model model) {

        List<RoleModel> roles = roleRepository.findAll();

        model.addAttribute("roles", roles);
        model.addAttribute("roleObj", new RoleModel());
        model.addAttribute("userId", userId);

        return "role-form";
    }

    @PostMapping("/admin/addrole/{userId}")
    public String processRoleForm(@PathVariable int userId, @ModelAttribute RoleModel roleModel, HttpSession session) {
        UserModel userModel = userService.findUserById(userId);

        Collection<RoleModel> existingRoles = userModel.getRoles();

        RoleModel dbRoleModel = roleRepository.findByName(roleModel.getName()).get(0);
        System.out.println(userModel.getUserName());
        System.out.println(dbRoleModel.getName());

        if (existingRoles.contains(dbRoleModel)) {
            session.setAttribute("msg", "Role Already Exists..");
            return "redirect:/imagegallery/admin/addrole/" + userId;

        } else {

            existingRoles.add(dbRoleModel);
            userModel.setRoles(existingRoles);
            userService.updateRole(userModel);

            return "redirect:/imagegallery/admin/listusers";
        }
    }

}
