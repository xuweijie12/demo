package com.xwj.Controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xwj.Service.UserService;
import com.xwj.pojo.User;
import com.xwj.util.R;
import com.xwj.util.SMSUtils;
import com.xwj.util.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService us;

    /**
     * 发送验证码
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        /*获取手机号*/
        String phone = user.getPhone();
        /*如果手机号不为空就随机生成4位验证码 注意是lang型那个*/
        if (StringUtils.isNotEmpty(phone)){

            /*随机生成4位验证码*/
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);  /*可以直接在控制台看到验证码*/
            /*当真的需要发送验证码就下面这行代码*/
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            /**将生成的验证码存到session以方便后续进行比对*/
            session.setAttribute(phone,code);  /**key设置为phone 内容为code*/
            return R.success("手机验证码发送成功");
        }
        return R.error("验证码发送失败");

    }

    /**
     * 移动端用户登录
     * @param map       用map是因为session存的是有key的  key为phone  内容为code
     * @param session
     * @return
     */

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);

        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
      /*  if(codeInSession != null && codeInSession.equals(code)){*/
            //如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);

            User user = us.getOne(queryWrapper);
            if(user == null){
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                us.save(user);
            }
            session.setAttribute("user",user.getId());  /*记得将用户的Id传给session以进行更具用户来展示页面*/
            return R.success(user);
      /*  }*/
       /* return R.error("登录失败");*/
    }
}
