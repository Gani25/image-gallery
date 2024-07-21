package com.sprk.imagegallery.model;

import java.util.Collection;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
public class UserModel {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int userId;

        @NotBlank(message = "please provide username")
        private String userName;

        @NotBlank(message = "please provide email")
        @Email(message = "incorrect email")
        @Column(nullable = false, unique = true)
        private String email;

        // @NotBlank(message = "please enter password")
        private String password;

        @Lob
        @Column(columnDefinition = "LONGBLOB")
        private byte[] profilePic;

        private String imageType;

        @ManyToMany(fetch = FetchType.EAGER)
        @JoinTable(name = "users_roles", joinColumns = {
                        @JoinColumn(name = "user_id") }, inverseJoinColumns = {
                                        @JoinColumn(name = "role_id") })
        private Collection<RoleModel> roles;

        @OneToMany(fetch = FetchType.LAZY, mappedBy = "userModel", cascade = CascadeType.ALL)
        private List<ImageModel> images;

}
