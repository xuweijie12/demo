package com.xwj.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xwj.Dao.SetmealMapper;
import com.xwj.Service.SetmealDishService;
import com.xwj.Service.SetmealService;
import com.xwj.dto.SetmealDto;
import com.xwj.pojo.Setmeal;
import com.xwj.pojo.SetmealDish;
import com.xwj.util.customException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
     @Autowired
     private SetmealDishService sds;

    /**
     * 新增套餐
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        /*保存套餐的基本信息 执行insert操作 操作的是setmeal表 */
        this.save(setmealDto);
        /*因为在setmealdish那的setmealId没有实际的值所以需要用setmeal那里的id赋值给它*/
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();/*获取SetmealDishes*/

        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        /*保存套餐和菜品的关联信息 操作的是setmeal_dish表*/
        sds.saveBatch(setmealDishes);

    }

    /**
     * 删除和批量删除
     * @param ids
     */
    @Transactional
    @Override
    public void deleteWithDish(List<Long> ids) {
        //先要查询套餐的状态是否是启用的 启用的话就不可以删除
        //sql：select count(*) from where id in (1,2,3) and status=1;
        /*查询套餐状态*/
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        if(count>0){
            /*不能删除抛出异常*/
            throw new customException("套餐正在售卖中，不可删除");
        }
        this.removeByIds(ids);

        /*删除关系表中的数据*/
        //sql: delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids); /*setmeal_id 和setmealid*/
        sds.remove(lambdaQueryWrapper);

    }
}
