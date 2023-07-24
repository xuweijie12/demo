package com.xwj.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.xwj.Dao.OrderDetailMapper;
import com.xwj.Service.OrderDetailService;
import com.xwj.pojo.OrderDetail;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}