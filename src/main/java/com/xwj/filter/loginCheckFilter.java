package com.xwj.filter;

import com.alibaba.fastjson.JSON;
import com.xwj.pojo.BaseContext;
import com.xwj.util.R;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 给拦截器取名  拦截所以路径的信息  !!!注意若要让拦截器生效需要在启动类加一个注解@ServletComponentScan
 * */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class loginCheckFilter implements Filter {
    /*创建路径匹配器，并且支持通配符*/
   public static final AntPathMatcher pathMatcher=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
         /*因为后面需要用到 req和res所以需要把servletRequest和servletResponse强制转型*/
        HttpServletRequest req=(HttpServletRequest) servletRequest;
        HttpServletResponse res=(HttpServletResponse) servletResponse;
        /*获取用户请求的uri   req.getRequestURI     */
        String requestUrl=req.getRequestURI();
            log.info("拦截到客户请求为{}",requestUrl);
        /*设置用户可以访问的ur*/
        String uris []=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg", /*移动端发送短信*/
                "/user/login"   /*移动端登录*/
        };
        /*判断此次访问的路径是否需要处理 将可以访问的路径与用户访问的进行对比 一样为true*/
        boolean check=check(uris,requestUrl);
        /*若不需要处理的就是放行*/
        if (check){  /*true 就放行*/
            log.info("请求是{}不需要处理",requestUrl);
            filterChain.doFilter(req,res);
            return;  /*别忘记return*/
        }
        /**若不匹配则需看用户是否已登录  网页用户登录*/
        if (req.getSession().getAttribute("employee")!=null){

/*
            long id=Thread.currentThread().getId();  每一次发送的http请求都会分配一个线程 所以do filter 和update等方法都是在同一线程里的
*/
              /**
               * 在这里设置给线程储存值是因为在这里的话代表用户已经登录了
               * */
            Long id = (Long) req.getSession().getAttribute("employee");
            BaseContext.setCurrentId(id);        /*在实体类里的BaseContext*/
            filterChain.doFilter(req,res);        /*放行*/
            return;   /*别忘记return*/
        }
        /**移动端用户登录*/
        if (req.getSession().getAttribute("user")!=null){

/*
            long id=Thread.currentThread().getId();  每一次发送的http请求都会分配一个线程 所以do filter 和update等方法都是在同一线程里的
*/
            /**
             * 在这里设置给线程储存值是因为在这里的话代表用户已经登录了
             * */
            Long id = (Long) req.getSession().getAttribute("user");
            BaseContext.setCurrentId(id);        /*在实体类里的BaseContext*/
            filterChain.doFilter(req,res);        /*放行*/
            return;   /*别忘记return*/
        }
                 /*都不行则爆出错误信息*/
        res.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }
    public boolean check(String [] uris,String requestUrl){
        for (String uri:uris){  /*遍历urls 把每次结果放进url*/
            boolean match=pathMatcher.match(uri,requestUrl);  /*用每一个可以访问的url与用户访问的url进行匹配*/
            if (match){ /*匹配就true*/
                return true;
            }
        }
        return false;
    }
}
