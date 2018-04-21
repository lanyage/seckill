-- 秒杀执行的存储工程
-- ；
DELIMITER $$ -- ;转换为$$
-- 定义存储过程
-- 参数 : in 输入参数, out 输出参数
-- row_count():返回上一条修改语句的影响行数
-- row_count: 0 : 为修改数据， >0 :修改了一条记录 <0 表示sql错误
CREATE PROCEDURE `seckill`.`execute_seckill`
  (in v_seckill_id bigint, in v_phone bigint, in v_kill_time TIMESTAMP,out r_result INT )

  BEGIN
    DECLARE  insert_count int DEFAULT 0;
    START TRANSACTION ;
    insert ignore into success_killed(seckill_id, user_phone, create_time)
    values(v_seckill_id, v_phone, v_kill_time);

    select row_count() into insert_count;

    IF (insert_count = 0) THEN
      ROLLBACK ;
      set r_result = -1;
    ELSEIF(insert_count < 0) THEN
      ROLLBACK ;
      SET r_result = -2;
    ELSE
      update seckill set number = number - 1 where seckill_id = v_seckill_id and end_time > v_kill_time and start_time < v_kill_time and number > 0;
      IF (insert_count = 0) THEN
        ROLLBACK ;
        set r_result = 0;
      ELSEIF (insert_count < 0) THEN
        ROLLBACK ;
        set r_result = -2;
      ELSE
        COMMIT ;
        set r_result = 1;
      END IF;
    END IF;
  END;
$$
-- 存储过程定义结束
DELIMITER ;
set @r_result = -3;
call execute_seckill(1001,18673862995, now(), @r_result);

-- 获取结果
select @r_result;

--存储过程
-- 存储过程优化：优化的是事务行级锁持有的时间，不要过度的依赖存储过程，简单的逻辑可以用存储过程，qps可以达到一个商品接近6000左右