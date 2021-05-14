-------------------------------------------------------------------
-- #1043 Herència de tipus d’expedient
-------------------------------------------------------------------

-- Afegeix la propietat d'heretable i la referència al TE pare a la taula de tipus d'expedients
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD (HERETABLE NUMBER (1) DEFAULT 0);
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD (EXPEDIENT_TIPUS_PARE_ID NUMBER (19));

ALTER TABLE HEL_EXPEDIENT_TIPUS ADD (
  CONSTRAINT HEL_EXPTIPUS_PARE_EXPTIPUS_FK FOREIGN KEY (EXPEDIENT_TIPUS_PARE_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID)
);

CREATE INDEX HEL_EXTIP_PARE_I on HEL_EXPEDIENT_TIPUS
(EXPEDIENT_TIPUS_PARE_ID);


-------------------------------------------------------------------
-- #1080 Escriptura de les tasques heretades. Poder afegir variables
-------------------------------------------------------------------

-- Modifica la taula camp tasca per tenir una referència al tipus d'expedient en el cas que hi hagi herència
ALTER TABLE HEL_CAMP_TASCA ADD (EXPEDIENT_TIPUS_ID NUMBER (19));

ALTER TABLE HEL_CAMP_TASCA ADD (
  CONSTRAINT HEL_EXPTIPUS_CAMPTASCA_FK FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID)
);

CREATE INDEX HEL_CAMP_TASCA_EXTIP_I on HEL_CAMP_TASCA
(EXPEDIENT_TIPUS_ID);

-- Modifica la taula camp document per tenir una referència al tipus d'expedient en el cas que hi hagi herència
ALTER TABLE HEL_DOCUMENT_TASCA ADD (EXPEDIENT_TIPUS_ID NUMBER (19));

ALTER TABLE HEL_DOCUMENT_TASCA ADD (
  CONSTRAINT HEL_EXPTIPUS_DOCTASCA_FK FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID)
);

CREATE INDEX HEL_DOCTASCA_EXTIP_I on HEL_DOCUMENT_TASCA
(EXPEDIENT_TIPUS_ID);

-- Modifica la taula firma tasca per tenir una referència al tipus d'expedient en el cas que hi hagi herència
ALTER TABLE HEL_FIRMA_TASCA ADD (EXPEDIENT_TIPUS_ID NUMBER (19));

ALTER TABLE HEL_FIRMA_TASCA ADD (
  CONSTRAINT HEL_EXPTIPUS_FIRTASCA_FK FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID)
);

CREATE INDEX HEL_FIRTASCA_EXTIP_I on HEL_FIRMA_TASCA
(EXPEDIENT_TIPUS_ID);


-- Esborra les restriccions úniques
--	HEL_CAMP_TASCA(TASCA_ID, CAMP_ID), HEL_CAMP_TASCA(TASCA_ID, ORDRE)
--	HEL_DOCUMENT_TASCA(TASCA_ID, DOCUMENT_ID), HEL_DOCUMENT_TASCA(TASCA_ID, ORDRE)
--	HEL_FIRMA_TASCA(TASCA_ID, DOCUMENT_ID), HEL_FIRMA_TASCA(TASCA_ID, ORDRE)
DECLARE
 PROCEDURE Borra_restr(p_taula VARCHAR2, p_columna1 VARCHAR2, p_columna2 VARCHAR2) IS
  src_cur pls_integer;
  src_rows  pls_integer;
  v_sql VARCHAR2(1000);
  CURSOR cur_rest IS
           
	SELECT UQ.CONSTRAINT_NAME nom
	FROM USER_CONSTRAINTS UQ
	    INNER JOIN USER_CONS_COLUMNS C1 ON C1.CONSTRAINT_NAME = UQ.CONSTRAINT_NAME
	    INNER JOIN USER_CONS_COLUMNS C2 ON C2.CONSTRAINT_NAME = UQ.CONSTRAINT_NAME
	WHERE
	    UQ.TABLE_NAME = UPPER(p_taula)    
	    AND C1.column_name LIKE UPPER(p_columna1)
	    AND C2.column_name LIKE UPPER(p_columna2);
    
 BEGIN
  FOR i IN cur_rest LOOP
      src_cur := DBMS_SQL.OPEN_CURSOR;
       v_sql := 'ALTER TABLE ' || p_taula || ' DROP CONSTRAINT ' || i.nom ;
       DBMS_SQL.PARSE(src_cur, v_sql, 2 );     -- Desactivar la restricción
       src_rows := DBMS_SQL.EXECUTE(src_cur);
       DBMS_SQL.CLOSE_CURSOR(src_cur); -- Cerrar el cursor
  END LOOP;
 END;
BEGIN
 Borra_restr('HEL_CAMP_TASCA', 'TASCA_ID', 'CAMP_ID');
 Borra_restr('HEL_CAMP_TASCA', 'TASCA_ID', 'ORDRE');
 Borra_restr('HEL_DOCUMENT_TASCA', 'TASCA_ID', 'DOCUMENT_ID');
 Borra_restr('HEL_DOCUMENT_TASCA', 'TASCA_ID', 'ORDRE');
 Borra_restr('HEL_FIRMA_TASCA', 'TASCA_ID', 'DOCUMENT_ID');
 Borra_restr('hel_firma_tasca', 'TASCA_ID', 'ORDRE');
END;


