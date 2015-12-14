-- Nou camp per a ocultar expedients a les consultes per tipus
ALTER TABLE HEL_CONSULTA ADD OCULTAR_ACTIU NUMBER(1) NOT NULL DEFAULT 0;

