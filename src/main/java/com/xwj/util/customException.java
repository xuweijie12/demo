package com.xwj.util;


/**
 * 自定义的业务异常  需要继承RuntimeException
 * */
public class customException extends RuntimeException {
         public customException (String message){  /*把提示信息传进来*/
             super(message);
         }/*创建一个构造方法*/
}
