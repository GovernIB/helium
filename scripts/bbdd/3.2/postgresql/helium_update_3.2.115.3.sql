--#1620 Flag per evitar la generació des de plantilla per certs documents definits
--Flag per permetre generar el document tipus plantilla des de la tasca, no es podrà generar des de la gestió de documents

--Postgres
ALTER TABLE HEL_DOCUMENT ADD GENERAR_NOMES_TASCA BOOLEAN DEFAULT false;