package com.xwj.Controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xwj.Service.IemployeeService;
import com.xwj.pojo.Employee;
import com.xwj.util.R;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang.StringUtils;
import org.apache.tomcat.util.digester.Digester;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class employeeController {
    @Autowired
    private IemployeeService ies;
    /**员工登录
     因为数据是以post方式传去的所以用Post 常用于添加操作
     @RequestBody 请求体用于传少的数据 也是用与接收json形式的数据                                 把后面获取的值填到名字为employee里去
     这里还设置了req是为了把登录进去的用户的ID保存到session中去 方便后续取出 req.getSession.setAttribute("employee",emp.getId)
     */
    @PostMapping("/login")        /*与这个大的类组成 employee/login 路径 用到过滤器时会用到*/
    public R<Employee> login(HttpServletRequest req, @RequestBody Employee employee){
        /*将从页面获取到的数据进行md5加密 DigestUtils.md5DigestAsHex */
        String password= employee.getPassword();     /*把密码数据化*/
        password= DigestUtils.md5DigestAsHex(password.getBytes());

        /*把传来用户名进到数据库中进行比对 查询数据库 注意：已经在数据库那将用户名设为唯一*/
        LambdaQueryWrapper<Employee> queryWrapper= new LambdaQueryWrapper<>();
        /**
         * Employee::getUsername是lambda表达式用于获取实体类中的username字段
         * 之后用employee.getUsername()获取 username的具体值用于与数据库的记录进行比较
         * */
        queryWrapper.eq(Employee::getUsername,employee.getUsername());  /*这两行是定义查询条件 用户名等于数据库里的用户名*/
        Employee employee1=ies.getOne(queryWrapper);  /*调用查询一个丰富条件上面已定义*/
        /*employee1是从数据库里获得到用户对象 如果匹配成功其就有值*/
        /*如果查询不到就返回错误*/
        if (employee1==null){
            return R.error("用户名错误");
        }
        /*进行密码比对 如果不对则返回错误 */ /*前面是数据库里的密码 后面是页面传来的密码*/
        if (!employee1.getPassword().equals(password)){
            return R.error("密码错误");
        }
        /*判断是否禁用*/
        if(employee1.getStatus()==0){
            return R.error("该用户已禁用");
        }
        /*登录成功*/                       /*!!!把数据库里的用户id存到session中去 方便后续取出*/
        req.getSession().setAttribute("employee",employee1.getId());
        return R.success(employee1);
    }
   /*员工退出*/
    @PostMapping("/logout") /*因为传出的类型是字符串的提示信息所以为字符串类型*/
    public R<String> logout(HttpServletRequest req){
        /*清空对应的session*/
        req.getSession().removeAttribute("employee");
        return R.success("退出成功");

    }
    /*新增员工 因为前台页面那边只需要判断R的code 就是String类型的  因为是json数据所以是@RequestBody*/
    @PostMapping
    public R<String> add(HttpServletRequest req,@RequestBody Employee employee){
        /**注意用户名是唯一的 假如添加已存在的用户名就会出现SQLIntegrityConstraintViolationException的错误
        因此需要处理这些异常需要搭建一个全局的异常处理类
         * */
            /*因为在页面中并不是所有数据都可以在页面中填写 所以需要后台自动生成*/
        /*先设置密码 MD5加密*/
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        /** 因为修改时间和修改人 是在许多地方都需要重新获取的 所以这些被称为公共字段 解决方法是在实体类里对应的字段加个 @TableField 具体看pojo
         *  之后创建 一个元数据对象处理器来处理这些数据   MyMetaDataHandler  在那需要实现MetaObjectHandler 接口
          */
       /* *//*设置创造时间和修改时间*//*
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        *//*获取添加人员以及修改人员*//*
        *//*先获取现在登录的人的id*//*
        Long empid =(Long) req.getSession().getAttribute("employee");
        employee.setCreateUser(empid);
        employee.setUpdateUser(empid);*/
        /*调用service里的方法*/
        ies.save(employee);
        return R.success("添加成功");
    }
    @GetMapping("/page")
    public R<Page> page11(int page,int pageSize,String name){
            /*log.info("page={},pageSize={},name={}",page,pageSize,name);*/
            /*搭建分页构造器  这里是分页*/
          Page page1=new Page<>(page,pageSize); /*把分页的数据传到Page中*/

        /*以下是条件*/
            /*搭建条件构造器  注意StringUtils是common包下的*/
           LambdaQueryWrapper<Employee> lambdaQueryWrapper=new LambdaQueryWrapper<>();
           lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name); /*获取数据库里的名字与输入的name*进行对比*/
           /*!!!添加排序的条件  Desc降序*/
           lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);/*按更新时间进行排序 orderByDesc*/
           ies.page(page1,lambdaQueryWrapper);

            return R.success(page1);
    }
    @PutMapping      /*修改*/
    public R<String> update(HttpServletRequest req,@RequestBody Employee employee){
        log.info(employee.toString());

        /*获取修改时间和修改用户*/
        employee.setUpdateTime(LocalDateTime.now());
        Long empId = (Long) req.getSession().getAttribute("employee");
        employee.setUpdateUser(empId);  /*获得进行修改操作的用户的Id*/  /*注意的是这里的Long型是19位的 js只可以读16 所以会出错*/
                                                                    /*进而无法修改  对象映射器将long型转位String解决了这个问题*/
                                          /**            JacksonObjectMapper
                                           * 解决方法步骤一是导入一个对象映射器 已经导了 在util里
                                           * 步骤二是在WebMvcConfig扩展mvc框架的消息转换器 作用是把返回的java对象结果传为json
                                           *
                                           * /
                                          /*在前端按照行获得ID 这里后端放进的是对象*/
        ies.updateById(employee);
        return R.success("修改成功");
    }

       /*修改操作*/
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee=ies.getById(id);
        if (employee!=null){
            return R.success(employee);
        }else return R.error("数据出错");
    }
}
