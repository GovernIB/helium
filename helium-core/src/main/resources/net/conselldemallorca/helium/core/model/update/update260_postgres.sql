-- Taules per a execució massiva
CREATE TABLE hel_exec_massiva (
        id BIGINT NOT NULL,
        data_inici TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
        data_fi TIMESTAMP(6) WITHOUT TIME ZONE,
        env_correu BOOLEAN,
        param1 CHARACTER VARYING(255),
        param2 OID,
        tipus INTEGER NOT NULL,
        usuari CHARACTER VARYING(64) NOT NULL,
        expedient_tipus_id BIGINT,
        entorn BIGINT,
        PRIMARY KEY (id),
        CONSTRAINT hel_exptipus_exemas_fk FOREIGN KEY (expedient_tipus_id) REFERENCES public.hel_expedient_tipus (id)
);
    
CREATE TABLE hel_exec_masexp (
        id BIGINT NOT NULL,
        data_inici TIMESTAMP(6) WITHOUT TIME ZONE,
        data_fi TIMESTAMP(6) WITHOUT TIME ZONE,
        estat INTEGER NOT NULL,
        ordre INTEGER NOT NULL,
        execmas_id BIGINT NOT NULL,
        expedient_id BIGINT,
        error TEXT,
        tasca_id CHARACTER VARYING(255),
        procinst_id CHARACTER VARYING(255),
        PRIMARY KEY (id),
        CONSTRAINT hel_execmas_exemasex_fk FOREIGN KEY (execmas_id) REFERENCES public.hel_exec_massiva (id),
        CONSTRAINT hel_expedient_exemasex_fk FOREIGN KEY (expedient_id) REFERENCES public.hel_expedient (id)
);

--Nous indexos pel millorar el rendiment a la consulta dels camps de la definició de procés
CREATE INDEX HEL_CAMP_CODI_TIP ON HEL_CAMP (CODI, TIPUS);
CREATE INDEX HEL_CAMP_COD_TIP_DP ON HEL_CAMP (CODI, TIPUS, DEFINICIO_PROCES_ID);

-- Per a permetre triar l'any en la generació del número d'expedient
alter table hel_expedient_tipus add seleccionar_any boolean not null set default 0;

-- Nou índex per la taula d'instàncies de tasca
CREATE INDEX IDX_TASKINST_PROC ON JBPM_TASKINSTANCE (PROCINST_);
CREATE INDEX IDX_TASKINST_TSK ON JBPM_TASKINSTANCE(TASK_);

-- Seqüències del tipus d'expedient
CREATE TABLE HEL_EXPEDIENT_TIPUS_SEQANY (
  id BIGINT NOT NULL,
  any_  BIGINT,
  sequencia BIGINT,
  expedient_tipus BIGINT,
  PRIMARY KEY (id),
  CONSTRAINT hel_exptipus_seqany_fkK FOREIGN KEY (expedient_tipus) REFERENCES public.hel_expedient_tipus(id)
);

-- Incrementar llargària camp params dels logs de l'expedient
alter table hel_expedient_log modify accio_params CHARACTER VARYING(2048),;

CREATE INDEX HEL_EXPLOG_EXPID_I ON HEL_EXPEDIENT_LOG (EXPEDIENT_ID);
CREATE INDEX HEL_EXPLOG_TARGETID_I ON HEL_EXPEDIENT_LOG (TARGET_ID);

-- Seqüències del tipus d'expedient
CREATE TABLE HEL_EXPEDIENT_TIPUS_SEQDEFANY (
  id BIGINT NOT NULL,
  any_  BIGINT,
  sequenciadefault BIGINT,
  expedient_tipus BIGINT,
  PRIMARY KEY (id),
  CONSTRAINT hel_exptipus_seqdefany_fkK FOREIGN KEY (expedient_tipus) REFERENCES public.hel_expedient_tipus(id)
);

/*
CREATE SEQUENCE SEQDEFANY START WITH 1000000000000000;

-- ATENCIÓ: SELECT per a l'expresió (app.numexp.expression) ${seq}-${any}. Si l'expresió és diferent, s'ha d'adaptar la select.
INSERT INTO HEL_EXPEDIENT_TIPUS_SEQDEFANY (id, any_, sequenciadefault, expedient_tipus)
SELECT nextval('SEQDEFANY'), s.anyo, s.seq, s.tipus_id 
FROM
(SELECT  TO_NUMBER(SUBSTR(numero_default, STRPOS(numero_default, '-') + 1), '9999999999') AS anyo, MAX(TO_NUMBER(SUBSTR(numero_default, 0, STRPOS(numero_default, '-')), '9999999999')) AS seq, tipus_id 
FROM    hel_expedient
GROUP BY tipus_id, TO_NUMBER(SUBSTR(numero_default, STRPOS(numero_default, '-') + 1), '9999999999')) s
WHERE SEQ IS NOT NULL;

DROP SEQUENCE SEQDEFANY;
*/

ALTER TABLE hel_expedient ADD COLUMN error_desc CHARACTER VARYING(255);
ALTER TABLE hel_expedient ADD COLUMN error_full text;

-- Actualització a la nova versió --
insert into hel_versio (
    id,
    codi,
    ordre,
    data_creacio,
    proces_executat,
    script_executat,
    data_execucio_script)
select
    nextval('hibernate_sequence') id,
    '2.6.0' codi,
    260 ordre,
    'now' data_creacio,
    false proces_executat,
    true script_executat,
    'now' data_execucio_script
where (select count(*) from hel_versio where ordre = 260) = 0;
