package com.middle.wcs.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
/**
 * @classDesc: 实体类:(UserInfo)
 * @author: makejava
 * @date: 2023-06-27 20:58:16
 * @copyright 作者
 */
@Data
@TableName("user_info")
public class UserInfo {
    /**
    * 用户主键id
    */    
    @TableId
    private Long userId;
            
    /**
    * 用户姓名
    */    
    private String userName;
                
    /**
    * 用户登录code
    */    
    private String userCode;
                
    /**
    * 用户密码
    */    
    private String userPassword;
    
}
