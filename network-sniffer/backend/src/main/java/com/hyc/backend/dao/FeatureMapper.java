package com.hyc.backend.dao;

import com.hyc.pojo.Features;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author kol Huang
 * @date 2021/4/13
 */
@Mapper
public interface FeatureMapper {

    /**
     * 根据偏移量查询特征数据
     * @param offset
     * @return
     */
    List<Features> queryFeaturesByOffset(@Param("offset") long offset);
}
