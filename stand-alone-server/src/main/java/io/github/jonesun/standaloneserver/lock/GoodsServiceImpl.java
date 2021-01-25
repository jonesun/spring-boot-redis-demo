package io.github.jonesun.standaloneserver.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author jone.sun
 * @date 2021/1/23 14:03
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CacheManager cacheManager;
//
//    //测试抛出异常
//    private final AtomicBoolean isThrowTestError = new AtomicBoolean();
//
//
//    //测试一个商品抛出一个异常
//    private final ConcurrentHashMap<Long, Boolean> throwTestHashMap = new ConcurrentHashMap<>();

    @Override
    public void init(Goods goods) {
//        throwTestHashMap.put(goods.getId(), false);
        getGoodsCache().put(goods.getId(), goods);
    }

    @Override
    public Integer getInventoryByGoodsId(Long goodsId) {
        return Optional.ofNullable(getGoodsCache().get(goodsId, Goods.class))
                .map(Goods::getInventory)
                .orElse(0);
    }

    @Override
    public boolean buy(Long goodsId) {
        //使用jvm提供的锁机制即可
        //方式一: 直接在方法上加上synchronized(或者在关键代码上使用)，缺点购买多个商品时效率会较低(当然因为java8对于synchronized做了很多优化，效率也不会有多差相较于lock)
        //方式二: 使用lock, 缺点如果逻辑上出现异常(未捕获的)导致锁未及时释放的话，会导致后面的请求都会由于获取不到锁而失败，一个商品抢购还好，如果好多商品，会因为某一个异常导致所有商品都失败
        //方式三(推荐)：使用ConcurrentHashMap，一个商品一个锁(或者一段数量一个锁)，这样可以在某个商品秒杀出现异常时，不影响其他商品

        //如果是单机环境的话，使用ConcurrentHashMap是不错的选择，不过如果是集群情况下(部署到多个服务器上)，使用jvm的锁机制就满足不了需求了，这个时候就需要使用Redisson了
        return buyWithRedisLock(goodsId);
    }


    private synchronized boolean buyWithSynchronized(Long goodsId) {
        throwTestError(goodsId);
        return normalBuy(goodsId);
    }

    private final Lock lock = new ReentrantLock();

    private boolean buyWithLock(Long goodsId) {
//        lock.lock();
//        try {
//            throwTestError(goodsId);
//            return normalBuy(goodsId);
//        } finally {
//            lock.unlock();
//        }
        try {
            if(lock.tryLock(2, TimeUnit.SECONDS)) {
                throwTestError(goodsId);
                return normalBuy(goodsId);
            }
            logger.error("获取不到锁");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取锁异常", e);
            return false;
        } finally {
            lock.unlock();
        }
    }

    private final ConcurrentHashMap<String, Lock> lockConcurrentHashMap = new ConcurrentHashMap<>();

    private boolean buyWithLockConcurrentHashMapLock(Long goodsId) {

        try {
            if(doLock(String.valueOf(goodsId))) {
                throwTestError(goodsId);
                return normalBuy(goodsId);
            }
            logger.error("获取不到锁");
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            logger.error("发生异常", e);
            return false;
        } finally {
            releaseLock(String.valueOf(goodsId));
        }
    }

    private boolean doLock(String key) throws InterruptedException {
        Lock newLock = new ReentrantLock();
        Lock oldLock = lockConcurrentHashMap.putIfAbsent(key, newLock);
        return Objects.requireNonNullElse(oldLock, newLock).tryLock(2, TimeUnit.SECONDS);
    }

    private void releaseLock(String key) {
        ReentrantLock lock = (ReentrantLock) lockConcurrentHashMap.get(key);
        if(lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    @Autowired
    RedisLockUtils redisLockUtils;

    private boolean buyWithRedisLock(Long goodsId) {
        String lockStr = null;
        try {
            lockStr = redisLockUtils.getLock(String.valueOf(goodsId), 2, TimeUnit.SECONDS);
            if(lockStr != null) {
                return normalBuy(goodsId);
            }
            logger.error("获取不到锁");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("发生异常", e);
            return false;
        } finally {
            if(lockStr != null) {
                redisLockUtils.unLock(String.valueOf(goodsId),lockStr);
            }

        }
    }

    private void throwTestError() {
//        if(!isThrowTestError.getAndSet(true)) {
//            logger.error("抛出异常: " + isThrowTestError);
//            throw new RuntimeException("手动测试抛出异常");
//        }
    }

    private void throwTestError(Long goodsId) {
//        throwTestError();
//        if(throwTestHashMap.replace(goodsId, false,true)) {
//            throw new RuntimeException(goodsId + "手动测试抛出异常");
//        }
    }


    private boolean normalBuy(Long goodsId) {
        Goods goods1 = getGoodsCache().get(goodsId, Goods.class);
        //操作原子性
        if (goods1 != null && goods1.getInventory() > 0) {
            //购买
            goods1.setInventory(goods1.getInventory() - 1);
            getGoodsCache().put(goodsId, goods1);
            logger.info("购买成功，还剩: " + goods1.getInventory());
            return true;
        } else {
            logger.error("购买失败，库存不足！");
            return false;
        }
    }

    private Cache getGoodsCache() {
        return cacheManager.getCache("goods");
    }
}
