package com.limito.order.order.service;

import org.springframework.stereotype.Service;

import com.limito.order.order.domain.repository.OrderServiceRepositoryV1;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceV1 {
	private final OrderServiceRepositoryV1 orderServiceRepository;
}
