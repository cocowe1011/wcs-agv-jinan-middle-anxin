package com.middle.wcs.login.service.impl;

import com.middle.wcs.hander.BusinessException;
import com.middle.wcs.hander.CommonErrorCode;
import com.middle.wcs.user.dao.UserInfoMapper;
import com.middle.wcs.user.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 登录失败次数管理服务
 * 使用独立事务处理失败次数更新，避免主事务回滚影响
 * @author 文亮
 */
@Service
@Slf4j
public class LoginFailCountService {
    
    @Resource
    private UserInfoMapper userInfoMapper;
    
    /**
     * 在新事务中更新登录失败次数
     * @param userCode 用户代码
     * @return 剩余尝试次数，如果返回0表示账号已被锁定
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int updateLoginFailCount(String userCode) {
        log.info("更新用户 {} 的登录失败次数", userCode);
        
        // 重新查询用户信息，获取最新的失败次数
        UserInfo queryUser = new UserInfo();
        queryUser.setUserCode(userCode);
        List<UserInfo> users = userInfoMapper.selectUserList(queryUser);
        
        if (users.size() != 1) {
            throw BusinessException.build(CommonErrorCode.NOT_EXITS_USER_CODE);
        }
        
        UserInfo user = users.get(0);
        int currentFailCount = user.getLoginFailCount() == null ? 0 : user.getLoginFailCount();
        int newFailCount = currentFailCount + 1;
        
        log.info("用户 {} 当前失败次数: {}, 更新后: {}", userCode, currentFailCount, newFailCount);
        
        user.setLoginFailCount(newFailCount);
        
        // 失败3次锁定账号
        if (newFailCount >= 3) {
            user.setIsLocked(1);
            userInfoMapper.updateLoginFailCount(user);
            log.warn("用户 {} 登录失败次数达到3次，账号已被锁定", userCode);
            return 0; // 账号已锁定，返回0
        } else {
            userInfoMapper.updateLoginFailCount(user);
            int remainingChances = 3 - newFailCount;
            log.info("用户 {} 还有 {} 次尝试机会", userCode, remainingChances);
            return remainingChances; // 返回剩余次数
        }
    }
    
    /**
     * 在新事务中重置登录失败次数
     * @param userCode 用户代码
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetLoginFailCount(String userCode) {
        log.info("重置用户 {} 的登录失败次数", userCode);
        
        UserInfo queryUser = new UserInfo();
        queryUser.setUserCode(userCode);
        List<UserInfo> users = userInfoMapper.selectUserList(queryUser);
        
        if (users.size() == 1) {
            UserInfo user = users.get(0);
            user.setLoginFailCount(0);
            user.setIsLocked(0);
            userInfoMapper.updateLoginFailCount(user);
            log.info("用户 {} 登录失败次数已重置，账号已解锁", userCode);
        }
    }
} 