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