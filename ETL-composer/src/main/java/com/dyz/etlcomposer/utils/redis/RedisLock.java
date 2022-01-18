package com.dyz.etlcomposer.utils.redis;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Date;

@Data
public class RedisLock {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    int tryMaxCount = 3600;
    String lockName;

    public RedisLock(RedisTemplate redisTemplate, String lockName) {
        this.redisTemplate = redisTemplate;
        this.lockName = lockName;
    }

    /**
     * 不设置过期时间 只限制获取次数
     *
     * @return
     */
    public boolean lock() {
        ValueOperations<String, Object> vops = redisTemplate.opsForValue();
        Boolean setIfAbsent = false;
        setIfAbsent = vops.setIfAbsent(lockName, new Date().getTime());
        int tryCount = 0;
        while (!setIfAbsent && tryCount < tryMaxCount) {
            tryCount++;
            setIfAbsent = vops.setIfAbsent(lockName, new Date().getTime());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return setIfAbsent;
    }

    public Boolean unlock() {
        return redisTemplate.delete(lockName);
    }
}
