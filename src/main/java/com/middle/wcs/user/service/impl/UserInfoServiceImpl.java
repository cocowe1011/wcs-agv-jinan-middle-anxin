package com.middle.wcs.user.service.impl;

import com.middle.wcs.hander.BusinessException;
import com.middle.wcs.hander.CommonErrorCode;
import com.middle.wcs.user.entity.UserInfo;
import com.middle.wcs.user.dao.UserInfoMapper;
import com.middle.wcs.user.service.UserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @classDesc: 业务逻辑:(UserInfo)
 * @author: makejava
 * @date: 2023-06-27 20:58:08
 * @copyright 作者
 */
@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    private UserInfoMapper userInfoMapper;


    @Override
    public Integer save (UserInfo userInfo) {
        // 先判断userCode有没有被注册过
        List<UserInfo> entity =  userInfoMapper.selectUserList(userInfo);
        if(entity.size() > 0) {
            throw BusinessException.build(CommonErrorCode.DUPLICATE_USER_CODE);
        }
        return userInfoMapper.insert(userInfo);
    }

    @Override
    public Boolean verifyName(UserInfo userInfo) {
        List<UserInfo> entity =  userInfoMapper.selectUserList(userInfo);
        if(entity.size() < 1) {
            throw BusinessException.build(CommonErrorCode.NOT_EXITS_USER_CODE);
        }
        return userInfo.getUserName().equals(entity.get(0).getUserName());
    }

    @Override
    public Integer updatePassword(UserInfo userInfo) {
        return userInfoMapper.updatePassword(userInfo);
    }

    /**
     * verifyPassword
     */
    @Override
    public Boolean verifyPassword(UserInfo userInfo) {
        List<UserInfo> entity =  userInfoMapper.selectUserList(userInfo);
        if(entity.size() < 1) {
            throw BusinessException.build(CommonErrorCode.NOT_EXITS_USER_CODE);
        }
        return userInfo.getUserPassword().equals(entity.get(0).getUserPassword());
    }
}
