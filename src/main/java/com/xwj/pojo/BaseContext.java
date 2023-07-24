package com.xwj.pojo;


/**
 * 这里是基于ThreadLocal的类 用于存放一些基本信息 如创造时间，人员 修改时间，人员 类似的值
 * 每一次发送的http请求都会分配一个线程 所以do filter 和update等方法都是在同一线程(thread)里的
 * 会给每一个ThreadLocal提供一个存储空间    ThreadLocal是thread的一个属性
 * */

public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();
      /**
       * 在这定义两个方法 设置和获取方法  在别处运用session可以获取当前用户的ID 以方便不能获取Session的地方去用get获得ID
       * */
      /*设置当前用户Id 用在loginCheckFilter*/
    public  static void setCurrentId(Long id){
        threadLocal.set(id);
    }
              /*获取当前用户Id 用在MyMetaDataHandler*/
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
