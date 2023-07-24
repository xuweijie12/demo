package com.xwj.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xwj.pojo.Category;

public interface ICategoryService extends IService<Category>{
    public void remove(Long id);
}
