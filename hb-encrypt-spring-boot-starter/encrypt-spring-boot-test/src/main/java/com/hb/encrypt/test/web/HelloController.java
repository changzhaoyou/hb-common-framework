package com.hb.encrypt.test.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ycz on  2021/08/22/5:33 下午
 */
@RestController
public class HelloController {

    @GetMapping("/get")
    @ResponseBody
    public String get(HttpServletRequest httpServletRequest,String businessStatus) {
        return businessStatus;
    }
    @PostMapping("/getUser")
    @ResponseBody
    public User getUser(User user,HttpServletRequest httpServletRequest) {
        return user;
    }

}
