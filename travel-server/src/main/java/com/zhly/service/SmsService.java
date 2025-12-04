package com.zhly.service;

/**
 * 短信服务接口
 * 
 * @author zhly
 * @since 2024-01-01
 */
public interface SmsService {
    
    /**
     * 发送验证码短信
     * @param phone 手机号
     * @param captcha 验证码
     * @return 是否发送成功
     */
    boolean sendCaptchaSms(String phone, String captcha);
    
    /**
     * 发送通知短信
     * @param phone 手机号
     * @param content 短信内容
     * @return 是否发送成功
     */
    boolean sendNotificationSms(String phone, String content);
}









