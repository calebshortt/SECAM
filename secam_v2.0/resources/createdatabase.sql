CREATE DATABASE secam_secure;

use secam_secure;

CREATE TABLE audit_login (
	id int PRIMARY KEY AUTO_INCREMENT,
	username varchar(30) NOT NULL,
	user_id int NOT NULL,
	computer_id int NOT NULL,
	success char(1) NOT NULL,
	timestamp timestamp DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE users (
	id int PRIMARY KEY,
	first_name varchar(20),
	last_name varchar(20),
	company varchar(35),
	username varchar(30),
	password varchar(30)
);

CREATE TABLE computers (
	id int PRIMARY KEY,
	user_id int,
	mac varchar(30),
	FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE videos (
	id int PRIMARY KEY,
	computer_id int,
	user_id int,
	start_date datetime,
	end_date datetime,
	video_file char(255),
	FOREIGN KEY(computer_id) REFERENCES computers(id),
	FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE settings (
	id int PRIMARY KEY,
	user_id int,
	computer_id int,
	threshold double,
	resolution varchar(20),
	zoom varchar(20),
	record_duration int,
	notify_security int,
	stream int,
	FOREIGN KEY(user_id) REFERENCES users(id),
	FOREIGN KEY(computer_id) REFERENCES computers(id)
);

INSERT INTO users VALUES (0,"Testy","McTesterson","Test","test","testpass");
INSERT INTO computers VALUES (0,0,"TESTMACADDRESS");


