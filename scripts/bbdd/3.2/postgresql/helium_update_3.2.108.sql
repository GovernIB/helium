-------------------------------------------------------------------
-- #1314 Afegir dades d'enviament als interessats per a les notificacions postals
-------------------------------------------------------------------
ALTER TABLE HEL_INTERESSAT ADD ENTREGAPOSTAL BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD ENTREGATIPUS INTEGER;
ALTER TABLE HEL_INTERESSAT ADD LINIA1 VARCHAR(50);
ALTER TABLE HEL_INTERESSAT ADD LINIA2 VARCHAR(50);
ALTER TABLE HEL_INTERESSAT ADD ENTREGADEH BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD ENTREGADEHOBLIGAT BOOLEAN DEFAULT FALSE NOT NULL;
ALTER TABLE HEL_INTERESSAT ADD CODIPOSTAL VARCHAR(5);
ALTER TABLE HEL_INTERESSAT ADD DIR3CODI VARCHAR(9);


-------------------------------------------------------------------
-- #1356 Error guardant repros amb més de 4000 caràcters d'informació
-- Canvia la columna valors per un clob
-------------------------------------------------------------------
ALTER TABLE HEL_REPRO ADD COLUMN tmpvalors TEXT;
UPDATE HEL_REPRO SET tmpvalors=valors;
COMMIT;
ALTER TABLE HEL_REPRO DROP COLUMN valors;
ALTER TABLE HEL_REPRO RENAME COLUMN tmpvalors TO valors;
