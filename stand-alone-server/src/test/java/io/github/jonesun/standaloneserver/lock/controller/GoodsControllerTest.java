package io.github.jonesun.standaloneserver.lock.controller;

import io.github.jonesun.standaloneserver.lock.Goods;
import io.github.jonesun.standaloneserver.lock.GoodsService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author jone.sun
 * @date 2021/1/23 17:12
 */
@SpringBootTest
class GoodsControllerTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

//    @Test
//    void buy() {
//        RestTemplate restTemplate = new RestTemplate();
//        String response1 = restTemplate.getForEntity("http://localhost:8080/stand-alone-server/buy?goodsId=127", String.class).getBody();
//        logger.info(response1);
//    }

    @Autowired
    GoodsService goodsService;

    @Disabled
    @DisplayName("集群下商品秒杀")
    @Test
    void buyWithCluster() {
        //先模拟设置100件商品(仅仅演示方便，实际不能在Controller中测试service)
        final Long TEST_GOODS_ID = 123L;
        Goods goods = new Goods(TEST_GOODS_ID, "商品1", 100);
        goodsService.init(goods);

        //需修改端口同时启动两个服务用于测试 如8080、8180
        AtomicInteger atomicInteger = new AtomicInteger();
        RestTemplate restTemplate = new RestTemplate();
        //并发测试
        AtomicInteger atomicInteger1 = new AtomicInteger();
        final int totalNum = 300;
        //用于发出开始信号
        final CountDownLatch countDownLatchSwitch = new CountDownLatch(1);
        final CountDownLatch countDownLatch = new CountDownLatch(totalNum);

        //控制并发量 10 50 100 200
        Semaphore semaphore = new Semaphore(100);

        for(int i = 0; i < totalNum; i++) {
            new Thread(() -> {
                try {
                    countDownLatchSwitch.await();
                    semaphore.acquire();
                    TimeUnit.SECONDS.sleep(1);
                    String port;
                    if(atomicInteger1.incrementAndGet() % 2 == 0) {
                        port = "8180";
                    } else {
                        port = "8080";
                    }
                    String response2 = restTemplate.getForEntity("http://localhost:" + port + "/stand-alone-server/buy?goodsId=" + TEST_GOODS_ID, String.class).getBody();
                    if (response2.contains("true")) {
                        atomicInteger.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                    countDownLatch.countDown();
                }

            }).start();

        }

        try {
            countDownLatchSwitch.countDown();
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("测试完成，总共{}个用户进行购买，成功{}个, 商品还剩: {}", totalNum, atomicInteger.get(), goodsService.getInventoryByGoodsId(TEST_GOODS_ID));
    }
}