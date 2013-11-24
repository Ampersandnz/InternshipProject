drop table if exists book;

create table BOOK ( 
 id INT NOT NULL auto_increment, 
 author VARCHAR(100) default NULL, 
 title VARCHAR(100) default NULL, 
 series VARCHAR(100) default NULL, 
 description VARCHAR(10000) default NULL, 
 date VARCHAR(20) default NULL, 
 numratings VARCHAR(20) default NULL, 
 numstars INT default NULL, 
 imageData BLOB default NULL, 
 PRIMARY KEY (id) 
); 