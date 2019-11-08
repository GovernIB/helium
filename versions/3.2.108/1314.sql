-------------------------------------------------------------------
-- #1314 Afegir dades d'enviament als interessats per a les notificacions postals
-------------------------------------------------------------------

-- Oracle
ALTER TABLE HEL_INTERESSAT ADD ENTREGAPOSTAL NUMBER(1,0) DEFAULT 0 NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD ENTREGATIPUS NUMBER(10,0);
ALTER TABLE HEL_INTERESSAT ADD LINIA1 VARCHAR2(50 CHAR);
ALTER TABLE HEL_INTERESSAT ADD LINIA2 VARCHAR2(50 CHAR);
ALTER TABLE HEL_INTERESSAT ADD ENTREGADEH NUMBER(1,0) DEFAULT 0 NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD ENTREGADEHOBLIGAT NUMBER(1,0) DEFAULT 0 NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD CODIPOSTAL VARCHAR2(5 CHAR);


-- Postgres
ALTER TABLE HEL_INTERESSAT ADD ENTREGAPOSTAL BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD ENTREGATIPUS INTEGER;
ALTER TABLE HEL_INTERESSAT ADD LINIA1 VARCHAR(50);
ALTER TABLE HEL_INTERESSAT ADD LINIA2 VARCHAR(50);
ALTER TABLE HEL_INTERESSAT ADD ENTREGADEH BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD ENTREGADEHOBLIGAT BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD CODIPOSTAL VARCHAR(5);