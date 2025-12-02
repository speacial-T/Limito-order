package com.limito.order.cart.controller;

import com.limito.order.cart.domain.dto.limitedProduct.AddCartLimitedRequestV1;
import com.limito.order.cart.domain.dto.limitedProduct.AddCartLimitedResponseV1;
import com.limito.order.cart.domain.dto.resellProduct.AddCartResellRequestV1;
import com.limito.order.cart.domain.dto.resellProduct.AddCartResellResponseV1;
import com.limito.order.cart.service.CartServiceV1;
import jakarta.validation.Valid;
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
    private final CartServiceV1 cartService;

    // 한정판매 장바구니 추가
    @PostMapping("/limited")
    public ResponseEntity<AddCartLimitedResponseV1> addLimitedItem(
            @Valid @RequestBody AddCartLimitedRequestV1 addLimitedProductReqDto
    ) {
        // Todo. userId 헤더에서 추출, 권한 검증
        Long userId = 1111L;
        AddCartLimitedResponseV1 result = cartService.addLimitedItem(userId, addLimitedProductReqDto);
        return ResponseEntity.ok(result);
    }

    // 리셀 장바구니 추가
    @PostMapping("/resell")
    public ResponseEntity<AddCartResellResponseV1> addResellItem(
            @Valid @RequestBody AddCartResellRequestV1 addResellProductReqDto
    ) {
        // Todo. userId 헤더에서 추출, 권한 검증
        Long userId = 1111L;
        AddCartResellResponseV1 result = cartService.addResellItem(userId, addResellProductReqDto);
        return ResponseEntity.ok(result);
    }
}
