package com.limito.order.cart.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.limito.common.exception.AppException;
import com.limito.order.cart.domain.dto.feignclient.limited.GetInCartProductInfoResponseV1;
import com.limito.order.cart.domain.dto.feignclient.limited.GetPurchaseAmountLimitRequestV1;
import com.limito.order.cart.domain.dto.feignclient.limited.GetPurchaseAmountLimitResponseV1;
import com.limito.order.cart.domain.dto.feignclient.resell.OptionInfosGetResponseV1;
import com.limito.order.cart.domain.dto.limitedproduct.AddCartLimitedRequestV1;
import com.limito.order.cart.domain.dto.limitedproduct.AddCartLimitedResponseV1;
import com.limito.order.cart.domain.dto.limitedproduct.GetCartLimitedResponseV1;
import com.limito.order.cart.domain.dto.resellproduct.AddCartResellRequestV1;
import com.limito.order.cart.domain.dto.resellproduct.AddCartResellResponseV1;
import com.limito.order.cart.domain.dto.resellproduct.GetCartResellResponseV1;
import com.limito.order.cart.domain.mapper.CartMapper;
import com.limito.order.cart.domain.model.LimitedCacheItem;
import com.limito.order.cart.domain.model.ResellCacheItem;
import com.limito.order.common.feignclient.LimitedFeignClient;
import com.limito.order.common.feignclient.ResellFeignClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceV1 {
	private static final String LIMITED_KEY = "cart:limited:%d";
	private static final String RESELL_KEY = "cart:resell:%d";

	private final RedisTemplate<String, Object> redisTemplate;
	private final LimitedFeignClient limitedFeignClient;
	private final ResellFeignClient resellFeignClient;

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

		// 한정판매 feign client 요청 - 최대 구매 가능 수량
		ResponseEntity<GetPurchaseAmountLimitResponseV1> feignRes = getFeignResponse(addLimitedProductReqDto);

		// 장바구니 추가는 단건만 가능
		GetPurchaseAmountLimitResponseV1.PurchaseAmountLimit purchaseAmountLimit = getPurchaseAmountLimit(feignRes);
		UUID limitedProductItemId = purchaseAmountLimit.getLimitedProductItemId();
		int purchaseAmountLimitCnt = purchaseAmountLimit.getPurchaseAmountLimit();
		log.info("최대 구매 가능 수량 : {}", purchaseAmountLimitCnt);

		// 한정판매 feign client 요청 - 상품 정보
		ResponseEntity<GetInCartProductInfoResponseV1> productInfoRes = limitedFeignClient.getInCartProductInfo(
			limitedProductItemId);
		GetInCartProductInfoResponseV1 productInfo = productInfoRes.getBody();
		if (productInfo == null) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "상품 정보 요청에 실패했습니다");
		}

		if (productInfo.getIsSoldOut()) {
			throw AppException.of(HttpStatus.BAD_REQUEST, "픔절된 상품은 장바구니에 추가할 수 없습니다.");
		}
		LimitedCacheItem cacheItem = CartMapper.toDomain(addLimitedProductReqDto, productInfo);
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

		return CartMapper.toAddResponse(saved);
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

		return CartMapper.toAddResponse(saved);
	}

	// 한정판매 장바구니 조회
	public List<GetCartLimitedResponseV1> getLimitedCart(Long userId) {
		String key = LIMITED_KEY.formatted(userId);
		HashOperations<String, String, Object> hashOps = hashOps();

		// HGETALL cart:limited:{userId}
		Map<String, Object> entries = hashOps.entries(key);

		// 값(value)만 꺼내서 LimitedCacheItem → 응답 DTO로 변환
		return entries.values().stream()
			.map(value -> (LimitedCacheItem)value)
			.map(CartMapper::toGetResponse)
			.toList();

	}

	// 리셀 장바구니 조회
	public List<GetCartResellResponseV1> getResellCart(Long userId) {
		String key = RESELL_KEY.formatted(userId);
		HashOperations<String, String, Object> hashOps = hashOps();

		// HGETALL cart:resell:{userId}
		Map<String, Object> entries = hashOps.entries(key);
		List<UUID> optionIds = entries.keySet().stream()
			.map(UUID::fromString)
			.toList();

		// 리셀 feign client 요청 - 상품 정보
		ResponseEntity<List<OptionInfosGetResponseV1>> productInfoRes = resellFeignClient.getOptionInfos(optionIds);
		List<OptionInfosGetResponseV1> productInfos = productInfoRes.getBody();
		if (productInfos == null) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "리셀 상품 정보 요청에 실패했습니다.");
		}

		// 상품 정보  응답 optionId -> 정보 맵으로 변환
		Map<UUID, OptionInfosGetResponseV1> optionInfoMap = productInfos.stream()
			.collect(Collectors.toMap(
				OptionInfosGetResponseV1::getOptionId,
				info -> info
			));

		// 아이디 집합 검증 (양쪽이 정확히 같은지)
		Set<UUID> requestIdSet = new HashSet<>(optionIds);
		Set<UUID> responseIdSet = optionInfoMap.keySet();
		if (!requestIdSet.equals(responseIdSet)) {
			log.error("리셀 상품 정보 요청의 결과가 올바르지 않습니다. requestIds={}, responseIds={}",
				requestIdSet, responseIdSet);
			throw AppException.of(HttpStatus.NOT_ACCEPTABLE, "리셀 상품 정보 요청의 결과가 올바르지 않습니다.");
		}

		return entries.values().stream()
			.map(value -> (ResellCacheItem)value)
			.map(cacheItem -> {
				UUID optionId = cacheItem.getOptionId();
				OptionInfosGetResponseV1 optionInfo = optionInfoMap.get(optionId);
				return CartMapper.toGetResponse(cacheItem, optionInfo);
			})
			.toList();
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

		String key = RESELL_KEY.formatted(userId);
		deleteOrderItems(key, optionIds);
	}

	private void deleteOrderItems(String key, List<UUID> ids) {
		HashOperations<String, String, Object> hashOps = hashOps();

		String[] fields = ids.stream()
			.map(UUID::toString)
			.toArray(String[]::new);

		// HDEL cart:limited:{userId} field1 field2 ...
		Long deletedCount = hashOps.delete(key, (Object[])fields);
		log.info("주문 완료된 장바구니 아이템 삭제 완료");

		if (!deletedCount.equals((long)ids.size())) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "주문 완료 아이템 장바구니에서 삭제하기에 실패했습니다.");
		}

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

	private void canAddNewProduct(
		AddCartLimitedRequestV1 addLimitedProductReqDto,
		LimitedCacheItem cacheItem,
		UUID limitedProductItemId,
		int purchaseAmountLimitCnt
	) {
		boolean canAdd = limitedProductItemId.equals(addLimitedProductReqDto.getProductItemId())
			&& cacheItem.getProductAmount() <= purchaseAmountLimitCnt; // 여기서 merged 총량 기준으로 보는 것도 가능

		if (!canAdd) {
			throw AppException.of(HttpStatus.BAD_REQUEST,
				"최대 구매 가능한 수량을 초과하였습니다. 최대 구매 가능 수량 : " + purchaseAmountLimitCnt + "개");
		}
	}

	public ResellFeignClient getResellFeignClient() {
		return resellFeignClient;
	}
}
