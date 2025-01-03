SET REFERENTIAL_INTEGRITY FALSE; --제약조건 비활성화
truncate table transaction_tb;
truncate table account_tb;
truncate table user_tb;
SET REFERENTIAL_INTEGRITY TRUE;