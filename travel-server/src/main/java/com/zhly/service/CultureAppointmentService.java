package com.zhly.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhly.entity.CultureAppointment;

public interface CultureAppointmentService extends IService<CultureAppointment> {

    Page<CultureAppointment> pageList(int page, int size, Integer appointmentType, Integer status);

    boolean updateStatus(Long id, Integer status);
}
























