#DDL

create table users(
	id int unique AUTO_INCREMENT,
	first_name varchar(255) not null,
	last_name varchar(255) not null,
	balance real not null default 0 CHECK (balance >= 0),
	CONSTRAINT empPk PRIMARY KEY(id)
);

create table transactions(
	id int unique AUTO_INCREMENT,
	user_from int,
	user_to int,
	transaction_amount real not null,
	transaction_date timestamp not null default now(),
	CONSTRAINT transId PRIMARY KEY(id),
	CONSTRAINT userToFk FOREIGN KEY(user_to) REFERENCES users(id) ON UPDATE CASCADE,
	CONSTRAINT userFromFk FOREIGN KEY(user_from) REFERENCES users(id) ON UPDATE CASCADE
);

#balance checking triggers
delimiter $$
create trigger notNegativeBalanceInsert 
	before insert on users
	for each row
	begin
	if new.balance < 0 then
	signal sqlstate '45000'
	set message_text = 'balance cannot be negative';
	end if;
end $$
delimiter ;

delimiter $$
create trigger notNegativeBalanceUpdate 
	before update on users
	for each row
	begin
	if new.balance < 0 then
	signal sqlstate '45000'
	set message_text = 'balance cannot be negative';
	end if;
end $$
delimiter ;
