package com.sprk.imagegallery.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sprk.imagegallery.model.ImageModel;
import com.sprk.imagegallery.model.UserModel;
import com.sprk.imagegallery.repository.ImageRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class ImageServiceImpl implements ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Override
    public ImageModel saveImage(ImageModel imageModel) {
        ImageModel savedImage = imageRepository.save(imageModel);
        return savedImage;
    }

    @Override
    public List<ImageModel> getAllImagesByUser(UserModel model) {
        // TODO Auto-generated method stub
        List<ImageModel> allImages = imageRepository.findByUserModel(model);
        return allImages;
    }

    @Override
    public ImageModel getImageById(int id) {
        Optional<ImageModel> dbImage = imageRepository.findById(id);
        if (dbImage.isPresent()) {
            return dbImage.get();
        }
        return null;
    }

    @Override
    public boolean deleteImage(ImageModel dbImage) {

        imageRepository.delete(dbImage);

        return true;
    }

    @Override
    public void removeSessionMsg() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        HttpServletRequest request = attr.getRequest();
        HttpSession session = request.getSession();

        session.removeAttribute("msg");

    }

}
