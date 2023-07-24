package com.xwj.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xwj.Dao.CategoryMapper;
import com.xwj.Service.DishService;
import com.xwj.Service.ICategoryService;
import com.xwj.Service.SetmealService;
import com.xwj.pojo.Category;
import com.xwj.pojo.Dish;
import com.xwj.pojo.Setmeal;
import com.xwj.util.customException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**分类Service*/
@Service    //把其设置成bean 给spring管理
public class ICategoryServiceImpl extends ServiceImpl<CategoryMapper,Category> implements ICategoryService{

    @Autowired
    private DishService ds;
    @Autowired
    private SetmealService ss;


    @Override
    public void remove(Long id) {
    //添加条件1 分类是否关联了菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);  /*将页面传来的类型id与dish中的类型id进行比较*/
        int count1=ds.count(dishLambdaQueryWrapper);//调用DishService查看有多少菜品已关联
    //添加条件2 分类是否关联了套餐
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper =new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2=ss.count(setmealLambdaQueryWrapper);

        if (count1>0){
            /*代表已关联 抛出异常  异常定义是需要在util立再创建一个异常类 */
            throw new customException("该类型已关联菜品，无法删除");
        }
        if (count2>0){
            /*代表已关联 抛出异常*/
            throw new customException("该类型已关联套餐，无法删除");
        }
        //倘若无异常则直接调用IService里的Mybatis-Plus里的方法
        super.removeById(id);
    }
}
