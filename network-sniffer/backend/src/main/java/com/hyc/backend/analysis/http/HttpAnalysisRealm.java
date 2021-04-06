package com.hyc.backend.analysis.http;

import com.hyc.backend.analysis.HttpContentTypeEnum;
import com.hyc.backend.analysis.IAnalysisRealm;
import com.hyc.backend.analysis.ProtocolEnum;
import com.hyc.backend.packet.AbsAnalyzedPacket;
import com.hyc.backend.packet.AnalyzedHttpPacket;
import com.hyc.backend.packet.Packet;
import com.hyc.backend.packet.TCPPacket;
import com.hyc.backend.utils.Helper;
import com.hyc.backend.utils.NetworkUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author kol Huang
 * @date 2021/3/30
 */
public class HttpAnalysisRealm implements IAnalysisRealm {

    private static final String HEX_NEW_LINE = "0D0A0D0A";

    private static String HEADER_CONTENT_TYPE = "content-type";
    private static String HEADER_ENCODING = "content-encoding";
    private static String HEADER_ENCODING_GZIP = "gzip";

    protected Date time;
    protected boolean upstream;
    protected Integer batchId;
//    protected List<String> capturedPacketIds = new ArrayList<>();
    protected Long ackNum;
    protected boolean isGzip = false;
    protected String contentType;
    protected boolean hasHeader = false;
    protected Map<String, String> httpHeaders;
    protected byte[] contentBytes;
    protected String srcMac;
    protected String destMac;
    protected String srcIp;
    protected String destIp;

    @Override
    public String protocol() {
        return ProtocolEnum.HTTP.name();
    }

    /**
     * 初始化HttpAnalysisRealm，
     * @param batchId
     * @param upstream
     * @param packet
     */
    @Override
    public void initPacket(Integer batchId, boolean upstream, Packet packet) {
        this.batchId = batchId;
//        this.capturedPacketIds.add(capturePacketId);
        this.upstream = upstream;

        TCPPacket tcpPacket = (TCPPacket) packet;
        this.time = new Date(packet.getSec() * 1000L + packet.getUsec());
        this.ackNum = tcpPacket.getAckNum();
        this.srcIp = tcpPacket.getSrcIP().getHostAddress();
        this.destIp = tcpPacket.getDstIP().getHostAddress();
        String hex = NetworkUtils.bytesToHexString(tcpPacket.getData());
        if(tcpPacket.getEthernetPacket() != null){
            this.srcMac = tcpPacket.getEthernetPacket().readSourceAddress();
            this.destMac = tcpPacket.getEthernetPacket().readDestinationAddress();
        }
        if(hex != null){
            String contentHex = null;
            //由于HTTP协议请求头的结尾必须是"0D0A0D0A"即/r/n/r/n，所以可以根据这个来判断是否存在请求头
            int indexOfNewLine = hex.indexOf(HEX_NEW_LINE);
            if(indexOfNewLine > 0){
                this.hasHeader = true;
                String headerStr = hex.substring(0, indexOfNewLine);
                this.httpHeaders = NetworkUtils.readHttpHeadersByStr(new String(NetworkUtils.toBytes(headerStr)));
                if(this.httpHeaders != null){
                    this.contentType = this.httpHeaders.get(HEADER_CONTENT_TYPE);
                }
                //跳过/r/n/r/n，截取后面的子串作为请求体
                contentHex = hex.substring(indexOfNewLine + 8);
            }

            //unGzip content for gzip data解压缩
            //如果请求体的内容经过压缩，我们需要对其解压缩
            if(this.hasHeader && HEADER_ENCODING_GZIP.equals(httpHeaders.get(HEADER_ENCODING))){
                this.isGzip = true;
                String gzipContentHex = NetworkUtils.parseGzipContent(contentHex);
                this.contentBytes = NetworkUtils.toBytes(gzipContentHex);
            }else{
                this.isGzip = false;
                this.contentBytes = NetworkUtils.toBytes(contentHex);
            }
        }

    }

    @Override
    public void appendPacket( Packet packet) {
//        this.capturedPacketIds.add(capturePacketId);
        TCPPacket tcpPacket = (TCPPacket) packet;
        String hex = NetworkUtils.bytesToHexString(tcpPacket.getData());
        if(hex != null){
            String contentHex;
            int indexOfNewLine = hex.indexOf(HEX_NEW_LINE);
            if(indexOfNewLine > 0){
                contentHex = hex.substring(indexOfNewLine + 8);
            }else{
                contentHex = hex;
            }

            if(this.isGzip){
                String gzipContentHex = NetworkUtils.parseGzipContent(contentHex);
                byte[] newContentBytes = NetworkUtils.toBytes(gzipContentHex);
                this.contentBytes = NetworkUtils.concatBytes(this.contentBytes, newContentBytes);
            }else{
                this.contentBytes = NetworkUtils.concatBytes(this.contentBytes, NetworkUtils.toBytes(contentHex));
            }
        }

    }

    @Override
    public AbsAnalyzedPacket makePacket4Save() {
        byte[] realContentBytes;
        String realContent = null;
        realContentBytes = this.contentBytes;
        if(this.isGzip && this.contentBytes.length > 0){
            realContentBytes = NetworkUtils.unGzip(this.contentBytes);
        }
        if(realContentBytes != null)
            realContent = new String(realContentBytes, StandardCharsets.UTF_8);

        Boolean httpReq = null;
        String method = null;
        if(NetworkUtils.isHttpHeader(this.httpHeaders) && NetworkUtils.isRequestHttpHeader(this.httpHeaders)){
            httpReq = true;
            method = this.httpHeaders.get("METHOD");
        }else if(NetworkUtils.isHttpHeader(this.httpHeaders) && NetworkUtils.isResponseHttpHeader(this.httpHeaders)){
            httpReq = false;
            method = this.httpHeaders.get("METHOD");
        }

        AnalyzedHttpPacket analyzedHttpPacket = new AnalyzedHttpPacket();
        analyzedHttpPacket.setHttpReq(httpReq);
        analyzedHttpPacket.setMethod(method);
        analyzedHttpPacket.setTime(this.time);
        if (this.time != null) {
            analyzedHttpPacket.setMinuteTimeStr(Helper.MINUTE_DATE_FORMAT.format(this.time));
        }
        analyzedHttpPacket.setProtocol(this.protocol());
        analyzedHttpPacket.setUpstream(this.upstream);
        analyzedHttpPacket.setBatchId(this.batchId);
//        analyzedHttpPacket.setCapturedPacketIds(this.capturedPacketIds);
        analyzedHttpPacket.setAckNum(this.ackNum);
        analyzedHttpPacket.setHttpHeaders(this.httpHeaders);
        analyzedHttpPacket.setData(this.contentBytes);
        analyzedHttpPacket.setContent(realContent);
        analyzedHttpPacket.setCompressed(this.isGzip);
        analyzedHttpPacket.setSrcIp(this.srcIp);
        analyzedHttpPacket.setDstIp(this.destIp);
        analyzedHttpPacket.setSrcMac(this.srcMac);
        analyzedHttpPacket.setDstMac(this.destMac);
        HttpContentTypeEnum contentTypeEnum = HttpContentTypeEnum.extract(this.contentType);
        if (contentTypeEnum != null)
            analyzedHttpPacket.setContentType(contentTypeEnum.name());
        else {
            //如果没有content type，说明这是一个HTTP请求
            //如果没有方法参数，说明此body无法被解析，忽略它
            if (httpReq == null && method == null && this.httpHeaders == null) {
                System.out.println("------ignore http packet due to it's not complete: " + this.ackNum);
                return null;
            }
            analyzedHttpPacket.setContentType(HttpContentTypeEnum.HTML.name());
        }
        System.out.println("++++++save http packet: " + this.ackNum + ", " + analyzedHttpPacket.getContentType() + ", " + this.isGzip);
        return analyzedHttpPacket;
    }
}
