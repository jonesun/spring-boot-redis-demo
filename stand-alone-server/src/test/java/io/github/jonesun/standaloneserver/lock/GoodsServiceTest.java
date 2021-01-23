package io.github.jonesun.standaloneserver.lock;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author jone.sun
 * @date 2021/1/23 14:12
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
class GoodsServiceTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    GoodsService goodsService;


    public static final Long TEST_GOODS_ID = 123L;

    @Order(1)
    @Test
    void init() {
        Goods goods = new Goods(TEST_GOODS_ID, "商品1", 100);
        goodsService.init(goods);
    }


    @Order(2)
    @Test
    void buy() {
        goodsService.buy(TEST_GOODS_ID);
    }

    @DisplayName("秒杀单个商品")
    @Order(3)
    @Test
    void batchBuy() throws InterruptedException {
        Integer inventory = goodsService.getInventoryByGoodsId(TEST_GOODS_ID);
        logger.info("【{}】准备秒杀, 当前库存: {}", TEST_GOODS_ID, inventory);
        LocalDateTime startTime = LocalDateTime.now();
        AtomicInteger buySuccessAtomicInteger = new AtomicInteger();
        AtomicInteger notBoughtAtomicInteger = new AtomicInteger();
        AtomicInteger errorAtomicInteger = new AtomicInteger();
        final int totalNum = 300;
        //用于发出开始信号
        final CountDownLatch countDownLatchSwitch = new CountDownLatch(1);
        final CountDownLatch countDownLatch = new CountDownLatch(totalNum);

        //控制并发量 10 50 100 200
        Semaphore semaphore = new Semaphore(200);

        ExecutorService executorService = Executors.newFixedThreadPool(totalNum);

        for (int i = 0; i < totalNum; i++) {
            executorService.execute(() -> {
                try {
                    countDownLatchSwitch.await();
                    semaphore.acquire();
                    TimeUnit.SECONDS.sleep(1);

                    boolean result = goodsService.buy(TEST_GOODS_ID);
                    if (result) {
                        buySuccessAtomicInteger.incrementAndGet();
                    } else {
                        notBoughtAtomicInteger.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errorAtomicInteger.incrementAndGet();
                } finally {
                    semaphore.release();
                    countDownLatch.countDown();
                }

            });

        }
        countDownLatchSwitch.countDown();
        countDownLatch.await();
        logger.info("测试完成,花费 {}毫秒，【{}】总共{}个用户抢购{}件商品，{}个人买到 {}个人未买到，{}个人发生异常，商品还剩{}个", TEST_GOODS_ID, ChronoUnit.MILLIS.between(startTime, LocalDateTime.now()), totalNum,
                inventory, buySuccessAtomicInteger.get(), notBoughtAtomicInteger.get(), errorAtomicInteger.get(),
                goodsService.getInventoryByGoodsId(TEST_GOODS_ID));
        assertEquals(inventory, buySuccessAtomicInteger.get());
    }

}