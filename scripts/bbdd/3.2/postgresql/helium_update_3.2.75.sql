-------------------------------------------------------------------
-- #1154 Rectificar la versió NTI que apareix a l'expedient i al document
-- És necessari més espai a la columna nti_versio per guardar la URI
-------------------------------------------------------------------
ALTER TABLE HEL_EXPEDIENT ALTER COLUMN NTI_VERSIO TYPE varchar(256);
ALTER TABLE HEL_DOCUMENT_STORE ALTER COLUMN NTI_VERSIO TYPE varchar(256);