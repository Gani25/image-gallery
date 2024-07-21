package com.sprk.imagegallery.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.sprk.imagegallery.model.ImageModel;
import com.sprk.imagegallery.model.UserModel;
import com.sprk.imagegallery.service.ImageService;
import com.sprk.imagegallery.service.S3Service;
import com.sprk.imagegallery.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("imagegallery")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private S3Service s3Service;

    private static String uploadDir = "src/main/resources/static/images";

    @GetMapping("/user/image")
    public String showImageForm(Model model, HttpSession session) {
        model.addAttribute("imageObj", new ImageModel());
        session.setAttribute("imageId", 0);
        session.setAttribute("imageTitle", "");

        session.setAttribute("imageURL", "");

        return "image-form";
    }

    @PostMapping("/user/image")
    public String processImageForm(@Valid @ModelAttribute("imageObj") ImageModel imageModel,
            BindingResult bindingResult, @RequestParam("imageData") MultipartFile file, HttpSession session)
            throws Exception {
        UserModel userModel = null;
        if (bindingResult.hasErrors()) {
            // System.out.println("Inside if of error");
            return "image-form";
        } else {
            String imageTitle = "";
            String url = (String) session.getAttribute("imageURL");
            int imageId = (int) session.getAttribute("imageId");
            // System.out.println(imageId);
            // System.out.println(url);
            String existingTitle = (String) session.getAttribute("imageTitle");
            if (!file.isEmpty()) {

                if (imageId != 0) {

                    // This case is for update
                    // if update user pass image then delete old image
                    // Path fileNameAndPath = Paths.get(uploadDir, existingTitle);
                    // System.out.println(fileNameAndPath);
                    // Files.deleteIfExists(fileNameAndPath);

                    s3Service.deleteFile(url);

                    // System.out.println(imageId);
                    imageModel.setImageId(imageId);

                }

                imageTitle = file.getOriginalFilename();
                url = s3Service.uploadFile(file);
            } else {
                if (imageId == 0) {
                    // When user is inserting record and didn't provide the image
                    session.setAttribute("msg", "Please Add Image ");
                    return "image-form";

                }

                // When user is updating record and didn't provide the image
                imageTitle = existingTitle;
                imageModel.setImageId(imageId);

            }

            // Save user info in db with image
            userModel = (UserModel) session.getAttribute("user");

            imageModel.setImageTitle(imageTitle);
            imageModel.setUrl(url);
            imageModel.setUserModel(userModel);

            ImageModel savedImage = imageService.saveImage(imageModel);
            if (savedImage != null) {
                if (imageId != 0) {

                    session.setAttribute("msg", "Image updated successfully");
                } else {
                    session.setAttribute("msg", "Image saved successfully");
                }
            } else {
                session.setAttribute("msg", "Something bad happen on server");

            }
            return "redirect:/imagegallery/user/image/images";

        }
    }

    // @GetMapping("/")
    // public String showHomePage(Model model) {
    // List<ImageModel> allImages = imageService.getAllImagesByBoolean(true);
    // model.addAttribute("allImages", allImages);

    // return "image-dashboard";
    // }

    @GetMapping("/user/image/images")
    public String showDashboard(Model model, HttpSession session) {

        return showDashboardWithPagination(model, session, 1);
    }

    @GetMapping("/user/image/images/{pageNum}")
    public String showDashboardWithPagination(Model model, HttpSession session, @PathVariable int pageNum) {
        UserModel sessionUser = (UserModel) session.getAttribute("user");
        Page<ImageModel> pages = imageService.getAllImagesByUser(sessionUser, pageNum);
        List<ImageModel> allImages = pages.getContent();

        model.addAttribute("allImages", allImages);

        model.addAttribute("pageNo", pages.getNumber());
        model.addAttribute("totalElements", pages.getTotalElements());
        model.addAttribute("elementPerPage", pages.getNumberOfElements());
        model.addAttribute("totalPages", pages.getTotalPages());
        model.addAttribute("pageSize", pages.getSize());

        return "image-dashboard";
    }

    @GetMapping("/user/image/delete/{id}")
    // if passed string in path variable program fails
    public String deleteImage(@PathVariable int id, HttpSession session) throws Exception {
        // System.out.println(id);
        ImageModel dbImage = imageService.getImageById(id);
        if (dbImage != null) {

            UserModel sessionUserModel = (UserModel) session.getAttribute("user");
            UserModel imageUserModel = dbImage.getUserModel();
            if (sessionUserModel.getUserId() == imageUserModel.getUserId()) {

                boolean result = false;
                result = imageService.deleteImage(dbImage);
                String existingTitle = dbImage.getImageTitle();
                if (result) {
                    // Path fileNameAndPath = Paths.get(uploadDir, existingTitle);
                    // System.out.println(fileNameAndPath);
                    // Files.deleteIfExists(fileNameAndPath);
                    s3Service.deleteFile(dbImage.getUrl());
                    session.setAttribute("msg", "Deleted successfully");
                }
            } else {
                session.setAttribute("msg", "You are not authorized to change someone else data!!");
            }

        } else {
            session.setAttribute("msg", "Image with id = " + id + " doesnot exists in our records!!");
        }
        return "redirect:/imagegallery/user/image/images";
    }

    @GetMapping("/user/image/update/{id}")
    // if passed string in path variable program fails
    public String updateImage(@PathVariable int id, Model model, HttpSession session) throws Exception {
        // System.out.println(id);
        ImageModel dbImage = imageService.getImageById(id);

        if (dbImage != null) {

            UserModel sessionUser = (UserModel) session.getAttribute("user");
            // Fetch the user information from image and check user is valid or passing some
            // others id
            UserModel imageUserModel = dbImage.getUserModel();
            if (sessionUser.getUserId() == imageUserModel.getUserId()) {
                model.addAttribute("imageObj", dbImage);
                session.setAttribute("imageId", dbImage.getImageId());
                session.setAttribute("imageTitle", dbImage.getImageTitle());
                session.setAttribute("imageURL", dbImage.getUrl());
                return "image-form";
            } else {

                session.setAttribute("msg", "You are not authorized to change someone else data!!");
                return "redirect:/imagegallery/user/image/images";
            }

        } else {

            session.setAttribute("msg", "Image with id = " + id + " doesnot exists in our records!!");
            return "redirect:/imagegallery/user/image/images";
        }
    }

}
