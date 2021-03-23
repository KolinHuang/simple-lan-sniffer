package com.hyc.backend.dao;

import com.sun.org.apache.xpath.internal.operations.String;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author kol Huang
 * @date 2021/3/22
 */
@SpringBootTest
public class RedisMapperTest {

    @Resource
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Test
    public void test(){
        List clientList = redisTemplate.getClientList();
        for (Object o : clientList) {
            System.out.println(o);
        }
        redisTemplate.opsForValue().set("string", new User());
        System.out.println(redisTemplate.opsForValue().get("string"));
    }

    @Test
    public void jedisTest(){
    }
}
