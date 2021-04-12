package com.hyc.pojo;

/**
 * @author kol Huang
 * @date 2021/4/9
 */

public class Features {
    //TCP连接基本特征
    //TCP持续时间，默认设为0，因为无法获取
    private int duration = 0;
    //协议类型：TCP、UDP、ICMP
    private String protocol_type;
    //目标主机的网络服务类型：70种！
    private String service;
    //连接正常或错误的状态：11种
    private String flag;
    //从源主机到目标主机的数据的字节数
    private int src_byte;
    //从目标主机到源主机的数据的字节数
    private int dst_byte;
    //若连接来自/送达同一个主机/端口则为1，否则为0，检查是否是land dos攻击
    private byte land;
    //错误分段的数量，连续类型，范围是 [0, 3]
    private byte wrong_fragment;
    //加急包的个数，连续类型，范围是[0, 14]。
    private byte urgent;
    //访问系统敏感文件和目录的次数，连续，范围是 [0, 101]

    //TCP连接内容特征
    private byte hot;
    //登录尝试失败的次数。连续，[0, 5]。
    private byte num_failed_logins;
    //成功登录则为1，否则为0，离散，0或1。
    private byte logged_in;
    //compromised条件（**）出现的次数，连续，[0, 7479]。
    private int num_compromised;
    //若获得root shell 则为1，否则为0，离散，0或1。root_shell是指获得超级用户权限。
    private byte root_shell;
    // 若出现”su root” 命令则为1，否则为0，离散，0或1。
    private byte su_attempted;
    //root用户访问次数，连续，[0, 7468]。
    private int num_root;
    //文件创建操作的次数，连续，[0, 100]。
    private byte num_file_creations;
    //一个FTP会话中出站连接的次数，连续，0。数据集中这一特征出现次数为0。
    private byte num_outbound_cmds;
    //登录是否属于“hot”列表（***），是为1，否则为0，离散，0或1。例如超级用户或管理员登录。
    private byte is_hot_login;
    //若是guest 登录则为1，否则为0，离散，0或1。
    private byte is_guest_login;

    //基于主机的网络流量统计特征
    //前100个连接中，与当前连接具有相同目标主机的连接数，连续，[0, 255]。
    private int dst_host_count;
    //前100个连接中，与当前连接具有相同目标主机相同服务的连接数，连续，[0, 255]。
    private int dst_host_srv_count;
    //前100个连接中，与当前连接具有相同目标主机相同服务的连接所占的百分比，连续，[0.00, 1.00]。
    private double dst_host_same_srv_rate;
    //前100个连接中，与当前连接具有相同目标主机不同服务的连接所占的百分比，连续，[0.00, 1.00]。
    private double dst_host_diff_srv_rate;
    //前100个连接中，与当前连接具有相同目标主机相同源端口的连接所占的百分比，连续，[0.00, 1.00]。
    private double dst_host_same_src_port_rate;
    //前100个连接中，与当前连接具有相同目标主机相同服务的连接中，与当前连接具有不同源主机的连接所占的百分比，连续，[0.00, 1.00]。
    private double dst_host_srv_diff_host_rate;
    //前100个连接中，与当前连接具有相同目标主机的连接中，出现SYN错误的连接所占的百分比，连续，[0.00, 1.00]。
    private double dst_host_serror_rate;
    //前100个连接中，与当前连接具有相同目标主机相同服务的连接中，出现SYN错误的连接所占的百分比，连续，[0.00, 1.00]。
    private double dst_host_srv_serror_rate;
    //前100个连接中，与当前连接具有相同目标主机的连接中，出现REJ错误的连接所占的百分比，连续，[0.00, 1.00]。
    private double dst_host_rerror_rate;
    //前100个连接中，与当前连接具有相同目标主机相同服务的连接中，出现REJ错误的连接所占的百分比，连续，[0.00, 1.00]。
    private double dst_host_srv_rerror_rate;


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getProtocol_type() {
        return protocol_type;
    }

    public void setProtocol_type(String protocol_type) {
        this.protocol_type = protocol_type;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getSrc_byte() {
        return src_byte;
    }

    public void setSrc_byte(int src_byte) {
        this.src_byte = src_byte;
    }

    public int getDst_byte() {
        return dst_byte;
    }

    public void setDst_byte(int dst_byte) {
        this.dst_byte = dst_byte;
    }

    public byte getLand() {
        return land;
    }

    public void setLand(byte land) {
        this.land = land;
    }

    public byte getWrong_fragment() {
        return wrong_fragment;
    }

    public void setWrong_fragment(byte wrong_fragment) {
        this.wrong_fragment = wrong_fragment;
    }

    public byte getUrgent() {
        return urgent;
    }

    public void setUrgent(byte urgent) {
        this.urgent = urgent;
    }

    public byte getHot() {
        return hot;
    }

    public void setHot(byte hot) {
        this.hot = hot;
    }

    public byte getNum_failed_logins() {
        return num_failed_logins;
    }

    public void setNum_failed_logins(byte num_failed_logins) {
        this.num_failed_logins = num_failed_logins;
    }

    public byte getLogged_in() {
        return logged_in;
    }

    public void setLogged_in(byte logged_in) {
        this.logged_in = logged_in;
    }

    public int getNum_compromised() {
        return num_compromised;
    }

    public void setNum_compromised(int num_compromised) {
        this.num_compromised = num_compromised;
    }

    public byte getRoot_shell() {
        return root_shell;
    }

    public void setRoot_shell(byte root_shell) {
        this.root_shell = root_shell;
    }

    public byte getSu_attempted() {
        return su_attempted;
    }

    public void setSu_attempted(byte su_attempted) {
        this.su_attempted = su_attempted;
    }

    public int getNum_root() {
        return num_root;
    }

    public void setNum_root(int num_root) {
        this.num_root = num_root;
    }

    public byte getNum_file_creations() {
        return num_file_creations;
    }

    public void setNum_file_creations(byte num_file_creations) {
        this.num_file_creations = num_file_creations;
    }

    public byte getNum_outbound_cmds() {
        return num_outbound_cmds;
    }

    public void setNum_outbound_cmds(byte num_outbound_cmds) {
        this.num_outbound_cmds = num_outbound_cmds;
    }

    public byte getIs_hot_login() {
        return is_hot_login;
    }

    public void setIs_hot_login(byte is_hot_login) {
        this.is_hot_login = is_hot_login;
    }

    public byte getIs_guest_login() {
        return is_guest_login;
    }

    public void setIs_guest_login(byte is_guest_login) {
        this.is_guest_login = is_guest_login;
    }

    public int getDst_host_count() {
        return dst_host_count;
    }

    public void setDst_host_count(int dst_host_count) {
        this.dst_host_count = dst_host_count;
    }

    public int getDst_host_srv_count() {
        return dst_host_srv_count;
    }

    public void setDst_host_srv_count(int dst_host_srv_count) {
        this.dst_host_srv_count = dst_host_srv_count;
    }

    public double getDst_host_same_srv_rate() {
        return dst_host_same_srv_rate;
    }

    public void setDst_host_same_srv_rate(double dst_host_same_srv_rate) {
        this.dst_host_same_srv_rate = dst_host_same_srv_rate;
    }

    public double getDst_host_diff_srv_rate() {
        return dst_host_diff_srv_rate;
    }

    public void setDst_host_diff_srv_rate(double dst_host_diff_srv_rate) {
        this.dst_host_diff_srv_rate = dst_host_diff_srv_rate;
    }

    public double getDst_host_same_src_port_rate() {
        return dst_host_same_src_port_rate;
    }

    public void setDst_host_same_src_port_rate(double dst_host_same_src_port_rate) {
        this.dst_host_same_src_port_rate = dst_host_same_src_port_rate;
    }

    public double getDst_host_srv_diff_host_rate() {
        return dst_host_srv_diff_host_rate;
    }

    public void setDst_host_srv_diff_host_rate(double dst_host_srv_diff_host_rate) {
        this.dst_host_srv_diff_host_rate = dst_host_srv_diff_host_rate;
    }

    public double getDst_host_serror_rate() {
        return dst_host_serror_rate;
    }

    public void setDst_host_serror_rate(double dst_host_serror_rate) {
        this.dst_host_serror_rate = dst_host_serror_rate;
    }

    public double getDst_host_srv_serror_rate() {
        return dst_host_srv_serror_rate;
    }

    public void setDst_host_srv_serror_rate(double dst_host_srv_serror_rate) {
        this.dst_host_srv_serror_rate = dst_host_srv_serror_rate;
    }

    public double getDst_host_rerror_rate() {
        return dst_host_rerror_rate;
    }

    public void setDst_host_rerror_rate(double dst_host_rerror_rate) {
        this.dst_host_rerror_rate = dst_host_rerror_rate;
    }

    public double getDst_host_srv_rerror_rate() {
        return dst_host_srv_rerror_rate;
    }

    public void setDst_host_srv_rerror_rate(double dst_host_srv_rerror_rate) {
        this.dst_host_srv_rerror_rate = dst_host_srv_rerror_rate;
    }
}
