drop table if exists book;
drop table if exists users;

create table BOOK ( 
 id INT NOT NULL auto_increment, 
 isbn VARCHAR(100) default NULL, 
 title VARCHAR(100) default NULL,
 inPossessionOf VARCHAR(100) default 'Library', 
 PRIMARY KEY (id) 
);

create table USERS ( 
 id INT NOT NULL auto_increment, 
 name VARCHAR(100) default 'NULL', 
 email VARCHAR(100) default 'NULL', 
 PRIMARY KEY (id) 
);