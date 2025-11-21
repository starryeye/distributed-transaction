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

            /**
             * PENDING case
             *      1. 주문=PENDING, 상품재고=RESERVED, 포인트결제=RESERVED
             *      2. 주문=PENDING, 상품재고=CONFIRMED, 포인트결제=RESERVED
             *      3. 주문=PENDING, 상품재고=CONFIRMED, 포인트결제=CONFIRMED
             *
             * PENDING 상태의 주문을 처리하는 방식은 요구사항에 따라 매우 다를 수 있다.
             *      유저가 주문을 했는데 상품재고, 포인트결제는 confirm 되었지만 주문이 pending 인데
             *      막연히 pending 을 배치에 의해 confirm 처리하면 문제가 생김..
             *          -> 유저가 다시 주문을 할 수도 있기 때문.
             *
             * CANCELLED case
             *      1. 주문=CANCELLED, 상품재고=RESERVED, 포인트결제=RESERVED
             *      2. 주문=CANCELLED, 상품재고=CANCELLED, 포인트결제=RESERVED
             *      3. 주문=CANCELLED, 상품재고=CANCELLED, 포인트결제=CANCELLED (정상)
             *
             * CANCELLED 상태의 주문도 PENDING 과 마찬가지로 처리 방식이 다양할 수 있음..
             *
             * 아래는 어떤 요구사항이든 운영자가 수동으로 개입해서 비즈니스에 맞게 처리하는 것으로 공통적으로 처리할 수 있는 방법이다.
             * PENDING 상태의 주문은 운영자가 수동으로 처리하도록 유도
             *      PENDING 상태의 주문을 수집하는 application 을 두고 admin application 으로 처리 해주는 방법
             * CANCELLED 상태의 주문
             *      상품재고, 포인트결제 상태를 수집하는 application 을 두고 특정 기간이 지났음에도 RESERVED 로 남아 있는 자원이라면
             *      주문 상태를 보고 CANCELLED 라면 스스로 CANCELLED 처리하도록 한다.
             *
             */
            orderService.pendingReserve(orderId, userId);
            throw e;
        }
    }
}
