package io.github.jonesun.standaloneserver.lock;

import java.io.Serializable;

/**
 * @author jone.sun
 * @date 2021/1/23 13:59
 */
public class Goods implements Serializable {

    private Long id;

    private String name;

    /**
     * 库存-用于演示秒杀场景
     */
    private Integer inventory;

    public Goods() {}

    public Goods(Long id, String name, Integer inventory) {
        this.id = id;
        this.name = name;
        this.inventory = inventory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

    @Override
    public String toString() {
        return "Goods{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", inventory=" + inventory +
                '}';
    }
}
