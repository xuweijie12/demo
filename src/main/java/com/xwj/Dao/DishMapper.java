package com.xwj.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xwj.pojo.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
