package com.hyc.backend.service;


import com.hyc.backend.dao.RedisMapper;
import com.hyc.backend.redis.IDSKey;
import com.hyc.interfaces.IIDSService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import weka.experiment.InstanceQuery;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * 入侵检测服务：独立出来成为微服务
 * @author kol Huang
 * @date 2021/4/7
 */
public class IDSService {


    //当被判别为异常的数据包个数超过100个，就进入告警
    private static final Long THRESHOLD = 100L;


    @Reference
    IIDSService idsService;

    //用来记录当前数据段的主键偏移量，例如当前查出了121～220区间内的特征数据，那么偏移量就更新为221
    long offset;

    @PostConstruct
    void initIDSService(){
        //在初始化web项目的时候，就开启一条低优先级的线程，间隔10秒钟向数据库发起一段查询
        //查询100条流量特征数据
        //1. 如果不够100条怎么办？ 有多少查多少

    }





//    void attackDetecting(){
//        //1. 从mysql中取数据
//        //2. 数据转化为arff文件，作为验证集
//        Instances data = null;
//        try{
//            InstanceQuery query = new InstanceQuery();
//            query.setUsername("root");
//            query.setPassword("123456");
//            query.setQuery("select * from features");
//            data = query.retrieveInstances();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        //4. 模型反序列化到对象
//        J48 tree = null;
//        try {
//            tree = (J48) SerializationHelper.read("src/model/j48.model");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        //5. 用模型二分类，如果在某段时间内出现过多的异常流量，就发送邮件告警
//        int cnt = 0;
//        for (Instance instance : data) {
//            try {
//                cnt += (int)tree.classifyInstance(instance);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        if(cnt > THRESHOLD){
//            mailService.sendMail();
//        }
//    }
}
