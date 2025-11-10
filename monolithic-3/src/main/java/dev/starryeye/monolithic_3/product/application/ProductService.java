package dev.starryeye.monolithic_3.product.application;

import dev.starryeye.monolithic_3.product.application.command.BuyProductCommand;
import dev.starryeye.monolithic_3.product.domain.Product;
import dev.starryeye.monolithic_3.product.infrastructure.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Long buy(Long productId, Long orderQuantity) {

        return productRepository.findById(productId)
                .map(product -> {
                    product.reduceStock(orderQuantity);
                    return product.getPriceFor(orderQuantity);
                })
                .orElseThrow(() -> new RuntimeException("product not found, id: " + productId));
    }

    @Transactional
    public Long buyAll(BuyProductCommand command) {

        // 제한
        if (command.products().size() >= 1000) {
            throw new RuntimeException("too many products to buy..");
        }

        // 요청 수량 집계 (같은 productId가 여러 번 오면 합산)
        Map<Long, Long> qtyById = command.products().stream()
                .collect(Collectors.groupingBy(
                        BuyProductCommand.Product::productId,
                        Collectors.summingLong(BuyProductCommand.Product::orderQuantity)
                ));

        List<Product> products = productRepository.findAllById(qtyById.keySet());

        // 존재 검증
        if (products.size() != qtyById.size()) {
            Set<Long> foundIds = products.stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());
            List<Long> missing = qtyById.keySet().stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new RuntimeException("product not found: " + missing);
        }

        // 재고 차감 + 금액 합산
        long total = 0L;
        for (Product p : products) {
            long need = qtyById.get(p.getId());
            p.reduceStock(need);                 // 재고 차감
            total += p.getPriceFor(need);        // 금액 계산
        }

        return total;
    }
}
