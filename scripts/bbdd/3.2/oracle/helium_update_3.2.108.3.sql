-- v3.2.108.3 Correcció d'errors urgents i funció per modificar enumera
-- Data: 12/06/2020

-------------------------------------------------------------------
-- #1380 Modificar integració NOTIB per a que el codi DIR3 i el
-- codi SIA puguin ser diferents de les dades NTI
-- Afegeix 2 noves columnes per guardar el codi DIR3 de l'emisor i el 
-- codi SIA del procediment 
-------------------------------------------------------------------

-- Afegeix els camps del codi dir3 de l'emisor i el codi del procediment pel notib
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_EMISOR VARCHAR2(256);
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NOTIB_CODI_PROCEDIMENT VARCHAR2(9);

-- Actualtiza els valors actuals
UPDATE HEL_EXPEDIENT_TIPUS 
SET NOTIB_EMISOR = NTI_ORGANO, 
	NOTIB_CODI_PROCEDIMENT = NTI_CLASIFICACION
WHERE NOTIB_ACTIU = 1;

