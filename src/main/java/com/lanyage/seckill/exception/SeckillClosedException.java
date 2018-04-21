package com.lanyage.seckill.exception;

/**
 * 秒杀关闭异常
 * Created by lanyage on 2017/11/6.
 */
public class SeckillClosedException extends SeckillException{

    public SeckillClosedException(String message) {
        super(message);
    }

    public SeckillClosedException(String message, Throwable cause) {
        super(message, cause);
    }
}
