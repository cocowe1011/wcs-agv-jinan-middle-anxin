package com.middle.wcs.hander;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @description:
 * @fileName: CommonErrorCode.java
 * @author: jason
 * @createAt: 2020/8/20 10:02 上午
 * @updateBy: jason
 * @remark: Copyright
 */
@Getter
@AllArgsConstructor
public enum CommonErrorCode implements IErrorCode {

    /**
     * 已有重复账号
     */
    DUPLICATE_USER_CODE("0001", "已有重复账号！"),
    /**
     * 未查询到账户信息
     */
    NOT_EXITS_USER_CODE("0002", "未查询到账户信息！"),
    /**
     * 密码错误
     */
    PASSWORD_ERROR("0003", "密码错误！"),
    ;
    private String code;
    private String msg;

    @Override
    public String prefix() {
        return "common";
    }
}
