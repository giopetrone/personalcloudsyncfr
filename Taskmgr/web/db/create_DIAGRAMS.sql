CREATE TABLE `DIAGRAMS` (
  `ID` int(11) NOT NULL auto_increment,
  `USERNAME` varchar(2047) NOT NULL,
  `DATA` mediumtext NOT NULL,
  `NAME` varchar(40) NOT NULL,
  `CREATED` int(11) NOT NULL,
  `MODIFIED` int(11) NOT NULL,
  `DESCRIPTION` varchar(255) NOT NULL,
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=latin1