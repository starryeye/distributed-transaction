package dev.starryeye.order.consumer;

import dev.starryeye.order.application.OrderService;
import dev.starryeye.order.consumer.event.PointUsedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointUsedEventConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "point-used",
            groupId = "point-used-consumer",
            properties = {
                    "spring.json.value.default.type=dev.starryeye.order.consumer.event.PointUsedEvent" // deserialize 결과 타입 지정
            }
    )
    public void consume(PointUsedEvent event) {

        orderService.completeOrder(event.orderId());
    }
}
