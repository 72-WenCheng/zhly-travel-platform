package com.zhly.controller;

import com.zhly.common.Result;
import com.zhly.service.ThirdPartyApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 第三方API控制器
 * 
 * @author zhly
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/third-party")
public class ThirdPartyApiController {
    
    @Autowired
    private ThirdPartyApiService thirdPartyApiService;
    
    /**
     * 地理编码：根据地址获取经纬度
     */
    @GetMapping("/geocode")
    public Result<Map<String, Object>> geocode(@RequestParam String address) {
        try {
            Map<String, Object> result = thirdPartyApiService.geocode(address);
            if (result != null && !result.isEmpty()) {
                return Result.success("地理编码成功", result);
            } else {
                return Result.error("无法获取该地址的坐标信息");
            }
        } catch (Exception e) {
            return Result.error("地理编码失败: " + e.getMessage());
        }
    }
    
    /**
     * 高德地图地理编码（备用接口）
     */
    @GetMapping("/amap/geocode")
    public Result<Map<String, Object>> amapGeocode(@RequestParam String address) {
        try {
            Map<String, Object> result = thirdPartyApiService.geocode(address);
            if (result != null && !result.isEmpty()) {
                return Result.success("地理编码成功", result);
            } else {
                return Result.error("无法获取该地址的坐标信息");
            }
        } catch (Exception e) {
            return Result.error("地理编码失败: " + e.getMessage());
        }
    }
    
    /**
     * 高德地图输入提示：根据关键词获取地址建议
     */
    @GetMapping("/amap/inputtips")
    public Result<java.util.List<Map<String, Object>>> getAmapInputTips(
            @RequestParam String keywords,
            @RequestParam(required = false) String city,
            @RequestParam(required = false, defaultValue = "false") Boolean citylimit) {
        try {
            java.util.List<Map<String, Object>> tips = thirdPartyApiService.getAmapInputTips(keywords, city, citylimit);
            return Result.success("获取地址建议成功", tips);
        } catch (Exception e) {
            return Result.error("获取地址建议失败: " + e.getMessage());
        }
    }
    
    /**
     * 高德地图POI搜索：根据关键词搜索地点
     */
    @GetMapping("/amap/poi")
    public Result<Map<String, Object>> getAmapPoi(
            @RequestParam String keyword,
            @RequestParam(required = false) String city) {
        try {
            Map<String, Object> result = thirdPartyApiService.getAmapPoi(keyword, city != null ? city : "");
            if (result != null && !result.isEmpty()) {
                return Result.success("POI搜索成功", result);
            } else {
                return Result.error("未找到相关地点");
            }
        } catch (Exception e) {
            return Result.error("POI搜索失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取天气信息
     */
    @GetMapping("/weather")
    public Result<Map<String, Object>> getWeather(@RequestParam(required = false, defaultValue = "重庆") String city) {
        try {
            Map<String, Object> result = thirdPartyApiService.getWeatherInfo(city);
            if (result != null && !result.isEmpty()) {
                // 检查结果中是否包含错误信息
                if (result.containsKey("error") || result.containsKey("errorMessage")) {
                    // 如果包含错误信息，仍然返回200状态，但前端会检查error字段
                    return Result.success("获取天气信息（可能使用降级数据）", result);
                }
                return Result.success("获取天气信息成功", result);
            } else {
                return Result.error("无法获取天气信息");
            }
        } catch (Exception e) {
            return Result.error("获取天气信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 逆地理编码：根据经纬度获取地址和城市信息
     */
    @GetMapping("/reverse-geocode")
    public Result<Map<String, Object>> reverseGeocode(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        try {
            Map<String, Object> result = thirdPartyApiService.reverseGeocode(longitude, latitude);
            if (result != null && !result.isEmpty()) {
                return Result.success("逆地理编码成功", result);
            } else {
                return Result.error("无法获取位置信息");
            }
        } catch (Exception e) {
            return Result.error("逆地理编码失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据当前位置获取天气（一步到位）
     */
    @GetMapping("/weather/location")
    public Result<Map<String, Object>> getWeatherByLocation(
            @RequestParam double longitude,
            @RequestParam double latitude) {
        try {
            // 先进行逆地理编码获取城市
            Map<String, Object> locationInfo = thirdPartyApiService.reverseGeocode(longitude, latitude);
            if (locationInfo == null || locationInfo.isEmpty()) {
                return Result.error("无法获取位置信息");
            }
            
            String city = (String) locationInfo.get("city");
            if (city == null || city.isEmpty()) {
                city = (String) locationInfo.get("province");
            }
            if (city == null || city.isEmpty()) {
                return Result.error("无法确定城市信息");
            }
            
            // 获取该城市的天气
            Map<String, Object> weatherInfo = thirdPartyApiService.getWeatherInfo(city);
            if (weatherInfo != null && !weatherInfo.isEmpty()) {
                // 合并位置信息和天气信息
                weatherInfo.put("location", locationInfo);
                return Result.success("获取天气信息成功", weatherInfo);
            } else {
                return Result.error("无法获取天气信息");
            }
        } catch (Exception e) {
            return Result.error("获取天气信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据IP地址获取位置信息（备用方案，不依赖浏览器定位）
     */
    @GetMapping("/location/ip")
    public Result<Map<String, Object>> getLocationByIp(
            @RequestParam(required = false) String ip,
            HttpServletRequest request) {
        try {
            // 重要说明：
            // 1. 如果后端调用高德API时不传IP参数，高德API会检测到**服务器的IP**，而不是客户端的IP
            // 2. 所以必须传递客户端真实IP给高德API，才能准确定位到用户位置
            // 3. 优先使用前端传递的IP参数（前端已获取用户真实公网IP）
            // 4. 如果前端没有传递，才尝试从请求头获取
            
            String clientIp = null;
            
            // 优先使用前端传递的IP参数（前端已获取用户真实公网IP）
            if (ip != null && !ip.isEmpty() && !isPrivateIp(ip) && !ip.contains(":")) {
                clientIp = ip;
                System.out.println("✅ 使用前端传递的用户真实IP: " + clientIp);
            } else {
                // 如果前端没有传递，尝试从请求头获取
                clientIp = getClientIp(request);
                System.out.println("🔍 从请求头获取客户端IP: " + clientIp);
                
                if (clientIp == null || clientIp.isEmpty() || clientIp.equals("unknown") || isPrivateIp(clientIp)) {
                    System.out.println("⚠️ 无法获取客户端真实IP，使用高德API自动检测模式");
                    System.out.println("⚠️ 警告：自动检测会识别到服务器IP而非客户端IP，可能导致定位不准确（如定位到服务器位置）");
                    clientIp = ""; // 让高德API自动检测（但会检测到服务器IP）
                } else {
                    System.out.println("✅ 从请求头获取到客户端IP: " + clientIp);
                }
            }
            
            System.out.println("🌐 最终使用的IP参数: " + (clientIp.isEmpty() ? "(自动检测 - 可能检测到服务器IP)" : clientIp));
            
            Map<String, Object> result = thirdPartyApiService.getLocationByIp(clientIp);
            if (result != null && !result.isEmpty()) {
                // 检查返回的城市信息是否为空
                String city = (String) result.get("city");
                String province = (String) result.get("province");
                
                // 如果城市和省份都为空，说明高德API自动检测失败
                if ((city == null || city.isEmpty()) && (province == null || province.isEmpty())) {
                    System.err.println("❌ 高德IP定位API返回空数据，可能是：");
                    System.err.println("   1. 高德API无法识别客户端真实IP（可能经过代理/NAT）");
                    System.err.println("   2. 高德API Key权限不足");
                    System.err.println("   3. 客户端IP是内网地址且自动检测失败");
                    
                    // 尝试使用请求的RemoteAddr（服务器看到的IP）
                    String remoteAddr = request.getRemoteAddr();
                    System.out.println("🔄 尝试使用RemoteAddr: " + remoteAddr);
                    if (remoteAddr != null && !remoteAddr.isEmpty() && !isPrivateIp(remoteAddr)) {
                        Map<String, Object> retryResult = thirdPartyApiService.getLocationByIp(remoteAddr);
                        if (retryResult != null && !retryResult.isEmpty()) {
                            String retryCity = (String) retryResult.get("city");
                            if (retryCity != null && !retryCity.isEmpty()) {
                                System.out.println("✅ 使用RemoteAddr成功获取城市: " + retryCity);
                                result = retryResult;
                                city = retryCity;
                            }
                        }
                    }
                }
                
                // 获取城市信息后，同时获取天气
                if (city != null && !city.isEmpty()) {
                    Map<String, Object> weatherInfo = thirdPartyApiService.getWeatherInfo(city);
                    if (weatherInfo != null && !weatherInfo.isEmpty()) {
                        // 将位置信息合并到weatherInfo中
                        weatherInfo.put("location", result);
                        // 同时将city和district也放到顶层，方便前端直接使用
                        weatherInfo.put("city", city);
                        if (result.containsKey("district")) {
                            weatherInfo.put("district", result.get("district"));
                        }
                        return Result.success("获取位置和天气信息成功", weatherInfo);
                    } else {
                        // 天气获取失败，但位置信息成功，返回位置信息
                        result.put("city", city); // 确保city在顶层
                        return Result.success("获取位置信息成功，但天气信息获取失败", result);
                    }
                }
                // 没有城市信息，返回位置信息
                return Result.success("获取位置信息成功", result);
            } else {
                return Result.error("无法获取位置信息");
            }
        } catch (Exception e) {
            System.err.println("❌ 获取位置信息异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取位置信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 判断是否是内网IP
     */
    private boolean isPrivateIp(String ip) {
        if (ip == null || ip.isEmpty()) {
            return true;
        }
        
        // 检查IPv6（包含冒号）
        if (ip.contains(":")) {
            return ip.startsWith("::1") || ip.startsWith("fe80:") || ip.startsWith("fc00:");
        }
        
        // 检查IPv4内网地址
        return ip.equals("127.0.0.1") || ip.equals("localhost") ||
               ip.startsWith("192.168.") || ip.startsWith("10.") ||
               ip.startsWith("172.16.") || ip.startsWith("172.17.") ||
               ip.startsWith("172.18.") || ip.startsWith("172.19.") ||
               ip.startsWith("172.20.") || ip.startsWith("172.21.") ||
               ip.startsWith("172.22.") || ip.startsWith("172.23.") ||
               ip.startsWith("172.24.") || ip.startsWith("172.25.") ||
               ip.startsWith("172.26.") || ip.startsWith("172.27.") ||
               ip.startsWith("172.28.") || ip.startsWith("172.29.") ||
               ip.startsWith("172.30.") || ip.startsWith("172.31.");
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 如果是多IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}



