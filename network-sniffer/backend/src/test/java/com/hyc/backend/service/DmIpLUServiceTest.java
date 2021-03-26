package com.hyc.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

/**
 * @author kol Huang
 * @date 2021/3/26
 */
@SpringBootTest
public class DmIpLUServiceTest {


    @Autowired
    DmIpLookUpService dmIpLookUpService;

    @Test
    public void test(){
        dmIpLookUpService.ipLookUp(1010, "www.baidu.com");
        Set<String> allIps = dmIpLookUpService.getAllIps(1010);
        for (String allIp : allIps) {
            System.out.println(allIp);
        }
    }


}
