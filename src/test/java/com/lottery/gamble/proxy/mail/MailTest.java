//package com.lottery.gamble.manage.mail;
//
//import com.lottery.gamble.manage.base.BaseTest;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.ui.velocity.VelocityEngineUtils;
//
//import javax.annotation.Resource;
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author Created by 王亚平 on 2017/6/21.
// *
// * 参考地址 <a href="http://docs.spring.io/spring/docs/4.3.6.RELEASE/spring-framework-reference/htmlsingle/#mail-javamail-mime"></a>
// */
//public class MailTest  extends BaseTest {
//
//    @Value("${spring.mail.username}")
//    private String form;
//
//    @Resource
//    private JavaMailSender javaMailSender;
//
//    @Test
//    public void testSendMail () {
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setFrom(form);
//        simpleMailMessage.setTo("wypsir@163.com");
//        simpleMailMessage.setSubject("测试");
//        simpleMailMessage.setText("这是测试mail2");
//
//        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//        //mimeMessage.addFrom();
//        javaMailSender.send(simpleMailMessage);
//    }
//
//    @Test
//    public void sendAttachmentsEmail() throws MessagingException {
//        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//
//        final MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
//        FileSystemResource file1 = new FileSystemResource(new File("D:/test.jpg"));
//        mimeMessageHelper.setFrom(form);
//        mimeMessageHelper.setTo("wypsir@163.com");
//        mimeMessageHelper.setSubject("附件测试");
//        mimeMessageHelper.setText("有附件哦");
//        mimeMessageHelper.addAttachment("test.jpg",file1);
//
//        javaMailSender.send(mimeMessage);
//    }
//
//    /**
//     * 邮件中使用静态资源.
//     * @throws Exception
//     */
//    @Test
//    public void sendInlineMail() throws Exception {
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//        //基本设置.
//        helper.setFrom(form);
//        helper.setTo("wypsir@163.com");
//        helper.setSubject("测试静态资源（邮件主题）");//邮件主题.
//        // 邮件内容，第二个参数指定发送的是HTML格式
//        //说明：嵌入图片<img src='cid:head'/>，其中cid:是固定的写法，而aaa是一个contentId。
//        helper.setText("<body>这是图片：<img src='cid:head' /></body>", true);
//
//        FileSystemResource file = new FileSystemResource(new File("D:/test.jpg"));
//        helper.addInline("head",file);
//
//        javaMailSender.send(mimeMessage);
//
//    }
//
//
//    @Resource
//    private VelocityEngine velocityEngine;
//
//    /**
//     * 模板邮件；
//     * @throws Exception
//     */
//    @Test
//    public void sendTemplateMail() throws Exception {
//
//        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
//
//        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
//        Map<String,Object> data  = new HashMap<String,Object>();
//        helper.setFrom(form);
//        helper.setTo("wypsir@163.com");
//        helper.setSubject("测试模板邮件");//邮件主题.
//        data.put("username", "王亚平");
//        String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/test.vm", data);
//        helper.setText(text, true);
//        javaMailSender.send(mimeMessage);
//
//    }
//}
