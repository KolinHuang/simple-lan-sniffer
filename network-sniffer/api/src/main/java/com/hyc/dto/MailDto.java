package com.hyc.dto;


import java.util.Arrays;

/**
 * @author kol Huang
 * @date 2020/11/20
 */


public class MailDto {

    //邮件主题
    private String subject;
    //邮件内容
    private String content;
    //接收人
    private String[] tos;

    public MailDto() {
    }

    public MailDto(String subject, String content, String[] tos) {
        this.subject = subject;
        this.content = content;
        this.tos = tos;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getTos() {
        return tos;
    }

    public void setTos(String[] tos) {
        this.tos = tos;
    }


    @Override
    public String toString() {
        return "MailDto{" +
                "subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", tos=" + Arrays.toString(tos) +
                '}';
    }
}
