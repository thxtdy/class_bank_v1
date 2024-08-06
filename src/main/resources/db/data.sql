-- User
insert into user_tb(username, password, fullname, created_at)
values('길동', '1234', '고', now());

insert into user_tb(username, password, fullname, created_at)
values('둘리', '1234', '애기공룡', now());

insert into user_tb(username, password, fullname, created_at)
values('콜', '1234', '마이', now());

-- Account
insert into account_tb
		(number, password, balance, user_id, created_at)
values('1111', '1234', 1300, 1, now());        

insert into account_tb
		(number, password, balance, user_id, created_at)
values('2222', '1234', 1100, 2, now());        

insert into account_tb
		(number, password, balance, user_id, created_at)
values('3333', '1234', 0, 3, now()); 

-- History
INSERT INTO history_tb(amount, w_account_id, d_account_id, w_balance, d_balance, created_at)
VALUES(100, 1, 2, 900, 1100, now());

INSERT INTO history_tb(amount, w_account_id, d_account_id, w_balance, d_balance, created_at)
VALUES(100, 1, null, 800, null, now());

INSERT INTO history_tb(amount, w_account_id, d_account_id, w_balance, d_balance, created_at)
VALUES(500, null, 1, null, 1300, now());