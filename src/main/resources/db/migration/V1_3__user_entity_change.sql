alter table if exists users drop column username;
alter table if exists users add column email varchar(255);
