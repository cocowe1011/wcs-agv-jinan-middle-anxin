package com.middle.wcs.login.controller;

import com.middle.wcs.hander.ResponseResult;
import com.middle.wcs.login.entity.LoginDTO;
import com.middle.wcs.login.service.LoginService;
import com.middle.wcs.user.entity.UserInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 上报网站后台登陆接口
 * @author 文亮
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Resource
    private LoginService loginService;

    @RequestMapping("/login")
    public ResponseResult<UserInfo> login(@RequestBody LoginDTO loginDTO) {
        return ResponseResult.success(loginService.login(loginDTO));
    }
}
