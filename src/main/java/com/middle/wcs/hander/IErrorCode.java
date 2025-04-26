package com.middle.wcs.hander;

/**
 * @description:
 * @fileName: BaseErrorCode.java
 * @author: jason
 * @createAt: 2020/8/20 8:39 上午
 * @updateBy: jason
 * @remark: Copyright
 */
public interface IErrorCode {

    /**
     * 设置前缀
     *
     * @return
     */
    String prefix();

    /**
     * 获取Code
     *
     * @return
     */
    String getCode();

    /**
     * 获取错误信息
     *
     * @return
     */
    String getMsg();

}
