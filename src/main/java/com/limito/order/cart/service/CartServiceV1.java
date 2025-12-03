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
		// 필드 : 리셀은 옵션아이디, 한정판매는 재고아이디
		String field = addLimitedProductReqDto.getStockId().toString();

		// 기존 장바구니에 있는 상품과 동일한 상품을 추가하는 경우
		// - 수량 추가
		// - 신규 상품 추가
		HashOperations<String, String, Object> hashOps = hashOps();
		AddCartLimitedRequestV1 existing =
			(AddCartLimitedRequestV1)hashOps.get(key, field);

		AddCartLimitedRequestV1 merged = addLimitedProductReqDto;

		// 해당 상품이 이미 장바구니에 있는 경우
		if (existing != null) {
			// 기존 수량 + 새 수량
			existing.setProductAmount(existing.getProductAmount() + addLimitedProductReqDto.getProductAmount());
			merged = existing;
		}

		// 레디스 캐싱
		hashOps.put(key, field, merged);

		// 캐싱 된 데이터 조회 후 반환
		AddCartLimitedRequestV1 saved = (AddCartLimitedRequestV1)hashOps.get(key, field);
		/**
		 * 응답용으로 ReqDto → ResDto 변환
		 *  레디서스 저장 시 CartAddLimitedProductReqDtoV1 객체가 저장됨
		 *  이 상테에서 조회 시 CartAddLimitedProductResDtoV1로 강제 캐스팅 하면 안됨
		 *  ReqDto → ResDto로 변환해서 리턴 해야됨
		 */
		AddCartLimitedResponseV1 result = AddCartLimitedResponseV1.toResDto(saved);

		return result;
	}

	// 리셀 장바구니 추가
	public AddCartResellResponseV1 addResellItem(Long userId, AddCartResellRequestV1 addResellProductReqDto) {

		String key = RESELL_KEY.formatted(userId);
		String field = addResellProductReqDto.getOptionId().toString();

		HashOperations<String, String, Object> hashOps = hashOps();
		AddCartResellRequestV1 existing =
			(AddCartResellRequestV1)hashOps.get(key, field);

		AddCartResellRequestV1 merged = addResellProductReqDto;

		// Todo : 예외처리가 안잡힘 -> common레포 수정 끝나면 다시 확인하기
		// 동일 옵션의 상품은 추가 할 수 없음
		if (existing != null) {
			log.error("동일 옵션 상품 추가 불가능");
			throw AppException.of(HttpStatus.BAD_REQUEST, "동일 옵션의 상품은 추가할 수 없습니다.");
		}

		hashOps.put(key, field, merged);
		log.info("리셀 상품 추가 성공");

		AddCartResellRequestV1 saved = (AddCartResellRequestV1)hashOps.get(key, field);

		AddCartResellResponseV1 result = AddCartResellResponseV1.toResDto(saved);
		return result;
	}
}
