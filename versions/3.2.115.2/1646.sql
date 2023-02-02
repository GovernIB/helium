--#1646 Error amb els tipus NTI de documents

-- Rectifica el tipus de document pels documetns creats a partir del 09/01/2023

--Oracle

-- Incrementa el tipus a tots els documents creats a partir del dia de la publicació
UPDATE HEL_DOCUMENT_STORE SET NTI_TIPO_DOC = NTI_TIPO_DOC + 1 
WHERE DATA_CREACIO >= TO_DATE('2023/01/09 07:25:00', 'yyyy/mm/dd HH24:MI:SS') 
		AND NTI_TIPO_DOC >= 20;

-- Posa novament el valor de tipus "ALTRES" (20) als nous documents creats a partir de la data amb tipus (39+1)
UPDATE HEL_DOCUMENT_STORE SET NTI_TIPO_DOC = 20 
WHERE DATA_CREACIO >= TO_DATE('2023/01/09 07:25:00', 'yyyy/mm/dd HH24:MI:SS')
		AND NTI_TIPO_DOC = 40;
