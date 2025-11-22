package dev.starryeye.product.application.command;

import java.util.List;

public record BuyProductCommand(
        String boughtId,
        List<ItemToBuy> items
) {

    public record ItemToBuy(
            Long productId,
            Long boughtQuantity
    ) {
    }
}
