package com.hyc.backend.dao;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyc.backend.redis.CommonKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author kol Huang
 * @date 2021/3/22
 */
@SpringBootTest
public class RedisMapperTest {

    @Autowired
//    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        TargetObj to = new TargetObj();
        to.setId(1);
        to.setName("toname");
        Son son = new Son();
        son.setFatherName("fatherName");
        son.setSonName("sonName");
        to.setProperty(son);
        redisTemplate.opsForValue().set("test", to);
        ObjectMapper om = new ObjectMapper();
        HashMap<Object, Object> test = (HashMap<Object, Object>) redisTemplate.opsForValue().get("test");

        System.out.println(test instanceof HashMap);

//        TargetObj test = om.convertValue(redisTemplate.opsForValue().get("test"), TargetObj.class);
        System.out.println(test);
        Class<?> clazz = Class.forName("com.hyc.backend.dao.TargetObj");
        TargetObj ot2 = (TargetObj) clazz.newInstance();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Object value = test.get(declaredField.getName());
            if(value instanceof Map){
                Object fieldObj = declaredField.getType().newInstance();
                declaredField.setAccessible(true);
                Field[] declaredFields1 = declaredField.getType().getDeclaredFields();
                for (Field field : declaredFields1) {
                    field.setAccessible(true);
                    field.set(fieldObj, ((Map) value).get(field.getName()));
                }
                char[] chars = declaredField.getName().toCharArray();
                chars[0] -= 32;

                Method method = clazz.getDeclaredMethod("set".concat(String.valueOf(chars)), Father.class);
                method.invoke(ot2, fieldObj);
            }else{
                declaredField.setAccessible(true);
                declaredField.set(ot2, value);
            }

        }
        System.out.println(ot2);
        Father father = ot2.getProperty();
        Son property = (Son) father;
        System.out.println(property);
    }

    @Test
    public void jedisTest(){
    }
}
