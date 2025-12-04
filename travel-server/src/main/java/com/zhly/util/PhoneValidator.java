package com.zhly.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 手机号验证工具类
 * 支持各地区手机号格式验证
 * 
 * @author zhly
 * @since 2024-01-01
 */
public class PhoneValidator {
    
    // 各地区手机号正则表达式
    private static final Map<String, Pattern> PHONE_PATTERNS = new HashMap<>();
    
    static {
        // 中国大陆
        PHONE_PATTERNS.put("CN", Pattern.compile("^1[3-9]\\d{9}$"));
        // 中国香港
        PHONE_PATTERNS.put("HK", Pattern.compile("^(5|6|9)\\d{7}$"));
        // 中国澳门
        PHONE_PATTERNS.put("MO", Pattern.compile("^6\\d{7}$"));
        // 中国台湾
        PHONE_PATTERNS.put("TW", Pattern.compile("^09\\d{8}$"));
        // 美国/加拿大
        PHONE_PATTERNS.put("US", Pattern.compile("^\\+?1[2-9]\\d{2}[2-9]\\d{2}\\d{4}$"));
        // 英国
        PHONE_PATTERNS.put("GB", Pattern.compile("^\\+?44[1-9]\\d{8,9}$"));
        // 日本
        PHONE_PATTERNS.put("JP", Pattern.compile("^\\+?81[789]0\\d{8}$"));
        // 韩国
        PHONE_PATTERNS.put("KR", Pattern.compile("^\\+?82[1-9]\\d{8,9}$"));
        // 新加坡
        PHONE_PATTERNS.put("SG", Pattern.compile("^\\+?65[689]\\d{7}$"));
        // 马来西亚
        PHONE_PATTERNS.put("MY", Pattern.compile("^\\+?60[1-9]\\d{7,8}$"));
        // 泰国
        PHONE_PATTERNS.put("TH", Pattern.compile("^\\+?66[689]\\d{8}$"));
        // 印度
        PHONE_PATTERNS.put("IN", Pattern.compile("^\\+?91[6-9]\\d{9}$"));
        // 澳大利亚
        PHONE_PATTERNS.put("AU", Pattern.compile("^\\+?61[2-478]\\d{8}$"));
        // 德国
        PHONE_PATTERNS.put("DE", Pattern.compile("^\\+?49[1-9]\\d{9,10}$"));
        // 法国
        PHONE_PATTERNS.put("FR", Pattern.compile("^\\+?33[1-9]\\d{8}$"));
        // 意大利
        PHONE_PATTERNS.put("IT", Pattern.compile("^\\+?39[3-9]\\d{8,9}$"));
        // 西班牙
        PHONE_PATTERNS.put("ES", Pattern.compile("^\\+?34[6-9]\\d{8}$"));
        // 俄罗斯
        PHONE_PATTERNS.put("RU", Pattern.compile("^\\+?7[3-9]\\d{9}$"));
        // 巴西
        PHONE_PATTERNS.put("BR", Pattern.compile("^\\+?55[1-9]\\d{8,9}$"));
        // 墨西哥
        PHONE_PATTERNS.put("MX", Pattern.compile("^\\+?52[1-9]\\d{9,10}$"));
    }
    
    /**
     * 验证手机号格式（自动识别地区）
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValid(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        
        // 移除空格和连字符
        phone = phone.replaceAll("[\\s-]", "");
        
        // 尝试匹配各地区格式
        for (Pattern pattern : PHONE_PATTERNS.values()) {
            if (pattern.matcher(phone).matches()) {
                return true;
            }
        }
        
        // 如果都不匹配，尝试通用国际格式（+国家代码+号码）
        if (phone.startsWith("+")) {
            // 通用国际格式：+国家代码+号码（7-15位数字）
            Pattern internationalPattern = Pattern.compile("^\\+[1-9]\\d{6,14}$");
            return internationalPattern.matcher(phone).matches();
        }
        
        return false;
    }
    
    /**
     * 验证指定地区的手机号
     * @param phone 手机号
     * @param countryCode 国家代码（如：CN, US, GB等）
     * @return 是否有效
     */
    public static boolean isValid(String phone, String countryCode) {
        if (phone == null || phone.trim().isEmpty() || countryCode == null) {
            return false;
        }
        
        phone = phone.replaceAll("[\\s-]", "");
        Pattern pattern = PHONE_PATTERNS.get(countryCode.toUpperCase());
        
        if (pattern != null) {
            return pattern.matcher(phone).matches();
        }
        
        return false;
    }
    
    /**
     * 检测手机号所属地区
     * @param phone 手机号
     * @return 国家代码，如果无法识别返回null
     */
    public static String detectCountry(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        
        phone = phone.replaceAll("[\\s-]", "");
        
        for (Map.Entry<String, Pattern> entry : PHONE_PATTERNS.entrySet()) {
            if (entry.getValue().matcher(phone).matches()) {
                return entry.getKey();
            }
        }
        
        // 如果以+开头，尝试从国家代码识别
        if (phone.startsWith("+")) {
            String countryCode = extractCountryCode(phone);
            if (countryCode != null) {
                return countryCode;
            }
        }
        
        return null;
    }
    
    /**
     * 从国际格式手机号中提取国家代码
     * @param phone 手机号（+国家代码+号码）
     * @return 国家代码
     */
    private static String extractCountryCode(String phone) {
        // 常见国家代码映射
        Map<String, String> countryCodeMap = new HashMap<>();
        countryCodeMap.put("86", "CN");   // 中国
        countryCodeMap.put("852", "HK");  // 香港
        countryCodeMap.put("853", "MO");  // 澳门
        countryCodeMap.put("886", "TW");  // 台湾
        countryCodeMap.put("1", "US");    // 美国/加拿大
        countryCodeMap.put("44", "GB");   // 英国
        countryCodeMap.put("81", "JP");   // 日本
        countryCodeMap.put("82", "KR");   // 韩国
        countryCodeMap.put("65", "SG");   // 新加坡
        countryCodeMap.put("60", "MY");   // 马来西亚
        countryCodeMap.put("66", "TH");   // 泰国
        countryCodeMap.put("91", "IN");    // 印度
        countryCodeMap.put("61", "AU");   // 澳大利亚
        countryCodeMap.put("49", "DE");   // 德国
        countryCodeMap.put("33", "FR");   // 法国
        countryCodeMap.put("39", "IT");   // 意大利
        countryCodeMap.put("34", "ES");   // 西班牙
        countryCodeMap.put("7", "RU");    // 俄罗斯
        countryCodeMap.put("55", "BR");   // 巴西
        countryCodeMap.put("52", "MX");   // 墨西哥
        
        // 尝试匹配国家代码（1-3位）
        for (int len = 1; len <= 3; len++) {
            if (phone.length() > len) {
                String code = phone.substring(1, 1 + len);
                if (countryCodeMap.containsKey(code)) {
                    return countryCodeMap.get(code);
                }
            }
        }
        
        return null;
    }
    
    /**
     * 格式化手机号（统一格式）
     * @param phone 手机号
     * @return 格式化后的手机号
     */
    public static String format(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return phone;
        }
        
        // 移除所有空格和连字符
        return phone.replaceAll("[\\s-]", "");
    }
}









