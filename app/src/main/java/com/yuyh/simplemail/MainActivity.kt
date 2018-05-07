package com.yuyh.simplemail

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.yuyh.library.simplemail.MailHelper
import com.yuyh.library.simplemail.bean.LoginInfo
import com.yuyh.library.simplemail.bean.SendMailInfo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var helper: MailHelper? = null
    final val name = ""
    final val pwd = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        helper = MailHelper.getInstance(this@MainActivity)
        text_send_mail.setOnClickListener {
            Thread {
                val session = MailApp.session
                if (session != null) {
                    val list = helper!!.getAllMail(MailApp.info)
                    Log.i("TAG", list!!.size.toString() + "")

                    if (list != null)
                        runOnUiThread { Toast.makeText(this@MainActivity, "收信成功", Toast.LENGTH_SHORT).show() }
                }
            }.start()
        }

        send_mail.setOnClickListener {
            Thread {
                val session = MailApp.session
                if (session != null) {
                    val info = SendMailInfo()
                    info.fromAddress = MailApp.info.userName
                    info.subject = "测试邮件"
                    info.content = "测试邮件 内容"
                    info.receiversTO = arrayOf(" ")
                    info.receiversCC = arrayOf("")
                    info.receiversBCC = arrayOf("")
                    helper!!.sendMail(info, session)

                }
            }.start()
        }
        login_imap.setOnClickListener {
            Thread {
                val info = LoginInfo()
                val session = helper!!.loginImap(name, pwd)
                if (session) {
                    runOnUiThread { Toast.makeText(this@MainActivity, "登陆成功", Toast.LENGTH_SHORT).show() }
                }
            }.start()
        }
        login_pop3.setOnClickListener {
            Thread {
                val session = helper!!.loginPop3(name, pwd)
                if (session) {
                    runOnUiThread { Toast.makeText(this@MainActivity, "登陆成功", Toast.LENGTH_SHORT).show() }
                }
            }.start()
        }
        login_smtp.setOnClickListener {
            Thread {
                val info = LoginInfo()
                info.mailServerHost = "smtp.yandex.com"
                info.mailServerPort = "465"
                info.userName = name
                info.password = pwd
                info.validate = true
                val session = helper?.login(info)
                if (session != null) {
                    MailApp.session = session
                    MailApp.info = info
                    runOnUiThread { Toast.makeText(this@MainActivity, "登陆成功", Toast.LENGTH_SHORT).show() }
                }
            }.start()
        }


    }
}
