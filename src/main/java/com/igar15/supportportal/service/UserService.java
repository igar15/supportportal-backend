package com.igar15.supportportal.service;

import com.igar15.supportportal.domain.User;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.util.List;

public interface UserService {

    User register(String firstName, String lastName, String username, String email) throws MessagingException;

    List<User> getUsers();

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    User addNewUser(String firstName, String lastName, String username, String email, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage);

    User updateUser(String currentUsername, String newFirstName, String newLastName, String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage);

    void deleteUser(long id);

    void resetPassword(String email);

    User updateProfileImage(String username, MultipartFile profileImage);
}
