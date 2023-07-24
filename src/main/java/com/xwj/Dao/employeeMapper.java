package com.xwj.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xwj.pojo.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface employeeMapper extends BaseMapper<Employee> {

}
