package com.lanyage.seckill.dao;

import com.lanyage.seckill.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    @Resource
    private SeckillDao seckillDao;

    @Test
    public void testReduceNumber() throws Exception {
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L, killTime);
        System.out.println("updateCount = " + updateCount);
    }

    @Test
    public void testQueryById() throws Exception {
        long id = 1000;
        Seckill seckill;
        seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    /*
    *    Sekill{seckillId=1000, name='1000元秒杀iphone8', number=100, startTime=Fri Nov 06 00:00:00 CST 2015, endTime=Sat Nov 07 00:00:00 CST 2015, createTime=Sun Nov 05 20:48:33 CST 2017}
         Sekill{seckillId=1001, name='500元秒杀ipad2', number=200, startTime=Fri Nov 06 00:00:00 CST 2015, endTime=Sat Nov 07 00:00:00 CST 2015, createTime=Sun Nov 05 20:48:33 CST 2017}
        Sekill{seckillId=1002, name='300元秒杀小米4', number=300, startTime=Fri Nov 06 00:00:00 CST 2015, endTime=Sat Nov 07 00:00:00 CST 2015, createTime=Sun Nov 05 20:48:33 CST 2017}
         Sekill{seckillId=1003, name='200元秒杀红米note', number=400, startTime=Fri Nov 06 00:00:00 CST 2015, endTime=Sat Nov 07 00:00:00 CST 2015, createTime=Sun Nov 05 20:48:33 CST 2017}

    * */
    @Test
    public void testQueryAll() throws Exception {
        List<Seckill> seckills = seckillDao.queryAll(0, 100);
        for (Seckill seckill : seckills) {
            System.out.println(seckill);
        }
    }
}