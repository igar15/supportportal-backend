package com.igar15.supportportal.resource;

import com.igar15.supportportal.domain.User;
import com.igar15.supportportal.exception.domain.EmailExistException;
import com.igar15.supportportal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserResource {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUserName(), user.getEmail());
        return new ResponseEntity<>(newUser, HttpStatus.OK);
    }

}
