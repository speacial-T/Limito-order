package com.limito.order.cart.service;

import com.limito.order.cart.domain.dto.limitedProduct.CartAddLimitedProductDtoV1;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 레디스 저장 형식
 * key = cart:limited:{userId} , cart:resell:{userId}
 * field = {optionId}    <-- Todo. 리셀은 옵션 아이디로 똑같이 했는데 재고 아이디로 바꿔야 할 지 생각히보기
 * value = dto
 */
@Service
@RequiredArgsConstructor
public class CartServiceV1 {

    private static final String LIMITED_KEY = "cart:limited:%d";
    private static final String RESELL_KEY = "cart:resell:%d";

    private final RedisTemplate<String, Object> redisTemplate;

    private HashOperations<String, String, Object> hashOps() {
        return redisTemplate.opsForHash();
    }

    /**
     * 한정판매 장바구니 추가
     * - 장바구니 생성 로직 없이 첫 상품을 담으면 장바구니가 생성됨
     * - 최대 구매 가능 수량 이내의 수량만 담을 수 있음
     */
    public CartAddLimitedProductDtoV1 addLimitedItem(Long userId, CartAddLimitedProductDtoV1 addLimitedProductReqDto) {
        String key = LIMITED_KEY.formatted(userId);
        String field = addLimitedProductReqDto.getOptionId().toString();

        HashOperations<String, String, Object> hashOps = hashOps();
        CartAddLimitedProductDtoV1 existing =
                (CartAddLimitedProductDtoV1) hashOps.get(key, field);

        CartAddLimitedProductDtoV1 merged = addLimitedProductReqDto;

        /**
         * 해당 상품이 이미 장바구니에 있는 경우 -> 기존 수량 + 새로 추가 한 수량
         * 새로 추가하는 경우 -> 그대로 담기
         */
        if (existing != null) {
            // 기존 수량 + 새 수량
            existing.setProductAmount(existing.getProductAmount() + addLimitedProductReqDto.getProductAmount());
            merged = existing;
        }

        hashOps.put(key, field, merged);

        CartAddLimitedProductDtoV1 saved = (CartAddLimitedProductDtoV1) hashOps.get(key, field);

        return saved;
    }


}
