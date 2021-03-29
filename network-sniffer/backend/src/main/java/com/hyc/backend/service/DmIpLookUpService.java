package com.hyc.backend.service;

import com.hyc.backend.dao.RedisMapper;
import com.hyc.backend.redis.AttackKey;
import com.hyc.backend.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author kol Huang
 * @date 2021/3/26
 */
@Service
public class DmIpLookUpService {

    private static final Logger logger = LoggerFactory.getLogger(DmIpLookUpService.class);

    @Resource
    @Qualifier("redisMapper")
    private RedisMapper redisMapper;

    public void ipLookUp(Integer batchId, String filterDomain){
        if(!StringUtils.hasLength(filterDomain))    return;
        //先清空该批次的domain ip
        redisMapper.del(AttackKey.domainip, String.valueOf(batchId));
        String[] domains = filterDomain.split(",");
        Set<String> ips = new HashSet<>();
        for (String domain : domains) {
            Set<String> set = NetworkUtils.lookup(domain);
            if(set != null)
                ips.addAll(set);
        }
        redisMapper.set(AttackKey.domainip, String.valueOf(batchId), ips);
    }

    public Set<String> getAllIps(Integer batchId){
        return (Set<String>) redisMapper.get(AttackKey.domainip, String.valueOf(batchId), Set.class);
    }
}
