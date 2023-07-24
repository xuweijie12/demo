package com.xwj.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xwj.dto.SetmealDto;
import com.xwj.pojo.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
         public void saveWithDish(SetmealDto setmealDto);

         public void deleteWithDish(List<Long> ids);
}
