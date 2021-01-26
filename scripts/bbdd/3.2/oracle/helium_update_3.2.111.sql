-- #1432 Poder diferenciar l'enton de PRE i PRO segons el color de la capçalera

ALTER TABLE HEL_ENTORN ADD COLOR_FONS VARCHAR2(255 CHAR);
ALTER TABLE HEL_ENTORN ADD COLOR_LLETRA VARCHAR2(255 CHAR);

-- #1443 Revisar el destinatari dels avisos
-- Treure obligatorietat del destinatari de les alertes 
ALTER TABLE HEL_ALERTA MODIFY (DESTINATARI NULL);
