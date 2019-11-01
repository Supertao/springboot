package com.springboot.controller;
import org.apache.catalina.core.StandardWrapper;
import org.apache.catalina.core.StandardWrapperFacade;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class UserController {
    @RequestMapping(value = "/")
    public String index() {
        return "index";
    }
    @RequestMapping(value = "/login")
    public String user() {
        System.out.println("controller");


        return "admin";
    }

}
