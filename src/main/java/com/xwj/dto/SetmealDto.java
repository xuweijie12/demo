package com.xwj.dto;

import com.xwj.pojo.Setmeal;
import com.xwj.pojo.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
