package io.github.jonesun.standaloneserver.lock;

/**
 * @author jone.sun
 * @date 2021/1/23 14:02
 */
public interface GoodsService {

    void init(Goods goods);

    Integer getInventoryByGoodsId(Long goodsId);

    boolean buy(Long goodsId);

}
