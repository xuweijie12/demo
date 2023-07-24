package com.xwj.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  /*配置文件需要加上@Configuration*/
public class mybatisPlus_config {
   /*拦截器Interceptor*/
    /**
     * 配置MP的分页查询插件
     * */
    @Bean   /*记得加个bean*/
    public MybatisPlusInterceptor mpi(){
        /*定义拦截器*/
        MybatisPlusInterceptor mpi=new MybatisPlusInterceptor();
        /*添加具体的拦截器                   用于分页查询*/
        mpi.addInnerInterceptor(new PaginationInnerInterceptor());
        return mpi;
    }

}
