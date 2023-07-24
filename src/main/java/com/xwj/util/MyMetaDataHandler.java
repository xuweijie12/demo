package com.xwj.util;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.xwj.pojo.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

/**
 * 自定义的元数据对象处理器
 * */
@Component  /*设置成bean 被spring管理*/
@Slf4j
public class MyMetaDataHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
        /** 在MetaObjectHandler无法获取session所以创造了pojo=> BaseContext来动态获取用户对象 在filter里获取session并把id存入线程中
        * 后在这里获取id  写BaseContext.getCurrentId()
        *  */
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurrentId());

    }
}
