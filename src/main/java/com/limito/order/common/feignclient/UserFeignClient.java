package com.limito.order.common.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.limito.order.order.domain.dto.feignclient.user.OrderedUserInfoResponseV1;

@FeignClient(name = "user-service", url = "${feign.user-service.url}")
public interface UserFeignClient {
	@PostMapping("/internal/api/v1/user/{userId}/ordered-user")
	public ResponseEntity<OrderedUserInfoResponseV1> getOrderedUserInfo(@PathVariable Long userId);
}
