-- #1445 Integrar Helium amb Sistra2 a través de les anotacions de Distribucio

-- Afegir propietats al tipus d'expedient 

--Oracle
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD DISTR_PROCES_AUTO NUMBER(1) DEFAULT 0;
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD DISTR_SISTRA NUMBER(1) DEFAULT 0;

--Postgres
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD DISTR_PROCES_AUTO BOOLEAN DEFAULT FALSE;
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD NUMBER BOOLEAN DEFAULT FALSE;
