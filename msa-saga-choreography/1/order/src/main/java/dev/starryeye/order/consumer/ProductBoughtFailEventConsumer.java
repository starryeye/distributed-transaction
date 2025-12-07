package dev.starryeye.order.consumer;

import dev.starryeye.order.application.OrderService;
import dev.starryeye.order.consumer.event.ProductBoughtFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductBoughtFailEventConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "product-bought-failed",
            groupId = "product-bought-failed-consumer",
            properties = {
                    "spring.json.value.default.type=dev.starryeye.order.consumer.event.ProductBoughtFailedEvent" // deserialize 결과 타입 지정
            }
    )
    public void consume(final ProductBoughtFailedEvent event) {

        orderService.failOrder(event.orderId());
    }
}
