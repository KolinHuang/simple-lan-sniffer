package com.hyc.backend.controller;

import com.sun.org.apache.xpath.internal.operations.String;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author kol Huang
 * @date 2021/3/18
 */

@Controller
public class HelloController {

    @RequestMapping("/hello")
    @ResponseBody
    public String test(){
        return new String();
    }
}
