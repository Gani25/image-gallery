package com.sprk.imagegallery.service;

import java.util.List;

import com.sprk.imagegallery.model.ImageModel;
import com.sprk.imagegallery.model.UserModel;

public interface ImageService {

    ImageModel saveImage(ImageModel imageModel);

    List<ImageModel> getAllImagesByUser(UserModel model);

    void removeSessionMsg();

    ImageModel getImageById(int id);

    boolean deleteImage(ImageModel dbImage);
}
