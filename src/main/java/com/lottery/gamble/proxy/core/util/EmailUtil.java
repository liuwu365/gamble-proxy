package com.lottery.gamble.proxy.core.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

/**
 * @author Created by 王亚平 on 2017/6/21.
 */
@Component
@EnableAsync
public class EmailUtil {

    Logger logger = LoggerFactory.getLogger(EmailUtil.class);


    @Value("${spring.mail.username}")
    private String form;

    @Value("${work.order.notify.subject}")
    private String default_subject;

    @Value("${work.order.notify.text}")
    private String default_text;

    @Resource
    private JavaMailSender mailSender;

    public void sendEmail(String email,String subject,String text) {
        if (email == null) {
            throw new NullPointerException("The receiving mailbox can not be empty");
        }
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(form);
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(StringUtils.isNotEmpty(subject)?subject:default_subject);
        simpleMailMessage.setText(StringUtils.isNotEmpty(text)?text:default_text);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            mailSender.send(simpleMailMessage);
        } catch (MailException e) {
            logger.warn("send email error|email:{}|text:{}|error:{}",email,text,ErrorWriterUtil.WriteError(e).toString());
        }
    }

    @Async
    public void sendEmail(String email) {
        sendEmail(email,null,null);
    }

}
