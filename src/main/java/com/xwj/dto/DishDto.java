package com.xwj.dto;

import com.xwj.pojo.Dish;
import com.xwj.pojo.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据传输对象 一般用于展示层与服务层之间的数据传输
 */
@Data
public class DishDto extends Dish {    /*它继承了Dish 又自行按照展示层进行扩展*/
                  /*这里又扩展了DishFlavor*/
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
