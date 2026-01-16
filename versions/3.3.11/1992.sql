-- #1992 
-- Afegeix la nova columna per diferenciar el tipus segons si és procediment o servei
ALTER TABLE HEL_PROCEDIMENT ADD TIPUS VARCHAR2(20 CHAR) DEFAULT 'PROCEDIMENT' NOT NULL;