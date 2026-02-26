-- #1990 
-- Afegeix la nova columna per poder marcar una variable tipus termini que només mostri els dies
ALTER TABLE HEL_CAMP ADD TERMINI_NOMES_DIES NUMBER(1,0) DEFAULT 0 NOT NULL;

-- #1992 
-- Afegeix la nova columna per diferenciar el tipus segons si és procediment o servei
ALTER TABLE HEL_PROCEDIMENT ADD TIPUS VARCHAR2(20 CHAR) DEFAULT 'PROCEDIMENT' NOT NULL;

ALTER TABLE HEL_ANOTACIO ADD SERVEI_CODI VARCHAR2(64 CHAR);
ALTER TABLE HEL_ANOTACIO ADD PRESENCIAL NUMBER(1,0);
ALTER TABLE HEL_ANOTACIO ADD TRAMIT_CODI VARCHAR2(64 CHAR);
ALTER TABLE HEL_ANOTACIO ADD TRAMIT_NOM VARCHAR2(255 CHAR);

-- #1975
-- Modifica el camp de la regla per a que no hagi de tenir un estat associat
ALTER TABLE HEL_ESTAT_REGLA MODIFY ESTAT_ID NUMBER(19,0) NULL;

-- #1979
-- Afegeix una columna per guardar el codi del servei de la petició a PINBAL i poder-la mostrar
ALTER TABLE HEL_PETICIO_PINBAL ADD SERVEI_CODI VARCHAR2(32 CHAR);
