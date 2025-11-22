package dev.starryeye.product.controller.request;

import dev.starryeye.product.application.command.BuyProductCommand;

import java.util.List;

public record BuyProductRequest(
        String boughtId,
        List<ItemToBuy> items
) {

    public BuyProductCommand toCommand() {
        return new BuyProductCommand(
                this.boughtId,
                items.stream()
                        .map(item -> new BuyProductCommand.ItemToBuy(
                                item.productId,
                                item.boughtQuantity
                        )).toList()
        );
    }

    public record ItemToBuy(
            Long productId,
            Long boughtQuantity
    ) {
    }
}
