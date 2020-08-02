package xyz.ret.base.redistimerspring;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import xyz.ret.base.redistimer.RedisTimer;
import xyz.ret.base.redistimer.Timer;
import xyz.ret.base.redistimerspring.util.DateUtil;

import java.util.Date;

@Configuration
@Slf4j
public class RedisTimerConfiguration {
    @Bean(name = "timerRedisTemplate")
    public RedisTemplate<String, Object> timerRedisTemplate(RedisConnectionFactory factory) {
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }


    @Bean(name = "redisTimer")
    public Timer redisTimer(RedisTemplate<String, Object> timerRedisTemplate) {
        RedisTimer timer = new SpringRedisTimer(timerRedisTemplate);
        timer.addAction("test", p -> {
            log.info("Execute [set] {} [now] {}", DateUtil.time((Date)p), DateUtil.time(new Date()));
        });
        timer.setFetchNum(2);
        timer.setRedisKey("redis-timer");
        return timer;
    }

    static class SpringRedisTimer extends RedisTimer implements DisposableBean, CommandLineRunner {

        public SpringRedisTimer(RedisTemplate<String, Object> redisTemplate) {
            super(redisTemplate);
        }

        @Override
        public void destroy() {
            log.info("Stopping redis timer.");
            stop();
        }

        @Override
        public void run(String... args) {
            log.info("Starting redis timer.");
            begin();
        }
    }
}
