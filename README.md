# Android JavaMail
### 一个基于POP3、SMTP、IMAP协议的邮件DEMO，方便后来者爬坑。。。


## 使用前先修改SMTP、POP3、IMAP服务器地址。
## 已经测试收发均正常，抄送、密送正常。


## 登录
```java
String address = "smuyyh@126.com";
String pwd = "xxxxxxxxxx";
String host = "smtp." + address.substring(address.lastIndexOf("@") + 1);

final LoginInfo info = new LoginInfo();
info.mailServerHost = host;
info.mailServerPort = "25";
info.userName = address;
info.password = pwd;
info.validate = true;
new Thread(new Runnable() {
    @Override
    public void run() {
        Session session = MailHelper.getInstance(MainActivity.this).login(info);
        if(session != null){
            // 登录成功
        }
    }
}).start();
```

## 收件箱
```java
try {
    List<ReceiverMailInfo> list = MailHelper.getInstance(MainActivity.this).getAllMail(Constants.MailFolder.INBOX, info, session);
    Log.i("TAG", list.size()+"");
} catch (MessagingException e) {
        e.printStackTrace();
}

```

## 发送邮件
```java
SendMailInfo info = new SendMailInfo();
info.fromAddress = address;
info.subject = "测试邮件";
info.content = "测试邮件 内容";
info.receivers = new String[]{"3xxxx26@qq.com"};
MailHelper.getInstance(MainActivity.this).sendMail(info, session);
```