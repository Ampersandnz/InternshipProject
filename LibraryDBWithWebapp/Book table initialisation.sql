drop table if exists book;

create table BOOK ( 
 id INT NOT NULL auto_increment, 
 isbn VARCHAR(100) default NULL, 
 title VARCHAR(100) default NULL,
 inPossessionOf VARCHAR(100) default 'Library', 
 PRIMARY KEY (id) 
);