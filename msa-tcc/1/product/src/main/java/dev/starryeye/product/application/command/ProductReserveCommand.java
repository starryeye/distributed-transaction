package dev.starryeye.product.application.command;

import java.util.List;

public record ProductReserveCommand(
        String reservationId,
        List<ReserveItem> items
) {

    /**
     * 하나의 유일한 reservationId 로 여러 Product 를 원하는 수량으로 예약하는 command
     */

    public record ReserveItem(
            Long productId,
            Long reserveStockQuantity
    ) {
    }
}
