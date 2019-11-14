-------------------------------------------------------------------
-- #1314 Afegir dades d'enviament als interessats per a les notificacions postals
-------------------------------------------------------------------
-- El NIF pot ser null per administracions públiques
ALTER TABLE HELIUM.HEL_INTERESSAT MODIFY NIF VARCHAR2(9) NULL;

ALTER TABLE HEL_INTERESSAT ADD ENTREGAPOSTAL NUMBER(1,0) DEFAULT 0 NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD ENTREGATIPUS NUMBER(10,0);
ALTER TABLE HEL_INTERESSAT ADD LINIA1 VARCHAR2(50 CHAR);
ALTER TABLE HEL_INTERESSAT ADD LINIA2 VARCHAR2(50 CHAR);
ALTER TABLE HEL_INTERESSAT ADD ENTREGADEH NUMBER(1,0) DEFAULT 0 NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD ENTREGADEHOBLIGAT NUMBER(1,0) DEFAULT 0 NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD CODIPOSTAL VARCHAR2(5 CHAR);
ALTER TABLE HEL_INTERESSAT ADD DIR3CODI VARCHAR2(9 CHAR);

-- Actualitza el codi Dir3 als interessats de tipus adminstració
UPDATE HEL_INTERESSAT i SET i.DIR3CODI = NIF, i.NIF = NULL WHERE i.DIR3CODI IS NULL AND i.TIPUS = 0;

-------------------------------------------------------------------
-- #1356 Error guardant repros amb més de 4000 caràcters d'informació
-- Canvia la columna valors per un clob
-------------------------------------------------------------------
ALTER TABLE HEL_REPRO ADD (tmpvalors  CLOB);
UPDATE HEL_REPRO SET tmpvalors=valors;
COMMIT;
ALTER TABLE HEL_REPRO DROP COLUMN valors;
ALTER TABLE HEL_REPRO RENAME COLUMN tmpvalors TO valors;