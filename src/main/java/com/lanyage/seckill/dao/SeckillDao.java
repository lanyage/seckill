package com.lanyage.seckill.dao;

import com.lanyage.seckill.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Mybatis:
 * 参数+SQL = Entity/List
 * SQL语句自己写,充分发挥SQL语句的灵活性
 * <p/>
 * SQL写在哪？
 * 1.XML提供SQL,建议
 * 2.注解提供SQL
 * <p/>
 * 如何实现DAO接口？
 * 1.Mapper自动实现DAO接口,只需要接口,不需要实现类
 * 2.API编程方式实现DAO接口
 * <p/>
 * Created by lanyage on 2017/11/5.
 */
public interface SeckillDao {

    /**
     * 减库存
     *
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNumber(@Param("seckillId")long seckillId, @Param("killTime")Date killTime);

    /**
     * 根据ID查询秒杀对象
     *
     * @param seckillId
     * @return
     */
    Seckill queryById(@Param("seckillId")long seckillId);

    /**
     * 根据偏移量查询秒杀商品列表
     *
     * @param offset
     * @param limit
     * @return
     */
    List<Seckill> queryAll(@Param("offset")int offset, @Param("limit")int limit);

    /**
     * 通过存储过程执行sql语句，这样的话可以减少行级锁持有的时间，先插入记录，然后再减库存
     */
    void killByProcedure(Map<String, Object> paramMap);
}
