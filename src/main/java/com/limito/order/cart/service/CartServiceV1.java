package com.limito.order.cart.service;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.limito.common.exception.AppException;
import com.limito.order.cart.domain.dto.limitedProduct.AddCartLimitedRequestV1;
import com.limito.order.cart.domain.dto.limitedProduct.AddCartLimitedResponseV1;
import com.limito.order.cart.domain.dto.resellProduct.AddCartResellRequestV1;
import com.limito.order.cart.domain.dto.resellProduct.AddCartResellResponseV1;
import com.limito.order.cart.domain.mapper.CartMapper;
import com.limito.order.cart.domain.model.LimitedCacheItem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceV1 {

	private static final String LIMITED_KEY = "cart:limited:%d";
	private static final String RESELL_KEY = "cart:resell:%d";

	private final RedisTemplate<String, Object> redisTemplate;

	private HashOperations<String, String, Object> hashOps() {
		return redisTemplate.opsForHash();
	}

	/**
	 * TODO(재현): 한정판매 장바구니 추가
	 * 1. 장바구니 캐시 조회
	 * 2. 장바구니 추가 가능 정책 검증
	 * 2-1. 최대 구매 가능 수량 이내의 수량만 담을 수 있음, 권한 검증
	 * 3. 장바구니 추가
	 * 4. 반환
	 */
	public AddCartLimitedResponseV1 addLimitedItem(Long userId, AddCartLimitedRequestV1 addLimitedProductReqDto) {
		String key = LIMITED_KEY.formatted(userId);
		// 필드 : 한정판매는 판매 아이템 아이디, 리셀은 옵션아이디
		String field = addLimitedProductReqDto.getProductItemId().toString();

		// 기존 장바구니에 있는 상품과 동일한 상품을 추가하는 경우
		// - 수량 추가
		// - 신규 상품 추가
		HashOperations<String, String, Object> hashOps = hashOps();
		LimitedCacheItem existing =
			(LimitedCacheItem)hashOps.get(key, field);

		LimitedCacheItem merged = CartMapper.toDomain(addLimitedProductReqDto);

		// 해당 상품이 이미 장바구니에 있는 경우
		if (existing != null) {
			// 기존 수량 + 새 수량
			existing.setProductAmount(existing.getProductAmount() + addLimitedProductReqDto.getProductAmount());
			merged = existing;
		}

		// 레디스 캐싱
		hashOps.put(key, field, merged);

		// 캐싱 된 데이터 조회 후 반환
		LimitedCacheItem saved = (LimitedCacheItem)hashOps.get(key, field);
		if (saved == null) {
			throw AppException.of(HttpStatus.NO_CONTENT, "캐싱된 한정판매 상품 조회에 실패하였습니다.");
		}

		return CartMapper.toResponse(saved);
	}

	// 리셀 장바구니 추가
	public AddCartResellResponseV1 addResellItem(Long userId, AddCartResellRequestV1 addResellProductReqDto) {

		String key = RESELL_KEY.formatted(userId);
		String field = addResellProductReqDto.getOptionId().toString();

		HashOperations<String, String, Object> hashOps = hashOps();
		AddCartResellRequestV1 existing =
			(AddCartResellRequestV1)hashOps.get(key, field);

		AddCartResellRequestV1 merged = addResellProductReqDto;

		// 동일 옵션의 상품은 추가 할 수 없음
		if (existing != null) {
			throw AppException.of(HttpStatus.BAD_REQUEST, "동일 옵션의 상품은 추가할 수 없습니다.");
		}

		hashOps.put(key, field, merged);
		log.info("리셀 상품 추가 성공");

		AddCartResellRequestV1 saved = (AddCartResellRequestV1)hashOps.get(key, field);

		AddCartResellResponseV1 result = AddCartResellResponseV1.toResDto(saved);
		return result;
	}
}
