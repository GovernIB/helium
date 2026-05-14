-- #2040 CAI- 2563221: Permetre visualitzar les dades de registres múltiples en les consultes personalitzades i en l'exportació de dades 

-- Nou camp indicar si el camp s'ha de reindexar
ALTER TABLE HEL_CAMP ADD INDEXABLE NUMBER(1) DEFAULT 0 NOT NULL;