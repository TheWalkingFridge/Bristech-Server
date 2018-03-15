package com.bristech.service;

import com.bristech.controllers.UserController;
import com.bristech.entities.User;
import com.bristech.repositories.UserRepository;
import com.google.common.base.Strings;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.criterion.NotNullExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserServiceDefault implements UserService {

    private static final Logger LOGGER = LogManager.getLogger(UserService.class);

    private final UserRepository userRepo;
    private final FirebaseAuth firebase;


    @Autowired
    public UserServiceDefault(UserRepository userRepo, FirebaseAuth firebase) {
        this.userRepo = userRepo;
        this.firebase = firebase;
    }


    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getOrCreateUserFromToken(String token) {
        User user = null;

        try {
            FirebaseToken decodedToken = firebase.verifyIdTokenAsync(token).get();

            String email = decodedToken.getEmail();
            user = getUserFromEmail(email);

            if(user == null){
                String name = decodedToken.getName();
                String backdrop = decodedToken.getPicture();

                user = new User(email, name, backdrop);
                createUser(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.warn("An error occur while getting user from token:" + token);
        }

        return user;
    }

    @Override
    public User getUserFromEmail(String email) {
        User user;
        if(!Strings.isNullOrEmpty(email)){
            LOGGER.warn("Email is empty");
            throw new IllegalArgumentException("Email is empty");
        }

        user = userRepo.getUserByEmail(email);
        if(user == null){
            LOGGER.warn("User with email:"+ email + ", was not found");
            throw new IllegalArgumentException("User with email:"+ email + ", was not found");
        }

        return user;
    }

    @Override
    public void createUser(User user) {

        if(user == null){
            LOGGER.error("User not saved. User is empty");
            throw new IllegalArgumentException("User is empty");
        }

        userRepo.save(user);
    }
}
