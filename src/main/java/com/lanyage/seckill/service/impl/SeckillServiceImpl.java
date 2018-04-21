package com.lanyage.seckill.service.impl;

import com.lanyage.seckill.dao.RedisDao;
import com.lanyage.seckill.dao.SeckillDao;
import com.lanyage.seckill.dao.SuccessKilledDao;
import com.lanyage.seckill.dto.Exposer;
import com.lanyage.seckill.dto.SeckillExecution;
import com.lanyage.seckill.entity.Seckill;
import com.lanyage.seckill.entity.SuccessKilled;
import com.lanyage.seckill.enums.SeckillState;
import com.lanyage.seckill.exception.RepeatKillException;
import com.lanyage.seckill.exception.SeckillClosedException;
import com.lanyage.seckill.exception.SeckillException;
import com.lanyage.seckill.service.SeckillService;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lanyage on 2017/11/6.
 */

//@Component @Service @Dao @ Controller

@Service("seckillService")
public class SeckillServiceImpl implements SeckillService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private final String slat = "adafehakfhea$$@@%22jfaef";


    @Resource
    private SeckillDao seckillDao;
    @Resource
    private SuccessKilledDao successKilledDao;
    @Resource
    private RedisDao redisDao;

    /*获取秒杀列表*/
    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    /*获取秒杀的详细情况*/
    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    /*展示秒杀暴露接口*/
    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        /*先按照Id查找seckill信息*/
        /*通过redis缓存起来，降低数据库访问的压力
        * 优化点：缓存优化，超时的基础上维护一致性
        *get from cache
        * if null
        *   get db
        * else
        *   put cache
        * other logic
        * 但是这是数据访问对象的地方
        * */
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //存在，放入redis
                redisDao.putSeckill(seckill);
            }
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        /*如果时间不在正常时间之内,那么就返回一个Exposer,exposer是false*/
        if (nowTime.getTime() < startTime.getTime() ||
                nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        /*如果是在正常的时间之内,那么就开始给秒杀加密,然后暴露秒杀*/
        //转化特定字符串的过程
        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMd5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    /*
    * 使用注解控制事物方法的有点
    * 1：开发团队明确标注事物方法的编程风格。
    * 2：保证事物方法执行的时间尽可能的短，不要穿插其他网络操作RPC/HTTP请求或者剥离到事物方法外部
    * 3：不是所有的方法都需要事物,只有一条修改操作，只读操作不需要事物控制
    * */

    //优化思路:调换update和insert的顺序，因为行锁在update
    public SeckillExecution executeSeckill(long seckillId,
                                           long userPhone,
                                           String md5) throws SeckillException, RepeatKillException, SeckillClosedException {
        /*如果md5不存在,或者md5与此ID的md5不一样,那么就是错误秒杀*/
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            throw new SeckillException("Seckill data rewrite");
        }

        /*否则执行秒杀逻辑:减库存+记录购买行为*/
        Date nowTime = new Date();
        try {
            //记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            //唯一:seckillId,userPhone
            if (insertCount <= 0) {
                    /*如果说没插进去那就属于重复秒杀*/
                throw new RepeatKillException("seckill repeated");
            } else {
                //用当前时间去执行秒杀，减库存，热定商品竞争
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                //如果影响的条数为0,也就没有成功,那就是说时间不对
                if (updateCount <= 0) {
                    //如果更新的条数为0，那么就是rollback
                    throw new SeckillClosedException("Seckill is closed");
                } else {
                    //秒杀成功，return代表commit
                    //将秒杀的成功记录封装成SeckillExecution返回
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillState.SUCCESS, successKilled);
                }
            }
        } catch (SeckillClosedException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常 转化为运行期异常，任何错误Spring会帮我们回滚
            throw new SeckillException("seckill inner error" + e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !md5.equals(getMd5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillState.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);

        try {
            seckillDao.killByProcedure(map);
            //获取result,通过mapUtil的方式
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled sk = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillState.SUCCESS, sk);
            }else {
                return new SeckillExecution(seckillId, SeckillState.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillState.INNER_ERROR);
        }
    }
}
