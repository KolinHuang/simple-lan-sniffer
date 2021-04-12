package com.hyc.sampleprovider.service;

import com.hyc.interfaces.IHelloService;
import org.apache.dubbo.config.annotation.Service;

/**
 * @author kol Huang
 * @date 2021/4/12
 */
@Service
public class HelloServiceImpl implements IHelloService {
    @Override
    public String sayHello(String name) {
        return name + ":rpc";
    }
}
