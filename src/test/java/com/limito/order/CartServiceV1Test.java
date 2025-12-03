package com.limito.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CartServiceV1Test {

    @Test
    @DisplayName("""
            장바구니 생성 로직 없이 첫 상품을 담으면 장바구니가 생성됨
            레디스 저장 형식
            - key = cart:limited:{userId} , cart:resell:{userId}
            - field = {optionId}    <-- Todo. 리셀은 옵션 아이디로 똑같이 했는데 재고 아이디로 바꿔야 할 지 생각히보기
            - value = dto
            """)
    public void addCart() {

    }

}
