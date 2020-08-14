package com.rmxc.logconsumer.web.controller;

import com.rmxc.tech.dagger.base.enums.ApiCallReturnCodeEnum;
import com.rmxc.tech.dagger.runtime.web.bean.result.ApiBaseResult;
import com.rmxc.tech.dagger.runtime.web.bean.result.ApiCallReturnCode;

/**
 * @author zhanbq
 */
public class LogConsumerBaseController {

    protected ApiBaseResult success(){

        ApiBaseResult res = new ApiBaseResult<>();
        res.setData("成功");
        res.setResult(new ApiCallReturnCode(ApiCallReturnCodeEnum.SUCCESS.getCode(), ApiCallReturnCodeEnum.SUCCESS.getMessage()));
        return res;
    }
    protected <T> ApiBaseResult success(T data){

        ApiBaseResult<T> res = new ApiBaseResult<>();
        res.setData(data);
        res.setResult(new ApiCallReturnCode(ApiCallReturnCodeEnum.SUCCESS.getCode(), ApiCallReturnCodeEnum.SUCCESS.getMessage()));
        return res;
    }

    protected <T> ApiBaseResult failed(){

        ApiBaseResult<T> res = new ApiBaseResult<>();
        res.setResult(new ApiCallReturnCode(ApiCallReturnCodeEnum.ERROR.getCode(), ApiCallReturnCodeEnum.ERROR.getMessage()));
        return res;
    }

    protected <T> ApiBaseResult paramError(){

        ApiBaseResult<T> res = new ApiBaseResult<>();
        res.setResult(new ApiCallReturnCode(ApiCallReturnCodeEnum.PARAMS_ERROR.getCode(), ApiCallReturnCodeEnum.PARAMS_ERROR.getMessage()));
        return res;
    }
}
