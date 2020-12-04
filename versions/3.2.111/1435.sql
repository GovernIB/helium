-- #1435 Mostrar una roda mentre s'està processant una anotació de registre

--Oracle
ALTER TABLE HEL_ENTORN ADD COLOR_FONS VARCHAR2(255 CHAR);
ALTER TABLE HEL_ENTORN ADD COLOR_LLETRA VARCHAR2(255 CHAR);

--Postgres
ALTER TABLE HEL_ENTORN ADD COLOR_FONS character varying(255);
ALTER TABLE HEL_ENTORN ADD COLOR_LLETRA character varying(255);

