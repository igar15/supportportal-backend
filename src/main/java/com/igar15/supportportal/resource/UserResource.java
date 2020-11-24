package com.igar15.supportportal.resource;

import com.igar15.supportportal.exception.domain.EmailExistException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserResource {

    @GetMapping("/home")
    public String test() {
        throw new EmailExistException("Such email exists");
    }

}
