package dev.starryeye.point.consumer;

import dev.starryeye.point.application.PointService;
import dev.starryeye.point.consumer.event.ProductBoughtEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductBoughtEventConsumer {

    private final PointService pointService;

    @KafkaListener(
            topics = "product-bought",
            groupId = "product-bought-consumer",
            properties = {
                    "spring.json.value.default.type=dev.starryeye.point.consumer.event.ProductBoughtEvent" // deserialize 결과 타입 지정
            }
    )
    public void consume(final ProductBoughtEvent event) {



    }
}
