package xyz.ret.base.redistimerspring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.ret.base.redistimer.RedisTimer;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Random;

@Slf4j
@RestController
public class TestController {
    @Resource
    private RedisTimer redisTimer;

    private Random random = new Random(System.currentTimeMillis());

    @RequestMapping("/addTask")
    public String addTask() {
        long now = System.currentTimeMillis();
        long delay = Math.abs(random.nextInt()) % 10000 + 1000;
        Date executeTime = new Date(now + delay);
        redisTimer.schedule("test", executeTime, executeTime);
        return "OK";
    }

    @RequestMapping("/addTask2")
    public String addTask2() {
        Date executeTime = new Date();
        redisTimer.execute("test", executeTime);
        return "OK";
    }
}
