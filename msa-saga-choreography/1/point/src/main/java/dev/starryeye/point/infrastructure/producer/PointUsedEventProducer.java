package dev.starryeye.point.infrastructure.producer;

import dev.starryeye.point.infrastructure.producer.event.PointUsedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointUsedEventProducer {

    private final KafkaTemplate<String, PointUsedEvent> kafkaTemplate;

    public void send(PointUsedEvent event) {

        /**
         * 키를 지정하면 항상 동일한 파티션으로 메시지가 발행되어 동일한 키를 가진 이벤트는 순서대로 처리됨을 보장 받을 수 있다.
         */
        kafkaTemplate.send(
                "point-used", // topic name
                event.orderId().toString(), // kafka record key
                event
        );
    }
}
