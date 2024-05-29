package com.sprk.imagegallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sprk.imagegallery.model.ImageModel;
import com.sprk.imagegallery.model.UserModel;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageModel, Integer> {

    List<ImageModel> findByUserModel(UserModel userModel);
}
