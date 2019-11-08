-------------------------------------------------------------------
-- #1356 Error guardant repros amb més de 4000 caràcters d'informació
-- Canvia la columna valors per un clob
-------------------------------------------------------------------

-- Oracle
ALTER TABLE HEL_REPRO ADD (tmpvalors  CLOB);
UPDATE HEL_REPRO SET tmpvalors=valors;
COMMIT;
ALTER TABLE HEL_REPRO DROP COLUMN valors;
ALTER TABLE HEL_REPRO RENAME COLUMN tmpvalors TO valors;


-- Postgresql
ALTER TABLE HEL_REPRO ADD (tmpvalors  TEXT);
UPDATE HEL_REPRO SET tmpvalors=valors;
COMMIT;
ALTER TABLE HEL_REPRO DROP COLUMN valors;
ALTER TABLE HEL_REPRO RENAME COLUMN tmpvalors TO valors;
