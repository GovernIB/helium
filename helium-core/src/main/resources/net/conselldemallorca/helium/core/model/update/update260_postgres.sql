-- Taules per a execució massiva
CREATE TABLE hel_exec_massiva (
	id BIGINT NOT NULL,
	data_fi TIMESTAMP(6) WITHOUT TIME ZONE,
	data_inici TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
	env_correu BOOLEAN,
	param1 CHARACTER VARYING(255),
	param2 OID,
	tipus INTEGER NOT NULL,
	usuari CHARACTER VARYING(64) NOT NULL,
	expedient_tipus_id BIGINT NOT NULL,
	PRIMARY KEY (id),
	CONSTRAINT hel_exptipus_exemas_fk FOREIGN KEY (expedient_tipus_id) REFERENCES public.hel_expedient_tipus (id)
);
    
CREATE TABLE hel_exec_masexp (
	id BIGINT NOT NULL,
	data_fi TIMESTAMP(6) WITHOUT TIME ZONE,
	data_inici TIMESTAMP(6) WITHOUT TIME ZONE,
	estat INTEGER NOT NULL,
	ordre INTEGER NOT NULL,
	execmas_id BIGINT NOT NULL,
	expedient_id BIGINT NOT NULL,
	error TEXT,
	tasca_id CHARACTER VARYING(255),
	PRIMARY KEY (id),
	CONSTRAINT hel_execmas_exemasex_fk FOREIGN KEY (execmas_id) REFERENCES public.hel_exec_massiva (id),
	CONSTRAINT hel_expedient_exemasex_fk FOREIGN KEY (expedient_id) REFERENCES public.hel_expedient (id)
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

--Nous indexos pel millorar el rendiment a la consulta dels camps de la definició de procés
CREATE INDEX
    HEL_CAMP_CODI_TIP
ON
    HEL_CAMP
    (
        CODI,
        TIPUS
    );

CREATE INDEX
    HEL_CAMP_COD_TIP_DP
ON
    HEL_CAMP
    (
        CODI,
        TIPUS,
        DEFINICIO_PROCES_ID
    );

