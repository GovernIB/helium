-- #1798 Resoldre el nom de la via pels interessats de tipus administració

-- Oracle
ALTER TABLE HEL_UNITAT_ORGANITZATIVA MODIFY (ADRESSA VARCHAR2(255 CHAR));

-- Posar dates de sincronització a null per tornar a recuperar l'adreça de totes les unitats organitzatives
UPDATE HEL_PARAMETRE SET VALOR = NULL WHERE CODI LIKE 'app.net.caib.helium.unitats.organitzatives.data.sincronitzacio';
UPDATE HEL_PARAMETRE SET VALOR = NULL WHERE CODI LIKE 'app.net.caib.helium.unitats.organitzatives.data.actualitzacio';



    
-- Postgresql
ALTER TABLE HEL_UNITAT_ORGANITZATIVA MODIFY (ADRESSA VARCHAR(255));

-- Posar dates de sincronització a null per tornar a recuperar l'adreça de totes les unitats organitzatives
UPDATE HEL_PARAMETRE SET VALOR = NULL WHERE CODI LIKE 'app.net.caib.helium.unitats.organitzatives.data.sincronitzacio';
UPDATE HEL_PARAMETRE SET VALOR = NULL WHERE CODI LIKE 'app.net.caib.helium.unitats.organitzatives.data.actualitzacio';