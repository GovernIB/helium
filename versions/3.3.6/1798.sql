-- #1798 Resoldre el nom de la via pels interessats de tipus administració

-- Oracle
ALTER TABLE HEL_UNITAT_ORGANITZATIVA MODIFY (ADRESSA VARCHAR2(255 CHAR));


    
-- Postgresql
ALTER TABLE HEL_UNITAT_ORGANITZATIVA MODIFY (ADRESSA VARCHAR(255));