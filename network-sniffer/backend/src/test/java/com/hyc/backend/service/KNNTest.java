package com.hyc.backend.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import weka.classifiers.lazy.IBk;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.ManhattanDistance;
import weka.core.SelectedTag;
import weka.core.converters.ArffLoader;
import weka.core.neighboursearch.LinearNNSearch;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;

import java.io.File;
import java.io.IOException;

/**
 * @author kol Huang
 * @date 2021/4/7
 */
@SpringBootTest
public class KNNTest {

    @Test
    public void knnTest() throws Exception {

        Instances newInstances = null;
        Instances instances = loadArfFile("src/main/resources/dataset/test.arff");
        instances.setClassIndex(instances.numAttributes() - 1);
        Instances testInstances = loadArfFile("src/main/resources/dataset/yuce.arff");

        testInstances.setClassIndex(testInstances.numAttributes() - 1);
        Instances newTestInstances = null;
        try {
            Normalize norm1 = new Normalize();
            norm1.setInputFormat(instances);
            //正则化
            newInstances = Filter.useFilter(instances, norm1);
            Normalize norm2 = new Normalize();
            norm2.setInputFormat(testInstances);
            newTestInstances = Filter.useFilter(testInstances, norm2);
        }catch (Exception e){
            e.printStackTrace();
        }
        //获得分类器
        IBk classifier = new IBk();
        classifier.setCrossValidate(true);
        classifier.setKNN(5);
//        classifier.setBatchSize("100");
        LinearNNSearch lknn = new LinearNNSearch();
        try {
            lknn.setDistanceFunction(new ManhattanDistance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        classifier.setNearestNeighbourSearchAlgorithm(lknn);
        classifier.buildClassifier(newInstances);
        System.out.println(classifier);
        Instance instance = newTestInstances.instance(2);
        double res = classifier.classifyInstance(instance);
        System.out.println(res);
    }

    public Instances loadArfFile(String path){
        Instances data = null;
        try{
            ArffLoader loader = new ArffLoader();
            loader.setSource(new File(path));
            data = loader.getDataSet();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
