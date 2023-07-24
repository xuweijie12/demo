package com.xwj.Service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xwj.Dao.DishMapper;
import com.xwj.Service.DishFlavorService;
import com.xwj.Service.DishService;
import com.xwj.dto.DishDto;
import com.xwj.pojo.Dish;
import com.xwj.pojo.DishFlavor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dfs;  /*因为也要操控DishFlavor所以也要把DishFlavorService给注入*/

    /**
     * 保存菜品的同时又保存菜品的口味 涉及两张表的操作
     * @param dishDto
     */
    @Override        /**！！！ 切记当修改或添加多个表的时候 记得加@Transactional
                             事务的一致性要么全成功要么全失败*/
    @Transactional  /*因为有两个表需要控制所以需要有事务控制 在需要在ReggieApplication里添加EnableTransactionManagement标签*/
    public void saveWithFlavor(DishDto dishDto) {
        /*保存Dish到Dish表中*/
        this.save(dishDto);
        /*获取Dish的id 赋值到DishFlavor表中的DishId中去*/
        Long dishId = dishDto.getId();

        /*菜品口味 其在dto里是列表的形式 */
        /*获取flavor列表                 因为缺少dishId所以要赋值*/
        List<DishFlavor> flavors=dishDto.getFlavors();
        /**因为在DishFlavor那的Dish_Id没有实际的值所以需要用Dish那里的id赋值给它!!!*/
//       将Dish的id赋值到DishFlavor的DishId里去 这里用的是流的方法  map是把里面的数据都拿出来
        flavors=flavors.stream().map((item)->{  /*把对象设置成item*/
            item.setDishId(dishId);     /*赋值*/
            return item;
        }).collect(Collectors.toList()); /*最后又将数据转换为列表*/

       /*保存菜品口味 到Dish_Flavor    */
        dfs.saveBatch(flavors);  /*saveBatch是批量保存*/
    }

    @Override
    public DishDto getWithFlavor(Long id) {
        /*查询基本信息*/
        Dish dish = this.getById(id);
        /*创建DishDto对象*/
        DishDto dishDto = new DishDto();
        /*复制 把dish的信息复制给dishDto*/
        BeanUtils.copyProperties(dish,dishDto);
        /*查询口味信息  从dish_flavor表里查*/
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());/*将flavor里的dishId与dish里的id进行比对*/
        List<DishFlavor> flavors=dfs.list(lambdaQueryWrapper);/*dto里的flavor是列表 调用dfs进行查询所以口味 */
        dishDto.setFlavors(flavors);  /*将查询到的口味设置到dto的口味中去*/
        return dishDto;
    }

    @Override
    @Transactional /*切记当修改或添加多个表的时候 记得加@Transactional*/
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);
        /*清理当前菜品对应的口味以方便修改*/
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dfs.remove(lambdaQueryWrapper);
        /*添加提交过来的口味数据*/
        List<DishFlavor> flavors = dishDto.getFlavors();
        /*因为会缺少dishId所以需要单独添加dishID*/
        flavors=flavors.stream().map((item->{
            item.setDishId(dishDto.getId());
            return item;
        })).collect(Collectors.toList());
        dfs.saveBatch(flavors);/*清理完修改最后进行批量保存*/

    }


}
