package io.github.jonesun.standaloneserver.lock.controller;

import io.github.jonesun.standaloneserver.lock.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jone.sun
 * @date 2021/1/23 17:07
 */
@RestController
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @GetMapping("buy")
    public String buy(@RequestParam Long goodsId) {
        return goodsService.buy(goodsId) + "";
    }

}
