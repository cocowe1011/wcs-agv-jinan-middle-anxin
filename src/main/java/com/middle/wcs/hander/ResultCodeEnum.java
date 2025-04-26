package com.middle.wcs.hander;

import lombok.Getter;

/**
 * @author 文亮
 * @description TODO
 * @date 2023-06-2023/6/10-10:25
 */
@Getter
public enum ResultCodeEnum {
    SUCCESS("200","成功"),
    FAIL("201", "失败")
    ;

    private String code;

    private String message;

    private ResultCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
