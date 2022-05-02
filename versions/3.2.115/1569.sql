--#1569 Quan es deshabilita la integració amb tràmits SISTRA es perd el codi del tràmit

--Oracle
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD SISTRA_ACTIU NUMBER(1) DEFAULT 0;

--Postgres
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD SISTRA_ACTIU BOOLEAN DEFAULT false;