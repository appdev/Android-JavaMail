package com.yuyh.library.simplemail.bean;

import java.io.Serializable;
import java.util.Properties;

/**
 * @author lengyue
 * @date 2016/8/1.
 */
public class LoginInfo implements Serializable {

    /**
     * 发件服务器地址和端口
     */
    public String mailServerHost;
    public String mailServerPort = "465";
    /**
     * 发件用户名与密码
     */
    public String userName;
    public String password;
    /**
     * 是否需要身份验证
     */
    public boolean validate = true;

    /**
     * 获得邮件会话属性
     */
    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    public Properties getProperties() {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", "smtp.yandex.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");

        return props;
    }

    public Properties getPropertiesPop3() {
        Properties props = new Properties();
        props.setProperty("mail.pop.host", "pop.yandex.com");
        props.setProperty("mail.pop.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.pop.socketFactory.fallback", "false");
        props.setProperty("mail.pop.port", "995");
        props.setProperty("mail.pop.socketFactory.port", "995");
        props.put("mail.pop3.starttls.enable", "true");
        props.put("mail.pop3.auth", "true");

        return props;
    }
    public Properties getPropertiesImap() {
        Properties props = new Properties();
        props.setProperty("mail.pop3.host", "pop.yandex.com");
        props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.port", "995");
        props.setProperty("mail.pop3.socketFactory.port", "995");
        props.put("mail.pop3.starttls.enable", "true");
        props.put("mail.pop3.auth", "true");

        return props;
    }
}
