CREATE TABLE HEL_ACCIO
(
  ID                   NUMBER(19)               NOT NULL,
  CODI                 VARCHAR2(64 CHAR)        NOT NULL,
  DESCRIPCIO           VARCHAR2(255 CHAR),
  JBPM_ACTION          VARCHAR2(255 CHAR)       NOT NULL,
  NOM                  VARCHAR2(255 CHAR)       NOT NULL,
  DEFINICIO_PROCES_ID  NUMBER(19)               NOT NULL
);

ALTER TABLE HEL_CAMP
 ADD (JBPM_ACTION  VARCHAR2(255 CHAR));

CREATE TABLE HEL_REDIR
(
  ID               NUMBER(19)                   NOT NULL,
  DATA_CANCELACIO  TIMESTAMP(6),
  DATA_FI          TIMESTAMP(6)                 NOT NULL,
  DATA_INICI       TIMESTAMP(6)                 NOT NULL,
  USUARI_DESTI     VARCHAR2(255 CHAR)           NOT NULL,
  USUARI_ORIGEN    VARCHAR2(255 CHAR)           NOT NULL
);

ALTER TABLE HEL_ACCIO ADD (
  PRIMARY KEY
 (ID),
  UNIQUE (CODI, DEFINICIO_PROCES_ID));

ALTER TABLE HEL_REDIR ADD (
  PRIMARY KEY
 (ID));

ALTER TABLE HEL_ACCIO
DROP CONSTRAINT HEL_DEFPROC_DOCUMENT_FK;
ALTER TABLE HEL_ACCIO ADD (
  CONSTRAINT HEL_DEFPROC_ACCIO_FK 
 FOREIGN KEY (DEFINICIO_PROCES_ID) 
 REFERENCES HEL_DEFINICIO_PROCES (ID));

ALTER TABLE JBPM_VARIABLEINSTANCE
 MODIFY(STRINGVALUE_ VARCHAR2(2048 CHAR));

ALTER TABLE HEL_EXPEDIENT_TIPUS
 MODIFY(SISTRA_MAPCAMPS VARCHAR2(2048 CHAR));
ALTER TABLE HEL_EXPEDIENT_TIPUS
 MODIFY(SISTRA_MAPDOCS VARCHAR2(2048 CHAR));

ALTER TABLE HEL_EXPEDIENT_TIPUS
 ADD (FORMEXT_URL  VARCHAR2(255 CHAR));
ALTER TABLE HEL_EXPEDIENT_TIPUS
 ADD (FORMEXT_USUARI  VARCHAR2(255 CHAR));
ALTER TABLE HEL_EXPEDIENT_TIPUS
 ADD (FORMEXT_CONTRASENYA  VARCHAR2(255 CHAR));

CREATE TABLE HEL_ALERTA
(
  ID                NUMBER(19)                  NOT NULL,
  DATA_CREACIO      TIMESTAMP(6)                NOT NULL,
  DATA_ELIMINACIO   TIMESTAMP(6),
  DATA_LECTURA      TIMESTAMP(6),
  DESTINATARI       VARCHAR2(255 CHAR)          NOT NULL,
  TASK_INSTANCE_ID  VARCHAR2(255 CHAR),
  TEXT              VARCHAR2(1024 CHAR),
  ENTORN_ID         NUMBER(19)                  NOT NULL,
  EXPEDIENT_ID      NUMBER(19)                  NOT NULL
);
ALTER TABLE HEL_ALERTA ADD (
  PRIMARY KEY
 (ID));
ALTER TABLE HEL_ALERTA ADD (
  CONSTRAINT HEL_ENTORN_ALERTA_FK 
 FOREIGN KEY (ENTORN_ID) 
 REFERENCES HEL_ENTORN (ID),
  CONSTRAINT HEL_EXPEDIENT_ALERTA_FK 
 FOREIGN KEY (EXPEDIENT_ID) 
 REFERENCES HEL_EXPEDIENT (ID));

ALTER TABLE HEL_CAMP
 ADD (ORDRE                NUMBER(10));

ALTER TABLE HEL_TERMINI
 ADD (DIES_PREVIS_AVIS     NUMBER(10));
ALTER TABLE HEL_TERMINI
 ADD (ALERTA_PREVIA        NUMBER(1));
ALTER TABLE HEL_TERMINI
 ADD (ALERTA_FINAL         NUMBER(1));
UPDATE HEL_TERMINI SET ALERTA_PREVIA=0, ALERTA_FINAL=0;

ALTER TABLE HEL_TERMINI_INICIAT
 ADD (ALERTA_PREVIA        NUMBER(1));
ALTER TABLE HEL_TERMINI_INICIAT
 ADD (ALERTA_FINAL         NUMBER(1));
ALTER TABLE HEL_TERMINI_INICIAT
 ADD (DATA_FI_PRORROGA     DATE);
ALTER TABLE HEL_TERMINI_INICIAT
 ADD (TASK_INSTANCE_ID     VARCHAR2(255 CHAR));
ALTER TABLE HEL_TERMINI_INICIAT
 ADD (TIMER_IDS            VARCHAR2(1024 CHAR));
ALTER TABLE HEL_TERMINI_INICIAT
 DROP COLUMN TIMER_NAME;
UPDATE HEL_TERMINI_INICIAT SET ALERTA_PREVIA=0, ALERTA_FINAL=0;

ALTER TABLE HEL_DOCUMENT_STORE
 ADD (REGISTRE_ANY         VARCHAR2(255 CHAR));
ALTER TABLE HEL_DOCUMENT_STORE
 ADD (REGISTRE_DATA        TIMESTAMP(6));
ALTER TABLE HEL_DOCUMENT_STORE
 ADD (REGISTRE_ENTRADA     NUMBER(1));
ALTER TABLE HEL_DOCUMENT_STORE
 ADD (REGISTRE_NUM         VARCHAR2(255 CHAR));
ALTER TABLE HEL_DOCUMENT_STORE
 ADD (REGISTRE_OFCODI      VARCHAR2(255 CHAR));
ALTER TABLE HEL_DOCUMENT_STORE
 ADD (REGISTRE_OFNOM       VARCHAR2(255 CHAR));
UPDATE HEL_DOCUMENT_STORE SET REGISTRE_ENTRADA=1;

ALTER TABLE HEL_TERMINI
 ADD (DURADA_PREDEF       NUMBER(1));
UPDATE HEL_TERMINI SET DURADA_PREDEF=1;
ALTER TABLE HEL_TERMINI_INICIAT
 ADD (ANYS        NUMBER(10));
ALTER TABLE HEL_TERMINI_INICIAT
 ADD (MESOS        NUMBER(10));
ALTER TABLE HEL_TERMINI_INICIAT
 ADD (DIES        NUMBER(10));
UPDATE HEL_TERMINI_INICIAT SET ANYS=0,MESOS=0,DIES=0;

ALTER TABLE HEL_EXPEDIENT
 ADD (REGISTRE_NUM       VARCHAR2(64 CHAR));
ALTER TABLE HEL_EXPEDIENT
 ADD (REGISTRE_DATA       DATE);
ALTER TABLE HEL_EXPEDIENT
 ADD (GEO_POSX       FLOAT(126));
ALTER TABLE HEL_EXPEDIENT
 ADD (GEO_POSY       FLOAT(126));
ALTER TABLE HEL_EXPEDIENT
 ADD (GEO_REFERENCIA       VARCHAR2(64 CHAR));

ALTER TABLE HEL_EXPEDIENT
 ADD (AVISOS_HABILITAT       NUMBER(1));
ALTER TABLE HEL_EXPEDIENT
 ADD (AVISOS_EMAIL       VARCHAR2(255 CHAR));
ALTER TABLE HEL_EXPEDIENT
 ADD (AVISOS_MOBIL       VARCHAR2(255 CHAR));
ALTER TABLE HEL_EXPEDIENT
 ADD (NOTTEL_HABILITAT       NUMBER(1));
UPDATE HEL_EXPEDIENT SET AVISOS_HABILITAT=0,NOTTEL_HABILITAT=0;

ALTER TABLE HEL_EXPEDIENT_TIPUS
 ADD (SISTRA_MAPADJ       VARCHAR2(2048 CHAR));

ALTER TABLE HEL_ENUMERACIO
 MODIFY VALORS VARCHAR2(4000 CHAR);

ALTER TABLE HEL_EXPEDIENT
 MODIFY REGISTRE_DATA TIMESTAMP;
ALTER TABLE HEL_EXPEDIENT
 ADD (UNITAT_ADM NUMBER(19));
ALTER TABLE HEL_EXPEDIENT
 ADD (IDIOMA VARCHAR2(8));
ALTER TABLE HEL_EXPEDIENT
 ADD (AUTENTICAT NUMBER(1));
ALTER TABLE HEL_EXPEDIENT
 ADD (TRAMITADOR_NIF VARCHAR2(16));
ALTER TABLE HEL_EXPEDIENT
 ADD (TRAMITADOR_NOM VARCHAR2(255));
ALTER TABLE HEL_EXPEDIENT
 ADD (INTERESSAT_NIF VARCHAR2(16));
ALTER TABLE HEL_EXPEDIENT
 ADD (INTERESSAT_NOM VARCHAR2(255));
ALTER TABLE HEL_EXPEDIENT
 ADD (REPRESENTANT_NIF VARCHAR2(16));
ALTER TABLE HEL_EXPEDIENT
 ADD (REPRESENTANT_NOM VARCHAR2(255));
UPDATE HEL_EXPEDIENT SET AUTENTICAT=0;

ALTER TABLE HEL_DOCUMENT_STORE
 DROP COLUMN REGISTRE_ANY;
ALTER TABLE HEL_DOCUMENT_STORE
 ADD (REGISTRE_ORGCODI VARCHAR2(255));
ALTER TABLE HEL_EXPEDIENT
 ADD (TRAMEXP_ID VARCHAR2(255));
ALTER TABLE HEL_EXPEDIENT
 ADD (TRAMEXP_CLAU VARCHAR2(255));

ALTER TABLE HEL_ALERTA
 DROP COLUMN TASK_INSTANCE_ID;
ALTER TABLE HEL_ALERTA
 ADD (TERMINI_INICIAT_ID NUMBER(19));

update hel_acl_class set class = 'net.conselldemallorca.helium.core.model.hibernate.Entorn' where class = 'net.conselldemallorca.helium.model.hibernate.Entorn';
update hel_acl_class set class = 'net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus' where class = 'net.conselldemallorca.helium.model.hibernate.ExpedientTipus';