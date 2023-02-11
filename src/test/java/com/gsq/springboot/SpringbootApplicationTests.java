package com.gsq.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
class SpringbootApplicationTests {

    @Resource
    private StringRedisTemplate redisTemplate;
    @Test
    void contextLoads() {
        System.out.println(redisTemplate.opsForValue().get("name"));
    }

}
