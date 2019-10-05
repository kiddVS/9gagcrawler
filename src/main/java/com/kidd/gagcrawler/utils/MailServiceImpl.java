package com.kidd.gagcrawler.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

@Service
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    //使用@Value注入application.properties中指定的用户名
    private String from;

    @Autowired
    //用于发送文件
    private JavaMailSender mailSender;

    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);//收信人
        message.setSubject(subject);//主题
        message.setText(content);//内容
        message.setFrom(from);//发信人
        mailSender.send(message);
    }

    @Override
    public void sendHtmlMail(String to, String subject, String content) throws MessagingException {
        System.out.println(String.format("发送HTML邮件开始：%s,%s,%s", to, subject, content));
        // 使用MimeMessage，MIME协议
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper;
        // MimeMessageHelper帮助我们设置更丰富的内容
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);//true代表支持html
            mailSender.send(message);
            System.out.println("发送HTML邮件成功");
        } catch (MessagingException e) {
            System.out.println("发送HTML邮件失败："+e.getMessage());
        }
    }

    @Override
    public void sendAttachmentMail(String to, String subject, String content, String filePath) {

    }

    @Override
    public void sendInlineResourceMailBatch(String to, String subject, String content, String rscPathFormat,List<String> rscIds) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            for (String rscId:rscIds){ FileSystemResource res = new FileSystemResource(new File(String.format(rscPathFormat,rscId)));
                helper.addInline(rscId, res);
            }
            mailSender.send(message);
            System.out.println("嵌入静态资源的邮件已经发送。");
        } catch (MessagingException e) {
            System.out.println("发送嵌入静态资源的邮件时发生异常！"+e);
        }
    }
}
