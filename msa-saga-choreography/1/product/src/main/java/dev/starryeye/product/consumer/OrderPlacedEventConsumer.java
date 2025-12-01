package dev.starryeye.product.consumer;

import dev.starryeye.product.application.ProductService;
import dev.starryeye.product.application.command.BuyProductCommand;
import dev.starryeye.product.application.command.CancelBoughtProductCommand;
import dev.starryeye.product.application.result.BuyProductResult;
import dev.starryeye.product.application.result.CancelBoughtProductResult;
import dev.starryeye.product.consumer.event.OrderPlacedEvent;
import dev.starryeye.product.infrastructure.producer.ProductBoughtEventProducer;
import dev.starryeye.product.infrastructure.producer.ProductBoughtFailedEventProducer;
import dev.starryeye.product.infrastructure.producer.event.ProductBoughtEvent;
import dev.starryeye.product.infrastructure.producer.event.ProductBoughtFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderPlacedEventConsumer {

    private final ProductService productService;

    private final ProductBoughtEventProducer productBoughtEventProducer;
    private final ProductBoughtFailedEventProducer productBoughtFailedEventProducer;

    @KafkaListener(
            topics = "order-placed",
            groupId = "order-placed-consumer",
            properties = {
                    "spring.json.value.default.type=dev.starryeye.product.consumer.event.OrderPlacedEvent" // deserialize 결과 타입 지정
            }
    )
    public void consume(OrderPlacedEvent event) {

        String boughtId = event.orderId().toString();

        try {
            // order 로 부터 온 OrderPlacedEvent 를 바탕으로 구매 처리 진행
            BuyProductCommand buyProductCommand = new BuyProductCommand(
                    boughtId,
                    event.orderItems().stream()
                            .map(orderItem -> new BuyProductCommand.ItemToBuy(
                                    orderItem.productId(),
                                    orderItem.orderQuantity()
                            )).toList()
            );
            BuyProductResult result = productService.buy(buyProductCommand);// 내부에서 transaction commit 완료됨

            // "구매됨" 이벤트 발행
            ProductBoughtEvent productBoughtEvent = new ProductBoughtEvent(
                    event.userId(),
                    event.orderId(),
                    result.totalBoughtPrice()
            );
            productBoughtEventProducer.send(productBoughtEvent);
        } catch (Exception e) {

            // 구매 처리 된 것을 취소시킨다.(멱등성)
            CancelBoughtProductCommand cancelBoughtProductCommand = new CancelBoughtProductCommand(boughtId);
            CancelBoughtProductResult result = productService.cancelBuying(cancelBoughtProductCommand);

            // "구매됨 이벤트를 실패" 이벤트 발행
            ProductBoughtFailedEvent productBoughtFailedEvent = new ProductBoughtFailedEvent(
                    event.orderId(),
                    result.cancelledTotalBoughtPrice()
            );
            productBoughtFailedEventProducer.send(productBoughtFailedEvent);
        }
    }
}
