package com.sprk.imagegallery.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.sprk.imagegallery.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("imagegallery")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    private static String uploadDir = "src/main/resources/static/images";

    @GetMapping("/user/image")
    public String showImageForm(Model model) {
        model.addAttribute("imageObj", new ImageModel());

        return "image-form";
    }

    @PostMapping("/user/image/{userId}")
    public String processImageForm(@PathVariable int userId, @Valid @ModelAttribute("imageObj") ImageModel imageModel,
            BindingResult bindingResult, @RequestParam("imageData") MultipartFile file, HttpSession session)
            throws Exception {
        UserModel userModel = null;
        if (bindingResult.hasErrors()) {
            // System.out.println("Inside if of error");
            return "image-form";
        } else {
            String imageTitle = "";
            int imageId = imageModel.getImageId();
            if (!file.isEmpty()) {

                if (imageId != 0) {

                    String existingTitle = imageModel.getImageTitle();
                    // This case is for update
                    // if update user pass image then delete old image
                    Path fileNameAndPath = Paths.get(uploadDir, existingTitle);
                    System.out.println(fileNameAndPath);
                    Files.deleteIfExists(fileNameAndPath);

                }
                // save image
                imageTitle = file.getOriginalFilename();
                Path fileNameAndPath = Paths.get(uploadDir, imageTitle);
                Files.write(fileNameAndPath, file.getBytes());
            } else {
                System.out.println("Inside else of file" + imageModel.getImageTitle());
                imageTitle = imageModel.getImageTitle();

            }

            // Save user info in db with image
            userModel = userService.findUserById(userId);
            if (userModel != null) {
                imageModel.setImageTitle(imageTitle);
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
                return "redirect:/imagegallery/user/image/images/" + userModel.getUserId();
            } else {
                return "redirect:/";
            }
        }
    }

    @GetMapping("/user/image/images/{userId}")
    public String showDashboard(@PathVariable int userId, Model model) {
        List<ImageModel> allImages = imageService.getAllImagesByUser(userService.findUserById(userId));
        model.addAttribute("allImages", allImages);

        return "image-dashboard";
    }

    @GetMapping("/user/image/delete/{id}/{userId}")
    // if passed string in path variable program fails
    public String deleteImage(@PathVariable int id, @PathVariable int userId, HttpSession session) throws Exception {
        // System.out.println(id);
        ImageModel dbImage = imageService.getImageById(id);
        if (dbImage != null) {
            boolean result = false;
            result = imageService.deleteImage(dbImage);
            String existingTitle = dbImage.getImageTitle();
            if (result) {
                Path fileNameAndPath = Paths.get(uploadDir, existingTitle);
                System.out.println(fileNameAndPath);
                Files.deleteIfExists(fileNameAndPath);
                session.setAttribute("msg", "Deleted successfully");
            }

        } else {
            session.setAttribute("msg", "Image with id = " + id + " doesnot exists in our records!!");
        }
        return "redirect:/imagegallery/user/image/images/" + userId;
    }

    @GetMapping("/user/image/update/{id}")
    // if passed string in path variable program fails
    public String updateImage(@PathVariable int id, Model model, HttpSession session) throws Exception {
        // System.out.println(id);
        ImageModel dbImage = imageService.getImageById(id);
        if (dbImage != null) {
            model.addAttribute("imageObj", dbImage);
            return "image-form";

        } else {

            session.setAttribute("msg", "Image with id = " + id + " doesnot exists in our records!!");
            return "redirect:/imagegallery/user/image/images";
        }
    }

}
