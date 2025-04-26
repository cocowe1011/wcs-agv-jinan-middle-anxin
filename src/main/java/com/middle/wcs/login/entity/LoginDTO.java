package com.middle.wcs.login.entity;

import lombok.Data;

/**
 * @classDesc: 实体类:(UserInfo)
 * @author: makejava
 * @date: 2023-06-27 20:58:16
 * @copyright 作者
 */
@Data
public class LoginDTO {

    /**
    * 用户登录code
    */    
    private String userCode;
                
    /**
    * 用户密码
    */    
    private String userPassword;
    
}
