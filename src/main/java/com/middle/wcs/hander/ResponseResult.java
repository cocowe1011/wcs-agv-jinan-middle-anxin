package com.middle.wcs.hander;


import lombok.Data;

import java.io.Serializable;

/**
 * 全局统一返回结果类
 *
 */
@Data
public class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = -5858147992383769655L;

    //返回码
    private String code;

    //返回消息
    private String message;

    //返回数据
    private T data;

    public ResponseResult(){}

    // 返回数据
    protected static <T> ResponseResult<T> build(T data) {
        ResponseResult<T> result = new ResponseResult<T>();
        if (data != null) {
            result.setData(data);
        }
        return result;
    }

    public static <T> ResponseResult<T> build(T body, String code, String message) {
        ResponseResult<T> result = build(body);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> ResponseResult<T> build(T body, ResultCodeEnum resultCodeEnum) {
        ResponseResult<T> result = build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static<T> ResponseResult<T> success(){
        return ResponseResult.success(null);
    }

    /**
     * 操作成功
     * @param data  baseCategory1List
     * @param <T>
     * @return
     */
    public static<T> ResponseResult<T> success(T data){
        ResponseResult<T> result = build(data);
        return build(data, ResultCodeEnum.SUCCESS);
    }

    public static<T> ResponseResult<T> fail(){
        return ResponseResult.fail(null);
    }

    /**
     * 操作失败
     * @param data
     * @param <T>
     * @return
     */
    public static<T> ResponseResult<T> fail(T data){
        ResponseResult<T> result = build(data);
        return build(data, ResultCodeEnum.FAIL);
    }

    public ResponseResult<T> message(String msg){
        this.setMessage(msg);
        return this;
    }

    public ResponseResult<T> code(String code){
        this.setCode(code);
        return this;
    }
}