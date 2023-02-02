-- ORACLE

--#1620 Flag per evitar la generació des de plantilla per certs documents definits
ALTER TABLE HEL_DOCUMENT ADD GENERAR_NOMES_TASCA NUMBER(1) DEFAULT 0;