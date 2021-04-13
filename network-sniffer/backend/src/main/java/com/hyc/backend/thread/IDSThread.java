package com.hyc.backend.thread;

import com.hyc.backend.dao.FeatureMapper;
import com.hyc.dto.MailDto;
import com.hyc.interfaces.IIDSService;
import com.hyc.interfaces.IMailService;
import com.hyc.pojo.Features;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author kol Huang
 * @date 2021/4/13
 */
public class IDSThread extends Thread{

    @Reference
    private IIDSService idsService;

    @Reference
    private IMailService mailService;

    public IDSThread(int priority){
        if(priority < 1)    priority = 1;
        if(priority > 10)   priority = 10;
        setPriority(priority);
    }

    @Override
    public void run() {
        boolean res = idsService.isAttacked();
        if (res){
            String subject = "alert!";
            String content = "Your server may under attack!";
            String[] tos = {"xxxx@xx.com"};
            MailDto dto = new MailDto(subject, content, tos);
            mailService.sendMail(dto);
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
