package dev.starryeye.order.infrastructure.product.request;

import java.util.List;

public record BuyProductRequest(
        String boughtId,
        List<ItemToBuy> items
) {

    public record ItemToBuy(
            Long productId,
            Long boughtQuantity
    ) {
    }
}
