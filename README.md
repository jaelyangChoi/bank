# Security를 활용한 Bank 애플리케이션

- Spring Security, Spring data JPA, JWT를 활용한 REST API
- TDD 기반 구현
---

## 주요 기능
1. 회원
    + 회원 가입
        - 회원 가입 시 유효성 검사 (``AOP``와 ``정규표현식`` 적용)
    + 로그인
        - 로그인 성공 시 ``JWT`` 토큰을 발급한다
    + 인가
        - ``JWT`` 토큰을 이용해 인가를 진행한다. 
---
2. 계좌
    + 계좌 등록
    + 계좌 목록 조회
    + 계좌 상세 보기
    + 입금
      - ATM 기계에서 입금
    + 출금
      - ATM 기계로 출금
    + 이체
      - A 계좌에서 B 계좌로 이체
---
3. 거래
   + 거래 이력 조회
     - 입금 시, 보낸 계좌 ATM
     - 출금 시, 받은 계좌 ATM
     - 거래 당시 잔액 조회
     - ``페이징`` 처리

---    
## API 정의
### 회원 API
     - /api/join     POST  유효성 검사 후 회원 가입
     - /api/login    POST  로그인 성공 시 JWT 토큰 발생 
     - /api/s/user/login-user/password    PUT 

### 계좌 API
     - /api/s/account               POST 
     - /api/s/account/login-user    GET
     - /api/s/account/{account_id}  DELETE
     - /api/account/deposit         POST
     - /api/s/account/withdraw      POST
     - /api/s/account/transfer      POST
     - /api/s/account/{account_id}?page={page_number} GET

### 거래 API
      - api/s/account/{account_id}/transaction?gubun={transaction_type}&page={page_nubmer} GET

