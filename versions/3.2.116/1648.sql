--#1664 El check d'activar tràmits no s'acaba d'exportar bé
--Script per posar el check a true a tots els que no s'hagin importat correctament

--Oracle
UPDATE HEL_EXPEDIENT_TIPUS SET SISTRA_ACTIU = 1 WHERE DISTR_SISTRA = 1 AND SISTRA_ACTIU = 0

--Postgres
UPDATE HEL_EXPEDIENT_TIPUS SET SISTRA_ACTIU = TRUE WHERE DISTR_SISTRA = TRUE AND SISTRA_ACTIU = FALSE