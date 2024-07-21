package com.sprk.imagegallery.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        return getAllUsersWithPaginationAndSorting("userName", "ASC", model, 1);
    }

    @GetMapping("/admin/listusers/{pageNum}")
    public String getAllUsersWithPaginationAndSorting(@RequestParam(name = "sortField") String sortField,
            @RequestParam(name = "sortDir") String sortDir, Model model, @PathVariable int pageNum) {
        Page<UserModel> pages = userService.getAllUsers(pageNum, sortField, sortDir);
        List<UserModel> allUsers = pages.getContent();
        // System.out.println(allUsers);
        model.addAttribute("users", allUsers);
        model.addAttribute("pageNo", pages.getNumber());
        model.addAttribute("totalElements", pages.getTotalElements());
        model.addAttribute("elementPerPage", pages.getNumberOfElements());
        model.addAttribute("totalPages", pages.getTotalPages());
        model.addAttribute("pageSize", pages.getSize());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equalsIgnoreCase("ASC") ? "Desc" : "Asc");

        return "admin-dashboard";
    }

    @GetMapping("/admin/addrole/{userId}")
    public String showRoleForm(@PathVariable int userId, Model model, HttpSession session) {

        UserModel userModel = userService.findUserById(userId);
        if (userModel != null) {

            List<RoleModel> roles = roleRepository.findAll();

            model.addAttribute("roles", roles);
            model.addAttribute("roleObj", new RoleModel());
            model.addAttribute("userId", userId);

            return "role-form";
        } else {

            session.setAttribute("msg", "User not found..");
            return "redirect:/imagegallery/admin/listusers";

        }
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
