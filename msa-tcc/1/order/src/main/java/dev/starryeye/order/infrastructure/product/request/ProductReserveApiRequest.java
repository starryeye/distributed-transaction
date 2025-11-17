package dev.starryeye.order.infrastructure.product.request;

import java.util.List;

public record ProductReserveApiRequest(
        String reservationId,
        List<ReserveItem> items
) {
    public record ReserveItem(
            Long productId,
            Long reserveStockQuantity
    ) {
    }
}
