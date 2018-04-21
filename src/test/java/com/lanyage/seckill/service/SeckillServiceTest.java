package com.lanyage.seckill.service;

import com.lanyage.seckill.dto.Exposer;
import com.lanyage.seckill.dto.SeckillExecution;
import com.lanyage.seckill.entity.Seckill;
import com.lanyage.seckill.exception.RepeatKillException;
import com.lanyage.seckill.exception.SeckillClosedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-services.xml", "classpath:spring/spring-dao.xml"})
public class SeckillServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private SeckillService seckillService;

    @Test
    public void testGetSeckillList() throws Exception {
        List<Seckill> sekillList;
        sekillList = seckillService.getSeckillList();
        logger.info("list={}", sekillList);
    }

    @Test
    public void testGetById() throws Exception {
        long id;
        Seckill seckill;
        id = 1000L;
        seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void testExportSeckillUrl() throws Exception {
        long seckillId;
        Exposer exposer;
        seckillId = 1000L;
        exposer = seckillService.exportSeckillUrl(seckillId);
        logger.info("exposer = {}", exposer);

    }

    @Test
    public void testExecuteSeckill() throws Exception {
        long seckillId;
        long userPhone;
        String md5;
        SeckillExecution execution;
        seckillId = 1000L;
        userPhone = 18673862995L;
        md5 = "0800aec028a8b749446880b4a865ff69";
        try {
            execution = seckillService.executeSeckill(seckillId, userPhone, md5);
            logger.info("execution = {}", execution);
        } catch (RepeatKillException e1) {
            logger.error(e1.getMessage());
        } catch (SeckillClosedException e2) {
            logger.error(e2.getMessage());
        }
    }

    //测试代码完整逻辑，注意可重复执行。
    @Test
    public void testSeckillLogic() throws Exception {
        long id = 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            logger.info("exposer = {}", exposer);
            long phone = 18673862997L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
                logger.info("execution = {}", execution);
            } catch (RepeatKillException e1) {
                logger.error(e1.getMessage());
            } catch (SeckillClosedException e2) {
                logger.error(e2.getMessage());
            }
        } else {
            logger.warn("exposer={}", exposer);
        }
    }

    @Test
    public void testExecuteSeckillProcedure() {
        long seckillId = 1001;
        long phone = 18673862995l;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId, phone, md5);
            logger.info(execution.getStateInfo());
        }
    }
}