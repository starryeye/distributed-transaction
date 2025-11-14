package dev.starryeye.product.controller.request;

import dev.starryeye.product.application.command.ProductReserveCommand;

import java.util.List;

public record ProductReserveRequest(
        String reservationId,
        List<ReserveItem> items
) {

    public ProductReserveCommand toCommand() {
        return new ProductReserveCommand(
                reservationId,
                items.stream()
                        .map(item -> new ProductReserveCommand.ReserveItem(
                                item.productId,
                                item.reserveStockQuantity
                        ))
                        .toList()
        );
    }

    public record ReserveItem(
            Long productId,
            Long reserveStockQuantity
    ) {
    }
}
