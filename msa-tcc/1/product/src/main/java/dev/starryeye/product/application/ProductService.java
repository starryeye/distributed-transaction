package dev.starryeye.product.application;

import dev.starryeye.product.application.command.ProductReserveCommand;
import dev.starryeye.product.application.command.ReservedProductCancelCommand;
import dev.starryeye.product.application.command.ReservedProductConfirmCommand;
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

        List<ProductReservation> reservations = productReservationRepository.findAllByReservationId(command.reservationId());

        // 예약 이미 존재
        if (!reservations.isEmpty()) {
            long totalReservePrice = reservations.stream()
                    .mapToLong(ProductReservation::getReservedPrice)
                    .sum();

            return new ProductReserveResult(totalReservePrice);
        }

        // 예약이 없음, 각 Product 별 예약 생성
        Long totalReservePrice = 0L;
        for (ProductReserveCommand.ReserveItem reserveItem : command.items()) {
            Product product = productRepository.findById(reserveItem.productId())
                    .orElseThrow(() -> new RuntimeException("product not found, id: " + reserveItem.productId()));

            product.reserveStock(reserveItem.reserveStockQuantity());
            Long reservePrice = product.getPriceFor(reserveItem.reserveStockQuantity());
            totalReservePrice += reservePrice;

            ProductReservation productReservation = ProductReservation.create(
                    command.reservationId(),
                    product.getId(),
                    reserveItem.reserveStockQuantity(),
                    reservePrice
            );

            productReservationRepository.save(productReservation);
        }

        return new ProductReserveResult(totalReservePrice);
    }

    @Transactional
    public void confirmReserve(ReservedProductConfirmCommand command) {

        List<ProductReservation> reservations = productReservationRepository.findAllByReservationId(command.reservationId());

        if (reservations.isEmpty()) {
            throw new RuntimeException("reservation not found, reservationId: " + command.reservationId());
        }

        List<ProductReservation> cancelledOrConfirmedReservations = reservations.stream()
                .filter(ProductReservation::isCancelledOrConfirmed)
                .toList();
        if (!cancelledOrConfirmedReservations.isEmpty()) {
            System.out.println("there are cancelled or confirmed reservations, reservationId: " + command.reservationId());
            return;
        }

        for (ProductReservation reservation : reservations) {
            Product product = productRepository.findById(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("product not found, id: " + reservation.getProductId()));

            product.confirmReservedStock(reservation.getReservedStockQuantity());
            reservation.confirm();
        }
    }

    @Transactional
    public void cancelReserve(ReservedProductCancelCommand command) {

        List<ProductReservation> reservations = productReservationRepository.findAllByReservationId(command.reservationId());

        if (reservations.isEmpty()) {
            throw new RuntimeException("reservation not found, reservationId: " + command.reservationId());
        }

        List<ProductReservation> cancelledOrConfirmedReservations = reservations.stream()
                .filter(ProductReservation::isCancelledOrConfirmed)
                .toList();
        if (!cancelledOrConfirmedReservations.isEmpty()) {
            throw new RuntimeException("there are cancelled or confirmed reservations, reservationId: " + command.reservationId());
        }

        for (ProductReservation reservation : reservations) {
            Product product = productRepository.findById(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("product not found, id: " + reservation.getProductId()));

            product.cancelReservedStock(reservation.getReservedStockQuantity());
            reservation.cancel();
        }
    }
}
