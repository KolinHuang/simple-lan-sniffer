package com.hyc.backend.service;


import com.hyc.backend.dao.RedisMapper;
import com.hyc.backend.redis.IDSKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import weka.experiment.InstanceQuery;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * 入侵检测服务：独立出来成为微服务
 * @author kol Huang
 * @date 2021/4/7
 */
@Service
public class IDSService {


    //当被判别为异常的数据包个数超过100个，就进入告警
    private static final Long THRESHOLD = 100L;

    @Autowired
    MailService mailService;

    void attackDetecting(){
        //1. 从mysql中取数据
        //2. 数据转化为arff文件，作为验证集
        Instances data = null;
        try{
            InstanceQuery query = new InstanceQuery();
            query.setUsername("root");
            query.setPassword("123456");
            query.setQuery("select * from features");
            data = query.retrieveInstances();
        }catch (Exception e){
            e.printStackTrace();
        }
        //4. 模型反序列化到对象
        J48 tree = null;
        try {
            tree = (J48) SerializationHelper.read("src/model/j48.model");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //5. 用模型二分类，如果在某段时间内出现过多的异常流量，就发送邮件告警
        int cnt = 0;
        for (Instance instance : data) {
            try {
                cnt += (int)tree.classifyInstance(instance);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(cnt > THRESHOLD){
            mailService.sendMail();
        }
    }
}
