package com.middle.wcs.hander;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @classDesc: 功能描述:(业务异常-需要捕获并处理为error，需要业务方关心的异常)
 * @author: 作者
 * @date: 2020/4/7 11:19
 * @copyright 作者
 */
@Slf4j
@RestControllerAdvice
public class ExceptionAdvice {
    public static final int MAX_DEEP = 4;

    /**
     * 处理业务异常
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessException.class)
    public ResponseResult handleBusinessException(HttpServletRequest request, BusinessException e) {
        log.warn("业务异常(BusinessException),{}", e);
        ResponseResult result = new ResponseResult();
        result.setMessage(e.getMessage());
        result.setCode(e.getCode());
        return result;
    }


    public BusinessException getNestException(Exception e) {
        Throwable throwable = e;
        int deep = 0;
        while (!(throwable instanceof BusinessException)) {
            throwable = throwable.getCause();
            deep++;
            if (deep > MAX_DEEP) {
                return null;
            }
            if (throwable == null) {
                return null;
            }
        }
        return (BusinessException) throwable;
    }


    /**
     * 处理异常
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseResult handleException(HttpServletRequest request, Exception e) {
        BusinessException businessException = getNestException(e);
        if (businessException != null) {
            return handleBusinessException(request, businessException);
        }
        log.error("通用异常stack info={}",getExceptionStack(e));
        ResponseResult result = new ResponseResult();
        result.setMessage(e.getMessage());
        return result;
    }

    public static String getExceptionStack(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }


}
