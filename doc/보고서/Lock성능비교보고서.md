# 좌석 예약 서비스 낙관적 락, 비관적 락 동시성 제어 성능 비교
### 1. 테스트 개요
- 대기열의 정원이 20명이라고 가정하고, 동시에 20개의 스레드로 좌석 예약 진행
### 2. 낙관적 락 테스트
![OptimisticTest](../img/optimisticTest_detail.png)
![OptimisticTest](../img/optimisticTest.png)

### 3. 비관적 락 테스트
![PessimisticTest](../img/pessimisticTest_detail.png)
![PessimisticTest](../img/pessimisticTest.png)