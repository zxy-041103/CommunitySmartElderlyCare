package com.community.elderly.common.exception;

import com.community.elderly.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(value = RuntimeException.class)
    public Result<Object> handleRuntimeException(RuntimeException e, HttpServletRequest request, HttpServletResponse response) {
        log.error("业务异常：{}，请求路径：{}", e.getMessage(), request.getRequestURI(), e);
        
        Result<Object> result = Result.error(e.getMessage());
        result.setPath(request.getRequestURI());
        
        return result;
    }

    /**
     * 处理参数验证异常
     *
     * @param e 参数验证异常
     * @return 错误响应
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<Object> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request, HttpServletResponse response) {
        log.error("参数验证异常：{}，请求路径：{}", e.getMessage(), request.getRequestURI(), e);
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        
        Result<Object> result = new Result<>();
        result.setCode(400);
        result.setMessage("参数验证失败");
        result.setData(errors);
        result.setPath(request.getRequestURI());
        
        return result;
    }

    /**
     * 处理数据访问异常
     *
     * @param e 数据访问异常
     * @return 错误响应
     */
    @ExceptionHandler(value = DataAccessException.class)
    public Result<Object> handleDataAccessException(DataAccessException e, HttpServletRequest request, HttpServletResponse response) {
        log.error("数据访问异常：{}，请求路径：{}", e.getMessage(), request.getRequestURI(), e);
        
        Result<Object> result = Result.error("数据库操作失败，请稍后重试");
        result.setPath(request.getRequestURI());
        
        return result;
    }

    /**
     * 处理认证异常
     *
     * @param e 认证异常
     * @return 错误响应
     */
    @ExceptionHandler(value = BadCredentialsException.class)
    public Result<Object> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request, HttpServletResponse response) {
        log.error("认证异常：{}，请求路径：{}", e.getMessage(), request.getRequestURI());
        
        Result<Object> result = new Result<>();
        result.setCode(401);
        result.setMessage("用户名或密码错误");
        result.setPath(request.getRequestURI());
        
        return result;
    }

    /**
     * 处理访问拒绝异常
     *
     * @param e 访问拒绝异常
     * @return 错误响应
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public Result<Object> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request, HttpServletResponse response) {
        log.error("访问拒绝异常：{}，请求路径：{}，请求方法：{}，请求头：{}", 
            e.getMessage(), request.getRequestURI(), request.getMethod(), request.getHeader("Authorization"));
        
        Result<Object> result = new Result<>();
        result.setCode(403);
        result.setMessage("权限不足，拒绝访问");
        result.setPath(request.getRequestURI());
        
        return result;
    }

    /**
     * 处理非法参数异常
     *
     * @param e 非法参数异常
     * @return 错误响应
     */
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result<Object> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request, HttpServletResponse response) {
        log.error("非法参数异常：{}，请求路径：{}", e.getMessage(), request.getRequestURI(), e);
        
        Result<Object> result = new Result<>();
        result.setCode(400);
        result.setMessage(e.getMessage() != null ? e.getMessage() : "参数错误");
        result.setPath(request.getRequestURI());
        
        return result;
    }

    /**
     * 处理系统异常
     *
     * @param e 系统异常
     * @return 错误响应
     */
    @ExceptionHandler(value = Exception.class)
    public Result<Object> handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        log.error("系统异常：{}，请求路径：{}", e.getMessage(), request.getRequestURI(), e);
        
        Result<Object> result = Result.error("系统内部错误，请稍后重试");
        result.setPath(request.getRequestURI());
        
        return result;
    }
}
