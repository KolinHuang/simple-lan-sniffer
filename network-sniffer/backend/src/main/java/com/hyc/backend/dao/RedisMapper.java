package com.hyc.backend.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hyc.backend.redis.KeyPrefix;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * @author kol Huang
 * @date 2021/3/22
 */
@Component
public class RedisMapper {

    @Resource
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 普通缓存获取
     * @param key 键
     * @return 值
     */
    public Object get(KeyPrefix prefix, String key, Class<?> clazz){
        String realKey = prefix.getPrefix().concat(key);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(redisTemplate.opsForValue().get(realKey), clazz);
    }

    /**
     * 普通缓存放入
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(KeyPrefix prefix, String key,Object value) {
        try {
            String realKey = prefix.getPrefix().concat(key);
            redisTemplate.opsForValue().set(realKey, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(KeyPrefix prefix, String key,Object value,long time){
        try {
            if(time > 0){
                String realKey = prefix.getPrefix().concat(key);
                redisTemplate.opsForValue().set(realKey, value, time, TimeUnit.SECONDS);
            }else{
                set(prefix, key, value);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除缓存
     * @param key 可以传一个值或多个
     */
    public void del(KeyPrefix prefix, String ... key){
        if(key != null && key.length > 0){
            if(key.length == 1){
                String realKey = prefix.getPrefix().concat(key[0]);
                redisTemplate.delete(realKey);
            }else{
                //加前缀
                Collection<String> keys = new ArrayList<>();
                for (String s : key) {
                    keys.add(prefix.getPrefix().concat(s));
                }
                redisTemplate.delete(keys);
            }
        }
    }

    /**
     * 如果不存在这个key就设置他
     * @param key
     * @param value
     * @return
     */
    public Boolean setnx(KeyPrefix prefix, String key, Object value){
        try{
            String realKey = prefix.getPrefix().concat(key);
            return redisTemplate.opsForValue().setIfAbsent(realKey, value);
        }catch (Exception e){
            e.printStackTrace();
            return Boolean.FALSE;
        }

    }

}
