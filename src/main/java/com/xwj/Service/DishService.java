package com.xwj.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xwj.dto.DishDto;
import com.xwj.pojo.Dish;

public interface DishService extends IService<Dish> {
    /**保存菜品信息和菜品口味 需要操作两个表 dish 和dish_flavor*/
    public void saveWithFlavor(DishDto dishDto);

    /**跟据id查询菜品和菜品口味 需要操作两个表 dish 和dish_flavor*/
    public DishDto getWithFlavor(Long id);
    /**更新菜品信息和菜品口味 需要操作两个表 dish 和dish_flavor*/
    public void updateWithFlavor(DishDto dishDto);
}
