--#1577  Incorporar en el grup de handlers predefinits en la gestió d'expedients la integració amb PINBAL
--Corregir la tasca de pinbal i afegir la configuració de la integració

--Oracle
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD PINBAL_ACTIU NUMBER(1) DEFAULT 0;
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD PINBAL_NIF_CIF VARCHAR2(44 CHAR);

--Postgres
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD PINBAL_ACTIU BOOLEAN DEFAULT false;
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD PINBAL_NIF_CIF VARCHAR (44);