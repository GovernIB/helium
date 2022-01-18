CREATE TABLE HEL_DOMINI
(
    ID                  NUMBER(19)                NOT NULL,
    CACHE_SEGONS        NUMBER(10),
    TIMEOUT             NUMBER(10),
    CODI                VARCHAR2(64 CHAR)         NOT NULL,
    CONTRASENYA         VARCHAR2(255 CHAR),
    DESCRIPCIO          VARCHAR2(255 CHAR),
    JNDI_DATASOURCE     VARCHAR2(255 CHAR),
    NOM                 VARCHAR2(255 CHAR)        NOT NULL,
    ORDRE_PARAMS        VARCHAR2(255 CHAR),
    ORIGEN_CREDS        NUMBER(10),
    SQLEXPR             VARCHAR2(1024 CHAR),
    TIPUS               NUMBER(10)                NOT NULL,
    TIPUS_AUTH          NUMBER(10),
    URL                 VARCHAR2(255 CHAR),
    USUARI              VARCHAR2(255 CHAR),
    ENTORN_ID           NUMBER(19)                NOT NULL,
    EXPEDIENT_TIPUS_ID  NUMBER(19)
);

CREATE INDEX HEL_DOMINI_EXPTIP_I ON HEL_DOMINI (EXPEDIENT_TIPUS_ID);
CREATE INDEX HEL_DOMINI_ENTORN_I ON HEL_DOMINI (ENTORN_ID);

ALTER TABLE HEL_DOMINI ADD (
    PRIMARY KEY (ID),
    UNIQUE (CODI, ENTORN_ID, EXPEDIENT_TIPUS_ID));

CREATE SEQUENCE HEL_DOMINI_SEQ START WITH 10000000;

CREATE PUBLIC SYNONYM HEL_DOMINI FOR HEL_DOMINI;
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_DOMINI TO WWW_HELIUM;
GRANT SELECT ON HEL_DOMINI_SEQ TO WWW_HELIUM;

