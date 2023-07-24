package com.xwj.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xwj.Service.ICategoryService;
import com.xwj.Service.SetmealDishService;
import com.xwj.Service.SetmealService;
import com.xwj.dto.SetmealDto;
import com.xwj.pojo.Category;
import com.xwj.pojo.Setmeal;
import com.xwj.util.R;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealDishService sds;
    @Autowired
    private SetmealService ss;
    @Autowired
    private ICategoryService ics;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
         ss.saveWithDish(setmealDto);
        return R.success("新增套餐成功");

    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        Page<Setmeal> setmealPage=new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage =new Page<>();   /*创建一个page泛型为dto的page对象以显示套餐名*/
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        /*查询*/
        lambdaQueryWrapper.like(name!=null,Setmeal::getName,name);
        /*排序*/
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        ss.page(setmealPage,lambdaQueryWrapper);
        /*复制之前配置好的page对象*/
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> records1=records.stream().map((item)->{
            /*先创建一个dto对象*/
            SetmealDto setmealDto=new SetmealDto();
            /*复制基本信息到dto对象中去 以完善dto  page里的records保存着setmeal的基本信息*/
            BeanUtils.copyProperties(item,setmealDto);
            /*获得id*/
            Long categoryId = item.getCategoryId();
            /*根据id得到category分类对象  分类有菜品分类 套餐分类 tpye分别为 1 2*/
            /**调用ics查询category对象*/
            Category category = ics.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(records1); /*将stream流操作后得到的dto的records存到dtopage里去*/
        return R.success(setmealDtoPage);
    }

    /**
     * 删除
     * 要触碰到两个表
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
       /*需要在service里创一个方法*/
       ss.deleteWithDish(ids);
        return R.success("套餐删除成功");
    }

    /**
     * 查询
     * 根据条件查询套餐、就是在儿童套餐里查询到儿童套餐A，儿童套餐B
     * @param setmeal
     * @return
     */
    @GetMapping("/list")        /*当不是json格式时不用加RequestBody*/
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        /*获取套餐分类该分类的id 先判断是否存在  儿童套餐这个大分类的id*/
        lambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        /*查询所有符合条件的套餐 从大分类里查询到在这下面所有的套餐*/
        List<Setmeal> setmealList=ss.list(lambdaQueryWrapper);

        return R.success(setmealList);
    }
}
