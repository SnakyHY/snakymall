package com.snakyhy.snakymail.product.exception;

import com.snakyhy.common.exception.BizCodeEnum;
import com.snakyhy.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 统一异常处理
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.snakyhy.snakymail.product.controller")
public class SnakymailExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        log.error("发生数据校验异常{},类型为{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> allErrors = bindingResult.getFieldErrors();
        Map<String,String> errors=new HashMap<>();
        allErrors.forEach((objectError -> {
            errors.put(objectError.getField(),objectError.getDefaultMessage());
        }));
        return R.error(BizCodeEnum.INVALID_EXCEPTION.getCode(),BizCodeEnum.INVALID_EXCEPTION.getMsg()).put("data",errors);
    }

//    @ExceptionHandler(value = Throwable.class)
//    public R handleOtherException(Throwable e){
//        log.error("发生数据自定义异常{},类型为{},路径为{}",e.getMessage(),e.getClass(),e.getStackTrace());
//        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(),BizCodeEnum.UNKNOW_EXCEPTION.getMsg());
//    }
}
