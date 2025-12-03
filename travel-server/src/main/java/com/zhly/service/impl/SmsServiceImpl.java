package com.zhly.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.zhly.service.SmsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 短信服务实现类（阿里云短信服务）
 * 
 * @author zhly
 * @since 2024-01-01
 */
@Service
public class SmsServiceImpl implements SmsService {
    
    @Value("${aliyun.sms.access-key-id:}")
    private String accessKeyId;
    
    @Value("${aliyun.sms.access-key-secret:}")
    private String accessKeySecret;
    
    @Value("${aliyun.sms.sign-name:智慧生态旅游平台}")
    private String signName;
    
    @Value("${aliyun.sms.template-code:}")
    private String templateCode;
    
    @Value("${aliyun.sms.enabled:false}")
    private boolean enabled;
    
    /**
     * 创建阿里云短信客户端
     */
    private Client createClient() throws Exception {
        Config config = new Config()
            .setAccessKeyId(accessKeyId)
            .setAccessKeySecret(accessKeySecret)
            .setEndpoint("dysmsapi.aliyuncs.com");
        return new Client(config);
    }
    
    @Override
    public boolean sendCaptchaSms(String phone, String captcha) {
        try {
            // 如果未启用短信服务，使用模拟发送
            if (!enabled || accessKeyId == null || accessKeyId.isEmpty() || 
                accessKeySecret == null || accessKeySecret.isEmpty()) {
                System.out.println("=== 模拟发送手机验证码（短信服务未配置） ===");
                System.out.println("手机号: " + phone);
                System.out.println("验证码: " + captcha);
                System.out.println("提示: 请在配置文件中配置阿里云短信服务参数");
                System.out.println("================================");
                return true;
            }
            
            // 检查模板代码是否配置
            if (templateCode == null || templateCode.isEmpty()) {
                System.out.println("=== 短信模板代码未配置，使用模拟发送 ===");
                System.out.println("手机号: " + phone);
                System.out.println("验证码: " + captcha);
                System.out.println("提示: 请配置短信模板代码");
                System.out.println("================================");
                return true;
            }
            
            // 创建客户端
            Client client = createClient();
            
            // 构建请求
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam("{\"code\":\"" + captcha + "\"}");
            
            // 发送短信
            SendSmsResponse response = client.sendSms(sendSmsRequest);
            
            // 检查响应
            if ("OK".equals(response.getBody().getCode())) {
                System.out.println("短信发送成功 - 手机号: " + phone + ", 请求ID: " + response.getBody().getRequestId());
                return true;
            } else {
                System.err.println("短信发送失败 - 手机号: " + phone + ", 错误码: " + response.getBody().getCode() + ", 错误信息: " + response.getBody().getMessage());
                return false;
            }
        } catch (Exception e) {
            System.err.println("发送短信异常: " + e.getMessage());
            e.printStackTrace();
            // 发送失败时，使用模拟发送（开发环境友好）
            System.out.println("=== 短信发送失败，使用模拟发送 ===");
            System.out.println("手机号: " + phone);
            System.out.println("验证码: " + captcha);
            System.out.println("错误: " + e.getMessage());
            System.out.println("================================");
            return true; // 开发环境返回true，避免阻塞流程
        }
    }
    
    @Override
    public boolean sendNotificationSms(String phone, String content) {
        try {
            // 如果未启用短信服务，使用模拟发送
            if (!enabled || accessKeyId == null || accessKeyId.isEmpty() || 
                accessKeySecret == null || accessKeySecret.isEmpty()) {
                System.out.println("=== 模拟发送通知短信（短信服务未配置） ===");
                System.out.println("手机号: " + phone);
                System.out.println("内容: " + content);
                System.out.println("================================");
                return true;
            }
            
            // 创建客户端
            Client client = createClient();
            
            // 构建请求（使用通用模板）
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phone)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam("{\"content\":\"" + content + "\"}");
            
            // 发送短信
            SendSmsResponse response = client.sendSms(sendSmsRequest);
            
            // 检查响应
            if ("OK".equals(response.getBody().getCode())) {
                System.out.println("通知短信发送成功 - 手机号: " + phone);
                return true;
            } else {
                System.err.println("通知短信发送失败 - 手机号: " + phone + ", 错误: " + response.getBody().getMessage());
                return false;
            }
        } catch (Exception e) {
            System.err.println("发送通知短信异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

