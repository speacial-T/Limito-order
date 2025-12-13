package com.limito.order.order.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.limito.common.exception.AppException;
import com.limito.common.security.context.UserContext;
import com.limito.order.cart.service.CartServiceV1;
import com.limito.order.common.OrderStatus;
import com.limito.order.common.feignclient.LimitedFeignClient;
import com.limito.order.common.feignclient.ResellFeignClient;
import com.limito.order.common.feignclient.UserFeignClient;
import com.limito.order.order.domain.dto.feignclient.limited.GetOrderedProductInfoRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.GetOrderedProductInfoResponseV1;
import com.limito.order.order.domain.dto.feignclient.limited.GetOrderedProductInfoRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.GetOrderedProductInfoResponseV1;
import com.limito.order.order.domain.dto.feignclient.limited.ReserveStockItemRequestV1;
import com.limito.order.order.domain.dto.feignclient.limited.ReserveStockRequestV1;
import com.limito.order.order.domain.dto.feignclient.resell.request.ProductInfosGetRequestV1;
import com.limito.order.order.domain.dto.feignclient.resell.request.StockReduceRequest;
import com.limito.order.order.domain.dto.feignclient.resell.response.ProductInfosGetResponseV1;
import com.limito.order.order.domain.dto.feignclient.user.OrderedUserInfoResponseV1;
import com.limito.order.order.domain.dto.feignclient.resell.dto.request.ProductInfosGetRequestV1;
import com.limito.order.order.domain.dto.feignclient.resell.dto.request.StockReduceRequest;
import com.limito.order.order.domain.dto.feignclient.resell.dto.response.ProductInfosGetResponseV1;
import com.limito.order.order.domain.dto.request.AddOrdererRequestV1;
import com.limito.order.order.domain.dto.request.CreateLimitedOrderRequestV1;
import com.limito.order.order.domain.dto.request.CreateResellOrderRequestV1;
import com.limito.order.order.domain.dto.response.CreateLimitedOrderResponseV1;
import com.limito.order.order.domain.dto.response.CreateResellOrderResponseV1;
import com.limito.order.order.domain.dto.response.GetOrderForCompanyResponseV1;
import com.limito.order.order.domain.dto.response.GetOrderForUserResponseV1;
import com.limito.order.order.domain.dto.response.GetOrdersForCompanyResponseV1;
import com.limito.order.order.domain.dto.response.GetOrdersForUserResponseV1;
import com.limito.order.order.domain.mapper.OrderMapper;
import com.limito.order.order.domain.model.CompanyOrder;
import com.limito.order.order.domain.model.Order;
import com.limito.order.order.domain.model.OrderItem;
import com.limito.order.order.domain.repository.OrderRepositoryV1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceV1 {
	private final OrderRepositoryV1 orderRepository;
	private final OrderMapper orderMapper;
	private final ResellFeignClient resellFeignClient;
	private final LimitedFeignClient limitedFeignClient;
	private final UserFeignClient userFeignClient;
	private final CartServiceV1 cartService;

	// 한정판매 주문서 생성
	@Transactional
	public CreateLimitedOrderResponseV1 createLimitedOrderSheet(
		UserContext user,
		CreateLimitedOrderRequestV1 createLimitedOrderRequest
	) {
		Long userId = user.getUserId();
		log.info("userId : {}", userId);

		// requestDto -> entity 매핑해서 주문 엔티티 생성
		Order order = orderMapper.toOrderEntity(userId, createLimitedOrderRequest);

		// requestDto -> entity 매핑해서 주문 아이템 엔티티 생성 후 orderItems로 반환
		List<OrderItem> orderItems = orderMapper.toOrderItemEntity(createLimitedOrderRequest);
		// 연관관계 설정
		order.attachOrderItems(orderItems);

		//유저 feign : 사용자 기본 배송지 정보 요청
		ResponseEntity<OrderedUserInfoResponseV1> userFeignResponse = userFeignClient.getOrderedUserInfo(userId);
		order.attachOrdererDefault(userFeignResponse);

		// 한정판매 feign: 상품 정보 요청
		GetOrderedProductInfoRequestV1 feignRequest = orderMapper.toGetOrderedProductInfoRequest(
			createLimitedOrderRequest);
		ResponseEntity<GetOrderedProductInfoResponseV1> feignResponse = limitedFeignClient.getOrderedProductInfo(
			feignRequest);

		List<GetOrderedProductInfoResponseV1.OrderedProductInfo> productInfos = validateLimitedOrderSheetFeignResponse(
			feignResponse, orderItems);

		// 주문 상품 정보 추가
		log.info("상품가격 : {}", productInfos.get(0).getPrice());
		attachLimitedProductInfos(orderItems, productInfos);
		order.attachSummary(orderItems);
		order.attachTotalPrice(orderItems);

		// 생성된 주문 엔티티 저장
		orderRepository.save(order);

		return orderMapper.toLimitedOrderResponse(order);
	}

	// 한정판매 주문자 정보 추가
	@Transactional
	public CreateLimitedOrderResponseV1 addLimitedOrdererData(
		UserContext user,
		UUID orderId,
		AddOrdererRequestV1 ordererRequest
	) {
		Long userId = user.getUserId();
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

		// 주문자 정보 추가
		order.attachOrderer(ordererRequest);

		List<OrderItem> orderItems = order.getOrderItems();

		// 재고 예약 요청
		// feign requestDto 생성
		ReserveStockRequestV1 reserveStockRequest = new ReserveStockRequestV1();
		List<ReserveStockItemRequestV1> reserveStockItemRequests = new ArrayList<>();

		for (OrderItem orderItem : orderItems) {
			UUID productItemId = orderItem.getProductItemId();
			int amount = orderItem.getProductAmount();

			ReserveStockItemRequestV1 reserveStockItemRequest = ReserveStockItemRequestV1.of(productItemId, amount);

			reserveStockItemRequests.add(reserveStockItemRequest);
		}

		if (reserveStockItemRequests.isEmpty()) {
			throw AppException.of(HttpStatus.NO_CONTENT, "reserveStockItemRequests 비어있습니다.");
		}

		reserveStockRequest.attachItems(reserveStockItemRequests);

		// feign 요청
		ResponseEntity<Void> reserveFeignResponse = limitedFeignClient.reserveStock(reserveStockRequest);
		validateReserveFeign(reserveFeignResponse);

		return orderMapper.toLimitedOrderResponse(order);
	}

	// 리셀 주문서 생성
	@Transactional
	public CreateResellOrderResponseV1 createResellOrderSheet(
		UserContext user,
		CreateResellOrderRequestV1 createResellOrderRequest
	) {
		Long userId = user.getUserId();

		Order order = orderMapper.toOrderEntity(userId, createResellOrderRequest);

		List<OrderItem> orderItems = orderMapper.toOrderItemEntity(createResellOrderRequest);
		order.attachOrderItems(orderItems);

		//유저 feign : 사용자 기본 배송지 정보 요청
		ResponseEntity<OrderedUserInfoResponseV1> userFeignResponse = userFeignClient.getOrderedUserInfo(userId);
		order.attachOrdererDefault(userFeignResponse);

		// 리셀 주문 상품 정보 요청
		List<ProductInfosGetRequestV1> feignRequests = orderMapper.toProductInfosGetRequests(orderItems);
		ResponseEntity<List<ProductInfosGetResponseV1>> feignResponse = resellFeignClient.getProductInfos(
			feignRequests);

		List<ProductInfosGetResponseV1> productInfos = validateResellOrderSheetFeignResponse(feignResponse, orderItems);
		// 주문 상품 정보 추가
		attachResellProductInfos(orderItems, productInfos);
		order.attachSummary(orderItems);
		order.attachTotalPrice(orderItems);

		orderRepository.save(order);

		return orderMapper.toResellOrderResponse(order);
	}

	// 리셀 주문자 정보 추가
	@Transactional
	public CreateResellOrderResponseV1 addResellOrdererData(
		UserContext user,
		UUID orderId,
		AddOrdererRequestV1 ordererRequest
	) {
		Long userId = user.getUserId();

		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다."));

		// 주문자 정보 추가
		order.attachOrderer(ordererRequest);

		// 상품 feign : 임시 재고 예약
		List<UUID> stockIds = order.getStockIds(order);
		ResponseEntity<Void> feignReponse = resellFeignClient.reserveStock(stockIds);
		if (!feignReponse.getStatusCode().equals(HttpStatus.OK)) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "리셀 임시 재고 예약에 실패했습니다.");
		}

		return orderMapper.toResellOrderResponse(order);
	}

	public Slice<GetOrdersForUserResponseV1> getOrdersForUser(Long userId, String userRole, Pageable pageable) {
		validateRole(userRole, "USER");

		Slice<Order> orders = orderRepository.findAllByUserIdAndOrderStatusNotOrderBySuccessedAtDesc(
			userId,
			OrderStatus.ORDER_PENDING,
			pageable
		);
		return orders.map(orderMapper::toGetOrdersForUserResponse);
	}

	public Slice<GetOrdersForCompanyResponseV1> getOrdersForCompany(Long userId, String userRole, Pageable pageable) {
		validateRole(userRole, "COMPANY");

		Slice<CompanyOrder> companyOrders =
			orderRepository.findAllBySellerIdAndOrderStatusNot(userId, OrderStatus.ORDER_PENDING, pageable);

		return companyOrders.map(orderMapper::toGetOrdersForCompanyResponse);
	}

	public GetOrderForUserResponseV1 getOrderForUser(Long userId, String userRole, UUID orderId) {
		validateRole(userRole, "USER");

		Order order = orderRepository.findByIdAndUserIdAndOrderStatusNot(orderId, userId, OrderStatus.ORDER_PENDING)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."));

		return orderMapper.toGetOrderForUserResponse(order);
	}

	public GetOrderForCompanyResponseV1 getOrderForCompany(Long userId, String userRole, UUID orderId) {
		validateRole(userRole, "COMPANY");

		Order order = orderRepository
			.findByIdAndOrderStatusNotAndOrderItems_SellerId(orderId, OrderStatus.ORDER_PENDING, userId)
			.orElseThrow(() -> AppException.of(HttpStatus.NOT_FOUND, "주문 정보를 찾을 수 없습니다."));

		return orderMapper.toGetOrderForCompanyResponse(order, userId);
	}

	private List<StockReduceRequest> createStockReduceRequests(List<OrderItem> orderItems) {
		List<StockReduceRequest> requests = new ArrayList<>();
		orderItems.forEach(orderItem -> {
			StockReduceRequest req = StockReduceRequest.createRequest(orderItem);
			requests.add(req);
		});
		return requests;
	}

	private void validateReserveFeign(ResponseEntity<Void> reserveFeignResponse) {
		if (!HttpStatus.OK.equals(reserveFeignResponse.getStatusCode())) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "한정판매 임시 재고 예약에 실패했습니다");
		}
	}

	private void validateRole(String userRole, String... roles) {
		if (
			!Arrays.asList(roles)
				.contains(userRole)
		) {
			throw AppException.of(HttpStatus.FORBIDDEN, "조회 권한이 없습니다.");
		}
	}

	private GetOrderedProductInfoResponseV1.OrderedProductInfo findLimitedInfoByItemId(
		List<GetOrderedProductInfoResponseV1.OrderedProductInfo> productInfos,
		UUID itemId
	) {
		for (GetOrderedProductInfoResponseV1.OrderedProductInfo info : productInfos) {
			if (info.getLimitedProductItemId().equals(itemId)) {
				return info;
			}
		}
		return null;
	}

	private ProductInfosGetResponseV1 findResellInfoByItemId(
		List<ProductInfosGetResponseV1> productInfos,
		UUID stockId
	) {
		for (ProductInfosGetResponseV1 info : productInfos) {
			if (info.getStockId().equals(stockId)) {
				return info;
			}
		}
		return null;
	}

	private List<GetOrderedProductInfoResponseV1.OrderedProductInfo> validateLimitedOrderSheetFeignResponse(
		ResponseEntity<GetOrderedProductInfoResponseV1> feignResponse,
		List<OrderItem> orderItems) {
		if (!feignResponse.getStatusCode().equals(HttpStatus.OK)) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "한정판매 주문 상품 정보 요청에 실패했습니다");
		}

		GetOrderedProductInfoResponseV1 body = feignResponse.getBody();
		if (body == null || body.getProducts() == null) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "한정판매 주문 상품 정보 응답이 비어있습니다");
		}

		List<GetOrderedProductInfoResponseV1.OrderedProductInfo> productInfos = body.getProducts();

		// 요청한 아이템 수와 응답 상품 정보 수가 같은지 검증
		if (productInfos.size() != orderItems.size()) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED,
				"요청한 주문 상품 수와 조회된 상품 정보 수가 일치하지 않습니다.");
		}

		return productInfos;
	}

	private void attachLimitedProductInfos(List<OrderItem> orderItems,
		List<GetOrderedProductInfoResponseV1.OrderedProductInfo> productInfos) {
		for (OrderItem orderItem : orderItems) {
			GetOrderedProductInfoResponseV1.OrderedProductInfo info =
				findLimitedInfoByItemId(productInfos, orderItem.getLimitedProductItemId());

			if (info == null) {
				throw AppException.of(HttpStatus.EXPECTATION_FAILED,
					"주문 상품 아이템 정보가 응답에서 누락되었습니다. itemId=" + orderItem.getLimitedProductItemId());
			}

			orderItem.attachProductInfo(info);
		}
	}

	private List<ProductInfosGetResponseV1> validateResellOrderSheetFeignResponse(
		ResponseEntity<List<ProductInfosGetResponseV1>> feignResponse,
		List<OrderItem> orderItems) {
		if (!feignResponse.getStatusCode().equals(HttpStatus.OK)) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "리셀 주문 상품 정보 요청에 실패했습니다");
		}

		List<ProductInfosGetResponseV1> productInfos = feignResponse.getBody();
		if (productInfos == null) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED, "리셀 주문 상품 정보 응답이 비어있습니다");
		}

		// 요청한 아이템 수와 응답 상품 정보 수가 같은지 검증
		if (productInfos.size() != orderItems.size()) {
			throw AppException.of(HttpStatus.EXPECTATION_FAILED,
				"요청한 주문 상품 수와 조회된 상품 정보 수가 일치하지 않습니다.");
		}

		return productInfos;
	}

	private void attachResellProductInfos(List<OrderItem> orderItems,
		List<ProductInfosGetResponseV1> productInfos) {
		for (OrderItem orderItem : orderItems) {
			ProductInfosGetResponseV1 info =
				findResellInfoByItemId(productInfos, orderItem.getStockId());

			if (info == null) {
				throw AppException.of(HttpStatus.EXPECTATION_FAILED,
					"주문 상품 아이템 정보가 응답에서 누락되었습니다. itemId=" + orderItem.getStockId());
			}

			orderItem.attachProductInfo(info);
		}
	}

	private void attachProductInfos(List<OrderItem> orderItems,
		List<GetOrderedProductInfoResponseV1.OrderedProductInfo> productInfos) {
		for (OrderItem orderItem : orderItems) {
			GetOrderedProductInfoResponseV1.OrderedProductInfo info =
				findInfoByItemId(productInfos, orderItem.getLimitedProductItemId());

			if (info == null) {
				throw AppException.of(HttpStatus.EXPECTATION_FAILED,
					"주문 상품 아이템 정보가 응답에서 누락되었습니다. itemId=" + orderItem.getLimitedProductItemId());
			}

			orderItem.attachProductInfo(info);
		}
	}

	private GetOrderedProductInfoResponseV1.OrderedProductInfo findInfoByItemId(
		List<GetOrderedProductInfoResponseV1.OrderedProductInfo> productInfos,
		UUID itemId
	) {
		for (GetOrderedProductInfoResponseV1.OrderedProductInfo info : productInfos) {
			if (info.getLimitedProductItemId().equals(itemId)) {
				return info;
			}
		}
		return null;
	}
}
