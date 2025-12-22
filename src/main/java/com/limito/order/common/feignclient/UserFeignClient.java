package com.limito.order.common.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.limito.order.order.domain.dto.feignclient.user.OrderedUserInfoResponseV1;

@FeignClient(name = "user-service")
public interface UserFeignClient {
	@GetMapping("/internal/api/v1/user/{userId}/ordered-user")
	public ResponseEntity<OrderedUserInfoResponseV1> getOrderedUserInfo(@PathVariable Long userId);
}
