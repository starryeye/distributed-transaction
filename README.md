# distributed-transaction
distributed-transaction

## projects
- 간단한 주문시스템을 통해 분산 트랜잭션을 알아본다.
- 동일한 요구 사항으로 monolithic, msa 아키텍처 각각의 구현을 해봄
- monolithic
  - 모놀리식 아키텍처에서 트랜잭션을 알아본다.
- msa
  - MSA 아키텍처에서 분산트랜잭션을 알아본다.

## requirements
- Order, 주문 기능
- Product, 재고 관리 기능
- Point, 포인트로 결제 기능
- 주문, 재고, 포인트의 정합성
- 동일한 주문은 1회만 처리 보장
