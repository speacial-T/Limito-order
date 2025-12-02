package com.limito.order.order.service;

import com.limito.order.order.domain.repository.OrderServiceRepositoryV1;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceV1 {
    private final OrderServiceRepositoryV1 orderServiceRepository;
}
