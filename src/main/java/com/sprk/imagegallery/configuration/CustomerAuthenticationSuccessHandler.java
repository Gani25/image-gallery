package com.sprk.imagegallery.configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.sprk.imagegallery.model.RoleModel;
import com.sprk.imagegallery.model.UserModel;
import com.sprk.imagegallery.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
public class CustomerAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    private static final String DEFAULT_PASSWORD = "DEMO@123";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String userEmail;
        String username = null;
        byte[] profilePic = null;
        String imageType = "image/jpeg";

        if (authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            userEmail = oidcUser.getEmail();
            username = oidcUser.getAttribute("name");
            String picture = oidcUser.getAttribute("picture");
            profilePic = fetchProfilePicture(picture);

        } else {

            userEmail = authentication.getName();
        }

        UserModel dbUser = userService.getUserByEmail(userEmail);
        if (dbUser == null) {
            dbUser = new UserModel();
            dbUser.setUserName(username);
            dbUser.setEmail(userEmail);
            dbUser.setProfilePic(ImageUtil.compressImage(profilePic));
            dbUser.setImageType(imageType);
            dbUser.setPassword(encoder.encode(DEFAULT_PASSWORD));
            dbUser = userService.saveUser(dbUser);

        } else {

        }
        Collection<SimpleGrantedAuthority> authorities = mapRolesToAuthorities(dbUser.getRoles());
        User userDetails = new User(dbUser.getEmail(), dbUser.getPassword(), authorities);
        Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        HttpSession session = request.getSession();
        session.setAttribute("user", dbUser);

        response.sendRedirect(request.getContextPath() + "/imagegallery/user/image/images");

        // request.getRequestDispatcher("/user/image/images").forward(request,
        // response); this will just forward ur request
        // and on refresh again your url will hit login authentication

    }

    private byte[] fetchProfilePicture(String pictureUrl) {
        try {

            URI uri = new URI(pictureUrl);
            URL url = uri.toURL();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedImage image = ImageIO.read(url);
            ImageIO.write(image, "jpeg", baos);
            return baos.toByteArray();
        } catch (Exception e) {
            return null;
        }
    }

    private Collection<SimpleGrantedAuthority> mapRolesToAuthorities(Collection<RoleModel> roles) {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (RoleModel role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

}
