-- #1723 Afegir un avís quan s'importi un document amb un flux associat que no es troba al tipus d'expedient

-- Oracle

-- Nova columa per guardar el nom del flux
ALTER TABLE HEL_DOCUMENT ADD (PORTAFIRMES_FLUX_NOM VARCHAR2(512 CHAR));

-- Postgesql
	
-- Nova columa per guardar el nom del flux	
ALTER TABLE HEL_DOCUMENT ADD PORTAFIRMES_FLUX_NOM VARCHAR(512);	
