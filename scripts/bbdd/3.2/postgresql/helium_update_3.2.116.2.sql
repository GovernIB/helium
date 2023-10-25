-- #1700 Canciar a la nova API REST de regles de Distribucio

-- afegim la columna presencial
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD DISTR_PRESENCIAL BOOLEAN;

--#1658 Poder afegir un manual d'ajuda per tipus d'expedient
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD MANUAL_AJUDA_CONTENT BYTEA;
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD MANUAL_AJUDA_NOM character varying(1024);
