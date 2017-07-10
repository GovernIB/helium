
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
    NEXTVAL('HIBERNATE_SEQUENCE') ID,
    '3.2.0' CODI,
    310 ORDRE,
    current_date data_creacio,
    false PROCES_EXECUTAT,
    true SCRIPT_EXECUTAT,
    current_date DATA_EXECUCIO_SCRIPT
WHERE (SELECT COUNT(*) FROM HEL_VERSIO WHERE ORDRE = 310) = 0;

--------------------------------------------------------
-- 820 Simplificació de la configuració en les definicions de procés
--------------------------------------------------------

-- Crea les noves columnes
ALTER TABLE HEL_EXPEDIENT_TIPUS ADD AMB_INFO_PROPIA BOOLEAN DEFAULT false NOT NULL;
ALTER TABLE HEL_CAMP 		ADD EXPEDIENT_TIPUS_ID BIGINT;
ALTER TABLE HEL_CAMP		ADD DEFPROC_JBPMKEY  VARCHAR(255);
ALTER TABLE HEL_CAMP_AGRUP 	ADD EXPEDIENT_TIPUS_ID BIGINT;
ALTER TABLE HEL_DOCUMENT 	ADD EXPEDIENT_TIPUS_ID BIGINT;
ALTER TABLE HEL_TERMINI 	ADD EXPEDIENT_TIPUS_ID BIGINT;
ALTER TABLE HEL_ACCIO 		ADD EXPEDIENT_TIPUS_ID BIGINT;
ALTER TABLE HEL_ACCIO 		ADD DEFPROC_JBPMKEY  VARCHAR(255);

-- Modifica les columnes existents per a que puguin ser nules
ALTER TABLE HEL_CAMP ALTER COLUMN 	DEFINICIO_PROCES_ID DROP NOT NULL;
ALTER TABLE HEL_CAMP_AGRUP ALTER COLUMN	DEFINICIO_PROCES_ID DROP NOT NULL;
ALTER TABLE HEL_DOCUMENT ALTER COLUMN	DEFINICIO_PROCES_ID DROP NOT NULL;
ALTER TABLE HEL_TERMINI ALTER COLUMN	DEFINICIO_PROCES_ID DROP NOT NULL;
ALTER TABLE HEL_ACCIO ALTER COLUMN	DEFINICIO_PROCES_ID DROP NOT NULL;

-- Distribució de camps de formulari
ALTER TABLE HEL_CAMP_TASCA ADD AMPLE_COLS BIGINT DEFAULT 12;
ALTER TABLE HEL_CAMP_TASCA ADD BUIT_COLS BIGINT DEFAULT 0;
ALTER TABLE HEL_CONSULTA_CAMP ADD AMPLE_COLS BIGINT DEFAULT 12;
ALTER TABLE HEL_CONSULTA_CAMP ADD BUIT_COLS BIGINT DEFAULT 0;

-- AFEGIM LA COLUMNA ROLS_ PER A CONTROL DE PERMISOS EN TASQUES EN SEGON PLA
ALTER TABLE JBPM_TASKINSTANCE ADD ROLS_ VARCHAR(2000);

-- Crea els índexos per a les noves columnes
CREATE INDEX HEL_CAMP_EXPTIP_I 		ON HEL_CAMP 			(EXPEDIENT_TIPUS_ID);
CREATE INDEX HEL_CAMPAGRUP_EXPTIP_I ON HEL_CAMP_AGRUP 		(EXPEDIENT_TIPUS_ID);
CREATE INDEX HEL_DOCUMENT_EXPTIP_I 	ON HEL_DOCUMENT 		(EXPEDIENT_TIPUS_ID);
CREATE INDEX HEL_TERMINI_EXPTIP_I 	ON HEL_TERMINI 			(EXPEDIENT_TIPUS_ID);
CREATE INDEX HEL_ACCIO_EXPTIP_I 	ON HEL_ACCIO			(EXPEDIENT_TIPUS_ID);
CREATE INDEX IDX_TASKINST_SUSPOPEN 	ON JBPM_TASKINSTANCE	(ISSUSPENDED_, ISOPEN_);


-- Esborra les constraints actuals de les taules hel_camp i hel_camp_agrup sobre les columnes codi, definicio_proces_id

 CREATE OR REPLACE FUNCTION Borra_restr(p_tab TEXT) RETURNS void AS $$
 DECLARE
	taula TEXT := p_tab;
	const_nom TEXT;
 BEGIN
	FOR const_nom IN
		SELECT
		    tc.constraint_name
		FROM 
		    information_schema.table_constraints AS tc 
		    JOIN information_schema.key_column_usage AS kcu
		      ON tc.constraint_name = kcu.constraint_name
		    JOIN information_schema.constraint_column_usage AS ccu
		      ON ccu.constraint_name = tc.constraint_name
		WHERE 
			tc.table_name = taula
			AND ccu.column_name = 'definicio_proces_id'
			AND kcu.column_name = 'codi'
	LOOP
		EXECUTE format('ALTER TABLE %I DROP CONSTRAINT IF EXISTS %I', taula, const_nom.nom);
	END LOOP;
  END;
  $$ LANGUAGE plpgsql;

DO $$ BEGIN
 PERFORM Borra_restr('HEL_CAMP');
 PERFORM Borra_restr('HEL_CAMP_AGRUP');
 PERFORM Borra_restr('HEL_DOCUMENT');
 PERFORM Borra_restr('HEL_ACCIO');
 PERFORM Borra_restr('HEL_TERMINI');
END $$;

-- Afegeix les constraints úniques de 3 camps
ALTER TABLE HEL_CAMP 		ADD UNIQUE (CODI, DEFINICIO_PROCES_ID, EXPEDIENT_TIPUS_ID);
ALTER TABLE HEL_CAMP_AGRUP 	ADD UNIQUE (CODI, DEFINICIO_PROCES_ID, EXPEDIENT_TIPUS_ID);
ALTER TABLE HEL_DOCUMENT 	ADD UNIQUE (CODI, DEFINICIO_PROCES_ID, EXPEDIENT_TIPUS_ID);
ALTER TABLE HEL_TERMINI 	ADD UNIQUE (CODI, DEFINICIO_PROCES_ID, EXPEDIENT_TIPUS_ID);
ALTER TABLE HEL_ACCIO 		ADD UNIQUE (CODI, DEFINICIO_PROCES_ID, EXPEDIENT_TIPUS_ID);

ALTER TABLE HEL_CAMP 		ADD  CONSTRAINT HEL_EXPTIP_CAMP_FK 		FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS (ID);
ALTER TABLE HEL_CAMP_AGRUP 	ADD  CONSTRAINT HEL_EXPTIP_CAMPAGRUP_FK 	FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS (ID);
ALTER TABLE HEL_DOCUMENT 	ADD  CONSTRAINT HEL_EXPTIP_DOC_FK 		FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS (ID);
ALTER TABLE HEL_TERMINI		ADD  CONSTRAINT HEL_EXPTIP_TERMINI_FK 		FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS (ID);
ALTER TABLE HEL_ACCIO 		ADD  CONSTRAINT HEL_EXPTIP_ACCIO_FK 		FOREIGN KEY (EXPEDIENT_TIPUS_ID) REFERENCES HEL_EXPEDIENT_TIPUS (ID);

--------------------------------------------------------
-- 8889 Millores en el procés d'importació de tipus d'expedients
--------------------------------------------------------

-- Esborra la restricció de clau única [id entorn - codi] del comini i hi afegeix la clau [id entorn, id tipus expedient, codi]
 CREATE OR REPLACE FUNCTION Borra_unique(p_tab TEXT) RETURNS void AS $$
 DECLARE
	taula TEXT := p_tab;
	const_nom TEXT;
 BEGIN
	FOR const_nom IN
		SELECT
		    tc.constraint_name
		FROM 
		    information_schema.table_constraints AS tc 
		    JOIN information_schema.key_column_usage AS kcu
		      ON tc.constraint_name = kcu.constraint_name
		    JOIN information_schema.constraint_column_usage AS ccu
		      ON ccu.constraint_name = tc.constraint_name
		WHERE 
			tc.table_name = taula
			AND ccu.column_name = 'codi'
			AND kcu.column_name = 'entorn_id'
	LOOP
		EXECUTE format('ALTER TABLE %I DROP CONSTRAINT IF EXISTS %I', taula, const_nom.nom);
	END LOOP;
  END;
  $$ LANGUAGE plpgsql;

DO $$ BEGIN
 PERFORM Borra_unique('HEL_DOMINI');
 PERFORM Borra_unique('HEL_CONSULTA');
END $$;

-- Afegeix les constraints úniques de 3 camps
ALTER TABLE HEL_DOMINI 		ADD UNIQUE (CODI, ENTORN_ID, EXPEDIENT_TIPUS_ID);
ALTER TABLE HEL_CONSULTA	ADD UNIQUE (CODI, ENTORN_ID, EXPEDIENT_TIPUS_ID);
