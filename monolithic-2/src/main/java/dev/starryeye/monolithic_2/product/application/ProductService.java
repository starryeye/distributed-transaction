package dev.starryeye.monolithic_2.product.application;

import dev.starryeye.monolithic_2.product.domain.Product;
import dev.starryeye.monolithic_2.product.infrastructure.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public Long buy(Long productId, Long orderQuantity) {

        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("product not found, id: " + productId));

        Long orderTotalPrice = product.getPriceFor(orderQuantity);
        product.reduceStock(orderQuantity);

        return orderTotalPrice;
    }
}
