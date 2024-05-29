package com.sprk.imagegallery.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.sprk.imagegallery.model.RoleModel;
import com.sprk.imagegallery.repository.RoleRepository;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            RoleModel roleModel = new RoleModel();
            roleModel.setName("ROLE_USER");
            roleRepository.save(roleModel);
        }

    }

}
