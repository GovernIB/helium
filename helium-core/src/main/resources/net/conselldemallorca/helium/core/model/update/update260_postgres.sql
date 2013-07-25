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
