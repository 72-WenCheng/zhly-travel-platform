package com.zhly.controller;

import com.zhly.common.Result;
import com.zhly.entity.CultureBooking;
import com.zhly.entity.CultureExperience;
import com.zhly.entity.CultureHomestay;
import com.zhly.entity.CultureService;
import com.zhly.mapper.CultureBookingMapper;
import com.zhly.service.CultureExperienceService;
import com.zhly.service.CultureHomestayService;
import com.zhly.service.CultureServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import com.zhly.security.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 用户端-文旅预订控制器
 */
@Tag(name = "用户端-文旅预订")
@RestController
@RequestMapping("/api/culture/booking")
@RequiredArgsConstructor
public class CultureBookingController {
    
    private final CultureBookingMapper cultureBookingMapper;
    private final CultureExperienceService cultureExperienceService;
    private final CultureServiceService cultureServiceService;
    private final CultureHomestayService cultureHomestayService;
    private final SecurityUtils securityUtils;
    
    @Operation(summary = "创建预订（体验/服务/民宿）")
    @PostMapping
    public Result<String> createBooking(@RequestBody Map<String, Object> bookingData, HttpServletRequest request) {
        try {
            Integer bookingType = safeInt(bookingData.get("bookingType"));
            if (bookingType == null) bookingType = 1; // 默认体验

            CultureBooking booking = new CultureBooking();
            booking.setBookingNo(generateBookingNo());
            Long userId = securityUtils.getLoginUserId(request);
            if (userId == null) {
                return Result.unauthorized("未登录或Token无效");
            }
            booking.setUserId(userId);
            booking.setBookingType(bookingType);

            if (bookingType == 1) {
                // 文化体验
                Long experienceId = safeLong(bookingData.get("experienceId"));
                if (experienceId == null) return Result.error("缺少体验ID");
                CultureExperience experience = cultureExperienceService.getExperienceById(experienceId);
                if (experience == null) return Result.error("体验不存在");
                if (!"active".equalsIgnoreCase(experience.getStatus())) return Result.error("体验已下架，无法预订");

                booking.setItemId(experienceId);
                booking.setItemName(experience.getName());
                booking.setItemImage(experience.getImages());
                booking.setPrice(experience.getPrice());
                booking.setBookingDate(LocalDate.parse(bookingData.get("date").toString()));
                booking.setBookingTime(String.valueOf(bookingData.get("timeSlot")));
                booking.setPeopleCount(safeInt(bookingData.get("participants")));
            } else if (bookingType == 2) {
                // 农家乐服务
                Long serviceId = safeLong(bookingData.get("serviceId"));
                if (serviceId == null) return Result.error("缺少服务ID");
                CultureService service = cultureServiceService.getDetail(serviceId);
                if (service == null) return Result.error("服务不存在");
                if (service.getStatus() != null && service.getStatus() == 0) return Result.error("服务已下架");

                booking.setItemId(serviceId);
                booking.setItemName(service.getTitle());
                booking.setItemImage(service.getImages());
                booking.setPackageId(safeLong(bookingData.get("packageId")));
                booking.setPackageName(String.valueOf(bookingData.get("packageName")));
                booking.setPeopleCount(safeInt(bookingData.get("quantity")));
                booking.setBookingDate(bookingData.get("date") != null ? LocalDate.parse(bookingData.get("date").toString()) : null);
                booking.setBookingTime(String.valueOf(bookingData.get("timeSlot")));
                booking.setPrice(toDecimal(bookingData.get("unitPrice")));
            } else if (bookingType == 3) {
                // 特色民宿
                Long homestayId = safeLong(bookingData.get("homestayId"));
                if (homestayId == null) return Result.error("缺少民宿ID");
                CultureHomestay homestay = cultureHomestayService.getById(homestayId);
                if (homestay == null) return Result.error("民宿不存在");
                if (homestay.getStatus() != null && homestay.getStatus() == 0) return Result.error("民宿已下架");

                booking.setItemId(homestayId);
                booking.setItemName(homestay.getTitle());
                booking.setItemImage(homestay.getImages());
                booking.setPackageName(String.valueOf(bookingData.get("roomType")));
                booking.setPeopleCount(safeInt(bookingData.get("quantity")));
                booking.setBookingDate(bookingData.get("date") != null ? LocalDate.parse(bookingData.get("date").toString()) : null);
                booking.setBookingTime(String.valueOf(bookingData.get("timeSlot")));
                booking.setPrice(homestay.getPrice());
            } else {
                return Result.error("不支持的预订类型");
            }

            booking.setContactName(String.valueOf(bookingData.get("contactName")));
            booking.setContactPhone(String.valueOf(bookingData.get("contactPhone")));
            booking.setSpecialRequirements(bookingData.get("notes") == null ? null : bookingData.get("notes").toString());
            booking.setTotalAmount(toDecimal(bookingData.get("totalAmount")));
            booking.setStatus(1); // 待确认
            booking.setPaymentStatus(1); // 未支付
            // 若前端未传预订日期（例如农家乐/民宿即时预订），则默认使用当前日期，避免数据库非空约束错误
            if (booking.getBookingDate() == null) {
                booking.setBookingDate(LocalDate.now());
            }
            booking.setCreateTime(LocalDateTime.now());
            booking.setUpdateTime(LocalDateTime.now());

            cultureBookingMapper.insert(booking);

            if (bookingType == 1) {
                cultureExperienceService.updateOrderCount(booking.getItemId());
            }

            return Result.success("预订成功");
        } catch (Exception e) {
            return Result.error("预订失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取我的预订列表")
    @GetMapping("/my")
    public Result<List<CultureBooking>> getMyBookings(
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        try {
            Long userId = securityUtils.getLoginUserId(request);
            if (userId == null) {
                return Result.unauthorized("未登录或Token无效");
            }
            List<CultureBooking> bookings = cultureBookingMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CultureBooking>()
                    .eq("user_id", userId)
                    .eq(status != null, "status", status)
                    .orderByDesc("create_time")
            );
            return Result.success(bookings);
        } catch (Exception e) {
            return Result.error("获取预订列表失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取预订详情")
    @GetMapping("/{id}")
    public Result<CultureBooking> getBooking(@PathVariable Long id, HttpServletRequest request) {
        try {
            CultureBooking booking = cultureBookingMapper.selectById(id);
            if (booking == null) {
                return Result.error("预订不存在");
            }
            // 验证是否是当前用户的预订
            Long userId = securityUtils.getLoginUserId(request);
            if (userId == null) {
                return Result.unauthorized("未登录或Token无效");
            }
            if (!userId.equals(booking.getUserId())) {
                return Result.error("无权访问");
            }
            return Result.success(booking);
        } catch (Exception e) {
            return Result.error("获取预订详情失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "取消预订")
    @PutMapping("/{id}/cancel")
    public Result<String> cancelBooking(@PathVariable Long id, @RequestBody Map<String, String> cancelData, HttpServletRequest request) {
        try {
            CultureBooking booking = cultureBookingMapper.selectById(id);
            if (booking == null) {
                return Result.error("预订不存在");
            }
            // 验证是否是当前用户的预订
            Long userId = securityUtils.getLoginUserId(request);
            if (userId == null) {
                return Result.unauthorized("未登录或Token无效");
            }
            if (!userId.equals(booking.getUserId())) {
                return Result.error("无权操作");
            }
            if (booking.getStatus() == 4) {
                return Result.error("预订已取消");
            }
            if (booking.getStatus() == 3) {
                return Result.error("预订已完成，无法取消");
            }
            
            booking.setStatus(4); // 4-已取消
            booking.setCancelTime(LocalDateTime.now());
            booking.setCancelReason(cancelData.get("reason"));
            booking.setUpdateTime(LocalDateTime.now());
            cultureBookingMapper.updateById(booking);
            
            return Result.success("取消成功");
        } catch (Exception e) {
            return Result.error("取消失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成预订编号
     */
    private String generateBookingNo() {
        return "BK" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private Integer safeInt(Object o) {
        try {
            return o == null ? null : Integer.valueOf(o.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Long safeLong(Object o) {
        try {
            return o == null ? null : Long.valueOf(o.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal toDecimal(Object o) {
        try {
            return o == null ? null : new BigDecimal(o.toString());
        } catch (Exception e) {
            return null;
        }
    }
}

