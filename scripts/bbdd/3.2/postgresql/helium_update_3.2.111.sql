-- #1432 Poder diferenciar l'enton de PRE i PRO segons el color de la capçalera

ALTER TABLE HEL_ENTORN ADD COLOR_FONS character varying(255);
ALTER TABLE HEL_ENTORN ADD COLOR_LLETRA character varying(255);

-- #1443 Revisar el destinatari dels avisos
-- Treure obligatorietat del destinatari de les alertes 
ALTER TABLE HEL_ALERTA ALTER COLUMN  DESTINATARI DROP NOT NULL;

-- #1445 Integrar Helium amb Sistra2 a través de les anotacions de Distribucio
-- Afegir propietats al tipus d'expedient 
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD DISTR_PROCES_AUTO BOOLEAN DEFAULT FALSE;
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NUMBER BOOLEAN DEFAULT FALSE;