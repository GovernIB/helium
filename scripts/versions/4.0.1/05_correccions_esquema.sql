-- #2018 Correccions de l'esquema 4.0 detectades provant el motor Flowable.

-- La instància de procés dels logs de l'expedient és un id de Flowable (text), no un número.
-- Amb NUMBER(19), iniciar un expedient de flux falla amb ORA-01722 en desar el log d'inici.
-- Oracle no permet canviar el tipus d'una columna amb dades: es crea una columna nova i es copien els valors
-- (els ids de jBPM, numèrics, es conserven com a text).
ALTER TABLE HEL_EXPEDIENT_LOG ADD (PROCESS_INSTANCE_ID_NOU VARCHAR2(64 CHAR));
UPDATE HEL_EXPEDIENT_LOG SET PROCESS_INSTANCE_ID_NOU = TO_CHAR(PROCESS_INSTANCE_ID);
ALTER TABLE HEL_EXPEDIENT_LOG DROP COLUMN PROCESS_INSTANCE_ID;
ALTER TABLE HEL_EXPEDIENT_LOG RENAME COLUMN PROCESS_INSTANCE_ID_NOU TO PROCESS_INSTANCE_ID;

-- Camp.terminiNomesDies (variables de tipus termini que només demanen els dies). L'entitat el fa servir, però cap
-- script de la 4.0 no creava la columna: qualsevol consulta dels camps d'una tasca fallava amb ORA-00904.
ALTER TABLE HEL_CAMP ADD (TERMINI_NOMES_DIES NUMBER(1) DEFAULT 0 NOT NULL);

-- Grants de taules que ja creaven els scripts de la 4.0 però que no en tenien a helium_08_grant.sql:
-- les del registre d'events de Flowable i HEL_USUARI_PERMIS.
GRANT SELECT, UPDATE, INSERT, DELETE ON FLW_CHANNEL_DEFINITION TO WWW_HELIUM2;
GRANT SELECT, UPDATE, INSERT, DELETE ON FLW_EVENT_DEFINITION TO WWW_HELIUM2;
GRANT SELECT, UPDATE, INSERT, DELETE ON FLW_EVENT_DEPLOYMENT TO WWW_HELIUM2;
GRANT SELECT, UPDATE, INSERT, DELETE ON FLW_EVENT_RESOURCE TO WWW_HELIUM2;
GRANT SELECT, UPDATE, INSERT, DELETE ON FLW_EV_DATABASECHANGELOG TO WWW_HELIUM2;
GRANT SELECT, UPDATE, INSERT, DELETE ON FLW_EV_DATABASECHANGELOGLOCK TO WWW_HELIUM2;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_USUARI_PERMIS TO WWW_HELIUM2;
