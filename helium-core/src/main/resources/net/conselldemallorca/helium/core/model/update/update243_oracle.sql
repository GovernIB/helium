--nou camp check per ocultar expedients a les consultes per tipus
ALTER TABLE HEL_CONSULTA ADD (OCULTAR_ACTIU NUMBER(1) DEFAULT 0 NOT NULL);