package com.smart_contact_manager.Helper;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

public class Helper {
    public static String getEmailOfLoggedInUser(Authentication authentication){
        if(authentication instanceof OAuth2AuthenticationToken){

            var oauth2AuthenticationToken = (OAuth2AuthenticationToken)authentication;
            String clientId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
            var oAuth2User = (DefaultOAuth2User)authentication.getPrincipal();
            String username = "";

            if(clientId.equalsIgnoreCase("google")){
                // Fetch email(username), if we sing in with Google
                System.out.println("Getting email from google");
                username = oAuth2User.getAttribute("email").toString();
            } else if(clientId.equalsIgnoreCase("github")){
                // Fetch email(username), if we sing in with GitHub
                System.out.println("Getting email from gitHub");
                username = oAuth2User.getAttribute("email") != null ? oAuth2User.getAttribute("email").toString()
                        : oAuth2User.getAttribute("login").toString() + "@gmail.com";
            }
            return username;
        }else{
            // Fetch email(username), if we sing in with email and password
            System.out.println("Getting data from local database");
            return authentication.getName();
        }
    }
}
