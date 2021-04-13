package com.hyc.backend.controller;

import com.hyc.interfaces.IHelloService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author kol Huang
 * @date 2021/4/12
 */
@Controller
public class HelloController {

//    @Reference
//    IHelloService helloService;
//
//    @RequestMapping("/hello")
//    public String test(){
//        String res = helloService.sayHello("hyc");
//        return res;
//    }
}
