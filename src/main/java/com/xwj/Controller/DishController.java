package com.xwj.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xwj.Service.DishFlavorService;
import com.xwj.Service.DishService;
import com.xwj.Service.ICategoryService;
import com.xwj.dto.DishDto;
import com.xwj.pojo.Category;
import com.xwj.pojo.Dish;
import com.xwj.pojo.DishFlavor;
import com.xwj.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService ds;
    @Autowired
    private DishFlavorService dfs;
    @Autowired
    private ICategoryService ics;

    /**
       新增菜品
     * 这里注意因为展示层（浏览器）传过来的数据与实体类的类型并不是完全符合的所以需要创建一个dto 数据传输对象 一般用于展示层与服务层之间的数据传输
     * @return
     * 这里需要操作两张表！！
     */
    @PostMapping           /*因为是传来的数据是json数据 所以千万记得加@RequestBody 将参数封装到dishDto里去*/
    public R<String> save(@RequestBody DishDto dishDto){

        ds.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 分页查询
     * 要注意dish实体类里的菜品分类不是文字只是分类的ID  所以配合dto进行获取文字
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        /*原来的records查出的是不带菜品分类的，所以把之前的records提取出来处理一下加上菜品分类，再注入到DishDto所构造的分页查询中
        * 简单来说，就是把页面数据取出来，然后把名字加上去，然后一起装好，显示出去*/
        /*构造分页构造器*/
        Page<Dish> pageInfo=new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage =new Page<>();  /*因为需要把类型名字显示出来所以要用到dto*/
        /*条件构造器*/
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        /*添加过滤条件*/         /*传来的name不为空且与数据库的name一样*/
        queryWrapper.like(name!=null,Dish::getName,name);
        /*添加排序条件*/
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        ds.page(pageInfo,queryWrapper);  /*调用mp里的page方法*/
        /*对象拷贝*/
        /**原来的records查出的是不带菜品分类的,所以忽略，之后所以把之前的records提取出来处理一下加上菜品分类，再注入到DishDto所构造的分页查询中
         * 简单来说，就是把页面数据取出来，然后把名字加上去，然后一起装好，显示出去
         * */                      /**不复制records是因为两个page对象的泛型不一样*/
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");/*忽略records  是Page里的*/
        List<Dish> records=pageInfo.getRecords();
        List<DishDto>list=records.stream().map((item)->{
            DishDto dishDto = new DishDto();/*先创建一个dishDto对象*/
//            因为item里存放着除records的数据所以需要复制
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();/*获取类型id*/
            /*根据id查询分类对象*/
            Category category = ics.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();  /*获取分类名*/
                dishDto.setCategoryName(categoryName);    /*将分类名赋值给DishDto里的CategoryName（这个是扩展的）  */
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);  /*最后将这个列表赋给dto的分页查询*/
        return R.success(dishDtoPage);
/**原来的records查出的是不带菜品分类的,所以忽略，之后所以把之前的records提取出来处理一下加上菜品分类，再注入到DishDto所构造的分页查询中
 * 简单来说，就是把页面数据取出来，然后把名字加上去，然后一起装好，显示出去    ！！
 * */
    }

    /**
     * 根据id查询菜品的信息和口味信息  数据回显
     * 因为查询需要重新菜品信息的表也要查询口味的表所以在控制数据库之前又需要在Service里创建一个查询两个表的方法
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDetail(@PathVariable Long id){

        DishDto dishDto = ds.getWithFlavor(id);


        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping          /*因为是传来的数据是json数据 所以千万记得加@RequestBody 将参数封装到dishDto里去*/
    public R<String> update(@RequestBody DishDto dishDto){

        ds.updateWithFlavor(dishDto);
        return R.success("修改菜品成功");
    }
    /* *//**
     * 根据菜品的类别分类查询出其所有的菜品
     * 按照CategoryId来查询它所包含的菜品
     * @param dish
     * @return
     *//*
    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.eq(Dish::getStatus,1);*//*状态是启售的才查询*//**//*
        /*排序*//*
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = ds.list(lambdaQueryWrapper);

        return R.success(list);
    }*/

   /**
    * 更加完善的版本
    * @param dish
    * @return
    */
   @GetMapping("/list")            /*参数依然是Dish*/
   public R<List<DishDto>> list(Dish dish){
       LambdaQueryWrapper<Dish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
       lambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
       lambdaQueryWrapper.eq(Dish::getStatus,1);/*状态是启售的才查询*/
       /*排序*/
       lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
       List<Dish> list = ds.list(lambdaQueryWrapper);
       /*因为需要查出菜品的口味数据 口味数据是属于另一个表的 所以要重新创一个List<dishDto> 用stream*/
       List<DishDto> dtoList=list.stream().map((item)->{
        /*创一个dto对象*/
           DishDto dishDto1 = new DishDto();
           //因为item里存放着原先的基本数据所以需要复制
           BeanUtils.copyProperties(item,dishDto1);
           Long categoryId = item.getCategoryId();/*获取类型id*/
           /*根据id查询分类对象*/
           Category category = ics.getById(categoryId);
           if (category!=null){
               String categoryName = category.getName();  /*获取分类名*/
               dishDto1.setCategoryName(categoryName);    /*将分类名赋值给DishDto里的CategoryName（这个是扩展的）  */
           }
           /*获取菜品的Id以方便对应上菜品的口味*/
           Long id = item.getId();
           /*查询口味表*/
           LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
           queryWrapper.eq(DishFlavor::getDishId,id);   /*根据口味查询dishId 用item 即dish */
           /*将口味赋值进dto里的dishFlavor口味列表里去  DishDto类里有扩展一个列表专门放口味*/
           //SQL:select * from dish_flavor where dish_id = ?
           /*调用dfs （service） 查询符合条件的口味表中的数据*/
           List<DishFlavor> dishFlavorList=dfs.list(queryWrapper);
           dishDto1.setFlavors(dishFlavorList);
           return dishDto1;
       }).collect(Collectors.toList());
       return R.success(dtoList);
   }
  /*  @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }*/

}
