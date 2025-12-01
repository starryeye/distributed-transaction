package dev.starryeye.product.infrastructure.producer;

import dev.starryeye.product.infrastructure.producer.event.ProductBoughtFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductBoughtFailedEventProducer {

    private final KafkaTemplate<String, ProductBoughtFailedEvent> kafkaTemplate;

    public void send(ProductBoughtFailedEvent event) {

        /**
         * 키를 지정하면 항상 동일한 파티션으로 메시지가 발행되어 동일한 키를 가진 이벤트는 순서대로 처리됨을 보장 받을 수 있다.
         */
        kafkaTemplate.send(
                "product-bought-failed", // topic name
                event.orderId().toString(), // kafka record key
                event
        );
    }
}
