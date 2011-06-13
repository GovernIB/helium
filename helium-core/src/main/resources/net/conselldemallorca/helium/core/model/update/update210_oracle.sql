-- Canvi a MAVEN --
UPDATE HEL_ACL_CLASS SET CLASS = 'net.conselldemallorca.helium.core.model.hibernate.Entorn' WHERE CLASS = 'net.conselldemallorca.helium.model.hibernate.Entorn';
UPDATE HEL_ACL_CLASS SET CLASS = 'net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus' WHERE CLASS = 'net.conselldemallorca.helium.model.hibernate.ExpedientTipus';

-- Mapejos de SISTRA --
CREATE 
	TABLE HEL_MAP_SISTRA
	(
        ID NUMBER(19) NOT NULL,
        CODIHELIUM VARCHAR2(255 CHAR) NOT NULL,
        CODISISTRA VARCHAR2(255 CHAR) NOT NULL,
        TIPUS NUMBER(10) NOT NULL,
        EXPEDIENT_TIPUS_ID NUMBER(19) NOT NULL,
        PRIMARY KEY (ID),
        CONSTRAINT HEL_EXPTIPUS_MAP_SISTRA_FK FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES
        HEL_EXPEDIENT_TIPUS (ID),
        CONSTRAINT SYS_C0035059 UNIQUE (CODIHELIUM, EXPEDIENT_TIPUS_ID)
    );

-- Gestió de versions (versió inicial)--
ALTER TABLE HEL_VERSIO ADD DATA_CREACIO TIMESTAMP(6);
ALTER TABLE HEL_VERSIO ADD PROCES_EXECUTAT NUMBER(1);
ALTER TABLE HEL_VERSIO ADD DATA_EXECUCIO_PROCES TIMESTAMP(6);
ALTER TABLE HEL_VERSIO ADD SCRIPT_EXECUTAT NUMBER(1);
ALTER TABLE HEL_VERSIO ADD DATA_EXECUCIO_SCRIPT TIMESTAMP(6);
UPDATE HEL_VERSIO SET DATA_CREACIO = SYSDATE, PROCES_EXECUTAT = 1, DATA_EXECUCIO_PROCES = SYSDATE, SCRIPT_EXECUTAT = 1, DATA_EXECUCIO_SCRIPT = SYSDATE WHERE CODI = 'inicial';

-- Annexió automática de documents generats amb plantilla --
ALTER TABLE HEL_DOCUMENT
 ADD (ADJUNTAR_AUTO        NUMBER(1));
UPDATE HEL_DOCUMENT SET ADJUNTAR_AUTO = 1;

-- Manteniment valors enumeracions --
CREATE TABLE HEL_ENUMERACIO_VALORS(
        ID NUMBER(19) NOT NULL,
        CODI VARCHAR2(64 CHAR) NOT NULL,
        NOM VARCHAR2(255 CHAR) NOT NULL,
        ENUMERACIO_ID NUMBER(19) NOT NULL,
        PRIMARY KEY (ID),
        CONSTRAINT HEL_ENUMERACIO_VALORS_FK FOREIGN KEY (ENUMERACIO_ID) REFERENCES
        HEL_ENUMERACIO (ID)
    );

-- Actualització a la nova versió --
INSERT INTO HEL_VERSIO (
    ID,
    CODI,
    ORDRE,
    DATA_CREACIO,
    PROCES_EXECUTAT,
    SCRIPT_EXECUTAT,
    DATA_EXECUCIO_SCRIPT)
SELECT
    HIBERNATE_SEQUENCE.NEXTVAL ID,
    '2.1.0' CODI,
    210 ORDRE,
    SYSDATE DATA_CREACIO,
    0 PROCES_EXECUTAT,
    1 SCRIPT_EXECUTAT,
    SYSDATE DATA_EXECUCIO_SCRIPT
FROM DUAL
WHERE (SELECT COUNT(*) FROM HEL_VERSIO WHERE ORDRE = 210) = 0;

-- Camps per definir si s'ha de generar una alerta quan se completa una tasca amb un termini associat --
ALTER TABLE HEL_TERMINI
 ADD (ALERTA_COMPLETAT        NUMBER(1));
UPDATE HEL_TERMINI SET ALERTA_COMPLETAT = 0;

ALTER TABLE HEL_TERMINI_INICIAT
 ADD (DATA_COMPLETAT        DATE);
ALTER TABLE HEL_TERMINI_INICIAT
 ADD (ALERTA_COMPLETAT        NUMBER(1));
UPDATE HEL_TERMINI_INICIAT SET ALERTA_COMPLETAT = 0;

-- Prioritat i causa de les alertes --
ALTER TABLE HEL_ALERTA
 ADD (PRIORITAT        NUMBER(10));
ALTER TABLE HEL_ALERTA
 ADD (CAUSA        VARCHAR2(255 CHAR));
 
-- Enumeracions a nivell de tipus d'expedient --
ALTER TABLE HEL_ENUMERACIO
 ADD (EXPEDIENT_TIPUS_ID NUMBER(19));
ALTER TABLE HEL_ENUMERACIO
 ADD HEL_EXPTIP_ENUMERACIO_FK FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES
        HEL_EXPEDIENT_TIPUS (ID));

 
 