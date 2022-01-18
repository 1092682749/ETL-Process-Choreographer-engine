package com.dyz.etlcomposer.diagram.execute;

import com.dyz.etlcomposer.model.Diagram;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
public class HiveExecutor extends Executor{
    public HiveExecutor(RedisTemplate redisTemplate, Diagram diagram) {
        super(redisTemplate, diagram);
    }

    @Override
    public Object execute(String taskName, String code) {
       log.info(code);
        return  "yes";
    }
}
