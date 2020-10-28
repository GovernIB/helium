-- #1425 Error. Dades diferents entre l'expedient i les consultes (dades reindexades)
-- Crear una taula per reindexacions pendents

-- Nova taula per les reindexacions asíncrones
CREATE TABLE HEL_EXPEDIENT_REINDEXACIO (
  ID 						NUMBER(19) 	NOT NULL,
  EXPEDIENT_ID				NUMBER(19) 	NOT NULL,
  DATA_REINDEXACIO			TIMESTAMP(6) NOT NULL
);

ALTER TABLE HEL_EXPEDIENT_REINDEXACIO ADD (
  CONSTRAINT HEL_EXP_REIND_PK PRIMARY KEY (ID));
  
GRANT SELECT, UPDATE, INSERT, DELETE ON HEL_EXPEDIENT_REINDEXACIO TO WWW_HELIUM;


-- Insereix en la taula de reindexacions les dades dels expedients amb data de reindexació
INSERT INTO HEL_EXPEDIENT_REINDEXACIO (ID , EXPEDIENT_ID , DATA_REINDEXACIO )
SELECT ID,
		EXPEDIENT_ID,
		DATA_REINDEXACIO 
FROM (
	SELECT
		rownum ID,
		e.ID EXPEDIENT_ID,
		e.REINDEXAR_DATA DATA_REINDEXACIO
	FROM HEL_EXPEDIENT e
	WHERE e.REINDEXAR_DATA IS NOT NULL 
	ORDER BY e.REINDEXAR_DATA ASC
);