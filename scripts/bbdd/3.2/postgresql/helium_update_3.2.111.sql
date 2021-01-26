-- #1432 Poder diferenciar l'enton de PRE i PRO segons el color de la capçalera

ALTER TABLE HEL_ENTORN ADD COLOR_FONS character varying(255);
ALTER TABLE HEL_ENTORN ADD COLOR_LLETRA character varying(255);

-- #1443 Revisar el destinatari dels avisos
-- Treure obligatorietat del destinatari de les alertes 
ALTER TABLE HEL_ALERTA ALTER COLUMN  DESTINATARI DROP NOT NULL;
