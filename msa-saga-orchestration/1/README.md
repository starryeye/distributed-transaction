## Saga pattern
- 분산 시스템에서 데이터 정합성을 보장하기 위해 사용하는 분산트랜잭션 처리 방식
- 각 작업을 개별트랜잭션으로 나누고 실패시 보상트랜잭션을 수행하여 정합성을 맞춘다.
  - 보상트랜잭션 로직은 멱등성으로 재시도 가능하도록해야한다.
- TCC 와 비교하자면, 리소스 예약(reservation) 없이 즉시 상태 변경을 수행한다.
  - Product 를 예로 들면, 재고 차감 예약이 아닌 즉시 재고 차감이다.
- 각 작업이 개별트랜잭션으로 나누어져 있어서 순간적으로 정합성이 맞지 않을 수 있으나, 최종적 일관성을 보장하도록한다.
- Orchestration 방식, Choreography 방식이 존재한다.

### MSA, Saga pattern - orchestration
- 

### 구현
- 

### DB
- order
  - <img width="687" height="388" alt="image" src="https://github.com/user-attachments/assets/2bec14ae-ef5a-4217-8cd2-83f305b82b37" />
- product
  - <img width="359" height="423" alt="image" src="https://github.com/user-attachments/assets/84ee9cab-517d-4df8-98bf-5314848d2bc4" />
- point
  - <img width="336" height="400" alt="image" src="https://github.com/user-attachments/assets/c624a933-f215-445e-a9e7-67d381b9f377" />

### 비교
- 

### 장단점
-
