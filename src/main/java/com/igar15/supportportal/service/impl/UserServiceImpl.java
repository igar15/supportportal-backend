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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findUserByUserName(userName);
        if (user == null) {
            logger.error("User not found by username: {}", userName);
            throw new UsernameNotFoundException("User not found by username: " + userName);
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
        validateNewUsernameAndEmail("", userName, email); // this is a register method, so we put empty string instead of currentUsername
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
        User save = userRepository.save(user);
        logger.info("New user password: " + password);
        return null;
    }

    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    private String generateUserId() {
        return RandomStringUtils.randomNumeric(10);
    }

    private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail) {
        if (!currentUsername.isBlank()) {
            User currentUser = userRepository.findUserByUserName(currentUsername);
            if (currentUser == null) {
                throw new UserNotFoundException("No user found with username " + currentUsername);
            }
            User userByUserName = userRepository.findUserByUserName(newUsername);
            if (userByUserName != null && !currentUser.getId().equals(userByUserName.getId())) {
                throw new UsernameExistException("Username already exists");
            }
            User userByEmail = userRepository.findUserByEmail(newEmail);
            if (userByEmail != null && !currentUser.getId().equals(userByEmail.getId())) {
                throw new EmailExistException("Email already exists");
            }
            return currentUser;
        }
        else {
            User userByUserName = userRepository.findUserByUserName(newUsername);
            if (userByUserName != null) {
                throw new UsernameExistException("Username already exists");
            }
            User userByEmail = userRepository.findUserByEmail(newEmail);
            if (userByEmail != null) {
                throw new EmailExistException("Email already exists");
            }
            return null;
        }
    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    @Override
    public User findUserByUsername(String username) {
        return null;
    }

    @Override
    public User findUserByEmail(String email) {
        return null;
    }
}
