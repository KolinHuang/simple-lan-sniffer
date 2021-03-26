package com.hyc.backend.redis;

/**
 * @author kol Huang
 * @date 2021/3/23
 */
public class AttackKey extends BasePrefix  {

    public AttackKey(String prefix) {
        super(prefix);
    }

    public static AttackKey config = new AttackKey("conf_");
    public static AttackKey domainip = new AttackKey("domainip_");
}
