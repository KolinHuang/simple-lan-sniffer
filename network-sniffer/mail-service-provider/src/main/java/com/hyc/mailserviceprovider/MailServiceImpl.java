package com.hyc.mailserviceprovider;

import com.hyc.dto.MailDto;
import com.hyc.interfaces.IMailService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;

/**
 * @author kol Huang
 * @date 2021/4/13
 */
@Service
public class MailServiceImpl implements IMailService {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment env;
    @Override
    public void sendMail(MailDto dto) {
        try{
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true,"utf-8");
            messageHelper.setFrom(env.getProperty("mail.send.from"));
            messageHelper.setTo(dto.getTos());
            messageHelper.setSubject(dto.getSubject());
            messageHelper.setText(dto.getContent(),true);
            mailSender.send(message);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
