-- #1990 
-- Afegeix la nova columna per poder marcar una variable tipus termini que només mostri els dies
ALTER TABLE HEL_CAMP ADD TERMINI_NOMES_DIES NUMBER(1,0) DEFAULT 0 NOT NULL;
