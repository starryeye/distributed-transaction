package dev.starryeye.order.application;

import dev.starryeye.order.application.command.PlaceOrderCommand;
import dev.starryeye.order.application.result.GetOrderItemsResult;
import dev.starryeye.order.infrastructure.point.PointApiClient;
import dev.starryeye.order.infrastructure.point.request.PointReserveApiRequest;
import dev.starryeye.order.infrastructure.point.request.ReservedPointCancelApiRequest;
import dev.starryeye.order.infrastructure.point.request.ReservedPointConfirmApiRequest;
import dev.starryeye.order.infrastructure.product.ProductApiClient;
import dev.starryeye.order.infrastructure.product.request.ProductReserveApiRequest;
import dev.starryeye.order.infrastructure.product.request.ReservedProductCancelApiRequest;
import dev.starryeye.order.infrastructure.product.request.ReservedProductConfirmApiRequest;
import dev.starryeye.order.infrastructure.product.response.ProductReserveApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCoordinator {

    private final OrderService orderService;

    private final ProductApiClient productApiClient;
    private final PointApiClient pointApiClient;

    public void placeOrder(PlaceOrderCommand command) {
        reserve(command.userId(), command.orderId());
        confirm(command.userId(), command.orderId());
    }

    private void reserve(Long userId, Long orderId) {

        String reservationId = orderId.toString();

        // order reserve
        orderService.tryReserve(orderId, userId);

        try {
            GetOrderItemsResult orderItems = orderService.getOrderItems(orderId);

            // product reserve
            ProductReserveApiRequest productReserveApiRequest = new ProductReserveApiRequest(
                    reservationId,
                    orderItems.items().stream()
                            .map(orderItem -> new ProductReserveApiRequest.ReserveItem(
                                    orderItem.productId(),
                                    orderItem.orderQuantity()
                            )).toList()
            );
            ProductReserveApiResponse productReserveApiResponse = productApiClient.reserveProduct(productReserveApiRequest);

            // point reserve
            PointReserveApiRequest pointReserveApiRequest = new PointReserveApiRequest(
                    reservationId,
                    userId,
                    productReserveApiResponse.totalReservedPrice()
            );
            pointApiClient.reservePoint(pointReserveApiRequest);
        } catch (Exception e) {

            // order cancel
            orderService.cancelReserve(orderId, userId);

            // product cancel
            ReservedProductCancelApiRequest reservedProductCancelApiRequest = new ReservedProductCancelApiRequest(reservationId);
            productApiClient.cancelProduct(reservedProductCancelApiRequest);

            // point cancel
            ReservedPointCancelApiRequest reservedPointCancelApiRequest = new ReservedPointCancelApiRequest(reservationId);
            pointApiClient.cancelPoint(reservedPointCancelApiRequest);
        }
    }

    private void confirm(Long userId, Long orderId) {

        String reservationId = orderId.toString();

        try {

            // product confirm
            ReservedProductConfirmApiRequest reservedProductConfirmApiRequest = new ReservedProductConfirmApiRequest(reservationId);
            productApiClient.confirmProduct(reservedProductConfirmApiRequest);

            // point confirm
            ReservedPointConfirmApiRequest reservedPointConfirmApiRequest = new ReservedPointConfirmApiRequest(reservationId);
            pointApiClient.confirmPoint(reservedPointConfirmApiRequest);

            // order confirm
            orderService.confirmReserve(orderId, userId);
        } catch (Exception e) {

            // PENDING 상태의 주문은 운영자가 수동으로 처리하도록 유도
            orderService.pendingReserve(orderId, userId);
            throw e;
        }
    }
}
