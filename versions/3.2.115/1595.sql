-- #1595 Tractar els avisos de noves anotacions de Distribucio en una cua 
-- Modificar camps de la taula d'anotacions per a que puguin estar buits quan arribi una anotació

ALTER TABLE HEL_ANOTACIO MODIFY IDIOMA_CODI NULL;
ALTER TABLE HEL_ANOTACIO MODIFY LLIBRE_CODI NULL;
ALTER TABLE HEL_ANOTACIO MODIFY OFICINA_CODI NULL;
ALTER TABLE HEL_ANOTACIO MODIFY DESTI_CODI NULL;
ALTER TABLE HEL_ANOTACIO MODIFY "DATA" NULL;
ALTER TABLE HEL_ANOTACIO MODIFY ENTITAT_CODI NULL;
ALTER TABLE HEL_ANOTACIO MODIFY IDENTIFICADOR NULL;

ALTER TABLE HEL_ANOTACIO ADD CONSULTA_INTENTS NUMBER(10) DEFAULT 0;
ALTER TABLE HEL_ANOTACIO ADD CONSULTA_ERROR VARCHAR2(1024 CHAR);
ALTER TABLE HEL_ANOTACIO ADD CONSULTA_DATA TIMESTAMP(6);
ALTER TABLE HEL_ANOTACIO ADD ERROR_PROCESSAMENT VARCHAR2(1024 CHAR);

