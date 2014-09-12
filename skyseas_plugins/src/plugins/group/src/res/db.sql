CREATE TABLE sky_Group(
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(50) NOT NULL,
  owner VARCHAR(50) NOT NULL,
  subject VARCHAR(100) NOT NULL,
  category INT DEFAULT 0 ,
  logo VARCHAR(200),
  description TEXT,
  openness TINYINT NOT NULL,
  createTime DATETIME NOT NULL,
  numberOfMembers INT NOT NULL DEFAULT 0);


CREATE TABLE sky_GroupMembers(
  id INT PRIMARY KEY AUTO_INCREMENT,
  groupId INT NOT NULL ,
  userName VARCHAR(50) NOT NULL,
  nickName VARCHAR(50) NOT NULL,
  joinTime DATETIME NOT NULL
);
CREATE UNIQUE INDEX uidx_groupMember_groupId_and_userName_ ON sky_GroupMember (groupId, userName);

CREATE TABLE sky_GroupHistoryMessages (
  id INT PRIMARY KEY AUTO_INCREMENT,
  groupId INT NOT NULL,
  sender VARCHAR (100) NOT NULL ,
  sendTime DATETIME NOT NULL ,
  body TEXT NOT NULL ,
  inputTime DATETIME NOT NULL
);
CREATE INDEX idx_groupHistoryMessages_groupId   ON sky_GroupHistoryMessages (groupId);
CREATE INDEX idx_groupHistoryMessages_sendTime  ON sky_GroupHistoryMessages (sendTime);
