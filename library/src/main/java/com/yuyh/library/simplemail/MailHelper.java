package com.yuyh.library.simplemail;

import android.content.Context;
import android.util.Log;

import com.yuyh.library.simplemail.bean.Attachment;
import com.yuyh.library.simplemail.bean.LoginInfo;
import com.yuyh.library.simplemail.bean.ReceiverMailInfo;
import com.yuyh.library.simplemail.bean.SendMailInfo;
import com.yuyh.library.simplemail.utils.MailAuthenticator;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

/**
 * @author lengyue
 * @date 2016/8/1.
 */
public class MailHelper {

    private static MailHelper instance;
    private Context context;
    private List<ReceiverMailInfo> mailList;
    private HashMap<String, Integer> serviceHashMap;

    public static MailHelper getInstance(Context context) {
        if (instance == null)
            instance = new MailHelper(context);
        return instance;
    }

    public MailHelper(Context context) {
        this.context = context;
    }

    /**
     * 邮箱登录
     *
     * @param info
     * @return
     */
    public Session login(LoginInfo info) {
        // 创建密码验证器
        MailAuthenticator authenticator = null;
        if (info.validate) {
            authenticator = new MailAuthenticator(info.userName, info.password);
        }

        Session session = Session.getDefaultInstance(info.getProperties(), authenticator);

        try {
            //https smtps?
            Transport transport = session.getTransport("smtps");
            transport.connect(info.mailServerHost, info.userName, info.password);
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }

        return session;
    }

    /**
     * 邮箱登录
     *
     * @param name
     * @param pwd
     */
    public boolean loginPop3(String name, String pwd) {
        Properties propts = new Properties();
        propts.put("mail.store.protocol", "pop3");
        propts.put("mail.pop3.host", "pop.yandex.com");
        propts.put("mail.pop3.port", "995");
        propts.put("mail.pop3.starttls.enable", "true");
        Session emailsesion = Session.getInstance(propts, null);
        emailsesion.setDebug(true);

        try {
            Store storage = emailsesion.getStore("pop3s");
            storage.connect("pop.yandex.com", name, pwd);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * 邮箱登录
     *
     * @param name
     * @param pwd
     */
    public boolean loginImap(String name, String pwd) {
        Properties propts = new Properties();
        propts.put("mail.store.protocol", "imap");
        propts.put("mail.imap.host", "imap.yandex.com");
        propts.put("mail.imap.port", "993");
        propts.put("mail.imap.starttls.enable", "true");
        Session emailsesion = Session.getInstance(propts, null);
        emailsesion.setDebug(true);

        try {
            Store storage = emailsesion.getStore("imaps");
            storage.connect("imap.yandex.com", name, pwd);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 发送邮件
     *
     * @param mailInfo
     * @param session
     * @return
     */
    public boolean sendMail(SendMailInfo mailInfo, Session session) {
        try {
            Message mailMessage = new MimeMessage(session);

            // 发件人
            Address fromAddress = new InternetAddress(mailInfo.fromAddress);
            mailMessage.setFrom(fromAddress);
            // 收件人
            if (mailInfo.receiversTO == null) {
                return false;
            } else {
                mailMessage.setRecipients(Message.RecipientType.TO, createAddress(mailInfo.receiversTO));
                mailMessage.setRecipients(Message.RecipientType.CC, createAddress(mailInfo.receiversCC));
                mailMessage.setRecipients(Message.RecipientType.BCC, createAddress(mailInfo.receiversBCC));
            }


            mailMessage.setSubject(mailInfo.subject);
            mailMessage.setSentDate(new Date());

            Multipart multipart = new MimeMultipart();
            BodyPart mbpContent = new MimeBodyPart();
            mbpContent.setContent(mailInfo.content, "text/html;charset=gb2312");// 给BodyPart对象设置内容和格式/编码方式
            multipart.addBodyPart(mbpContent);

            FileDataSource fds;
            List<Attachment> list = mailInfo.attachmentInfos;
            if (list != null && !list.isEmpty()) {
                for (Attachment atta : list) {
                    fds = new FileDataSource(atta.filePath);
                    BodyPart mbpFile = new MimeBodyPart();
                    mbpFile.setDataHandler(new DataHandler(fds));
                    try {
                        mbpFile.setFileName(MimeUtility.encodeText(fds.getName()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    multipart.addBodyPart(mbpFile);
                }
            }
            mailMessage.setContent(multipart);
            mailMessage.saveChanges();

            // 设置邮件支持多种格式
            MailcapCommandMap mcm = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
            mcm.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mcm.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mcm.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mcm.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mcm.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(mcm);

            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private Address[] createAddress(String[] strings) throws AddressException {
        if (strings != null) {
            Address[] toAddress = new InternetAddress[strings.length];
            for (int i = 0; i < strings.length; i++) {
                toAddress[i] = new InternetAddress(strings[i]);
            }
            return toAddress;
        } else {
            return null;
        }
    }


    final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    private static final String TAG = "MailHelper";

    public List<ReceiverMailInfo> getAllMail(LoginInfo info) {
        Properties propts = new Properties();
        propts.put("mail.store.protocol", "pop3");
        propts.put("mail.pop3.host", "pop.yandex.com");
        propts.put("mail.pop3.port", "995");
        propts.put("mail.pop3.starttls.enable", "true");
        Session emailsesion = Session.getInstance(propts, null);
        emailsesion.setDebug(true);


        try {
            Store storage = emailsesion.getStore("pop3s");
            storage.connect("pop.yandex.com", info.userName, info.password);
            Folder[] folders = storage.getDefaultFolder().list();
            for (Folder folder : folders) {
                Log.i(TAG, "getAllMail: " + folder.getFullName());
                Log.i(TAG, "getAllMail: " + folder.getName());
            }
            // 打开文件夹
            Folder folder = storage.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);
            List<ReceiverMailInfo> mailList = new ArrayList<>();
            int mailCount = folder.getMessageCount();
            if (mailCount == 0) {
                folder.close(true);
                storage.close();
                return mailList;
            } else {

                Log.i(TAG, "未读邮件: " + folder.getUnreadMessageCount());
                Log.i(TAG, "新邮件: " + folder.getNewMessageCount());
                Log.i(TAG, "已删除邮件: " + folder.getDeletedMessageCount());
                Message[] messages = folder.getMessages();
                System.out.println("msges.length is -" + messages.length);
                for (Message message : messages) {
                    if (!message.getFolder().isOpen()) //判断是否open
                        message.getFolder().open(Folder.READ_WRITE); //如果close，就重新open

                    ReceiverMailInfo reciveMail = new ReceiverMailInfo((MimeMessage) message);
                    mailList.add(reciveMail);// 添加到邮件列表中
                    System.out.println("*******************************");
                }
                folder.close(false);
                storage.close();
                return mailList;
            }
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
