package com.zhly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhly.entity.CultureAppointment;
import com.zhly.mapper.CultureAppointmentMapper;
import com.zhly.service.CultureAppointmentService;
import org.springframework.stereotype.Service;

@Service
public class CultureAppointmentServiceImpl extends ServiceImpl<CultureAppointmentMapper, CultureAppointment> implements CultureAppointmentService {

    @Override
    public Page<CultureAppointment> pageList(int page, int size, Integer appointmentType, Integer status) {
        Page<CultureAppointment> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CultureAppointment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(appointmentType != null, CultureAppointment::getAppointmentType, appointmentType)
               .eq(status != null, CultureAppointment::getStatus, status)
               .orderByDesc(CultureAppointment::getUpdateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        CultureAppointment appointment = this.getById(id);
        if (appointment == null) return false;
        appointment.setStatus(status);
        return this.updateById(appointment);
    }
}
























