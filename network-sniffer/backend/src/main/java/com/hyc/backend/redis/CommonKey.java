package com.hyc.backend.redis;

/**
 * @author kol Huang
 * @date 2021/3/25
 */
public class CommonKey extends BasePrefix{
    public CommonKey(String prefix) {
        super(prefix);
    }

    public static final CommonKey COMMON_KEY = new CommonKey("common_");
}
