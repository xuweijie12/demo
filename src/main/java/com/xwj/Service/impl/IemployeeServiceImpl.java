package com.xwj.Service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xwj.Dao.employeeMapper;
import com.xwj.Service.DishService;
import com.xwj.Service.IemployeeService;
import com.xwj.pojo.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IemployeeServiceImpl extends ServiceImpl<employeeMapper, Employee> implements IemployeeService {
    @Autowired
    private employeeMapper em; /*当有自己的需求时可以直接定义*/

}
