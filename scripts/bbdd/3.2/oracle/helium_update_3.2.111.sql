-- #1432 Poder diferenciar l'enton de PRE i PRO segons el color de la capçalera

ALTER TABLE HEL_ENTORN ADD COLOR_FONS VARCHAR2(255 CHAR);
ALTER TABLE HEL_ENTORN ADD COLOR_LLETRA VARCHAR2(255 CHAR);

-- #1443 Revisar el destinatari dels avisos
-- Treure obligatorietat del destinatari de les alertes 
ALTER TABLE HEL_ALERTA MODIFY (DESTINATARI NULL);

-- #1445 Integrar Helium amb Sistra2 a través de les anotacions de Distribucio
-- Afegir propietats al tipus d'expedient 
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD DISTR_PROCES_AUTO NUMBER(1) DEFAULT 0;
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD DISTR_SISTRA NUMBER(1) DEFAULT 0;
