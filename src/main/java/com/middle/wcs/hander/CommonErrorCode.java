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
    /**
     * 账号已被锁定
     */
    ACCOUNT_LOCKED("0004", "账号已被锁定，请联系管理员解锁"),
    /**
     * 密码错误次数限制
     */
    PASSWORD_ERROR_LIMIT("0005", "用户名或密码错误，您还有$1次尝试机会，3次错误后账号将被锁定"),
    /**
     * 密码错误次数过多
     */
    PASSWORD_ERROR_TOO_MANY("0006", "密码错误次数过多，您的账号已被锁定，请联系管理员解锁"),
    
    /**
     * 文件上传相关错误码
     */
    FILE_EMPTY("0101", "上传的文件不能为空"),
    FILE_NAME_EMPTY("0102", "文件名不能为空"),
    FILE_TYPE_NOT_ALLOWED("0103", "只允许上传 .txt 或 .log 格式的文件"),
    FILE_SIZE_TOO_LARGE("0104", "文件大小不能超过50MB"),
    FILE_UPLOAD_FAILED("0105", "文件上传失败"),
    NO_FILES_SELECTED("0106", "没有选择要上传的文件"),
    TOO_MANY_FILES("0107", "单次最多只能上传10个文件"),
    ;
    private String code;
    private String msg;

    @Override
    public String prefix() {
        return "common";
    }
}
