package com.limito.order.cart.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.limito.common.exception.AppException;
import com.limito.order.cart.domain.dto.feignClient.GetPurchaseAmountLimitRequestV1;
import com.limito.order.cart.domain.dto.feignClient.GetPurchaseAmountLimitResponseV1;
import com.limito.order.cart.domain.dto.limitedProduct.AddCartLimitedRequestV1;
import com.limito.order.cart.domain.dto.limitedProduct.AddCartLimitedResponseV1;
import com.limito.order.cart.domain.dto.resellProduct.AddCartResellRequestV1;
import com.limito.order.cart.domain.dto.resellProduct.AddCartResellResponseV1;
import com.limito.order.cart.domain.mapper.CartMapper;
import com.limito.order.cart.domain.model.LimitedCacheItem;
import com.limito.order.cart.domain.model.ResellCacheItem;
import com.limito.order.common.feignClient.LimitedFeignClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceV1 {

	private final LimitedFeignClient limitedFeignClient;

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

		// 기존 장바구니에 있는 상품과 동일한 상품을 추가하는 경우 - 수량 추가
		HashOperations<String, String, Object> hashOps = hashOps();
		LimitedCacheItem existing = (LimitedCacheItem)hashOps.get(key, field);

		LimitedCacheItem cacheItem = CartMapper.toDomain(addLimitedProductReqDto);

		// 한정판매 feign client 요청 - 최대 구매 가능 수량
		ResponseEntity<GetPurchaseAmountLimitResponseV1> feignRes = getFeignResponse(addLimitedProductReqDto);

		// 장바구니 추가는 단건만 가능
		GetPurchaseAmountLimitResponseV1.PurchaseAmountLimit purchaseAmountLimit = getPurchaseAmountLimit(feignRes);
		UUID limitedProductItemId = purchaseAmountLimit.getLimitedProductItemId();
		int purchaseAmountLimitCnt = purchaseAmountLimit.getPurchaseAmountLimit();
		log.info("최대 구매 가능 수량 : {}", purchaseAmountLimitCnt);

		// 수량 추가
		if (existing != null) {
			cacheItem = validateIncreaseAmount(existing, addLimitedProductReqDto, purchaseAmountLimitCnt);
		}

		// 신규 상품 추가 검증
		canAddNewProduct(addLimitedProductReqDto, cacheItem, limitedProductItemId, purchaseAmountLimitCnt);

		// 레디스 캐싱
		hashOps.put(key, field, cacheItem);
		log.info("한정판매 상품 추가 성공");

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
		ResellCacheItem existing =
			(ResellCacheItem)hashOps.get(key, field);

		// 동일 옵션의 상품은 추가 할 수 없음
		if (existing != null) {
			throw AppException.of(HttpStatus.BAD_REQUEST, "동일 옵션의 상품은 추가할 수 없습니다.");
		}

		ResellCacheItem merged = CartMapper.toDomain(addResellProductReqDto);
		hashOps.put(key, field, merged);
		log.info("리셀 상품 추가 성공");

		ResellCacheItem saved = (ResellCacheItem)hashOps.get(key, field);
		if (saved == null) {
			throw AppException.of(HttpStatus.NO_CONTENT, "캐싱된 리셀 상품 조회에 실패하였습니다.");
		}

		return CartMapper.toResponse(saved);
	}

	// 주문 완료된 한정판매 상품 장바구니 삭제
	public void deleteLimitedOrderItem(Long userId, List<UUID> productItemIds) {
		if (productItemIds == null || productItemIds.isEmpty()) {
			throw AppException.of(HttpStatus.NO_CONTENT, "장바구니에서 삭제할 상품 아이디가 존재하지 않습니다.");
		}

		String key = LIMITED_KEY.formatted(userId);
		deleteOrderItems(key, productItemIds);
	}

	// 주문 완료된 리셀 상품 장바구니 삭제
	public void deleteResellOrderItem(Long userId, List<UUID> optionIds) {
		if (optionIds == null || optionIds.isEmpty()) {
			throw AppException.of(HttpStatus.NO_CONTENT, "장바구니에서 삭제할 상품 아이디가 존재하지 않습니다.");
		}

		String key = LIMITED_KEY.formatted(userId);
		deleteOrderItems(key, optionIds);
	}

	private void deleteOrderItems(String key, List<UUID> ids) {
		HashOperations<String, String, Object> hashOps = hashOps();

		// Redis에 실제로 존재하는 field만 모으기 (바로구매는 장바구니 거치지 않음)
		List<String> existingFields = ids.stream()
			.map(UUID::toString)
			.filter(field -> Boolean.TRUE.equals(hashOps.hasKey(key, field)))
			.toList();

		// 장바구니에는 하나도 없을 수도 있음 (예: 장바구니 거치지 않고 바로 주문한 경우)
		if (existingFields.isEmpty()) {
			log.info("삭제할 장바구니 아이템이 없습니다.");
			return;
		}

		// 존재하는 field들만 삭제
		// HDEL cart:limited:{userId} field1 field2 ...
		hashOps.delete(key, existingFields.toArray(new Object[0]));
		log.info("주문 완료된 장바구니 아이템 삭제 완료");
	}

	private ResponseEntity<GetPurchaseAmountLimitResponseV1> getFeignResponse(
		AddCartLimitedRequestV1 addLimitedProductReqDto) {
		List<UUID> itemIdList = new ArrayList<>();
		itemIdList.add(addLimitedProductReqDto.getProductItemId());
		GetPurchaseAmountLimitRequestV1 req = GetPurchaseAmountLimitRequestV1.create(itemIdList);

		ResponseEntity<GetPurchaseAmountLimitResponseV1> feignRes = limitedFeignClient.getPurchaseAmountLimits(req);
		if (!feignRes.getStatusCode().equals(HttpStatus.OK)) {
			throw AppException.of(HttpStatus.BAD_REQUEST, "최대 구매 가능 수량 확인에 실패하였습니다.");
		}

		return feignRes;
	}

	private GetPurchaseAmountLimitResponseV1.PurchaseAmountLimit getPurchaseAmountLimit(
		ResponseEntity<GetPurchaseAmountLimitResponseV1> feignRes) {
		GetPurchaseAmountLimitResponseV1 body = feignRes.getBody();
		if (body == null || body.getItems() == null || body.getItems().isEmpty()) {
			throw AppException.of(HttpStatus.NO_CONTENT, "limited feign client 응답 body가 비어있습니다.");
		}
		return feignRes.getBody().getItems().get(0);
	}

	// 기존 장바구니에 있는 상품과 동일한 상품을 추가하는 경우의 수량 추가 메서드
	private LimitedCacheItem validateIncreaseAmount(LimitedCacheItem existing,
		AddCartLimitedRequestV1 addLimitedProductReqDto, int purchaseAmountLimitCnt) {
		// 기존 수량 + 새 수량
		existing.setProductAmount(existing.getProductAmount() + addLimitedProductReqDto.getProductAmount());
		if (existing.getProductAmount() > purchaseAmountLimitCnt) {
			throw AppException.of(HttpStatus.BAD_REQUEST,
				"최대 구매 가능한 수량을 초과하였습니다. 최대 구매 가능 수량 : " + purchaseAmountLimitCnt + "개");
		}
		return existing;
	}

	private void canAddNewProduct(AddCartLimitedRequestV1 addLimitedProductReqDto, LimitedCacheItem cacheItem,
		UUID limitedProductItemId, int purchaseAmountLimitCnt) {
		boolean canAdd = limitedProductItemId.equals(addLimitedProductReqDto.getProductItemId())
			&& cacheItem.getProductAmount() <= purchaseAmountLimitCnt; // 여기서 merged 총량 기준으로 보는 것도 가능

		if (!canAdd) {
			throw AppException.of(HttpStatus.BAD_REQUEST,
				"최대 구매 가능한 수량을 초과하였습니다. 최대 구매 가능 수량 : " + purchaseAmountLimitCnt + "개");
		}
	}
}
