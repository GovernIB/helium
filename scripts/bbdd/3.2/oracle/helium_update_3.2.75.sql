-------------------------------------------------------------------
-- #1154 Rectificar la versió NTI que apareix a l'expedient i al document
-- És necessari més espai a la columna nti_versio per guardar la URI
-------------------------------------------------------------------

ALTER TABLE HEL_EXPEDIENT MODIFY (NTI_VERSIO VARCHAR2(256 CHAR));
ALTER TABLE HEL_DOCUMENT_STORE MODIFY (NTI_VERSIO VARCHAR2(256 CHAR));