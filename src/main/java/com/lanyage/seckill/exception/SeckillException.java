package com.lanyage.seckill.exception;

/**
 * 秒杀相关异常
 * Created by lanyage on 2017/11/6.
 */
public class SeckillException extends RuntimeException {

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
