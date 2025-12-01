package com.limito.order.cart.controller;

import com.limito.order.cart.domain.dto.limitedProduct.CartAddLimitedProductDtoV1;
import com.limito.order.cart.service.CartServiceV1;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartControllerV1 {

    private final CartServiceV1  cartService;

    @PostMapping("/limited")
    public ResponseEntity<CartAddLimitedProductDtoV1> addLimitedItem(@RequestBody CartAddLimitedProductDtoV1 addLimitedProductReqDto) {
        // Todo. userId 헤더에서 추출, 권한 검증
        Long userId = 1111L;
        CartAddLimitedProductDtoV1 itemDto = cartService.addLimitedItem(userId, addLimitedProductReqDto);
        return ResponseEntity.ok(itemDto);
    }
}
