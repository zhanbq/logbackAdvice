package com.rmxc.logconsumer.web.exception;

import com.rmxc.tech.dagger.base.enums.ApiCallReturnCodeEnum;
import com.rmxc.tech.dagger.runtime.base.exception.InternalException;
import com.rmxc.tech.dagger.runtime.web.bean.result.ApiBaseResult;
import com.rmxc.tech.dagger.runtime.web.validator.exception.ParamException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常处理
 *
 * @author renhao
 * @date 2020/04/15
 */
@Slf4j
@ControllerAdvice
public class DefExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object execute(HttpServletRequest httpServletRequest, Exception e) {
        ApiBaseResult apiBaseResult ;

        if (e instanceof InternalException) {
            return new ApiBaseResult(ApiCallReturnCodeEnum.ERROR.getCode(), e.getMessage());
        } else if (e instanceof ParamException) {
            return new ApiBaseResult(ApiCallReturnCodeEnum.PARAMS_ERROR.getCode(), e.getMessage());
        } else if (e instanceof MissingServletRequestParameterException) {
            return new ApiBaseResult(ApiCallReturnCodeEnum.PARAMS_ERROR.getCode(), ApiCallReturnCodeEnum.PARAMS_ERROR.getMessage());
        } else if (e instanceof HttpMessageNotReadableException) {
            log.info("请求体 Content-Type 错误");
            return new ApiBaseResult(ApiCallReturnCodeEnum.PARAMS_CONTENT_TYPE_ERROR.getCode(), ApiCallReturnCodeEnum.PARAMS_CONTENT_TYPE_ERROR.getMessage());
        } else if (e instanceof HttpRequestMethodNotSupportedException){
            log.info("Request method not supported");
            return new ApiBaseResult(ApiCallReturnCodeEnum.REQUEST_METHOD_NOT_SUPPORTED_ERROR.getCode(), ApiCallReturnCodeEnum.REQUEST_METHOD_NOT_SUPPORTED_ERROR.getMessage());
        }else {
            apiBaseResult = new ApiBaseResult(ApiCallReturnCodeEnum.ERROR.getCode(), ApiCallReturnCodeEnum.ERROR.getMessage());
            log.error("系统级别错误,请管理中心抓紧处理", e);
        }
        return apiBaseResult;
    }
}
