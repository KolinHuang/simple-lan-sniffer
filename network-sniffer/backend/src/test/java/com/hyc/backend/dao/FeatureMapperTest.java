package com.hyc.backend.dao;

import com.hyc.pojo.Features;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author kol Huang
 * @date 2021/4/13
 */
@SpringBootTest
public class FeatureMapperTest {

    @Autowired
    FeatureMapper featureMapper;

    @Test
    public void test(){
        List<Features> features = featureMapper.queryFeaturesByOffset(1);
        for (Features feature : features) {
            System.out.println(feature);
        }
    }
}
