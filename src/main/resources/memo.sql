create table IF NOT EXISTS MEMO (
    _id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id VARCHAR(255) NOT NULL,
	content TEXT NOT NULL,
	FOREIGN KEY(user_id) REFERENCES USER_INFO(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
	index user_by (user_id)
) DEFAULT CHARSET=utf8;