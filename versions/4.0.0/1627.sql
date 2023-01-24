-- #1627 HELIUM sense motor de WF 

-- Nou camp pel tipus d'expedient ESTATS/FLUX
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD TIPUS NUMBER(10) DEFAULT 0 NOT NULL;

-- Nou camp pel tipus d'acció handler/predefinit/script
ALTER TABLE HEL_ACCIO ADD TIPUS VARCHAR2(32 CHAR) DEFAULT 'HANDLER' NOT NULL;
ALTER TABLE HEL_ACCIO ADD SCRIPT VARCHAR2(1024 CHAR);
ALTER TABLE HEL_ACCIO MODIFY (JBPM_ACTION NULL);

-- Nous camps per accions de handlers predefinits
ALTER TABLE HEL_ACCIO ADD PREDEFINIT_CLASSE VARCHAR2(255 CHAR);
ALTER TABLE HEL_ACCIO ADD PREDEFINIT_DADES CLOB;


-- -- Nova taula per les regles dels estats
CREATE TABLE HEL_ESTAT_REGLA (
    ID				    NUMBER(19) 	        NOT NULL,
    NOM 				VARCHAR2(255 CHAR) 	NOT NULL,
    EXPEDIENT_TIPUS_ID  NUMBER(19) 	        NOT NULL,
    ESTAT_ID			NUMBER(19) 	        NOT NULL,
    ENTORN_ID			NUMBER(19) 	        NOT NULL,
    QUI 				VARCHAR2(32 CHAR) 	NOT NULL,
    QUE 				VARCHAR2(32 CHAR) 	NOT NULL,
    ACCIO 				VARCHAR2(32 CHAR) 	NOT NULL,
    ORDRE               NUMBER(10)          NOT NULL
);

CREATE TABLE HEL_ESTAT_REGLA_VALOR_QUI (
    REGLA_ID            NUMBER(19) 	        NOT NULL,
    VALOR 				VARCHAR2(255 CHAR) 	NOT NULL
);

CREATE TABLE HEL_ESTAT_REGLA_VALOR_QUE (
    REGLA_ID            NUMBER(19) 	        NOT NULL,
    VALOR 				VARCHAR2(255 CHAR) 	NOT NULL
);

ALTER TABLE HEL_ESTAT_REGLA ADD (CONSTRAINT HEL_ESTAT_REGLA_PK PRIMARY KEY (ID));
ALTER TABLE HEL_ESTAT_REGLA_VALOR_QUI ADD (CONSTRAINT HEL_ESTAT_REGLA_VALOR_QUI_PK PRIMARY KEY (REGLA_ID, VALOR));
ALTER TABLE HEL_ESTAT_REGLA_VALOR_QUE ADD (CONSTRAINT HEL_ESTAT_REGLA_VALOR_QUE_PK PRIMARY KEY (REGLA_ID, VALOR));

ALTER TABLE HEL_ESTAT_REGLA ADD (CONSTRAINT HEL_REGLA_EXPTIPUS_FK FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS (ID));
ALTER TABLE HEL_ESTAT_REGLA ADD (CONSTRAINT HEL_REGLA_ESTAT_FK FOREIGN KEY (ESTAT_ID) REFERENCES HEL_ESTAT (ID));
ALTER TABLE HEL_ESTAT_REGLA ADD (CONSTRAINT HEL_REGLA_ENTORN_FK FOREIGN KEY (ENTORN_ID) REFERENCES HEL_ENTORN (ID));
ALTER TABLE HEL_ESTAT_REGLA ADD (CONSTRAINT HEL_REGLA_NOM_UK UNIQUE (EXPEDIENT_TIPUS_ID, NOM));
ALTER TABLE HEL_ESTAT_REGLA_VALOR_QUI ADD (CONSTRAINT HEL_REGLA_VALOR_QUI_FK FOREIGN KEY (REGLA_ID) REFERENCES HEL_ESTAT_REGLA (ID));
ALTER TABLE HEL_ESTAT_REGLA_VALOR_QUE ADD (CONSTRAINT HEL_REGLA_VALOR_QUE_FK FOREIGN KEY (REGLA_ID) REFERENCES HEL_ESTAT_REGLA (ID));

CREATE INDEX HEL_REGLA_ENTORN_FK_I ON HEL_ESTAT_REGLA(ENTORN_ID);
CREATE INDEX HEL_REGLA_EXPTIPUS_FK_I ON HEL_ESTAT_REGLA(EXPEDIENT_TIPUS_ID);
CREATE INDEX HEL_REGLA_ESTAT_FK_I ON HEL_ESTAT_REGLA(ESTAT_ID);
CREATE INDEX HEL_REGLA_VALOR_QUI_FK_I ON HEL_ESTAT_REGLA_VALOR_QUI(REGLA_ID);
CREATE INDEX HEL_REGLA_VALOR_QUE_FK_I ON HEL_ESTAT_REGLA_VALOR_QUE(REGLA_ID);

GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ESTAT_REGLA TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ESTAT_REGLA_VALOR_QUI TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ESTAT_REGLA_VALOR_QUE TO WWW_HELIUM;


-- Noves taules per la relació entre estats i accions d'entrada i sortida

CREATE TABLE HEL_ESTAT_ACCIO_ENTRADA (
    ID				    NUMBER(19) 	        NOT NULL,
    ESTAT_ID			NUMBER(19) 	        NOT NULL,
    ACCIO_ID			NUMBER(19) 	        NOT NULL,
    ORDRE               NUMBER(10)          NOT NULL
);

CREATE TABLE HEL_ESTAT_ACCIO_SORTIDA (
    ID				    NUMBER(19) 	        NOT NULL,
    ESTAT_ID			NUMBER(19) 	        NOT NULL,
    ACCIO_ID			NUMBER(19) 	        NOT NULL,
    ORDRE               NUMBER(10)          NOT NULL
);

ALTER TABLE HEL_ESTAT_ACCIO_ENTRADA ADD (CONSTRAINT HEL_ESTACC_ENT_PK PRIMARY KEY (ID));
ALTER TABLE HEL_ESTAT_ACCIO_SORTIDA ADD (CONSTRAINT HEL_ESTACC_SORT_PK PRIMARY KEY (ID));

ALTER TABLE HEL_ESTAT_ACCIO_ENTRADA ADD (CONSTRAINT HEL_ESTACC_ENT_ESTAT_FK FOREIGN KEY (ESTAT_ID) REFERENCES HEL_ESTAT (ID));
ALTER TABLE HEL_ESTAT_ACCIO_ENTRADA ADD (CONSTRAINT HEL_ESTACC_ENT_ACCIO_FK FOREIGN KEY (ACCIO_ID) REFERENCES HEL_ACCIO (ID));
ALTER TABLE HEL_ESTAT_ACCIO_SORTIDA ADD (CONSTRAINT HEL_ESTACC_SORT_ESTAT_FK FOREIGN KEY (ESTAT_ID) REFERENCES HEL_ESTAT (ID));
ALTER TABLE HEL_ESTAT_ACCIO_SORTIDA ADD (CONSTRAINT HEL_ESTACC_SORT_ACCIO_FK FOREIGN KEY (ACCIO_ID) REFERENCES HEL_ACCIO (ID));

CREATE INDEX HEL_ESTACC_ENT_ESTAT_FK_I ON HEL_ESTAT_ACCIO_ENTRADA(ESTAT_ID);
CREATE INDEX HEL_ESTACC_SORT_ESTAT_FK_I ON HEL_ESTAT_ACCIO_SORTIDA(ESTAT_ID);

GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ESTAT_ACCIO_ENTRADA TO WWW_HELIUM;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_ESTAT_ACCIO_SORTIDA TO WWW_HELIUM;
