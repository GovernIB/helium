-------------------------------------------------------------------
-- #1043 Herència de tipus d’expedient
-------------------------------------------------------------------

-- Afegeix la propietat d'heretable i la referència al TE pare a la taula de tipus d'expedients
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD COLUMN HERETABLE BOOLEAN DEFAULT FALSE;
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD COLUMN EXPEDIENT_TIPUS_PARE_ID BIGINT;

ALTER TABLE HEL_EXPEDIENT_TIPUS ADD 
  CONSTRAINT HEL_EXPTIPUS_PARE_EXPTIPUS_FK 
 FOREIGN KEY (EXPEDIENT_TIPUS_PARE_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

CREATE INDEX HEL_EXTIP_PARE_I on HEL_EXPEDIENT_TIPUS
(EXPEDIENT_TIPUS_PARE_ID);

-------------------------------------------------------------------
-- #1080 Escriptura de les tasques heretades. Poder afegir variables
-------------------------------------------------------------------

-- Modifica la taula camp tasca per tenir una referència al tipus d'expedient en el cas que hi hagi herència
ALTER TABLE HEL_CAMP_TASCA ADD  COLUMN EXPEDIENT_TIPUS_ID BIGINT;

ALTER TABLE HEL_CAMP_TASCA ADD 
  CONSTRAINT HEL_EXPTIPUS_CAMPTASCA_FK 
 FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

CREATE INDEX HEL_CAMP_TASCA_EXTIP_I on HEL_CAMP_TASCA
(EXPEDIENT_TIPUS_ID);

-- Modifica la taula document tasca per tenir una referència al tipus d'expedient en el cas que hi hagi herència
ALTER TABLE HEL_DOCUMENT_TASCA ADD  COLUMN EXPEDIENT_TIPUS_ID BIGINT;

ALTER TABLE HEL_DOCUMENT_TASCA ADD 
  CONSTRAINT HEL_EXPTIPUS_DOCTASCA_FK 
 FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

CREATE INDEX HEL_DOCTASCA_EXTIP_I on HEL_DOCUMENT_TASCA
(EXPEDIENT_TIPUS_ID);

-- Modifica la taula firma tasca per tenir una referència al tipus d'expedient en el cas que hi hagi herència
ALTER TABLE HEL_FIRMA_TASCA ADD  COLUMN EXPEDIENT_TIPUS_ID BIGINT;

ALTER TABLE HEL_FIRMA_TASCA ADD 
  CONSTRAINT HEL_EXPTIPUS_FIRTASCA_FK 
 FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

CREATE INDEX HEL_FIRTASCA_EXTIP_I on HEL_FIRMA_TASCA
(EXPEDIENT_TIPUS_ID);

-- Esborra les restriccions úniques
--	HEL_CAMP_TASCA(TASCA_ID, CAMP_ID), HEL_CAMP_TASCA(TASCA_ID, ORDRE)
--	HEL_DOCUMENT_TASCA(TASCA_ID, DOCUMENT_ID), HEL_DOCUMENT_TASCA(TASCA_ID, ORDRE)
--	HEL_FIRMA_TASCA(TASCA_ID, DOCUMENT_ID), HEL_FIRMA_TASCA(TASCA_ID, ORDRE)
 CREATE OR REPLACE FUNCTION Borra_unique(p_taula TEXT, p_columna1 TEXT, p_columna2 TEXT) RETURNS void AS $$
 DECLARE
	taula TEXT := p_taula;
	columna1 TEXT := p_columna1;
	columna2 TEXT := p_columna2;
	const_nom TEXT;
 BEGIN
	FOR const_nom IN
		SELECT
		    tc.constraint_name
		FROM 
		    information_schema.table_constraints AS tc 
		    JOIN information_schema.key_column_usage AS cuColumna1
		      ON tc.constraint_name = cuColumna1.constraint_name
		    JOIN information_schema.constraint_column_usage AS cuColumna2
		      ON tc.constraint_name = cuColumna2.constraint_name
		WHERE 
			tc.table_name = taula
			AND cuColumna1.column_name = p_columna1
			AND cuColumna2.column_name = p_columna2
	LOOP
		EXECUTE format('ALTER TABLE %I DROP CONSTRAINT IF EXISTS %I', taula, const_nom);
		raise notice 'ALTER TABLE %I DROP CONSTRAINT IF EXISTS %I', taula, const_nom;
	END LOOP;
  END;
  $$ LANGUAGE plpgsql;

DO $$ BEGIN
 PERFORM Borra_unique('hel_camp_tasca', 'tasca_id', 'camp_id');
 PERFORM Borra_unique('hel_camp_tasca', 'tasca_id', 'ordre');
 PERFORM Borra_unique('hel_document_tasca', 'tasca_id', 'document_id');
 PERFORM Borra_unique('hel_document_tasca', 'tasca_id', 'ordre');
 PERFORM Borra_unique('hel_firma_tasca', 'tasca_id', 'document_id');
 PERFORM Borra_unique('hel_firma_tasca', 'tasca_id', 'ordre');
END $$;
