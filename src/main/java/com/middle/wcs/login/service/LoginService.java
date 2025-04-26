package com.middle.wcs.login.service;


import com.middle.wcs.login.entity.LoginDTO;
import com.middle.wcs.user.entity.UserInfo;

/**
 * 功能描述: (对外提供预约信息的中台接口)
 * @author 文亮
 * @since 2022年5月13日14:07:01
 */
public interface LoginService {

    /**
     * 登录
     * @param loginDTO 登录入参
     * @return 登录
     */
    UserInfo login(LoginDTO loginDTO);

}
