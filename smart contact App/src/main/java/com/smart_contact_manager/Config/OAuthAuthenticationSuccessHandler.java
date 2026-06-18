package com.smart_contact_manager.Config;

import com.smart_contact_manager.Enum.Providers;
import com.smart_contact_manager.Helper.AppConstants;
import com.smart_contact_manager.Model.UserModel;
import com.smart_contact_manager.Repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("OAuthAuthenticationSuccessHandler");

        // Identify the provider
        var oauth2AuthenticationToken = (OAuth2AuthenticationToken)authentication;
        String authorizedClientRegistrationId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
        logger.info(authorizedClientRegistrationId);

        DefaultOAuth2User oauthUser = (DefaultOAuth2User)authentication.getPrincipal();

        oauthUser.getAttributes().forEach((key, value) -> {
            logger.info(key + " : " + value);
        });

        UserModel user = new UserModel();
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setRoleList(List.of(AppConstants.ROLE_USER));

        if(authorizedClientRegistrationId.equalsIgnoreCase("google")){
            // Google
            // Google attributes
            String email = oauthUser.getAttribute("email").toString();
            String name = oauthUser.getAttribute("name").toString();
            String picture = oauthUser.getAttribute("picture").toString();
            String providerId = oauthUser.getName();

            user.setName(name);
            user.setEmail(email);
            user.setPassword("password");
            user.setAbout("This account is created using google...");
            user.setProfilePic(picture);
            user.setProvider(Providers.GOOGLE);
            user.setProviderUserId(providerId);

        } else if (authorizedClientRegistrationId.equalsIgnoreCase("github")){
            // GitHub
            // GitHub attributes
            String email = oauthUser.getAttribute("email") != null ? oauthUser.getAttribute("email").toString()
                    : oauthUser.getAttribute("login").toString() + "@gmail.com";
            String name = oauthUser.getAttribute("name") != null ? oauthUser.getAttribute("name").toString()
                    : oauthUser.getAttribute("login").toString();
            String picture = oauthUser.getAttribute("avatar_url").toString();
            String providerId = oauthUser.getName();

            user.setName(name);
            user.setEmail(email);
            user.setPassword("password");
            user.setAbout("This account is created using gitHub...");
            user.setProfilePic(picture);
            user.setProvider(Providers.GITHUB);
            user.setProviderUserId(providerId);
        } else {
            logger.info("OAuthAuthenticationSuccessHandler : Unknown provider");
        }

        // Save the user into database
        UserModel alreadySavedUser = userRepository.findByEmail(user.getEmail()).orElse(null);
        if(alreadySavedUser == null){
            userRepository.save(user);
        }

        /*
        // Save data into database
        DefaultOAuth2User user = (DefaultOAuth2User)authentication.getPrincipal();

//        logger.info(user.getName());
//        user.getAttributes().forEach((key, value) -> {
//            logger.info("{} => {}", key, value);
//        });
//        logger.info(user.getAuthorities().toString());

        String email = user.getAttribute("email").toString();
        String name = user.getAttribute("name").toString();
        String picture = user.getAttribute("picture").toString();

        UserModel savedUser = new UserModel();
        savedUser.setName(name);
        savedUser.setEmail(email);
        savedUser.setPassword("password");
        savedUser.setAbout("This account is created using google...");
        savedUser.setProfilePic(picture);
        savedUser.setEnabled(true);
        savedUser.setEmailVerified(true);
        savedUser.setProvider(Providers.GOOGLE);
        savedUser.setProviderUserId(user.getName());
        savedUser.setRoleList(List.of(AppConstants.ROLE_USER));

        UserModel alreadySavedUser = userRepository.findByEmail(email).orElse(null);
        if(alreadySavedUser == null){
            userRepository.save(savedUser);
            logger.info("User saved : " + email);
        }
        */

        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }
}
