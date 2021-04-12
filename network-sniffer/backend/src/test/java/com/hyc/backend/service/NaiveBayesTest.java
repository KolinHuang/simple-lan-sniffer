package com.hyc.backend.service;

import com.hyc.backend.dao.RedisMapper;
import com.hyc.backend.redis.IDSKey;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.OutputStream;


/**
 * @author kol Huang
 * @date 2021/4/8
 */
@SpringBootTest
public class NaiveBayesTest {

    @Resource
    RedisMapper redisMapper;

    @Test
    public void test() throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource("src/main/resources/dataset/train_norm.arff");
        Instances dataSet = source.getDataSet();
        dataSet.setClassIndex(dataSet.numAttributes() - 1);
        NaiveBayes nb = new NaiveBayes();
        nb.buildClassifier(dataSet);
        System.out.println(nb.getCapabilities().toString());
        ConverterUtils.DataSource source1 = new ConverterUtils.DataSource("src/main/resources/dataset/test_norm.arff");
        Instances testDS = source1.getDataSet();
        testDS.setClassIndex(testDS.numAttributes() - 1);
        double res = nb.classifyInstance(testDS.instance(0));
        System.out.println(res);
//
//        SMO svm = new SMO();
//        svm.buildClassifier(dataSet);
//        System.out.println(svm.getCapabilities().toString());
//
        String[] options = new String[4];
        options[0] = "-C";
        options[1] = "0.25";
        options[2] = "-M";
        options[3] = "2";
        J48 tree = new J48();
        tree.setOptions(options);
        tree.buildClassifier(dataSet);
//        System.out.println(tree.getCapabilities().toString());
//        System.out.println(tree.graph());
//        for(int i = 0; i < testDS.size(); ++i){
//            Instance instance = testDS.instance(i);
//            double val = tree.classifyInstance(instance);
//            System.out.println(val);
//        }

        //序列化模型
        File file = new File("src/model/j48.model");
        if(!file.exists()){
            file.createNewFile();
        }

        SerializationHelper.write("src/model/j48.model", tree);

        J48 readTree = (J48) SerializationHelper.read("src/model/j48.model");
        readTree.classifyInstance(testDS.instance(0));
    }
}
