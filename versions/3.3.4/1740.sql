-- #1740 Crides asíncrones a PINBAL

-- Oracle

-- -- Nova taula per les peticions a pinbal (asíncrones i síncrones)
CREATE TABLE HEL_PETICIO_PINBAL (
  ID           				NUMBER(19)          NOT NULL,
  ENTORN_ID    				NUMBER(19)          NOT NULL,
  TIPUS_ID     				NUMBER(19)          NOT NULL,
  EXPEDIENT_ID 				NUMBER(19)          NOT NULL,
  DOCUMENT_ID  				NUMBER(19),
  PROCEDIMENT  				VARCHAR2(32 CHAR)   NOT NULL,
  USUARI	  				VARCHAR2(64 CHAR)   NOT NULL,
  DATA_PETICIO				TIMESTAMP(6)		NOT NULL,
  ASINCRONA					NUMBER(1,0)			DEFAULT 0 NOT NULL,
  ESTAT		  				VARCHAR2(32 CHAR)   NOT NULL,
  ERROR_MSG					CLOB,
  PINBAL_ID					VARCHAR2(64 CHAR),
  TOKEN_ID					NUMBER(19),
  
  DATA_PREVISTA				TIMESTAMP(6),
  DATA_DARRERA_CONSULTA		TIMESTAMP(6),
  
  TRANSICIO_OK 				VARCHAR2(32 CHAR),
  TRANSICIO_KO 				VARCHAR2(32 CHAR),
  DATA_PROCESSAMENT_PRIMER	TIMESTAMP(6),
  DATA_PROCESSAMENT_DARRER	TIMESTAMP(6),  
  ERROR_PROCESSAMENT		CLOB
);

ALTER TABLE HEL_PETICIO_PINBAL ADD (CONSTRAINT HEL_PETICIO_PINBAL_PK PRIMARY KEY (ID));

ALTER TABLE HEL_PETICIO_PINBAL ADD (
  CONSTRAINT HEL_PETICIO_EXPEDIENT_FK FOREIGN KEY (EXPEDIENT_ID) 
    REFERENCES HEL_EXPEDIENT (ID));
    
    
-- Postgresql