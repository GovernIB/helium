-- #1443 Revisar el destinatari dels avisos
-- Treure obligatorietat del destinatari de les alertes 


--Oracle
ALTER TABLE HEL_ALERTA MODIFY (DESTINATARI NULL);

--Postgres
ALTER TABLE HEL_ALERTA ALTER COLUMN  DESTINATARI DROP NOT NULL;

