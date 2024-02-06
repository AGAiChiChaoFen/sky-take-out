package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){   //所有异常的父类
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 员工用户名(账号)重复异常
     * 有时候不需要跟SSM里面学的封装异常来处理
     * 我们可以先手动让程序抛一个异常，然后在控制台日志中将异常名复制过来，添加到全局异常处理器的形参中
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex) {
        //抛出异常后输出 Duplicate entry '3348578419' for key 'employee.idx_username' ，也就是ex.getMessage()的结果
        //我们要从这个异常输出中获取是哪个账号重复了，然后输出一下并返回给前端
        String message = ex.getMessage();

        //判断异常信息中有没有 Duplicate entry 来得出是不是这一类异常    -->数据库中设置了 unique属性的字段被重复添加
        if(message.contains("Duplicate entry")) {
            String split [] = message.split(" ");   //通过空格来推出用户信息名的位置

            String username = split[2];

            String msg = username + MessageConstant.ALREADY_EXISTS;

            return Result.error(msg);
        } else {
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }

    }

}
