package com.sprk.imagegallery.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.sprk.imagegallery.model.ImageModel;
import com.sprk.imagegallery.model.UserModel;

public interface ImageService {

    ImageModel saveImage(ImageModel imageModel);

    Page<ImageModel> getAllImagesByUser(UserModel model, int pageNum);

    void removeSessionMsg();

    ImageModel getImageById(int id);

    boolean deleteImage(ImageModel dbImage);

    Page<ImageModel> getAllImagesByBoolean(boolean b, int pageNum);
}
