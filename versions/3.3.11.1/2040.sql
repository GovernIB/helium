-- Nou camp indicar si el camp s'ha de reindexar --
ALTER TABLE HEL_CAMP ADD INDEXABLE NUMBER(1) DEFAULT 0 NOT NULL;