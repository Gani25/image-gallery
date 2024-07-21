package com.sprk.imagegallery.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.sprk.imagegallery.model.ImageModel;
import com.sprk.imagegallery.service.ImageService;

@Controller
public class PublicPageController {

    @Autowired
    private ImageService imageService;

    // Implementing pagination
    @GetMapping("/")
    public String showHomePage(Model model) {

        return showHomePageWithPagination(model, 1);
    }

    @GetMapping("/{pageNum}")
    public String showHomePageWithPagination(Model model, @PathVariable int pageNum) {
        Page<ImageModel> pages = imageService.getAllImagesByBoolean(true, pageNum);

        List<ImageModel> allImages = pages.getContent();

        model.addAttribute("allImages", allImages);

        model.addAttribute("pageNo", pages.getNumber());
        model.addAttribute("totalElements", pages.getTotalElements());
        model.addAttribute("elementPerPage", pages.getNumberOfElements());
        model.addAttribute("totalPages", pages.getTotalPages());
        model.addAttribute("pageSize", pages.getSize());
        return "index";
    }
}
