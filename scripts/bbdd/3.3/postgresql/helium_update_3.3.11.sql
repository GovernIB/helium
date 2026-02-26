-- #1990 
-- Afegeix la nova columna per poder marcar una variable tipus termini que només mostri els dies
ALTER TABLE HEL_CAMP ADD TERMINI_NOMES_DIES BOOLEAN DEFAULT FALSE NOT NULL;

-- #1992 
-- Afegeix la nova columna per diferenciar el tipus segons si és procediment o servei
ALTER TABLE HEL_PROCEDIMENT ADD TIPUS VARCHAR(20) DEFAULT 'PROCEDIMENT' NOT NULL;

ALTER TABLE HEL_ANOTACIO ADD SERVEI_CODI VARCHAR(64);
ALTER TABLE HEL_ANOTACIO ADD PRESENCIAL BOOLEAN;
ALTER TABLE HEL_ANOTACIO ADD TRAMIT_CODI VARCHAR(64);
ALTER TABLE HEL_ANOTACIO ADD TRAMIT_NOM VARCHAR(255);

-- #1975
-- Modifica el camp de la regla per a que no hagi de tenir un estat associat
ALTER TABLE HEL_ESTAT_REGLA MODIFY ESTAT_ID BIGINT NULL;

-- #1979
-- Afegeix una columna per guardar el codi del servei de la petició a PINBAL i poder-la mostrar
ALTER TABLE HEL_PETICIO_PINBAL ADD SERVEI_CODI VARCHAR(32);
