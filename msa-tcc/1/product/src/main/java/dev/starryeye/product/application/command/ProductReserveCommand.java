package dev.starryeye.product.application.command;

import java.util.List;

public record ProductReserveCommand(
        String reservationId,
        List<ReserveItem> items
) {

    public record ReserveItem(
            Long productId,
            Long reserveStockQuantity
    ) {
    }
}
