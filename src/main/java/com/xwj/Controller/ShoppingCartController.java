package com.xwj.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xwj.Service.ShoppingCartService;
import com.xwj.pojo.BaseContext;
import com.xwj.pojo.ShoppingCart;
import com.xwj.util.R;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService scs;

    /**
     * 添加
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        /*1.先获取用户id 知道是哪个用户用的 设置到购物车里去*/
        /**用BaseContext 来获取当前的用户id 因为登录的时候就已经把id存进了那里*/
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        /*2.判断是菜品还是套餐*/
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, currentId);
        /*获取dishId 假如它不为空就是菜品 为空的话就为套餐*/
        Long dishId = shoppingCart.getDishId();
        if (dishId != null) {
            lambdaQueryWrapper.eq(ShoppingCart::getDishId, dishId);  /*用dishId进行比对ShoppingCart里的DishId 相同的就可以*/
        } else {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //3.查询当前菜品或者套餐是否在购物车中
        //SQL:select * from shopping_cart where user_id = ? and dish_id/setmeal_id = ?
        //有了前面的查询条件 用scs进行查询
        ShoppingCart cart = scs.getOne(lambdaQueryWrapper);
        //因为购物车只有一个所有getOne
        //  这时添加的菜品或者套餐的信息以存到cart中

        /*对数量进行查询，如何之前没添加过cart为空的话就设置为一假如不为0的话就继续+1*/
        if (cart != null) {
            Integer number = cart.getNumber();
            cart.setNumber(number + 1);
            /*更新*/
            scs.updateById(cart);
        } else {
            /*为空就代表之前没有*/
            /*就要另在shoppingCart里设置数量1 不能在cart里设置*/
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            scs.save(shoppingCart);  /*添加进去*/
            cart = shoppingCart; //将新添加的数据添加到cart里去
        }
        return R.success(cart);


    }

    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        lambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = scs.list(lambdaQueryWrapper);
        return R.success(shoppingCartList);

    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        //SQL:delete from shopping_cart where user_id = ?

        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        scs.remove(lambdaQueryWrapper);
        return R.success("清空购物车成功");
    }

    /**
     * 删除
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(ShoppingCart shoppingCart) {
        /*1.先获取用户id 知道是哪个用户用的 设置到购物车里去*/
        /**用BaseContext 来获取当前的用户id 因为登录的时候就已经把id存进了那里*/
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);

            ShoppingCart cart = scs.getOne(queryWrapper);
            if (cart!=null){
                Integer number = cart.getNumber();
                if (number>1){
                    cart.setNumber(number-1);
                    scs.updateById(cart);
                } else if (number==1) {
                    //scs.removeById(cart);
                    cart.setNumber(0);
                    scs.updateById(cart);
                }
        }
       return R.success(cart);

    }
}
