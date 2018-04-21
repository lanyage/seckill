package com.lanyage.seckill.dao;

import com.lanyage.seckill.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

/**
 * Created by lanyage on 2017/11/5.
 */
public interface SuccessKilledDao {

    /**
     * 插入购买明细,可过滤重复,如果影响行数=1,表示更新的行数
     *
     * @param sekillId
     * @param userPhone
     * @return
     * 插入的行数
     */
    int insertSuccessKilled(@Param("seckillId")long sekillId,@Param("userPhone")long userPhone);
	
    /**
     * 查询SuccessKilled并携带秒杀seckill对象
     * 
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId, @Param("userPhone")long userPhone);

}
