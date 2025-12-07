package dev.starryeye.product.consumer;

import dev.starryeye.product.application.ProductService;
import dev.starryeye.product.application.command.CancelBoughtProductCommand;
import dev.starryeye.product.application.result.CancelBoughtProductResult;
import dev.starryeye.product.consumer.event.PointUsedFailedEvent;
import dev.starryeye.product.infrastructure.producer.ProductBoughtFailedEventProducer;
import dev.starryeye.product.infrastructure.producer.event.ProductBoughtFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointUsedFailEventConsumer {

    private final ProductService productService;

    private final ProductBoughtFailedEventProducer productBoughtFailedEventProducer;

    @KafkaListener(
            topics = "point-used-failed",
            groupId = "point-used-failed-consumer",
            properties = {
                    "spring.json.value.default.type=dev.starryeye.product.consumer.event.PointUsedFailedEvent" // deserialize 결과 타입 지정
            }
    )
    public void consume(final PointUsedFailedEvent event) {

        // point-used-failed 이벤트가 발생되면, product 에서는 구매 처리 된 것을 취소시킨다.(멱등성 처리되어있음)
        String boughtId = event.orderId().toString();

        CancelBoughtProductCommand cancelBoughtProductCommand = new CancelBoughtProductCommand(boughtId);
        CancelBoughtProductResult cancelBoughtProductResult = productService.cancelBuying(cancelBoughtProductCommand);


        // "구매됨 이벤트가 실패됨" 이벤트 발행
        ProductBoughtFailedEvent productBoughtFailedEvent = new ProductBoughtFailedEvent(
                event.orderId(),
                cancelBoughtProductResult.cancelledTotalBoughtPrice()
        );
        productBoughtFailedEventProducer.send(productBoughtFailedEvent);
    }
}
