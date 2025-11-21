## TCC pattern
- TCC(Try - Confirm - Cancel) 는 분산 시스템에서 데이터 정합성을 보장하기 위해 사용하는 분산 트랜잭션 처리 방식 중 하나이다.
- 트랜잭션을 3단계로 나누어 관리한다.
	- Try : 필요한 리소스를 점유할 수 있는지 검사하고 예약한다.
	- Confirm : 트랜잭션 처리가 완료되고 실제 리소스를 확정 처리하여 반영한다.
	- Cancel : 트랜잭션 처리 중 문제가 발생한 경우, 예약 상태를 취소하고 원복한다.
- Try, Confirm, Cancel 단계는 멱등하게 설계되어야한다.

### 구현
- order, product, point 3개의 application 간에 핵심 api 는 confirm 단계로 생각하면되고
- try, cancel api 를 새로 신설하며, db 에도 자원을 임시 예약 할 수 있도록 table 및 column 을 추가해줘야한다.
- order application 에서는 coordinator 를 두고 3개의 도메인 TCC 를 조율하는 코드를 개발

### monolithic 과 비교
- monolithic 에서는 데이터베이스에서 제공하는 트랜잭션 기능을 사용하여 rollback, commit 에 의존하던 것을 MSA 구조에서는 application layer 에서 논리적으로 구현하여 관리하도록 한다.(Try, Confirm, Cancel)

### 2PC 방식과 비교
- 2PC 방식에서는 여러 데이터베이스를 전역으로 관리하는 하나의 application 이 있고, 이를 전역 트랜잭션으로 관리한다.
- 따라서, 데이터베이스의 커넥션 및 락을 장시간 점유하는 것이다.
- TCC 에서는 각 서비스가 각 데이터베이스의 로컬 트랜잭션만 처리하므로 성능상 유리

### TCC 단점
- 데이터베이스가 제공해주던 기능을 application layer 에서 처리해야하므로.. 설계 구현 난이도가 대폭 상승한다.
- TCC 를 위해, 핵심 비즈니스에서 사용할 필요가 없던 데이터베이스 Table, column 이 추가된다.
- 모든 api 를 멱등하게 설계해야하고 api 재시도 전략을 적용해야한다.