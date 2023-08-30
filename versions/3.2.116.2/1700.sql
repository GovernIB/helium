-- #1700 Canciar a la nova API REST de regles de Distribucio

-- Oracle

-- afegim la columna presencial
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD DISTR_PRESENCIAL NUMBER(1);

-- Postgresql
-- Per Postgres no hi ha problema de tipus
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD DISTR_PRESENCIAL BOOLEAN;

