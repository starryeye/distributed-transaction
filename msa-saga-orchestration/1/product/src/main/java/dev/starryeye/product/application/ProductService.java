package dev.starryeye.product.application;

import dev.starryeye.product.application.command.BuyProductCommand;
import dev.starryeye.product.application.command.CancelBoughtProductCommand;
import dev.starryeye.product.application.result.BuyProductResult;
import dev.starryeye.product.application.result.CancelBoughtProductResult;
import dev.starryeye.product.domain.Product;
import dev.starryeye.product.domain.ProductBoughtHistory;
import dev.starryeye.product.infrastructure.ProductBoughtHistoryRepository;
import dev.starryeye.product.infrastructure.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductBoughtHistoryRepository productBoughtHistoryRepository;

    @Transactional
    public BuyProductResult buy(BuyProductCommand command) {

        List<ProductBoughtHistory> boughtHistories = productBoughtHistoryRepository.findAllByBoughtIdAndStatus(
                command.boughtId(),
                ProductBoughtHistory.Type.BOUGHT
        );

        // 구매 이력이 이미 존재함
        if (!boughtHistories.isEmpty()) {
            System.out.println("already bought, boughtId: " + command.boughtId());

            long totalBoughtPrice = boughtHistories.stream()
                    .mapToLong(ProductBoughtHistory::getBoughtPrice)
                    .sum();

            return new BuyProductResult(totalBoughtPrice);
        } // 멱등성

        // 구매 이력이 존재하지 않음, 구매 진행 및 구매 이력 생성
        Long totalBoughtPrice = 0L;
        for (BuyProductCommand.ItemToBuy item : command.items()) {

            Product product = productRepository.findById(item.productId())
                    .orElseThrow(() -> new RuntimeException("product not found, id: " + item.productId()));

            product.reduceStock(item.boughtQuantity());

            Long price = product.getPriceFor(item.boughtQuantity());
            totalBoughtPrice += price;

            ProductBoughtHistory productBoughtHistory = ProductBoughtHistory.createBought(
                    command.boughtId(),
                    item.productId(),
                    item.boughtQuantity(),
                    price
            );
            productBoughtHistoryRepository.save(productBoughtHistory);
        }

        return new BuyProductResult(totalBoughtPrice);
    }

    @Transactional
    public CancelBoughtProductResult cancelBuying(CancelBoughtProductCommand command) {

        List<ProductBoughtHistory> boughtHistories = productBoughtHistoryRepository.findAllByBoughtIdAndStatus(
                command.boughtId(),
                ProductBoughtHistory.Type.BOUGHT
        );

        if (boughtHistories.isEmpty()) {
            System.out.println("ProductBoughtHistory not found, boughtId: " + command.boughtId());
            return new CancelBoughtProductResult(0L);
        } // 멱등성

        List<ProductBoughtHistory> cancelledBoughtHistories = productBoughtHistoryRepository.findAllByBoughtIdAndStatus(
                command.boughtId(),
                ProductBoughtHistory.Type.CANCELLED
        );

        // 구매 취소 이력이 이미 존재함
        if (!cancelledBoughtHistories.isEmpty()) {
            System.out.println("ProductBoughtHistory already cancelled, boughtId: " + command.boughtId());

            long cancelledTotalBoughtPrice = cancelledBoughtHistories.stream()
                    .mapToLong(ProductBoughtHistory::getBoughtPrice)
                    .sum();
            return new CancelBoughtProductResult(cancelledTotalBoughtPrice);
        } // 멱등성

        // 취소 이력이 존재하지 않음, 구매 취소 진행 및 구매 취소 이력 생성
        Long cancelledTotalBoughtPrice = 0L;
        for (ProductBoughtHistory productBoughtHistory : boughtHistories) {

            Product product = productRepository.findById(productBoughtHistory.getProductId())
                    .orElseThrow(() -> new RuntimeException("product not found, id: " + productBoughtHistory.getProductId()));

            product.cancelReduceStock(productBoughtHistory.getBoughtStockQuantity());
            cancelledTotalBoughtPrice += productBoughtHistory.getBoughtPrice();

            ProductBoughtHistory cancelled = productBoughtHistory.ofCancelled();
            productBoughtHistoryRepository.save(cancelled);
        }

        return new CancelBoughtProductResult(cancelledTotalBoughtPrice);
    }
}
