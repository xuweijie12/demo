package com.xwj.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@Slf4j
@ResponseBody  /*因为会接收json数据*/
                                    /*拦截对应的Controller*/
@ControllerAdvice(annotations = {RestController.class, Controller.class})  /*获取有对应标签的文件里的错误进行处理*/
public class GlobalException {
    /**
     * 这里是异常处理器
     * */

/*创建一个标签把所sql错误信息方进去  这里处理的是用户名冲突的问题  */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandle(SQLIntegrityConstraintViolationException ex){
//        因为前面的错误信息是关于sql的错误 并不是用户名冲突的错误所以需要在错误提示后面获得关键词
        if (ex.getMessage().contains("Duplicate entry")){/*判断错误信息是否有此关键词*/
            String[] split=ex.getMessage().split(" ");  /*因为错误信息是以空格分隔的所以将其以空格分隔并形成一个数组*/
            String msg="此用户名："+split[2]+"已存在"; /*获取错误信息中的动态信息拼成错误提示*/
           return R.error(msg);
        }
              return R.error("未知错误");
    }


    /**
     * 这里是自定义异常处理器
     * */

    /*创建一个标签把所sql错误信息方进去  这里处理的是用户名冲突的问题  */
    @ExceptionHandler(customException.class)
    public R<String> exceptionHandle(customException ex){
//        因为前面的错误信息是关于sql的错误 并不是用户名冲突的错误所以需要在错误提示后面获得关键词
        return R.error(ex.getMessage());
    }
}
