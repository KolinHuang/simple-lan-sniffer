package com.hyc.backend.redis;

/**
 * @author kol Huang
 * @date 2021/4/8
 */
public class IDSKey extends BasePrefix {
    public IDSKey(String prefix) {
        super(prefix);
    }

    public static final IDSKey j48Model = new IDSKey("classify_model_j48_");
}
