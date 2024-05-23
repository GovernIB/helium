--#1696 Guardar la traça de l'error en el processament d'anotacions

-- Oracle
ALTER TABLE HEL_ANOTACIO ADD (ERROR_PROCESSAMENT_CLOB  CLOB);
UPDATE HEL_ANOTACIO SET ERROR_PROCESSAMENT_CLOB = ERROR_PROCESSAMENT;
COMMIT;
ALTER TABLE HEL_ANOTACIO DROP COLUMN ERROR_PROCESSAMENT;
ALTER TABLE HEL_ANOTACIO RENAME COLUMN ERROR_PROCESSAMENT_CLOB TO ERROR_PROCESSAMENT;

-- Postgresql
ALTER TABLE HEL_ANOTACIO ALTER COLUMN ERROR_PROCESSAMENT TYPE text;