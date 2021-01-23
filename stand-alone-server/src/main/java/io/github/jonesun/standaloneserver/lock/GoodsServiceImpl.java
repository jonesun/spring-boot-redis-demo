package io.github.jonesun.standaloneserver.lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author jone.sun
 * @date 2021/1/23 14:03
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CacheManager cacheManager;

    @Override
    public void init(Goods goods) {
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
