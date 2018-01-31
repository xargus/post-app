create table IF NOT EXISTS MEMO (
    _id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id VARCHAR(255) NOT NULL,
	content TEXT NOT NULL,
	updateDate VARCHAR(50) NOT NULL,
	FOREIGN KEY(user_id) REFERENCES USER_INFO(user_id) ON UPDATE CASCADE ON DELETE CASCADE,
	index user_by (user_id),
	index id_with_date (user_id, updateDate),
	index update_date(updateDate)
) DEFAULT CHARSET=utf8;