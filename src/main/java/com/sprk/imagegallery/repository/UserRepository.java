package com.sprk.imagegallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sprk.imagegallery.model.UserModel;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {

    UserModel findByEmail(String email);

    @Modifying
    @Query("UPDATE UserModel set userName = :uname, email = :uemail, profilePic=:upic WHERE userId=:uid")
    void updateNameAndEmailAndProfilePicByUserId(int uid, String uname, String uemail, byte[] upic);
}
