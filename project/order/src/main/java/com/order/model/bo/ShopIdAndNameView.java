package com.order.model.bo;

import lombok.Data;

@Data
public class ShopIdAndNameView {
    private final Long id;
    private final String name;

    public ShopIdAndNameView(Shop shop) {
        this.id = shop.getId();
        this.name = shop.getName();
    }
}
