package com.hyc.idsserviceprovider.impl;

import com.hyc.interfaces.IIDSService;
import org.apache.dubbo.config.annotation.Service;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.experiment.InstanceQuery;

/**
 * @author kol Huang
 * @date 2021/4/12
 */
@Service
public class IDSServiceImpl implements IIDSService {

    int[] table = new int[41];

    @Override
    public boolean isAttacked() {
        //判断是否被攻击
        //1. 从mysql中查询验证集
//        ConverterUtils.DataSource source = null;
        Instances data = null;
        try {
            InstanceQuery query = new InstanceQuery();
            query.setUsername("root");
            query.setPassword("123456");
            //读100条，先处理
            query.setQuery("select * from features");
            data = query.retrieveInstances();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2. 读取模型
        J48 j48Tree = null;
        try {
            j48Tree = (J48) SerializationHelper.read("src/model/j48.model");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //3. 多分类
        for (Instance datum : data) {
            try {
                int type = (int) j48Tree.classifyInstance(datum);
                table[type]++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //4. 统计判别：当此次分析过程中，有百分之80的数据记录被判别为攻击流量，那么就说明发生了攻击
        int sum = 0;
        int norm = table[0];
        for(int i = 0; i < table.length; ++i){
            sum += table[i];
        }
        double at_rate = norm * 0.1 / sum;

        return at_rate >= 0.8;
    }
}
