package dev.starryeye.monolithic_3.product.application.command;

import java.util.List;

public record BuyProductCommand(
        List<Product> products
) {

    public record Product(
            Long productId,
            Long orderQuantity
    ) {
    }
}
