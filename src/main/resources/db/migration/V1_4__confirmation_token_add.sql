create sequence confirmation_token_sequence start 1 increment 1;
create table confirmation_token (id int8 not null, confirmed_at timestamp, created_at timestamp, expires_at timestamp, token varchar(255) not null, user_id int8 not null, primary key (id));
alter table if exists confirmation_token add constraint FKah4p1rycwibwm6s9bsyeckq51 foreign key (user_id) references users;