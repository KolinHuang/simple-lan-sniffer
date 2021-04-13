package com.hyc.interfaces;

import com.hyc.dto.MailDto;

/**
 * @author kol Huang
 * @date 2021/4/13
 */
public interface IMailService {
    void sendMail(MailDto dto);
}
