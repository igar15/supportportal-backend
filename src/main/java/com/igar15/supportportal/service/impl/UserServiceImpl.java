package com.igar15.supportportal.service.impl;

import com.igar15.supportportal.domain.User;
import com.igar15.supportportal.domain.UserPrincipal;
import com.igar15.supportportal.enumeration.Role;
import com.igar15.supportportal.exception.domain.EmailExistException;
import com.igar15.supportportal.exception.domain.UserNotFoundException;
import com.igar15.supportportal.exception.domain.UsernameExistException;
import com.igar15.supportportal.repository.UserRepository;
import com.igar15.supportportal.service.UserService;
import net.bytebuddy.utility.RandomString;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String NO_USER_FOUND_WITH_USERNAME = "No user found with username";

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findUserByUserName(userName);
        if (user == null) {
            logger.error(NO_USER_FOUND_WITH_USERNAME + ": {}", userName);
            throw new UsernameNotFoundException(NO_USER_FOUND_WITH_USERNAME + " " + userName);
        }
        else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            logger.info("Returning found user by username: {}", userName);
            return userPrincipal;
        }
    }

    @Override
    public User register(String firstName, String lastName, String username, String email) {
        validateNewUsernameAndEmail("", username, email); // this is a register method, so we put empty string instead of currentUsername
        User user = new User();
        user.setUserId(generateUserId());
        String password = generatePassword();
        String encodedPassword = encodePassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUserName(username);
        user.setEmail(email);
        user.setJoinDate(new Date());
        user.setPassword(encodedPassword);
        user.setActive(true);
        user.setNotLocked(true);
        user.setRole(Role.ROLE_USER.name());
        user.setAuthorities(Role.ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl());
        userRepository.save(user);
        logger.info("New user password: " + password);
        return user;
    }

    private String getTemporaryProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/temp").toUriString();
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) {
        User userByNewUserName = userRepository.findUserByUserName(newUsername);
        User userByNewEmail = userRepository.findUserByEmail(newEmail);

        if (!currentUsername.isBlank()) {
            User currentUser = userRepository.findUserByUserName(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException(NO_USER_FOUND_WITH_USERNAME + " " + currentUsername);
            }
            if (userByNewUserName != null && !currentUser.getId().equals(userByNewUserName.getId())) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return currentUser;
        }
        else {
            if (userByNewUserName != null) {
                throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
            }
            if (userByNewEmail != null) {
                throw new EmailExistException(EMAIL_ALREADY_EXISTS);
            }
            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findUserByUserName(username);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }
}
