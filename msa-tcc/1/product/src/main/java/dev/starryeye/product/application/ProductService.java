package dev.starryeye.product.application;

import dev.starryeye.product.application.command.ProductReserveCommand;
import dev.starryeye.product.application.result.ProductReserveResult;
import dev.starryeye.product.domain.Product;
import dev.starryeye.product.domain.ProductReservation;
import dev.starryeye.product.infrastructure.ProductRepository;
import dev.starryeye.product.infrastructure.ProductReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductReservationRepository productReservationRepository;

    @Transactional
    public ProductReserveResult tryReserve(ProductReserveCommand command) {

        List<ProductReservation> exists = productReservationRepository.findAllByReservationId(command.reservationId());

        // 예약 이미 존재
        if (!exists.isEmpty()) {
            long totalPrice = exists.stream()
                    .mapToLong(ProductReservation::getReservedPrice)
                    .sum();

            return new ProductReserveResult(totalPrice);
        }

        // 예약이 없음, 예약 생성
        Long totalPrice = 0L;
        for (ProductReserveCommand.ReserveItem reserveItem : command.items()) {
            Product product = productRepository.findById(reserveItem.productId())
                    .orElseThrow(() -> new RuntimeException("product not found, id: " + reserveItem.productId()));

            product.reserveStock(reserveItem.reserveStockQuantity());
            Long price = product.getPriceFor(reserveItem.reserveStockQuantity());
            totalPrice += price;

            ProductReservation productReservation = ProductReservation.create(
                    command.reservationId(),
                    product.getId(),
                    reserveItem.reserveStockQuantity(),
                    price
            );

            productReservationRepository.save(productReservation);
        }

        return new ProductReserveResult(totalPrice);
    }
}
