package com.xwj.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xwj.Service.ICategoryService;
import com.xwj.pojo.Category;
import com.xwj.util.R;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private ICategoryService ics;

    @PostMapping      //String : 返回的是提示信息
    public R<String > save(@RequestBody Category category){
        log.info("category{}",category);
        ics.save(category);
        return R.success("添加成功");
    }
    @GetMapping("/page")  //R<Page> 最终返回的是Page对象
    public R<Page> page(int page,int pageSize){
        /*调用分页构造器*/
        Page<Category> page1=new Page<>(page,pageSize);
        /*构造条件 按sort 排序*/
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);  //升序
        ics.page(page1,lambdaQueryWrapper);
        return R.success(page1);
    }

    @DeleteMapping
    public R<String> remove(Long id){
        log.info("删除分类的id为:{}",id);
        ics.remove(id);   /*这里运用的是Servic里已经定义好的方法*/
        return R.success("删除分类成功");
    }
    @PutMapping    /*修改  前端页面传来一个数据对象 所以这里需要创建一个参数来承接*/
    public R<String> update(@RequestBody  Category category){/*json格式的数据需要加个请求体的标签*/
      ics.updateById(category);
      return R.success("信息修改成功");
    }

    /**
     *  按条件查询   按前台传来的类型进行查询展示于下拉框中
     * @param category
     * @return
     * 当为菜品分类时 前台传来的Type是1
     * 当为套餐分类时 前台传来的Type是2
     * 也可以直接全部类别都查出来
     */
    @GetMapping("/list")           /*把category当作类对象 获取*/
    public R<List<Category>> list(Category category){
        /*条件构造器*/
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //添加条件                菜单类型不为空              要保证前台传进来的类型数字与后台数据一样  1 菜品分类 2 套餐分类
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        /*添加排序条件*/
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getCreateTime);
        List<Category> list = ics.list(queryWrapper);/*调用Base的查询所有并把条件放进去*/

        return R.success(list);
    }

}
