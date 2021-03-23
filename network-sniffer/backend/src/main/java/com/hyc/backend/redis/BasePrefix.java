package com.hyc.backend.redis;

/**
 * @author kol Huang
 * @date 2021/3/23
 */
public class BasePrefix implements KeyPrefix{

    private String prefix;

    public BasePrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className.concat(".").concat(prefix);
    }
}
