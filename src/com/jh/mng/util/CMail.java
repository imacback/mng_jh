package com.jh.mng.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.fileupload.FileItem;

public class CMail {
  String smtpHost = null;
  String username = null;
  String password = null;

  String from = null; // 发件人
  String to = null; // 直接发送
  String cc = null; // 抄送
  String bcc = null; // 密送
  int ifAuth = 1; // 是否需要认证
  int Priority = 3; // 指定邮件优先级 1：紧急 3：普通 5：缓慢
  String subject = null;
  Date sendDate = null;
  String contentText = null;
  List<String> toList = null;
  List<String> ccList = null;
  List<String> bccList = null;
  List<FileItem> fileList = new ArrayList<FileItem>();

  public CMail() {
  }

  public CMail(String smtpHost, String username, String password,String from) {
    this.smtpHost = smtpHost;
    this.username = username;
    this.password = password;
    this.from = from;
  }

// 直接发送
  public void setTo(String to) {
    String[] p = to.trim().split(";");
    toList = new ArrayList<String>();
    for (int i = 0; i < p.length; ++i) {
      if (p[i].trim().equals("")) {
        continue;
      }
      toList.add(p[i].trim());
    }
  }

// 抄送
  public void setCc(String cc) {
    String[] p = cc.trim().split(";");
    ccList = new ArrayList<String>();
    for (int i = 0; i < p.length; ++i) {
      if (p[i].trim().equals("")) {
        continue;
      }
      ccList.add(p[i].trim());
    }
  }

// 密送
  public void setBcc(String bcc) {
    String[] p = bcc.trim().split(";");
    bccList = new ArrayList<String>();
    for (int i = 0; i < p.length; ++i) {
      if (p[i].trim().equals("")) {
        continue;
      }
      bccList.add(p[i].trim());
    }
  }

// 主题
  public void setSubject(String subject) {
    this.subject = subject;
  }

// 邮件文本部分
  public void setContentText(String contentText) {
    this.contentText = contentText;
  }

  public void setFileList(FileItem item) {
    fileList.add(item);
  }

  @SuppressWarnings("static-access")
public int send() {
// 创建一个属性对象
    Properties props = new Properties();
// 指定SMTP服务器
    props.put("mail.smtp.host", smtpHost);
// 指定是否需要SMTP验证
    props.put("mail.smtp.auth", "true");
// 创建一个授权验证对象
    SmtpAuth auth = new SmtpAuth();
    auth.setAccount(username, password);

// 创建一个Session对象
    Session mailSession = Session.getDefaultInstance(props, auth);
    mailSession.setDebug(false);

// 创建一个MimeMessage 对象
    MimeMessage message = new MimeMessage(mailSession);

    try {
// 指定发件人邮箱
      message.setFrom(new InternetAddress(from));
// 指定收件人邮箱
      if (toList != null) {
        for (int i = 0; i < toList.size(); ++i) {
          message.addRecipient(Message.RecipientType.TO,
                               new InternetAddress( (String) toList.get(i)));
        }
      }
      if (ccList != null) {
        for (int i = 0; i < ccList.size(); ++i) {
          message.addRecipient(Message.RecipientType.CC,
                               new InternetAddress( (String) ccList.get(i)));
        }
      }
      if (bccList != null) {
        for (int i = 0; i < bccList.size(); ++i) {
          message.addRecipient(Message.RecipientType.BCC,
                               new InternetAddress( (String) bccList.get(i)));
        }
      }

// 指定邮件主题
      message.setSubject(subject);

// 指定邮件发送日期
      message.setSentDate(new Date());
// 指定邮件优先级 1：紧急 3：普通 5：缓慢
      message.setHeader("X-Priority", "3");
      message.saveChanges();

// 新建一个MimeMultipart对象用来存放多个BodyPart对象
      Multipart container = new MimeMultipart();
// 新建一个存放信件内容的BodyPart对象
      BodyPart textBodyPart = new MimeBodyPart();
// 给BodyPart对象设置内容和格式/编码方式
      textBodyPart.setContent(contentText, "text/html;charset=UTF-8");
// 将含有信件内容的BodyPart加入到MimeMultipart对象中
      container.addBodyPart(textBodyPart);

// 将container作为消息对象的内容
      if (container != null) {
        message.setContent(container);
      }

// 创建一个Transport对象
      Transport transport = mailSession.getTransport("smtp");
// 连接SMTP服务器
      transport.connect(smtpHost, username, password);
// 发送邮件
      transport.send(message, message.getAllRecipients());
      transport.close();

      return 1;
    }
    catch (Exception e) {
      e.printStackTrace();
      return 0;
    }

  }

  public int sendToSql() {
    return 0;
  }

// 定义一个SMTP授权验证类
  static class SmtpAuth
      extends Authenticator {
    String user, password;

// 设置帐号信息
    void setAccount(String user, String password) {
      this.user = user;
      this.password = password;
    }

// 取得PasswordAuthentication对象
    protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication(user, password);
    }
  }

  public static void main(String args[])
  {
    

  }
}
