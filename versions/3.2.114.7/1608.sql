-- #1608 Els documents mapejats de SISTRA1 apareixen marcats com a invàlids 

-- Tornar a posar com a vàlids els documents provinents de Sistra1

-- ORACLE

UPDATE HEL_DOCUMENT_STORE
SET DOCUMENT_VALID = NULL 
WHERE ID IN (
	SELECT ds.ID 
	FROM HEL_DOCUMENT_STORE ds
		INNER JOIN HEL_EXPEDIENT e ON ds.PROCESS_INSTANCE_ID = e.PROCESS_INSTANCE_ID 
	WHERE 
	ds.DOCUMENT_VALID = 0
		AND ds.DOCUMENT_ERROR IS NULL
		AND e.INICIADOR_TIPUS = 1
);

-- POSTGRESQL

UPDATE HEL_DOCUMENT_STORE
SET DOCUMENT_VALID = NULL 
WHERE ID IN (
	SELECT ds.ID 
	FROM HEL_DOCUMENT_STORE ds
		INNER JOIN HEL_EXPEDIENT e ON ds.PROCESS_INSTANCE_ID = e.PROCESS_INSTANCE_ID 
	WHERE 
	ds.DOCUMENT_VALID = false
		AND ds.DOCUMENT_ERROR IS NULL
		AND e.INICIADOR_TIPUS = 1
);