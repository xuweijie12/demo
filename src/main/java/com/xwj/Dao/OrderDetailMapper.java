package com.xwj.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xwj.pojo.OrderDetail;
import com.xwj.pojo.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
}
