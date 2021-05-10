package demo.util;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * @author chei1
 */
public class EmailUtil {
    public static boolean sendEmail(String emailAddress,String captcha){
        SimpleEmail email = new SimpleEmail();
        try {
            // 发送电子邮件的邮件服务器地址
            email.setHostName("smtp.163.com");
            email.setCharset("UTF-8");
            // 邮箱服务器身份验证
            email.setAuthentication("litemall2021@163.com", "DVXBRQCGGCMBZRSF");
            // 设置发件人邮箱(与用户名保持一致) 并且 设置发件人昵称
            email.setFrom("litemall2021@163.com","litemall密码重置服务");
            // 邮件主题
            email.setSubject("密码重置服务");
            // 邮件内容
            email.setMsg("【litemall商城】您正在使用litemall邮箱验证服务，您的验证码是"+captcha+"，如非本人操作，请忽略本邮件。");
            // 收件人地址qq.com
            email.addTo(emailAddress);
            // 邮件发送
            email.send();
            return true;
        }catch (EmailException e){
            e.printStackTrace();
            return false;
        }
    }

}
