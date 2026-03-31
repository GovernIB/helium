alter session set "_ORACLE_SCRIPT" = true;
 
CREATE USER helium2 IDENTIFIED BY helium2;
GRANT CONNECT, RESOURCE TO helium2;
ALTER USER helium2 quota unlimited on users;
