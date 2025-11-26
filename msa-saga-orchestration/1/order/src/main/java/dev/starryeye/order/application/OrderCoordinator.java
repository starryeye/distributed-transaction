package dev.starryeye.order.application;

import dev.starryeye.order.application.command.PlaceOrderCommand;
import dev.starryeye.order.application.result.GetOrderItemsResult;
import dev.starryeye.order.domain.CompensationRegistry;
import dev.starryeye.order.infrastructure.CompensationRegistryRepository;
import dev.starryeye.order.infrastructure.point.PointApiClient;
import dev.starryeye.order.infrastructure.point.request.CancelUsedPointRequest;
import dev.starryeye.order.infrastructure.point.request.UsePointRequest;
import dev.starryeye.order.infrastructure.product.ProductApiClient;
import dev.starryeye.order.infrastructure.product.request.BuyProductRequest;
import dev.starryeye.order.infrastructure.product.request.CancelBoughtProductRequest;
import dev.starryeye.order.infrastructure.product.response.BuyProductResponse;
import dev.starryeye.order.infrastructure.product.response.CancelBoughtProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderCoordinator {

    private final OrderService orderService;

    private final ProductApiClient productApiClient;
    private final PointApiClient pointApiClient;

    private final CompensationRegistryRepository compensationRegistryRepository;

    // 주문 처리
    public void placeOrder(PlaceOrderCommand command) {

        // 주문을 요청 상태로 변경
        orderService.requestOrder(command.orderId());

        // 주문 상세 조회
        GetOrderItemsResult orderItems = orderService.getOrderItems(command.orderId());

        try {
            // 주문 상세 정보로 재고 차감 요청
            String boughtId = command.orderId().toString();
            BuyProductRequest buyProductRequest = new BuyProductRequest(
                    boughtId,
                    orderItems.items().stream()
                            .map(orderItem -> new BuyProductRequest.ItemToBuy(
                                    orderItem.productId(),
                                    orderItem.orderQuantity()
                            )).toList()
            );
            BuyProductResponse buyProductResponse = productApiClient.buyProduct(buyProductRequest);

            // 주문 상세 정보로 포인트 사용 요청
            String transactionId = command.orderId().toString();
            UsePointRequest usePointRequest = new UsePointRequest(
                    command.userId(),
                    transactionId,
                    buyProductResponse.totalBoughtPrice()
            );
            pointApiClient.usePoint(usePointRequest);

            // 주문을 완료 상태로 변경
            orderService.completeRequestedOrder(command.orderId());

        } catch (Exception e) {
            // 보상트랜잭션 처리
            processCompensationTransaction(command.orderId(), e);

            throw e;
        }
    }

    public void processCompensationTransaction(Long orderId, Exception originalException) {

        try {
            // 재고 차감 취소 요청
            String boughtId = orderId.toString();
            CancelBoughtProductRequest cancelBoughtProductRequest = new CancelBoughtProductRequest(boughtId);
            CancelBoughtProductResponse cancelBoughtProductResponse = productApiClient.cancelBoughtProduct(cancelBoughtProductRequest);

            if (cancelBoughtProductResponse.cancelledTotalBoughtPrice() > 0) {
                // 포인트 사용 취소 요청
                String transactionId = orderId.toString();
                CancelUsedPointRequest cancelUsedPointRequest = new CancelUsedPointRequest(transactionId);
                pointApiClient.cancelUsedPoint(cancelUsedPointRequest);
            }

            // 주문을 실패 상태로 변경
            orderService.failRequestedOrder(orderId);

        } catch (Exception e) {

            /**
             * CompensationRegistry
             * 보상트랜잭션이 실패할 경우 기록해두는 용이다.
             *
             * 주기적으로 CompensationRegistry 의 상태가 PENDING 인 데이터를 확인하는 application 을 두고
             * 해당 application 이 보상트랜잭션을 처리해주고 처리가 완료되면 CompensationRegistry 상태를 COMPLETED 로 변경한다.
             */

            CompensationRegistry compensationRegistry = CompensationRegistry.createPending(orderId);
            compensationRegistryRepository.save(compensationRegistry);

            e.initCause(originalException);
            throw e;
        }
    }
}
