package com.middle.wcs.user.service;

import com.middle.wcs.user.entity.UserInfo;

/**
 * @classDesc: 业务接口:(UserInfo)
 * @author: makejava
 * @date: 2023-06-27 20:58:12
 * @copyright 作者
 */
public interface UserInfoService {
   
    Integer save (UserInfo userInfo);

    Boolean verifyName(UserInfo userInfo);

    Integer updatePassword(UserInfo userInfo);

    Boolean verifyPassword(UserInfo userInfo);
}
