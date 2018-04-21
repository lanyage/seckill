package com.lanyage.seckill.dao;

import com.lanyage.seckill.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})

public class SuccessKilledDaoTest {

    @Resource
    private SuccessKilledDao successKilledDao;

    @Test
    /*int insertSuccessKilled(long sekillId, long userPhone);*/
    public void testInsertSuccessKilled() throws Exception {
        long seckillId = 1000L;
        long userPhone = 18673862996L;
        int insertCount;
        insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
        System.out.println("insertCount = " + insertCount);
    }

    @Test
    public void testQueryByIdWithSeckill() throws Exception {
        long seckillId = 1000L;
        long userPhone = 18673862996L;
        SuccessKilled successKilled;
        successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
        System.out.println(successKilled);
    }
}