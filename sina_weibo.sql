/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50624
Source Host           : localhost:3306
Source Database       : sina_weibo

Target Server Type    : MYSQL
Target Server Version : 50624
File Encoding         : 65001

Date: 2016-08-16 12:19:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
  `weiboID` varchar(16) NOT NULL,
  `poster` varchar(64) NOT NULL,
  `content` varchar(4096) NOT NULL,
  `postTime` datetime NOT NULL,
  `fetchTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`poster`,`postTime`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for fenci
-- ----------------------------
DROP TABLE IF EXISTS `fenci`;
CREATE TABLE `fenci` (
  `weiboID` varchar(255) DEFAULT NULL,
  `id` int(50) NOT NULL AUTO_INCREMENT,
  `fenci` varchar(4096) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23407 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow` (
  `follower` varchar(255) NOT NULL,
  `followee` varchar(100) DEFAULT NULL,
  `level` tinyint(4) DEFAULT NULL,
  `fetchTime` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `isfetched` tinyint(1) DEFAULT '0',
  `isfetched1` tinyint(1) unsigned zerofill DEFAULT '0',
  PRIMARY KEY (`follower`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for follower
-- ----------------------------
DROP TABLE IF EXISTS `follower`;
CREATE TABLE `follower` (
  `follower` varchar(16) NOT NULL,
  `level` tinyint(4) NOT NULL DEFAULT '0',
  `isFetched` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`follower`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for repost
-- ----------------------------
DROP TABLE IF EXISTS `repost`;
CREATE TABLE `repost` (
  `weiboID` varchar(16) NOT NULL,
  `poster` varchar(64) NOT NULL,
  `content` varchar(4096) NOT NULL,
  `postTime` datetime NOT NULL,
  `fetchTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`weiboID`,`poster`,`postTime`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for topic
-- ----------------------------
DROP TABLE IF EXISTS `topic`;
CREATE TABLE `topic` (
  `id` int(10) NOT NULL,
  `topic` varchar(4000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `accountID` varchar(16) NOT NULL,
  `isFetched` tinyint(1) NOT NULL DEFAULT '0',
  `isread` tinyint(1) unsigned zerofill NOT NULL,
  `username` varchar(512) DEFAULT NULL,
  `password` varchar(512) DEFAULT NULL,
  `isfollowfetch` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`,`accountID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for weibo
-- ----------------------------
DROP TABLE IF EXISTS `weibo`;
CREATE TABLE `weibo` (
  `accountID` varchar(64) NOT NULL,
  `weiboID` varchar(16) NOT NULL,
  `content` varchar(4096) NOT NULL,
  `postTime` datetime NOT NULL,
  `fetchTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `isCommentFetched` tinyint(1) NOT NULL DEFAULT '0',
  `isRepostFetched` tinyint(1) NOT NULL DEFAULT '0',
  `commentcount` int(20) DEFAULT NULL,
  `topic` int(5) DEFAULT NULL,
  `gailv` varchar(255) DEFAULT NULL,
  `isclassified` tinyint(1) unsigned zerofill DEFAULT '0',
  `istm` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`weiboID`,`accountID`,`fetchTime`,`postTime`),
  KEY `isCommentFetched` (`isCommentFetched`,`isRepostFetched`),
  KEY `post` (`postTime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Event structure for u
-- ----------------------------
DROP EVENT IF EXISTS `u`;
DELIMITER ;;
CREATE DEFINER=``@`` EVENT `u` ON SCHEDULE AT '2016-04-15 11:26:10' ON COMPLETION NOT PRESERVE ENABLE DO UPDATE weibo SET isCommentFetched = 0
;;
DELIMITER ;
