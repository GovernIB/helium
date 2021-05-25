-- #1488 Helium MS. Fase1 Integrar Helium amb microservei de dominis 

-- Treure les claus foranes des de la taula HEL_CAMP a la taula HEL_DOMINI
-- Més endavant es pot eliminar la taula HEL_DOMINI

--Oracle
ALTER TABLE HEL_CAMP DROP CONSTRAINT HEL_DOMINI_CAMP_FK;

--Postgres
ALTER TABLE HEL_CAMP DROP CONSTRAINT HEL_DOMINI_CAMP_FK;

