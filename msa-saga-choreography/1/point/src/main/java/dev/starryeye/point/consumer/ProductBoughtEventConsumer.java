package dev.starryeye.point.consumer;

import dev.starryeye.point.application.PointService;
import dev.starryeye.point.application.command.UsePointCommand;
import dev.starryeye.point.consumer.event.ProductBoughtEvent;
import dev.starryeye.point.infrastructure.producer.PointUsedEventProducer;
import dev.starryeye.point.infrastructure.producer.event.PointUsedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductBoughtEventConsumer {

    private final PointService pointService;

    private final PointUsedEventProducer pointUsedEventProducer;

    @KafkaListener(
            topics = "product-bought",
            groupId = "product-bought-consumer",
            properties = {
                    "spring.json.value.default.type=dev.starryeye.point.consumer.event.ProductBoughtEvent" // deserialize 결과 타입 지정
            }
    )
    public void consume(final ProductBoughtEvent event) {

        String transactionId = event.orderId().toString();

        try {

            // product 로 부터 온 ProductBoughtEvent 를 바탕으로 point 결체 진행
            UsePointCommand usePointCommand = new UsePointCommand(
                    event.userId(),
                    transactionId,
                    event.totalBoughtPrice()
            );
            pointService.use(usePointCommand); // 내부에서 transaction commit 완료됨

            // "결제됨" 이벤트 발행
            PointUsedEvent pointUsedEvent = new PointUsedEvent(event.orderId());
            pointUsedEventProducer.send(pointUsedEvent);
        } catch (Exception ex) {

            // point 결제 취소


        }

    }
}
