--DROP TABLE HEL_ACL_ENTRY;
--DROP TABLE hel_acl_object_identity;
--DROP TABLE HEL_ACL_SID;
--DROP TABLE HEL_ACL_CLASS;
--DROP SEQUENCE HEL_ACL_CLASS_SEQ;
--DROP SEQUENCE HEL_ACL_ENTRY_SEQ;
--DROP SEQUENCE HEL_ACL_OBJECT_IDENTITY_SEQ;
--DROP SEQUENCE HEL_ACL_SID_SEQ;

ALTER TABLE hel_acl_class RENAME TO OLD_ACL_CLASS;
ALTER TABLE hel_acl_entry RENAME TO OLD_ACL_ENTRY;
ALTER TABLE hel_acl_object_identity RENAME TO OLD_ACL_OBJECT_IDENTITY;
ALTER TABLE hel_acl_sid RENAME TO OLD_ACL_SID;

--------------------------------------------------------
-- ACL_CLASS Table
--------------------------------------------------------
CREATE TABLE hel_acl_class (
  ID bigserial NOT NULL,
  class VARCHAR(100) NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT "ACL_CLASS_CLASS_UQ" UNIQUE (class)
);
 
--------------------------------------------------------
-- ACL_ENTRY Table
--------------------------------------------------------
CREATE TABLE hel_acl_entry (
  ID bigserial NOT NULL,
  ACL_OBJECT_IDENTITY BIGINT NOT NULL,
  ACE_ORDER BIGINT NOT NULL,
  SID BIGINT NOT NULL,
  MASK BIGINT NOT NULL,
  GRANTING BOOLEAN NOT NULL,
  AUDIT_SUCCESS BOOLEAN NOT NULL,
  AUDIT_FAILURE BOOLEAN NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT "hel_acl_entry_IDENT_ORDER_UQ" UNIQUE (ACL_OBJECT_IDENTITY, ACE_ORDER)
);


 
--------------------------------------------------------
-- ACL_OBJECT_IDENTITY Table
--------------------------------------------------------
CREATE TABLE hel_acl_object_identity (
  ID bigserial NOT NULL,
  OBJECT_ID_CLASS BIGINT NOT NULL,
  OBJECT_ID_IDENTITY BIGINT NOT NULL,
  PARENT_OBJECT BIGINT,
  OWNER_SID BIGINT NOT NULL,
  ENTRIES_INHERITING BOOLEAN NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT "HEL_ACL_OBJ_ID_CLASS_IDENT_UQ" UNIQUE (OBJECT_ID_CLASS, OBJECT_ID_IDENTITY)
);

 
--------------------------------------------------------
-- ACL_SID Table
--------------------------------------------------------
CREATE TABLE hel_acl_sid (
  ID bigserial NOT NULL,
  PRINCIPAL BOOLEAN NOT NULL,
  SID VARCHAR(100) NOT NULL,
  PRIMARY KEY (ID),
  CONSTRAINT "hel_acl_sid_PRINCIPAL_SID_UQ" UNIQUE (SID, PRINCIPAL)
);


 
--------------------------------------------------------
-- Relationships
--------------------------------------------------------
 
ALTER TABLE hel_acl_entry ADD CONSTRAINT "FK_ACL_ENTRY_ACL_OBJECT_ID"
  FOREIGN KEY (ACL_OBJECT_IDENTITY)
  REFERENCES hel_acl_object_identity (ID);
ALTER TABLE hel_acl_entry ADD CONSTRAINT "FK_ACL_ENTRY_SID"
  FOREIGN KEY (SID)
  REFERENCES hel_acl_sid (ID);
 
ALTER TABLE hel_acl_object_identity ADD CONSTRAINT "FK_ACL_OBJ_ID_CLASS"
  FOREIGN KEY (OBJECT_ID_CLASS)
  REFERENCES hel_acl_class (ID);
ALTER TABLE hel_acl_object_identity ADD CONSTRAINT "FK_ACL_OBJ_ID_PARENT"
  FOREIGN KEY (PARENT_OBJECT)
  REFERENCES hel_acl_object_identity (ID);
ALTER TABLE hel_acl_object_identity ADD CONSTRAINT "FK_ACL_OBJ_ID_SID"
  FOREIGN KEY (OWNER_SID)
  REFERENCES hel_acl_sid (ID);

--------------------------------------------------------
-- Copy from old tables
--------------------------------------------------------

INSERT INTO hel_acl_class (ID, class) SELECT ID, CLASS FROM OLD_ACL_CLASS;
INSERT INTO hel_acl_sid (ID, PRINCIPAL, SID) SELECT ID, PRINCIPAL, SID FROM OLD_ACL_SID;
INSERT INTO hel_acl_object_identity (ID,OBJECT_ID_CLASS,OBJECT_ID_IDENTITY,OWNER_SID,ENTRIES_INHERITING,PARENT_OBJECT) SELECT ID,OBJECT_ID_CLASS,OBJECT_ID_IDENTITY,OWNER_SID,ENTRIES_INHERITING,PARENT_OBJECT FROM OLD_ACL_OBJECT_IDENTITY;
INSERT INTO hel_acl_entry (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) SELECT ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE FROM OLD_ACL_ENTRY;

--------------------------------------------------------
-- Create sequences
--------------------------------------------------------


/*
DECLARE
  CURSOR C is SELECT MAX(ID)+1 FROM hel_acl_class;
  VRESULT NUMBER;
  STMT VARCHAR2(1000);
BEGIN
  OPEN C;
  FETCH C INTO VRESULT;
  CLOSE C;
  STMT := 'CREATE SEQUENCE "hel_acl_class_SEQ" '||
    'INCREMENT BY 1 '||
	'MAXVALUE 9999999999999999999999999999 '||
    'START WITH '|| TO_CHAR(VRESULT) ||
    ' CACHE 20 '||
    'NOORDER '||
    'NOCYCLE';
  EXECUTE IMMEDIATE STMT;
END;
/

DECLARE
  CURSOR C is SELECT MAX(ID)+1 FROM hel_acl_entry;
  VRESULT NUMBER;
  STMT VARCHAR2(1000);
BEGIN
  OPEN C;
  FETCH C INTO VRESULT;
  CLOSE C;
  STMT := 'CREATE SEQUENCE "hel_acl_entry_SEQ" '||
    'INCREMENT BY 1 '||
	'MAXVALUE 9999999999999999999999999999 '||
    'START WITH '|| TO_CHAR(VRESULT) ||
    ' CACHE 20 '||
    'NOORDER '||
    'NOCYCLE';
  EXECUTE IMMEDIATE STMT;
END;
/

DECLARE
  CURSOR C is SELECT MAX(ID)+1 FROM hel_acl_object_identity;
  VRESULT NUMBER;
  STMT VARCHAR2(1000);
BEGIN
  OPEN C;
  FETCH C INTO VRESULT;
  CLOSE C;
  STMT := 'CREATE SEQUENCE "hel_acl_object_identity_SEQ" '||
    'INCREMENT BY 1 '||
	'MAXVALUE 9999999999999999999999999999 '||
    'START WITH '|| TO_CHAR(VRESULT) ||
    ' CACHE 20 '||
    'NOORDER '||
    'NOCYCLE';
  EXECUTE IMMEDIATE STMT;
END;
/

DECLARE
  CURSOR C is SELECT MAX(ID)+1 FROM hel_acl_sid;
  VRESULT NUMBER;
  STMT VARCHAR2(1000);
BEGIN
  OPEN C;
  FETCH C INTO VRESULT;
  CLOSE C;
  STMT := 'CREATE SEQUENCE "hel_acl_sid_SEQ" '||
    'INCREMENT BY 1 '||
	'MAXVALUE 9999999999999999999999999999 '||
    'START WITH '|| TO_CHAR(VRESULT) ||
    ' CACHE 20 '||
    'NOORDER '||
    'NOCYCLE';
  EXECUTE IMMEDIATE STMT;
END;
/

*/

--------------------------------------------------------
-- Triggers
--------------------------------------------------------

/*

CREATE OR REPLACE TRIGGER "hel_acl_class_ID"
BEFORE INSERT ON hel_acl_class
FOR EACH ROW
  BEGIN
    SELECT hel_acl_class_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
 
CREATE OR REPLACE TRIGGER "hel_acl_entry_ID"
BEFORE INSERT ON hel_acl_entry
FOR EACH ROW
  BEGIN
    SELECT hel_acl_entry_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
 
CREATE OR REPLACE TRIGGER "hel_acl_object_identity_ID"
BEFORE INSERT ON hel_acl_object_identity
FOR EACH ROW
  BEGIN
    SELECT hel_acl_object_identity_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
 
CREATE OR REPLACE TRIGGER "hel_acl_sid_ID"
BEFORE INSERT ON hel_acl_sid
FOR EACH ROW
  BEGIN
    SELECT hel_acl_sid_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
*/

--------------------------------------------------------
-- Permesos
--------------------------------------------------------

-- CREATE PUBLIC SYNONYM hel_acl_class FOR hel_acl_class;
-- CREATE PUBLIC SYNONYM hel_acl_entry FOR hel_acl_entry;
-- CREATE PUBLIC SYNONYM hel_acl_object_identity FOR hel_acl_object_identity;
-- CREATE PUBLIC SYNONYM hel_acl_sid FOR hel_acl_sid;

/*

GRANT SELECT, UPDATE, INSERT, DELETE ON hel_acl_class TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON hel_acl_entry TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON hel_acl_object_identity TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON hel_acl_sid TO WWW_HELIUM;

GRANT SELECT ON hel_acl_class_SEQ TO WWW_HELIUM;
GRANT SELECT ON hel_acl_entry_SEQ TO WWW_HELIUM;
GRANT SELECT ON hel_acl_object_identity_SEQ TO WWW_HELIUM;
GRANT SELECT ON hel_acl_sid_SEQ TO WWW_HELIUM;

*/