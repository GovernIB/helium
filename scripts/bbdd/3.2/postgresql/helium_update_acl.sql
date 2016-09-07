
ALTER TABLE hel_acl_class RENAME TO old_acl_class;
ALTER TABLE hel_acl_entry RENAME TO old_acl_entry;
ALTER TABLE hel_acl_object_identity RENAME TO old_acl_object_identity;
ALTER TABLE hel_acl_sid RENAME TO old_acl_sid;

--------------------------------------------------------
-- ACL_CLASS Table
--------------------------------------------------------
CREATE TABLE "hel_acl_class" (
  "id" BIGINT NOT NULL,
  "class" VARCHAR(100) NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "ACL_CLASS_CLASS_UQ" UNIQUE ("class")
);
 
--------------------------------------------------------
-- ACL_ENTRY Table
--------------------------------------------------------
CREATE TABLE "hel_acl_entry" (
  "id" BIGINT NOT NULL,
  "acl_object_identity" BIGINT NOT NULL,
  "ace_order" BIGINT NOT NULL,
  "sid" BIGINT NOT NULL,
  "mask" BIGINT NOT NULL,
  "granting" BOOLEAN NOT NULL,
  "audit_success" BOOLEAN NOT NULL,
  "audit_failure" BOOLEAN NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "HEL_ACL_ENTRY_IDENT_ORDER_UQ" UNIQUE ("acl_object_identity", "ace_order")
);


 
--------------------------------------------------------
-- ACL_OBJECT_IDENTITY Table
--------------------------------------------------------
CREATE TABLE "hel_acl_object_identity" (
  "id" BIGINT NOT NULL,
  "object_id_class" BIGINT NOT NULL,
  "object_id_identity" BIGINT NOT NULL,
  "parent_object" BIGINT,
  "owner_sid" BIGINT NOT NULL,
  "entries_inheriting" BOOLEAN NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "HEL_ACL_OBJ_ID_CLASS_IDENT_UQ" UNIQUE ("object_id_class", "object_id_identity")
);

 
--------------------------------------------------------
-- ACL_SID Table
--------------------------------------------------------
CREATE TABLE "hel_acl_sid" (
  "id" BIGINT NOT NULL,
  "principal" BOOLEAN NOT NULL,
  "sid" VARCHAR(100) NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "HEL_ACL_SID_PRINCIPAL_SID_UQ" UNIQUE ("sid", "principal")
);


 
--------------------------------------------------------
-- Relationships
--------------------------------------------------------
 
ALTER TABLE "hel_acl_entry" ADD CONSTRAINT "FK_ACL_ENTRY_ACL_OBJECT_ID"
  FOREIGN KEY ("acl_object_identity")
  REFERENCES "hel_acl_object_identity" ("id");
ALTER TABLE "hel_acl_entry" ADD CONSTRAINT "FK_ACL_ENTRY_SID"
  FOREIGN KEY ("sid")
  REFERENCES "hel_acl_sid" ("id");
 
ALTER TABLE "hel_acl_object_identity" ADD CONSTRAINT "FK_ACL_OBJ_ID_CLASS"
  FOREIGN KEY ("object_id_class")
  REFERENCES "hel_acl_class" ("id");
ALTER TABLE "hel_acl_object_identity" ADD CONSTRAINT "FK_ACL_OBJ_ID_PARENT"
  FOREIGN KEY ("parent_object")
  REFERENCES "hel_acl_object_identity" ("id");
ALTER TABLE "hel_acl_object_identity" ADD CONSTRAINT "FK_ACL_OBJ_ID_SID"
  FOREIGN KEY ("owner_sid")
  REFERENCES "hel_acl_sid" ("id");

--------------------------------------------------------
-- Copy from old tables
--------------------------------------------------------

INSERT INTO "HEL_ACL_CLASS" ("ID", "CLASS") SELECT ID, CLASS FROM OLD_ACL_CLASS;
INSERT INTO "HEL_ACL_SID" ("ID", "PRINCIPAL", "SID") SELECT ID, PRINCIPAL, SID FROM OLD_ACL_SID;
INSERT INTO "HEL_ACL_OBJECT_IDENTITY" ("ID","OBJECT_ID_CLASS","OBJECT_ID_IDENTITY","OWNER_SID","ENTRIES_INHERITING","PARENT_OBJECT") SELECT ID,OBJECT_ID_CLASS,OBJECT_ID_IDENTITY,OWNER_SID,ENTRIES_INHERITING,PARENT_OBJECT FROM OLD_ACL_OBJECT_IDENTITY;
INSERT INTO "HEL_ACL_ENTRY" ("ID", "ACL_OBJECT_IDENTITY", "ACE_ORDER", "SID", "MASK", "GRANTING", "AUDIT_SUCCESS", "AUDIT_FAILURE") SELECT ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE FROM OLD_ACL_ENTRY;

--------------------------------------------------------
-- Create sequences
--------------------------------------------------------

/*
DECLARE
  CURSOR C is SELECT MAX(ID)+1 FROM HEL_ACL_CLASS;
  VRESULT NUMBER;
  STMT VARCHAR2(1000);
BEGIN
  OPEN C;
  FETCH C INTO VRESULT;
  CLOSE C;
  STMT := 'CREATE SEQUENCE "HEL_ACL_CLASS_SEQ" '||
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
  CURSOR C is SELECT MAX(ID)+1 FROM HEL_ACL_ENTRY;
  VRESULT NUMBER;
  STMT VARCHAR2(1000);
BEGIN
  OPEN C;
  FETCH C INTO VRESULT;
  CLOSE C;
  STMT := 'CREATE SEQUENCE "HEL_ACL_ENTRY_SEQ" '||
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
  CURSOR C is SELECT MAX(ID)+1 FROM HEL_ACL_OBJECT_IDENTITY;
  VRESULT NUMBER;
  STMT VARCHAR2(1000);
BEGIN
  OPEN C;
  FETCH C INTO VRESULT;
  CLOSE C;
  STMT := 'CREATE SEQUENCE "HEL_ACL_OBJECT_IDENTITY_SEQ" '||
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
  CURSOR C is SELECT MAX(ID)+1 FROM HEL_ACL_SID;
  VRESULT NUMBER;
  STMT VARCHAR2(1000);
BEGIN
  OPEN C;
  FETCH C INTO VRESULT;
  CLOSE C;
  STMT := 'CREATE SEQUENCE "HEL_ACL_SID_SEQ" '||
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

CREATE OR REPLACE TRIGGER "HEL_ACL_CLASS_ID"
BEFORE INSERT ON HEL_ACL_CLASS
FOR EACH ROW
  BEGIN
    SELECT HEL_ACL_CLASS_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
 
CREATE OR REPLACE TRIGGER "HEL_ACL_ENTRY_ID"
BEFORE INSERT ON HEL_ACL_ENTRY
FOR EACH ROW
  BEGIN
    SELECT HEL_ACL_ENTRY_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
 
CREATE OR REPLACE TRIGGER "HEL_ACL_OBJECT_IDENTITY_ID"
BEFORE INSERT ON HEL_ACL_OBJECT_IDENTITY
FOR EACH ROW
  BEGIN
    SELECT HEL_ACL_OBJECT_IDENTITY_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
 
CREATE OR REPLACE TRIGGER "HEL_ACL_SID_ID"
BEFORE INSERT ON HEL_ACL_SID
FOR EACH ROW
  BEGIN
    SELECT HEL_ACL_SID_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
*/

--------------------------------------------------------
-- Permesos
--------------------------------------------------------

-- CREATE PUBLIC SYNONYM HEL_ACL_CLASS FOR HEL_ACL_CLASS;
-- CREATE PUBLIC SYNONYM HEL_ACL_ENTRY FOR HEL_ACL_ENTRY;
-- CREATE PUBLIC SYNONYM HEL_ACL_OBJECT_IDENTITY FOR HEL_ACL_OBJECT_IDENTITY;
-- CREATE PUBLIC SYNONYM HEL_ACL_SID FOR HEL_ACL_SID;

/*

GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ACL_CLASS TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ACL_ENTRY TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ACL_OBJECT_IDENTITY TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ACL_SID TO WWW_HELIUM;

GRANT SELECT ON HEL_ACL_CLASS_SEQ TO WWW_HELIUM;
GRANT SELECT ON HEL_ACL_ENTRY_SEQ TO WWW_HELIUM;
GRANT SELECT ON HEL_ACL_OBJECT_IDENTITY_SEQ TO WWW_HELIUM;
GRANT SELECT ON HEL_ACL_SID_SEQ TO WWW_HELIUM;

*/
