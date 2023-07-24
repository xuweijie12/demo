package com.xwj.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String name;

    private String password;

    private String phone;

    private String sex;

    private String idNumber;  /*这里使用驼峰命名而数据库那使用的是下划线 不会出错的原因是在application.yml里已经配置*/

    private Integer status;

    /**
     * 公共字段
     * */

    @TableField(fill = FieldFill.INSERT) /*插入时即添加时填充*/
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)  /*插入和修改时都填充*/
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;



}
