package com.sprk.imagegallery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sprk.imagegallery.model.RoleModel;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<RoleModel, Integer> {

    List<RoleModel> findByName(String name);
}
