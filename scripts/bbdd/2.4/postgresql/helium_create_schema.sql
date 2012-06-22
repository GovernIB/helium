
--
-- TOC entry 1704 (class 1259 OID 27410)
-- Dependencies: 5
-- Name: hel_accio; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_accio (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    descripcio character varying(255),
    jbpm_action character varying(255) NOT NULL,
    nom character varying(255) NOT NULL,
    definicio_proces_id bigint NOT NULL
);


--
-- TOC entry 1705 (class 1259 OID 27417)
-- Dependencies: 5
-- Name: hel_acl_class; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_acl_class (
    id bigint NOT NULL,
    class character varying(255) NOT NULL
);


--
-- TOC entry 1706 (class 1259 OID 27424)
-- Dependencies: 5
-- Name: hel_acl_entry; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_acl_entry (
    id bigint NOT NULL,
    ace_order integer NOT NULL,
    audit_failure boolean NOT NULL,
    audit_success boolean NOT NULL,
    granting boolean NOT NULL,
    mask integer NOT NULL,
    acl_object_identity bigint,
    sid bigint
);


--
-- TOC entry 1707 (class 1259 OID 27429)
-- Dependencies: 5
-- Name: hel_acl_object_identity; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_acl_object_identity (
    id bigint NOT NULL,
    entries_inheriting boolean NOT NULL,
    object_id_identity bigint NOT NULL,
    object_id_class bigint,
    owner_sid bigint,
    parent_object bigint
);


--
-- TOC entry 1708 (class 1259 OID 27434)
-- Dependencies: 5
-- Name: hel_acl_sid; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_acl_sid (
    id bigint NOT NULL,
    principal boolean,
    sid character varying(100) NOT NULL
);


--
-- TOC entry 1709 (class 1259 OID 27439)
-- Dependencies: 5
-- Name: hel_action_log; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_action_log (
    id bigint NOT NULL,
    accio character varying(255) NOT NULL,
    columna_pk character varying(255) NOT NULL,
    data timestamp without time zone NOT NULL,
    taula character varying(255) NOT NULL,
    usuari character varying(255) NOT NULL,
    valors character varying(255) NOT NULL
);


--
-- TOC entry 1710 (class 1259 OID 27444)
-- Dependencies: 5
-- Name: hel_alerta; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_alerta (
    id bigint NOT NULL,
    causa character varying(255),
    data_creacio timestamp without time zone NOT NULL,
    data_eliminacio timestamp without time zone,
    data_lectura timestamp without time zone,
    destinatari character varying(255) NOT NULL,
    prioritat integer,
    text character varying(1024),
    entorn_id bigint NOT NULL,
    expedient_id bigint NOT NULL,
    termini_iniciat_id bigint
);


--
-- TOC entry 1711 (class 1259 OID 27449)
-- Dependencies: 5
-- Name: hel_area; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_area (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    descripcio character varying(255),
    nom character varying(255) NOT NULL,
    entorn_id bigint NOT NULL,
    pare_id bigint,
    tipus_id bigint NOT NULL
);


--
-- TOC entry 1712 (class 1259 OID 27456)
-- Dependencies: 5
-- Name: hel_area_jbpmid; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_area_jbpmid (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    descripcio character varying(255),
    nom character varying(255) NOT NULL
);


--
-- TOC entry 1713 (class 1259 OID 27461)
-- Dependencies: 5
-- Name: hel_area_membre; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_area_membre (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    area_id bigint NOT NULL
);


--
-- TOC entry 1714 (class 1259 OID 27468)
-- Dependencies: 5
-- Name: hel_area_tipus; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_area_tipus (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    descripcio character varying(255),
    nom character varying(255) NOT NULL,
    entorn_id bigint NOT NULL
);


--
-- TOC entry 1715 (class 1259 OID 27475)
-- Dependencies: 5
-- Name: hel_camp; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_camp (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    domini_camp_text character varying(64),
    domini_camp_valor character varying(64),
    domini_paramid character varying(255),
    domini_params character varying(255),
    etiqueta character varying(255) NOT NULL,
    jbpm_action character varying(255),
    multiple boolean,
    observacions character varying(255),
    ocult boolean,
    ordre integer,
    tipus integer NOT NULL,
    camp_agrupacio_id bigint,
    definicio_proces_id bigint NOT NULL,
    domini_id bigint,
    enumeracio_id bigint
);


--
-- TOC entry 1716 (class 1259 OID 27482)
-- Dependencies: 5
-- Name: hel_camp_agrup; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_camp_agrup (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    descripcio character varying(255),
    nom character varying(255) NOT NULL,
    ordre integer,
    definicio_proces_id bigint NOT NULL
);


--
-- TOC entry 1717 (class 1259 OID 27489)
-- Dependencies: 5
-- Name: hel_camp_registre; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_camp_registre (
    id bigint NOT NULL,
    llistar boolean,
    obligatori boolean,
    ordre integer,
    membre_id bigint NOT NULL,
    registre_id bigint NOT NULL
);


--
-- TOC entry 1718 (class 1259 OID 27498)
-- Dependencies: 5
-- Name: hel_camp_tasca; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_camp_tasca (
    id bigint NOT NULL,
    ordre integer,
    rf boolean,
    ro boolean,
    rq boolean,
    wt boolean,
    camp_id bigint NOT NULL,
    tasca_id bigint NOT NULL
);


--
-- TOC entry 1719 (class 1259 OID 27507)
-- Dependencies: 5
-- Name: hel_carrec; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_carrec (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    descripcio character varying(255),
    nom_dona character varying(255) NOT NULL,
    nom_home character varying(255) NOT NULL,
    persona_codi character varying(64),
    persona_sexe integer,
    carrec_dona character varying(255) NOT NULL,
    tractament_home character varying(255) NOT NULL,
    area_id bigint NOT NULL,
    entorn_id bigint NOT NULL
);


--
-- TOC entry 1720 (class 1259 OID 27514)
-- Dependencies: 5
-- Name: hel_carrec_jbpmid; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_carrec_jbpmid (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    descripcio character varying(255),
    nom_dona character varying(255) NOT NULL,
    nom_home character varying(255) NOT NULL,
    persona_sexe integer,
    carrec_dona character varying(255) NOT NULL,
    tractament_home character varying(255) NOT NULL
);


--
-- TOC entry 1721 (class 1259 OID 27519)
-- Dependencies: 5
-- Name: hel_consulta; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_consulta (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    descripcio character varying(255),
    exportar_actiu boolean,
    generica boolean,
    informe_contingut oid,
    informe_nom character varying(255),
    nom character varying(255) NOT NULL,
    valors_predef character varying(1024),
    entorn_id bigint NOT NULL,
    expedient_tipus_id bigint NOT NULL
);


--
-- TOC entry 1722 (class 1259 OID 27526)
-- Dependencies: 5
-- Name: hel_consulta_camp; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_consulta_camp (
    id bigint NOT NULL,
    camp_codi character varying(64) NOT NULL,
    defproc_jbpmkey character varying(255),
    defproc_versio integer,
    ordre integer,
    tipus integer NOT NULL,
    consulta_id bigint NOT NULL
);


--
-- TOC entry 1723 (class 1259 OID 27533)
-- Dependencies: 5
-- Name: hel_consulta_sub; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_consulta_sub (
    pare_id bigint NOT NULL,
    fill_id bigint NOT NULL
);


--
-- TOC entry 1724 (class 1259 OID 27538)
-- Dependencies: 5
-- Name: hel_definicio_proces; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_definicio_proces (
    id bigint NOT NULL,
    datacreacio timestamp without time zone NOT NULL,
    etiqueta character varying(64),
    jbpm_id character varying(255) NOT NULL,
    jbpm_key character varying(255) NOT NULL,
    versio integer,
    entorn_id bigint NOT NULL,
    expedient_tipus_id bigint
);


--
-- TOC entry 1725 (class 1259 OID 27545)
-- Dependencies: 5
-- Name: hel_document; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_document (
    id bigint NOT NULL,
    adjuntar_auto boolean,
    arxiu_contingut oid,
    arxiu_nom character varying(255),
    codi character varying(64) NOT NULL,
    content_type character varying(255),
    custodia_codi character varying(255),
    descripcio character varying(255),
    nom character varying(255) NOT NULL,
    plantilla boolean,
    tipus_portasignatures integer,
    camp_data_id bigint,
    definicio_proces_id bigint NOT NULL
);


--
-- TOC entry 1726 (class 1259 OID 27552)
-- Dependencies: 5
-- Name: hel_document_store; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_document_store (
    id bigint NOT NULL,
    adjunt boolean,
    adjunt_titol character varying(255),
    arxiu_contingut oid,
    arxiu_nom character varying(255) NOT NULL,
    data_creacio timestamp without time zone NOT NULL,
    data_document date NOT NULL,
    data_modificacio timestamp without time zone NOT NULL,
    font integer NOT NULL,
    jbpm_variable character varying(255) NOT NULL,
    process_instance_id character varying(64) NOT NULL,
    ref_custodia character varying(255),
    ref_font character varying(255),
    registre_data timestamp without time zone,
    registre_entrada boolean,
    registre_num character varying(255),
    registre_ofcodi character varying(255),
    registre_ofnom character varying(255),
    registre_orgcodi character varying(255),
    signat boolean
);


--
-- TOC entry 1727 (class 1259 OID 27560)
-- Dependencies: 5
-- Name: hel_document_tasca; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_document_tasca (
    id bigint NOT NULL,
    ordre integer,
    ro boolean,
    rq boolean,
    document_id bigint NOT NULL,
    tasca_id bigint NOT NULL
);


--
-- TOC entry 1728 (class 1259 OID 27569)
-- Dependencies: 5
-- Name: hel_domini; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_domini (
    id bigint NOT NULL,
    cache_segons integer,
    codi character varying(64) NOT NULL,
    descripcio character varying(255),
    jndi_datasource character varying(255),
    nom character varying(255) NOT NULL,
    ordre_params character varying(255),
    sqlexpr character varying(1024),
    tipus integer NOT NULL,
    url character varying(255),
    entorn_id bigint NOT NULL,
    expedient_tipus_id bigint
);


--
-- TOC entry 1729 (class 1259 OID 27579)
-- Dependencies: 5
-- Name: hel_entorn; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_entorn (
    id bigint NOT NULL,
    actiu boolean,
    codi character varying(64) NOT NULL,
    descripcio character varying(255),
    nom character varying(255) NOT NULL
);


--
-- TOC entry 1730 (class 1259 OID 27586)
-- Dependencies: 5
-- Name: hel_enumeracio; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_enumeracio (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    nom character varying(255) NOT NULL,
    valors character varying(4000),
    entorn_id bigint NOT NULL,
    expedient_tipus_id bigint
);


--
-- TOC entry 1731 (class 1259 OID 27596)
-- Dependencies: 5
-- Name: hel_enumeracio_valors; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_enumeracio_valors (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    nom character varying(255) NOT NULL,
    ordre integer NOT NULL,
    enumeracio_id bigint NOT NULL
);


--
-- TOC entry 1732 (class 1259 OID 27601)
-- Dependencies: 5
-- Name: hel_estat; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_estat (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    nom character varying(255) NOT NULL,
    ordre integer NOT NULL,
    expedient_tipus_id bigint NOT NULL
);


--
-- TOC entry 1733 (class 1259 OID 27608)
-- Dependencies: 5
-- Name: hel_expedient; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_expedient (
    id bigint NOT NULL,
    anulat boolean,
    autenticat boolean,
    avisos_email character varying(255),
    avisos_habilitat boolean,
    avisos_mobil character varying(255),
    comentari character varying(255),
    data_fi timestamp without time zone,
    data_inici timestamp without time zone NOT NULL,
    geo_posx double precision,
    geo_posy double precision,
    geo_referencia character varying(64),
    idioma character varying(8),
    info_aturat character varying(1024),
    iniciador_codi character varying(64),
    iniciador_tipus integer,
    interessat_nif character varying(16),
    interessat_nom character varying(255),
    nottel_habilitat boolean,
    numero character varying(64),
    numero_default character varying(64),
    process_instance_id character varying(255) NOT NULL,
    registre_data timestamp without time zone,
    registre_num character varying(64),
    representant_nif character varying(16),
    representant_nom character varying(255),
    responsable_codi character varying(64),
    titol character varying(255),
    tramexp_clau character varying(255),
    tramexp_id character varying(255),
    tramitador_nif character varying(16),
    tramitador_nom character varying(255),
    unitat_adm bigint,
    grup_codi character varying(64),
    entorn_id bigint NOT NULL,
    estat_id bigint,
    tipus_id bigint NOT NULL
);

CREATE TABLE hel_expedient_log(
  id BIGINT NOT NULL,
  accio_tipus INTEGER NOT NULL,
  accio_params CHARACTER VARYING(255),
  data TIMESTAMP(6) WITHOUT TIME ZONE NOT NULL,
  estat INTEGER NOT NULL,
  jbpm_logid BIGINT,
  process_instance_id BIGINT,
  target_id CHARACTER VARYING(255) NOT NULL,
  usuari CHARACTER VARYING(255) NOT NULL,
  expedient_id BIGINT NOT NULL
);


--
-- TOC entry 1734 (class 1259 OID 27618)
-- Dependencies: 5
-- Name: hel_expedient_rels; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_expedient_rels (
    origen_id bigint NOT NULL,
    desti_id bigint NOT NULL
);


--
-- TOC entry 1735 (class 1259 OID 27623)
-- Dependencies: 5
-- Name: hel_expedient_tipus; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_expedient_tipus (
    id bigint NOT NULL,
    any_actual integer,
    codi character varying(64) NOT NULL,
    demana_numero boolean,
    demana_titol boolean,
    expressio_numero character varying(255),
    formext_contrasenya character varying(255),
    formext_url character varying(255),
    formext_usuari character varying(255),
    jbpm_pd_key character varying(255),
    nom character varying(255) NOT NULL,
    reiniciar_anual boolean,
    respdefault_codi character varying(64),
    sequencia bigint,
    sequencia_def bigint,
    sistra_codtra character varying(64),
    sistra_mapadj character varying(2048),
    sistra_mapcamps character varying(2048),
    sistra_mapdocs character varying(2048),
    te_numero boolean,
    te_titol boolean,
    restringir_grup boolean,
    tram_massiva boolean,
    entorn_id bigint NOT NULL
);


--
-- TOC entry 1736 (class 1259 OID 27635)
-- Dependencies: 5
-- Name: hel_festiu; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_festiu (
    id bigint NOT NULL,
    data date NOT NULL
);


--
-- TOC entry 1737 (class 1259 OID 27642)
-- Dependencies: 5
-- Name: hel_firma_tasca; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_firma_tasca (
    id bigint NOT NULL,
    ordre integer,
    rq boolean,
    document_id bigint NOT NULL,
    tasca_id bigint NOT NULL
);


--
-- TOC entry 1738 (class 1259 OID 27651)
-- Dependencies: 5
-- Name: hel_formext; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_formext (
    id bigint NOT NULL,
    data_darrpet timestamp without time zone,
    data_inici timestamp without time zone NOT NULL,
    data_recdades timestamp without time zone,
    data_formheight integer,
    data_formwidth integer,
    formid character varying(255) NOT NULL,
    taskid character varying(255) NOT NULL,
    url character varying(1024) NOT NULL
);


--
-- TOC entry 1753 (class 1259 OID 28495)
-- Dependencies: 5
-- Name: hel_idgen; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_idgen (
    taula character varying(255),
    valor integer
);


--
-- TOC entry 1739 (class 1259 OID 27658)
-- Dependencies: 5
-- Name: hel_map_sistra; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_map_sistra (
    id bigint NOT NULL,
    codihelium character varying(255) NOT NULL,
    codisistra character varying(255) NOT NULL,
    tipus integer NOT NULL,
    expedient_tipus_id bigint NOT NULL
);


--
-- TOC entry 1740 (class 1259 OID 27665)
-- Dependencies: 5
-- Name: hel_permis; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_permis (
    codi character varying(64) NOT NULL,
    descripcio character varying(255)
);


--
-- TOC entry 1741 (class 1259 OID 27670)
-- Dependencies: 5
-- Name: hel_persona; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_persona (
    id bigint NOT NULL,
    avis_correu boolean NOT NULL,
    codi character varying(64) NOT NULL,
    data_naixement date,
    dni character varying(64),
    email character varying(255),
    font integer,
    llinatge1 character varying(255) NOT NULL,
    llinatge2 character varying(255),
    llinatges character varying(255),
    nom character varying(255) NOT NULL,
    nom_sencer character varying(255),
    sexe integer NOT NULL,
    relleu_id bigint
);


--
-- TOC entry 1742 (class 1259 OID 27677)
-- Dependencies: 5
-- Name: hel_portasignatures; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_portasignatures (
    id bigint NOT NULL,
    data_enviat timestamp without time zone,
    document_id integer NOT NULL,
    document_store_id bigint,
    estat integer,
    motiu_rebuig character varying(255),
    token_id bigint NOT NULL,
    transicio_ko character varying(255),
    transicio_ok character varying(255),
    transicio integer
);


--
-- TOC entry 1743 (class 1259 OID 27684)
-- Dependencies: 5
-- Name: hel_redir; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_redir (
    id bigint NOT NULL,
    data_cancelacio timestamp without time zone,
    data_fi timestamp without time zone NOT NULL,
    data_inici timestamp without time zone NOT NULL,
    usuari_desti character varying(255) NOT NULL,
    usuari_origen character varying(255) NOT NULL
);


--
-- TOC entry 1744 (class 1259 OID 27689)
-- Dependencies: 5
-- Name: hel_registre; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_registre (
    id bigint NOT NULL,
    accio integer NOT NULL,
    data timestamp without time zone NOT NULL,
    entitat integer NOT NULL,
    entitat_id character varying(255) NOT NULL,
    expedient_id bigint NOT NULL,
    missatge character varying(1024),
    process_instance_id character varying(255),
    responsable_codi character varying(64) NOT NULL,
    valor_nou character varying(1024),
    valor_vell character varying(1024)
);


--
-- TOC entry 1745 (class 1259 OID 27697)
-- Dependencies: 5
-- Name: hel_tasca; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_tasca (
    id bigint NOT NULL,
    expressio_delegacio character varying(255),
    form_extern character varying(255),
    jbpm_name character varying(255) NOT NULL,
    missatge_info character varying(255),
    missatge_warn character varying(255),
    nom character varying(255),
    nom_script character varying(1024),
    recurs_form character varying(255),
    tipus integer NOT NULL,
    definicio_proces_id bigint NOT NULL
);


--
-- TOC entry 1746 (class 1259 OID 27707)
-- Dependencies: 5
-- Name: hel_termini; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_termini (
    id bigint NOT NULL,
    alerta_completat boolean,
    alerta_final boolean,
    alerta_previa boolean,
    anys integer,
    codi character varying(64) NOT NULL,
    descripcio character varying(255),
    dies integer,
    dies_previs_avis integer,
    durada_predef boolean,
    laborable boolean,
    manual boolean,
    mesos integer,
    nom character varying(255) NOT NULL,
    definicio_proces_id bigint NOT NULL
);


--
-- TOC entry 1747 (class 1259 OID 27714)
-- Dependencies: 5
-- Name: hel_termini_iniciat; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_termini_iniciat (
    id bigint NOT NULL,
    alerta_completat boolean,
    alerta_final boolean,
    alerta_previa boolean,
    anys integer,
    data_aturada date,
    data_cancel date,
    data_completat date,
    data_fi date NOT NULL,
    data_fi_prorroga date,
    data_inici date NOT NULL,
    dies integer,
    dies_aturat integer,
    mesos integer,
    process_instance_id character varying(255) NOT NULL,
    task_instance_id character varying(255),
    timer_ids character varying(1024),
    termini_id bigint NOT NULL
);


--
-- TOC entry 1748 (class 1259 OID 27721)
-- Dependencies: 5
-- Name: hel_usuari; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_usuari (
    codi character varying(64) NOT NULL,
    actiu boolean NOT NULL,
    contrasenya character varying(255)
);


--
-- TOC entry 1749 (class 1259 OID 27726)
-- Dependencies: 5
-- Name: hel_usuari_permis; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_usuari_permis (
    codi character varying(64) NOT NULL,
    permis character varying(64) NOT NULL
);


--
-- TOC entry 1750 (class 1259 OID 27731)
-- Dependencies: 5
-- Name: hel_usuari_prefs; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_usuari_prefs (
    codi character varying(64) NOT NULL,
    default_entorn character varying(64),
    idioma character varying(255)
);


--
-- TOC entry 1751 (class 1259 OID 27736)
-- Dependencies: 5
-- Name: hel_validacio; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_validacio (
    id bigint NOT NULL,
    expressio character varying(1024) NOT NULL,
    missatge character varying(255) NOT NULL,
    nom character varying(255),
    ordre integer NOT NULL,
    camp_id bigint,
    tasca_id bigint
);


--
-- TOC entry 1752 (class 1259 OID 27741)
-- Dependencies: 5
-- Name: hel_versio; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE hel_versio (
    id bigint NOT NULL,
    codi character varying(64) NOT NULL,
    data_creacio timestamp without time zone NOT NULL,
    data_execucio_proces timestamp without time zone,
    data_execucio_script timestamp without time zone,
    descripcio character varying(255),
    ordre integer NOT NULL,
    proces_executat boolean,
    script_executat boolean
);


--
-- TOC entry 1754 (class 1259 OID 28498)
-- Dependencies: 5
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


--
-- TOC entry 1672 (class 1259 OID 27216)
-- Dependencies: 5
-- Name: jbpm_action; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_action (
    id_ bigint NOT NULL,
    class character(1) NOT NULL,
    name_ character varying(255),
    ispropagationallowed_ boolean,
    actionexpression_ character varying(255),
    isasync_ boolean,
    referencedaction_ bigint,
    actiondelegation_ bigint,
    event_ bigint,
    processdefinition_ bigint,
    expression_ text,
    timername_ character varying(255),
    duedate_ character varying(255),
    repeat_ character varying(255),
    transitionname_ character varying(255),
    timeraction_ bigint,
    eventindex_ integer,
    exceptionhandler_ bigint,
    exceptionhandlerindex_ integer
);


--
-- TOC entry 1673 (class 1259 OID 27224)
-- Dependencies: 5
-- Name: jbpm_bytearray; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_bytearray (
    id_ bigint NOT NULL,
    name_ character varying(255),
    filedefinition_ bigint
);


--
-- TOC entry 1674 (class 1259 OID 27229)
-- Dependencies: 5
-- Name: jbpm_byteblock; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_byteblock (
    processfile_ bigint NOT NULL,
    bytes_ bytea,
    index_ integer NOT NULL
);


--
-- TOC entry 1675 (class 1259 OID 27237)
-- Dependencies: 5
-- Name: jbpm_comment; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_comment (
    id_ bigint NOT NULL,
    version_ integer NOT NULL,
    actorid_ character varying(255),
    time_ timestamp without time zone,
    message_ text,
    token_ bigint,
    taskinstance_ bigint,
    tokenindex_ integer,
    taskinstanceindex_ integer
);


--
-- TOC entry 1676 (class 1259 OID 27245)
-- Dependencies: 5
-- Name: jbpm_decisionconditions; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_decisionconditions (
    decision_ bigint NOT NULL,
    transitionname_ character varying(255),
    expression_ character varying(255),
    index_ integer NOT NULL
);


--
-- TOC entry 1677 (class 1259 OID 27250)
-- Dependencies: 5
-- Name: jbpm_delegation; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_delegation (
    id_ bigint NOT NULL,
    classname_ text,
    configuration_ text,
    configtype_ character varying(255),
    processdefinition_ bigint
);


--
-- TOC entry 1678 (class 1259 OID 27258)
-- Dependencies: 5
-- Name: jbpm_event; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_event (
    id_ bigint NOT NULL,
    eventtype_ character varying(255),
    type_ character(1),
    graphelement_ bigint,
    processdefinition_ bigint,
    node_ bigint,
    transition_ bigint,
    task_ bigint
);


--
-- TOC entry 1679 (class 1259 OID 27263)
-- Dependencies: 5
-- Name: jbpm_exceptionhandler; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_exceptionhandler (
    id_ bigint NOT NULL,
    exceptionclassname_ text,
    type_ character(1),
    graphelement_ bigint,
    processdefinition_ bigint,
    graphelementindex_ integer,
    node_ bigint,
    transition_ bigint,
    task_ bigint
);


--
-- TOC entry 1680 (class 1259 OID 27271)
-- Dependencies: 5
-- Name: jbpm_id_group; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_id_group (
    id_ bigint NOT NULL,
    class_ character(1) NOT NULL,
    name_ character varying(255),
    type_ character varying(255),
    parent_ bigint
);


--
-- TOC entry 1681 (class 1259 OID 27276)
-- Dependencies: 5
-- Name: jbpm_id_membership; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_id_membership (
    id_ bigint NOT NULL,
    class_ character(1) NOT NULL,
    name_ character varying(255),
    role_ character varying(255),
    user_ bigint,
    group_ bigint
);


--
-- TOC entry 1682 (class 1259 OID 27281)
-- Dependencies: 5
-- Name: jbpm_id_permissions; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_id_permissions (
    entity_ bigint NOT NULL,
    class_ character varying(255),
    name_ character varying(255),
    action_ character varying(255)
);


--
-- TOC entry 1683 (class 1259 OID 27284)
-- Dependencies: 5
-- Name: jbpm_id_user; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_id_user (
    id_ bigint NOT NULL,
    class_ character(1) NOT NULL,
    name_ character varying(255),
    email_ character varying(255),
    password_ character varying(255)
);


--
-- TOC entry 1684 (class 1259 OID 27289)
-- Dependencies: 5
-- Name: jbpm_job; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_job (
    id_ bigint NOT NULL,
    class_ character(1) NOT NULL,
    version_ integer NOT NULL,
    duedate_ timestamp without time zone,
    processinstance_ bigint,
    token_ bigint,
    taskinstance_ bigint,
    issuspended_ boolean,
    isexclusive_ boolean,
    lockowner_ character varying(255),
    locktime_ timestamp without time zone,
    exception_ text,
    retries_ integer,
    name_ character varying(255),
    repeat_ character varying(255),
    transitionname_ character varying(255),
    action_ bigint,
    graphelementtype_ character varying(255),
    graphelement_ bigint,
    node_ bigint
);


--
-- TOC entry 1685 (class 1259 OID 27297)
-- Dependencies: 5
-- Name: jbpm_log; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_log (
    id_ bigint NOT NULL,
    class_ character(1) NOT NULL,
    index_ integer,
    date_ timestamp without time zone,
    token_ bigint,
    parent_ bigint,
    message_ text,
    exception_ text,
    action_ bigint,
    node_ bigint,
    enter_ timestamp without time zone,
    leave_ timestamp without time zone,
    duration_ bigint,
    newlongvalue_ bigint,
    transition_ bigint,
    child_ bigint,
    sourcenode_ bigint,
    destinationnode_ bigint,
    variableinstance_ bigint,
    oldbytearray_ bigint,
    newbytearray_ bigint,
    olddatevalue_ timestamp without time zone,
    newdatevalue_ timestamp without time zone,
    olddoublevalue_ double precision,
    newdoublevalue_ double precision,
    oldlongidclass_ character varying(255),
    oldlongidvalue_ bigint,
    newlongidclass_ character varying(255),
    newlongidvalue_ bigint,
    oldstringidclass_ character varying(255),
    oldstringidvalue_ character varying(255),
    newstringidclass_ character varying(255),
    newstringidvalue_ character varying(255),
    oldlongvalue_ bigint,
    oldstringvalue_ text,
    newstringvalue_ text,
    taskinstance_ bigint,
    taskactorid_ character varying(255),
    taskoldactorid_ character varying(255),
    swimlaneinstance_ bigint
);


--
-- TOC entry 1686 (class 1259 OID 27305)
-- Dependencies: 5
-- Name: jbpm_moduledefinition; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_moduledefinition (
    id_ bigint NOT NULL,
    class_ character(1) NOT NULL,
    name_ character varying(255),
    processdefinition_ bigint,
    starttask_ bigint
);


--
-- TOC entry 1687 (class 1259 OID 27310)
-- Dependencies: 5
-- Name: jbpm_moduleinstance; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_moduleinstance (
    id_ bigint NOT NULL,
    class_ character(1) NOT NULL,
    version_ integer NOT NULL,
    processinstance_ bigint,
    taskmgmtdefinition_ bigint,
    name_ character varying(255)
);


--
-- TOC entry 1688 (class 1259 OID 27315)
-- Dependencies: 5
-- Name: jbpm_node; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_node (
    id_ bigint NOT NULL,
    class_ character(1) NOT NULL,
    name_ character varying(255),
    description_ text,
    processdefinition_ bigint,
    isasync_ boolean,
    isasyncexcl_ boolean,
    action_ bigint,
    superstate_ bigint,
    subprocname_ character varying(255),
    subprocessdefinition_ bigint,
    decisionexpression_ character varying(255),
    decisiondelegation bigint,
    script_ bigint,
    parentlockmode_ character varying(255),
    signal_ integer,
    createtasks_ boolean,
    endtasks_ boolean,
    nodecollectionindex_ integer
);


--
-- TOC entry 1689 (class 1259 OID 27323)
-- Dependencies: 5
-- Name: jbpm_pooledactor; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_pooledactor (
    id_ bigint NOT NULL,
    version_ integer NOT NULL,
    actorid_ character varying(255),
    swimlaneinstance_ bigint
);


--
-- TOC entry 1690 (class 1259 OID 27328)
-- Dependencies: 5
-- Name: jbpm_processdefinition; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_processdefinition (
    id_ bigint NOT NULL,
    class_ character(1) NOT NULL,
    name_ character varying(255),
    description_ text,
    version_ integer,
    isterminationimplicit_ boolean,
    startstate_ bigint
);


--
-- TOC entry 1691 (class 1259 OID 27336)
-- Dependencies: 5
-- Name: jbpm_processinstance; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_processinstance (
    id_ bigint NOT NULL,
    version_ integer NOT NULL,
    key_ character varying(255),
    start_ timestamp without time zone,
    end_ timestamp without time zone,
    issuspended_ boolean,
    processdefinition_ bigint,
    roottoken_ bigint,
    superprocesstoken_ bigint
);


--
-- TOC entry 1692 (class 1259 OID 27341)
-- Dependencies: 5
-- Name: jbpm_runtimeaction; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_runtimeaction (
    id_ bigint NOT NULL,
    version_ integer NOT NULL,
    eventtype_ character varying(255),
    type_ character(1),
    graphelement_ bigint,
    processinstance_ bigint,
    action_ bigint,
    processinstanceindex_ integer
);


--
-- TOC entry 1693 (class 1259 OID 27346)
-- Dependencies: 5
-- Name: jbpm_swimlane; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_swimlane (
    id_ bigint NOT NULL,
    name_ character varying(255),
    actoridexpression_ character varying(255),
    pooledactorsexpression_ character varying(255),
    assignmentdelegation_ bigint,
    taskmgmtdefinition_ bigint
);


--
-- TOC entry 1694 (class 1259 OID 27351)
-- Dependencies: 5
-- Name: jbpm_swimlaneinstance; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_swimlaneinstance (
    id_ bigint NOT NULL,
    version_ integer NOT NULL,
    name_ character varying(255),
    actorid_ character varying(255),
    swimlane_ bigint,
    taskmgmtinstance_ bigint
);


--
-- TOC entry 1695 (class 1259 OID 27356)
-- Dependencies: 5
-- Name: jbpm_task; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_task (
    id_ bigint NOT NULL,
    name_ character varying(255),
    description_ text,
    processdefinition_ bigint,
    isblocking_ boolean,
    issignalling_ boolean,
    condition_ character varying(255),
    duedate_ character varying(255),
    priority_ integer,
    actoridexpression_ character varying(255),
    pooledactorsexpression_ character varying(255),
    taskmgmtdefinition_ bigint,
    tasknode_ bigint,
    startstate_ bigint,
    assignmentdelegation_ bigint,
    swimlane_ bigint,
    taskcontroller_ bigint
);


--
-- TOC entry 1696 (class 1259 OID 27364)
-- Dependencies: 5
-- Name: jbpm_taskactorpool; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_taskactorpool (
    taskinstance_ bigint NOT NULL,
    pooledactor_ bigint NOT NULL
);


--
-- TOC entry 1697 (class 1259 OID 27369)
-- Dependencies: 5
-- Name: jbpm_taskcontroller; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_taskcontroller (
    id_ bigint NOT NULL,
    taskcontrollerdelegation_ bigint
);


--
-- TOC entry 1698 (class 1259 OID 27374)
-- Dependencies: 5
-- Name: jbpm_taskinstance; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_taskinstance (
    id_ bigint NOT NULL,
    class_ character(1) NOT NULL,
    version_ integer NOT NULL,
    name_ character varying(255),
    description_ text,
    actorid_ character varying(255),
    create_ timestamp without time zone,
    start_ timestamp without time zone,
    end_ timestamp without time zone,
    duedate_ timestamp without time zone,
    priority_ integer,
    iscancelled_ boolean,
    issuspended_ boolean,
    isopen_ boolean,
    issignalling_ boolean,
    isblocking_ boolean,
    task_ bigint,
    token_ bigint,
    procinst_ bigint,
    swimlaninstance_ bigint,
    taskmgmtinstance_ bigint
);


--
-- TOC entry 1699 (class 1259 OID 27382)
-- Dependencies: 5
-- Name: jbpm_token; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_token (
    id_ bigint NOT NULL,
    version_ integer NOT NULL,
    name_ character varying(255),
    start_ timestamp without time zone,
    end_ timestamp without time zone,
    nodeenter_ timestamp without time zone,
    nextlogindex_ integer,
    isabletoreactivateparent_ boolean,
    isterminationimplicit_ boolean,
    issuspended_ boolean,
    lock_ character varying(255),
    node_ bigint,
    processinstance_ bigint,
    parent_ bigint,
    subprocessinstance_ bigint
);


--
-- TOC entry 1700 (class 1259 OID 27387)
-- Dependencies: 5
-- Name: jbpm_tokenvariablemap; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_tokenvariablemap (
    id_ bigint NOT NULL,
    version_ integer NOT NULL,
    token_ bigint,
    contextinstance_ bigint
);


--
-- TOC entry 1701 (class 1259 OID 27392)
-- Dependencies: 5
-- Name: jbpm_transition; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_transition (
    id_ bigint NOT NULL,
    name_ character varying(255),
    description_ text,
    processdefinition_ bigint,
    from_ bigint,
    to_ bigint,
    condition_ character varying(255),
    fromindex_ integer
);


--
-- TOC entry 1702 (class 1259 OID 27400)
-- Dependencies: 5
-- Name: jbpm_variableaccess; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_variableaccess (
    id_ bigint NOT NULL,
    variablename_ character varying(255),
    access_ character varying(255),
    mappedname_ character varying(255),
    script_ bigint,
    processstate_ bigint,
    taskcontroller_ bigint,
    index_ integer
);


--
-- TOC entry 1703 (class 1259 OID 27405)
-- Dependencies: 5
-- Name: jbpm_variableinstance; Type: TABLE; Schema: public; Owner: -; Tablespace: 
--

CREATE TABLE jbpm_variableinstance (
    id_ bigint NOT NULL,
    class_ character(1) NOT NULL,
    version_ integer NOT NULL,
    name_ character varying(255),
    converter_ character(1),
    token_ bigint,
    tokenvariablemap_ bigint,
    processinstance_ bigint,
    bytearrayvalue_ bigint,
    datevalue_ timestamp without time zone,
    doublevalue_ double precision,
    longidclass_ character varying(255),
    longvalue_ bigint,
    stringidclass_ character varying(255),
    stringvalue_ character varying(255),
    taskinstance_ bigint
);


--
-- TOC entry 2095 (class 2606 OID 27416)
-- Dependencies: 1704 1704 1704
-- Name: hel_accio_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_accio
    ADD CONSTRAINT hel_accio_codi_key UNIQUE (codi, definicio_proces_id);


--
-- TOC entry 2097 (class 2606 OID 27414)
-- Dependencies: 1704 1704
-- Name: hel_accio_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_accio
    ADD CONSTRAINT hel_accio_pkey PRIMARY KEY (id);


--
-- TOC entry 2099 (class 2606 OID 27423)
-- Dependencies: 1705 1705
-- Name: hel_acl_class_class_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_acl_class
    ADD CONSTRAINT hel_acl_class_class_key UNIQUE (class);


--
-- TOC entry 2101 (class 2606 OID 27421)
-- Dependencies: 1705 1705
-- Name: hel_acl_class_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_acl_class
    ADD CONSTRAINT hel_acl_class_pkey PRIMARY KEY (id);


--
-- TOC entry 2103 (class 2606 OID 27428)
-- Dependencies: 1706 1706
-- Name: hel_acl_entry_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_acl_entry
    ADD CONSTRAINT hel_acl_entry_pkey PRIMARY KEY (id);


--
-- TOC entry 2105 (class 2606 OID 27433)
-- Dependencies: 1707 1707
-- Name: hel_acl_object_identity_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_acl_object_identity
    ADD CONSTRAINT hel_acl_object_identity_pkey PRIMARY KEY (id);


--
-- TOC entry 2107 (class 2606 OID 27438)
-- Dependencies: 1708 1708
-- Name: hel_acl_sid_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_acl_sid
    ADD CONSTRAINT hel_acl_sid_pkey PRIMARY KEY (id);


--
-- TOC entry 2109 (class 2606 OID 27443)
-- Dependencies: 1709 1709
-- Name: hel_action_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_action_log
    ADD CONSTRAINT hel_action_log_pkey PRIMARY KEY (id);


--
-- TOC entry 2111 (class 2606 OID 27448)
-- Dependencies: 1710 1710
-- Name: hel_alerta_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_alerta
    ADD CONSTRAINT hel_alerta_pkey PRIMARY KEY (id);


--
-- TOC entry 2113 (class 2606 OID 27455)
-- Dependencies: 1711 1711 1711
-- Name: hel_area_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_area
    ADD CONSTRAINT hel_area_codi_key UNIQUE (codi, entorn_id);


--
-- TOC entry 2117 (class 2606 OID 27460)
-- Dependencies: 1712 1712
-- Name: hel_area_jbpmid_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_area_jbpmid
    ADD CONSTRAINT hel_area_jbpmid_pkey PRIMARY KEY (id);


--
-- TOC entry 2119 (class 2606 OID 27467)
-- Dependencies: 1713 1713 1713
-- Name: hel_area_membre_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_area_membre
    ADD CONSTRAINT hel_area_membre_codi_key UNIQUE (codi, area_id);


--
-- TOC entry 2121 (class 2606 OID 27465)
-- Dependencies: 1713 1713
-- Name: hel_area_membre_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_area_membre
    ADD CONSTRAINT hel_area_membre_pkey PRIMARY KEY (id);


--
-- TOC entry 2115 (class 2606 OID 27453)
-- Dependencies: 1711 1711
-- Name: hel_area_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_area
    ADD CONSTRAINT hel_area_pkey PRIMARY KEY (id);


--
-- TOC entry 2123 (class 2606 OID 27474)
-- Dependencies: 1714 1714 1714
-- Name: hel_area_tipus_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_area_tipus
    ADD CONSTRAINT hel_area_tipus_codi_key UNIQUE (codi, entorn_id);


--
-- TOC entry 2125 (class 2606 OID 27472)
-- Dependencies: 1714 1714
-- Name: hel_area_tipus_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_area_tipus
    ADD CONSTRAINT hel_area_tipus_pkey PRIMARY KEY (id);


--
-- TOC entry 2131 (class 2606 OID 27488)
-- Dependencies: 1716 1716 1716
-- Name: hel_camp_agrup_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_camp_agrup
    ADD CONSTRAINT hel_camp_agrup_codi_key UNIQUE (codi, definicio_proces_id);


--
-- TOC entry 2133 (class 2606 OID 27486)
-- Dependencies: 1716 1716
-- Name: hel_camp_agrup_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_camp_agrup
    ADD CONSTRAINT hel_camp_agrup_pkey PRIMARY KEY (id);


--
-- TOC entry 2127 (class 2606 OID 27481)
-- Dependencies: 1715 1715 1715
-- Name: hel_camp_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_camp
    ADD CONSTRAINT hel_camp_codi_key UNIQUE (codi, definicio_proces_id);


--
-- TOC entry 2129 (class 2606 OID 27479)
-- Dependencies: 1715 1715
-- Name: hel_camp_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_camp
    ADD CONSTRAINT hel_camp_pkey PRIMARY KEY (id);


--
-- TOC entry 2135 (class 2606 OID 27493)
-- Dependencies: 1717 1717
-- Name: hel_camp_registre_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_camp_registre
    ADD CONSTRAINT hel_camp_registre_pkey PRIMARY KEY (id);


--
-- TOC entry 2137 (class 2606 OID 27495)
-- Dependencies: 1717 1717 1717
-- Name: hel_camp_registre_registre_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_camp_registre
    ADD CONSTRAINT hel_camp_registre_registre_id_key UNIQUE (registre_id, ordre);


--
-- TOC entry 2139 (class 2606 OID 27497)
-- Dependencies: 1717 1717 1717
-- Name: hel_camp_registre_registre_id_key1; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_camp_registre
    ADD CONSTRAINT hel_camp_registre_registre_id_key1 UNIQUE (registre_id, membre_id);


--
-- TOC entry 2141 (class 2606 OID 27506)
-- Dependencies: 1718 1718 1718
-- Name: hel_camp_tasca_camp_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_camp_tasca
    ADD CONSTRAINT hel_camp_tasca_camp_id_key UNIQUE (camp_id, tasca_id);


--
-- TOC entry 2143 (class 2606 OID 27502)
-- Dependencies: 1718 1718
-- Name: hel_camp_tasca_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_camp_tasca
    ADD CONSTRAINT hel_camp_tasca_pkey PRIMARY KEY (id);


--
-- TOC entry 2145 (class 2606 OID 27504)
-- Dependencies: 1718 1718 1718
-- Name: hel_camp_tasca_tasca_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_camp_tasca
    ADD CONSTRAINT hel_camp_tasca_tasca_id_key UNIQUE (tasca_id, ordre);


--
-- TOC entry 2147 (class 2606 OID 27513)
-- Dependencies: 1719 1719 1719
-- Name: hel_carrec_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_carrec
    ADD CONSTRAINT hel_carrec_codi_key UNIQUE (codi, entorn_id);


--
-- TOC entry 2151 (class 2606 OID 27518)
-- Dependencies: 1720 1720
-- Name: hel_carrec_jbpmid_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_carrec_jbpmid
    ADD CONSTRAINT hel_carrec_jbpmid_pkey PRIMARY KEY (id);


--
-- TOC entry 2149 (class 2606 OID 27511)
-- Dependencies: 1719 1719
-- Name: hel_carrec_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_carrec
    ADD CONSTRAINT hel_carrec_pkey PRIMARY KEY (id);


--
-- TOC entry 2157 (class 2606 OID 27532)
-- Dependencies: 1722 1722 1722 1722 1722 1722
-- Name: hel_consulta_camp_consulta_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_consulta_camp
    ADD CONSTRAINT hel_consulta_camp_consulta_id_key UNIQUE (consulta_id, camp_codi, defproc_jbpmkey, defproc_versio, tipus);


--
-- TOC entry 2159 (class 2606 OID 27530)
-- Dependencies: 1722 1722
-- Name: hel_consulta_camp_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_consulta_camp
    ADD CONSTRAINT hel_consulta_camp_pkey PRIMARY KEY (id);


--
-- TOC entry 2153 (class 2606 OID 27525)
-- Dependencies: 1721 1721 1721
-- Name: hel_consulta_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_consulta
    ADD CONSTRAINT hel_consulta_codi_key UNIQUE (codi, entorn_id);


--
-- TOC entry 2155 (class 2606 OID 27523)
-- Dependencies: 1721 1721
-- Name: hel_consulta_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_consulta
    ADD CONSTRAINT hel_consulta_pkey PRIMARY KEY (id);


--
-- TOC entry 2161 (class 2606 OID 27537)
-- Dependencies: 1723 1723 1723
-- Name: hel_consulta_sub_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_consulta_sub
    ADD CONSTRAINT hel_consulta_sub_pkey PRIMARY KEY (pare_id, fill_id);


--
-- TOC entry 2163 (class 2606 OID 27544)
-- Dependencies: 1724 1724
-- Name: hel_definicio_proces_jbpm_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_definicio_proces
    ADD CONSTRAINT hel_definicio_proces_jbpm_id_key UNIQUE (jbpm_id);


--
-- TOC entry 2165 (class 2606 OID 27542)
-- Dependencies: 1724 1724
-- Name: hel_definicio_proces_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_definicio_proces
    ADD CONSTRAINT hel_definicio_proces_pkey PRIMARY KEY (id);


--
-- TOC entry 2167 (class 2606 OID 27551)
-- Dependencies: 1725 1725 1725
-- Name: hel_document_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_document
    ADD CONSTRAINT hel_document_codi_key UNIQUE (codi, definicio_proces_id);


--
-- TOC entry 2169 (class 2606 OID 27549)
-- Dependencies: 1725 1725
-- Name: hel_document_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_document
    ADD CONSTRAINT hel_document_pkey PRIMARY KEY (id);


--
-- TOC entry 2171 (class 2606 OID 27559)
-- Dependencies: 1726 1726
-- Name: hel_document_store_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_document_store
    ADD CONSTRAINT hel_document_store_pkey PRIMARY KEY (id);


--
-- TOC entry 2173 (class 2606 OID 27568)
-- Dependencies: 1727 1727 1727
-- Name: hel_document_tasca_document_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_document_tasca
    ADD CONSTRAINT hel_document_tasca_document_id_key UNIQUE (document_id, tasca_id);


--
-- TOC entry 2175 (class 2606 OID 27564)
-- Dependencies: 1727 1727
-- Name: hel_document_tasca_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_document_tasca
    ADD CONSTRAINT hel_document_tasca_pkey PRIMARY KEY (id);


--
-- TOC entry 2177 (class 2606 OID 27566)
-- Dependencies: 1727 1727 1727
-- Name: hel_document_tasca_tasca_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_document_tasca
    ADD CONSTRAINT hel_document_tasca_tasca_id_key UNIQUE (tasca_id, ordre);


--
-- TOC entry 2179 (class 2606 OID 27578)
-- Dependencies: 1728 1728 1728
-- Name: hel_domini_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_domini
    ADD CONSTRAINT hel_domini_codi_key UNIQUE (codi, entorn_id);


--
-- TOC entry 2181 (class 2606 OID 27576)
-- Dependencies: 1728 1728
-- Name: hel_domini_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_domini
    ADD CONSTRAINT hel_domini_pkey PRIMARY KEY (id);


--
-- TOC entry 2183 (class 2606 OID 27585)
-- Dependencies: 1729 1729
-- Name: hel_entorn_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_entorn
    ADD CONSTRAINT hel_entorn_codi_key UNIQUE (codi);


--
-- TOC entry 2185 (class 2606 OID 27583)
-- Dependencies: 1729 1729
-- Name: hel_entorn_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_entorn
    ADD CONSTRAINT hel_entorn_pkey PRIMARY KEY (id);


--
-- TOC entry 2187 (class 2606 OID 27595)
-- Dependencies: 1730 1730 1730
-- Name: hel_enumeracio_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_enumeracio
    ADD CONSTRAINT hel_enumeracio_codi_key UNIQUE (codi, entorn_id);


--
-- TOC entry 2189 (class 2606 OID 27593)
-- Dependencies: 1730 1730
-- Name: hel_enumeracio_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_enumeracio
    ADD CONSTRAINT hel_enumeracio_pkey PRIMARY KEY (id);


--
-- TOC entry 2191 (class 2606 OID 27600)
-- Dependencies: 1731 1731
-- Name: hel_enumeracio_valors_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_enumeracio_valors
    ADD CONSTRAINT hel_enumeracio_valors_pkey PRIMARY KEY (id);


--
-- TOC entry 2193 (class 2606 OID 27607)
-- Dependencies: 1732 1732 1732
-- Name: hel_estat_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_estat
    ADD CONSTRAINT hel_estat_codi_key UNIQUE (codi, expedient_tipus_id);


--
-- TOC entry 2195 (class 2606 OID 27605)
-- Dependencies: 1732 1732
-- Name: hel_estat_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_estat
    ADD CONSTRAINT hel_estat_pkey PRIMARY KEY (id);


--
-- TOC entry 2197 (class 2606 OID 27615)
-- Dependencies: 1733 1733
-- Name: hel_expedient_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_expedient
    ADD CONSTRAINT hel_expedient_pkey PRIMARY KEY (id);


--
-- TOC entry 2199 (class 2606 OID 27617)
-- Dependencies: 1733 1733
-- Name: hel_expedient_process_instance_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_expedient
    ADD CONSTRAINT hel_expedient_process_instance_id_key UNIQUE (process_instance_id);

ALTER TABLE ONLY hel_expedient_log
    ADD CONSTRAINT hel_expedient_log_pkey primary key (id);

--
-- TOC entry 2201 (class 2606 OID 27622)
-- Dependencies: 1734 1734 1734
-- Name: hel_expedient_rels_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_expedient_rels
    ADD CONSTRAINT hel_expedient_rels_pkey PRIMARY KEY (origen_id, desti_id);


--
-- TOC entry 2203 (class 2606 OID 27634)
-- Dependencies: 1735 1735 1735
-- Name: hel_expedient_tipus_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_expedient_tipus
    ADD CONSTRAINT hel_expedient_tipus_codi_key UNIQUE (codi, entorn_id);


--
-- TOC entry 2205 (class 2606 OID 27630)
-- Dependencies: 1735 1735
-- Name: hel_expedient_tipus_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_expedient_tipus
    ADD CONSTRAINT hel_expedient_tipus_pkey PRIMARY KEY (id);


--
-- TOC entry 2207 (class 2606 OID 27632)
-- Dependencies: 1735 1735
-- Name: hel_expedient_tipus_sistra_codtra_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_expedient_tipus
    ADD CONSTRAINT hel_expedient_tipus_sistra_codtra_key UNIQUE (sistra_codtra);


--
-- TOC entry 2209 (class 2606 OID 27641)
-- Dependencies: 1736 1736
-- Name: hel_festiu_data_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_festiu
    ADD CONSTRAINT hel_festiu_data_key UNIQUE (data);


--
-- TOC entry 2211 (class 2606 OID 27639)
-- Dependencies: 1736 1736
-- Name: hel_festiu_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_festiu
    ADD CONSTRAINT hel_festiu_pkey PRIMARY KEY (id);


--
-- TOC entry 2213 (class 2606 OID 27650)
-- Dependencies: 1737 1737 1737
-- Name: hel_firma_tasca_document_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_firma_tasca
    ADD CONSTRAINT hel_firma_tasca_document_id_key UNIQUE (document_id, tasca_id);


--
-- TOC entry 2215 (class 2606 OID 27646)
-- Dependencies: 1737 1737
-- Name: hel_firma_tasca_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_firma_tasca
    ADD CONSTRAINT hel_firma_tasca_pkey PRIMARY KEY (id);


--
-- TOC entry 2217 (class 2606 OID 27648)
-- Dependencies: 1737 1737 1737
-- Name: hel_firma_tasca_tasca_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_firma_tasca
    ADD CONSTRAINT hel_firma_tasca_tasca_id_key UNIQUE (tasca_id, ordre);


--
-- TOC entry 2219 (class 2606 OID 27657)
-- Dependencies: 1738 1738
-- Name: hel_formext_formid_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_formext
    ADD CONSTRAINT hel_formext_formid_key UNIQUE (formid);


--
-- TOC entry 2221 (class 2606 OID 27655)
-- Dependencies: 1738 1738
-- Name: hel_formext_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_formext
    ADD CONSTRAINT hel_formext_pkey PRIMARY KEY (id);


--
-- TOC entry 2223 (class 2606 OID 27664)
-- Dependencies: 1739 1739 1739
-- Name: hel_map_sistra_codihelium_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_map_sistra
    ADD CONSTRAINT hel_map_sistra_codihelium_key UNIQUE (codihelium, expedient_tipus_id);


--
-- TOC entry 2225 (class 2606 OID 27662)
-- Dependencies: 1739 1739
-- Name: hel_map_sistra_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_map_sistra
    ADD CONSTRAINT hel_map_sistra_pkey PRIMARY KEY (id);


--
-- TOC entry 2227 (class 2606 OID 27669)
-- Dependencies: 1740 1740
-- Name: hel_permis_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_permis
    ADD CONSTRAINT hel_permis_pkey PRIMARY KEY (codi);


--
-- TOC entry 2229 (class 2606 OID 27676)
-- Dependencies: 1741 1741
-- Name: hel_persona_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_persona
    ADD CONSTRAINT hel_persona_codi_key UNIQUE (codi);


--
-- TOC entry 2231 (class 2606 OID 27674)
-- Dependencies: 1741 1741
-- Name: hel_persona_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_persona
    ADD CONSTRAINT hel_persona_pkey PRIMARY KEY (id);


--
-- TOC entry 2233 (class 2606 OID 27683)
-- Dependencies: 1742 1742 1742
-- Name: hel_portasignatures_document_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_portasignatures
    ADD CONSTRAINT hel_portasignatures_document_id_key UNIQUE (document_id, token_id);


--
-- TOC entry 2235 (class 2606 OID 27681)
-- Dependencies: 1742 1742
-- Name: hel_portasignatures_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_portasignatures
    ADD CONSTRAINT hel_portasignatures_pkey PRIMARY KEY (id);


--
-- TOC entry 2237 (class 2606 OID 27688)
-- Dependencies: 1743 1743
-- Name: hel_redir_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_redir
    ADD CONSTRAINT hel_redir_pkey PRIMARY KEY (id);


--
-- TOC entry 2239 (class 2606 OID 27696)
-- Dependencies: 1744 1744
-- Name: hel_registre_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_registre
    ADD CONSTRAINT hel_registre_pkey PRIMARY KEY (id);


--
-- TOC entry 2241 (class 2606 OID 27706)
-- Dependencies: 1745 1745 1745
-- Name: hel_tasca_jbpm_name_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_tasca
    ADD CONSTRAINT hel_tasca_jbpm_name_key UNIQUE (jbpm_name, definicio_proces_id);


--
-- TOC entry 2243 (class 2606 OID 27704)
-- Dependencies: 1745 1745
-- Name: hel_tasca_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_tasca
    ADD CONSTRAINT hel_tasca_pkey PRIMARY KEY (id);


--
-- TOC entry 2245 (class 2606 OID 27713)
-- Dependencies: 1746 1746 1746
-- Name: hel_termini_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_termini
    ADD CONSTRAINT hel_termini_codi_key UNIQUE (codi, definicio_proces_id);


--
-- TOC entry 2249 (class 2606 OID 27718)
-- Dependencies: 1747 1747
-- Name: hel_termini_iniciat_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_termini_iniciat
    ADD CONSTRAINT hel_termini_iniciat_pkey PRIMARY KEY (id);


--
-- TOC entry 2251 (class 2606 OID 27720)
-- Dependencies: 1747 1747 1747
-- Name: hel_termini_iniciat_termini_id_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_termini_iniciat
    ADD CONSTRAINT hel_termini_iniciat_termini_id_key UNIQUE (termini_id, process_instance_id);


--
-- TOC entry 2247 (class 2606 OID 27711)
-- Dependencies: 1746 1746
-- Name: hel_termini_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_termini
    ADD CONSTRAINT hel_termini_pkey PRIMARY KEY (id);


--
-- TOC entry 2255 (class 2606 OID 27730)
-- Dependencies: 1749 1749 1749
-- Name: hel_usuari_permis_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_usuari_permis
    ADD CONSTRAINT hel_usuari_permis_pkey PRIMARY KEY (codi, permis);


--
-- TOC entry 2253 (class 2606 OID 27725)
-- Dependencies: 1748 1748
-- Name: hel_usuari_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_usuari
    ADD CONSTRAINT hel_usuari_pkey PRIMARY KEY (codi);


--
-- TOC entry 2257 (class 2606 OID 27735)
-- Dependencies: 1750 1750
-- Name: hel_usuari_prefs_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_usuari_prefs
    ADD CONSTRAINT hel_usuari_prefs_pkey PRIMARY KEY (codi);


--
-- TOC entry 2259 (class 2606 OID 27740)
-- Dependencies: 1751 1751
-- Name: hel_validacio_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_validacio
    ADD CONSTRAINT hel_validacio_pkey PRIMARY KEY (id);


--
-- TOC entry 2261 (class 2606 OID 27747)
-- Dependencies: 1752 1752
-- Name: hel_versio_codi_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_versio
    ADD CONSTRAINT hel_versio_codi_key UNIQUE (codi);


--
-- TOC entry 2263 (class 2606 OID 27749)
-- Dependencies: 1752 1752
-- Name: hel_versio_ordre_key; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_versio
    ADD CONSTRAINT hel_versio_ordre_key UNIQUE (ordre);


--
-- TOC entry 2265 (class 2606 OID 27745)
-- Dependencies: 1752 1752
-- Name: hel_versio_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY hel_versio
    ADD CONSTRAINT hel_versio_pkey PRIMARY KEY (id);


--
-- TOC entry 2033 (class 2606 OID 27223)
-- Dependencies: 1672 1672
-- Name: jbpm_action_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_action
    ADD CONSTRAINT jbpm_action_pkey PRIMARY KEY (id_);


--
-- TOC entry 2035 (class 2606 OID 27228)
-- Dependencies: 1673 1673
-- Name: jbpm_bytearray_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_bytearray
    ADD CONSTRAINT jbpm_bytearray_pkey PRIMARY KEY (id_);


--
-- TOC entry 2037 (class 2606 OID 27236)
-- Dependencies: 1674 1674 1674
-- Name: jbpm_byteblock_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_byteblock
    ADD CONSTRAINT jbpm_byteblock_pkey PRIMARY KEY (processfile_, index_);


--
-- TOC entry 2039 (class 2606 OID 27244)
-- Dependencies: 1675 1675
-- Name: jbpm_comment_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_comment
    ADD CONSTRAINT jbpm_comment_pkey PRIMARY KEY (id_);


--
-- TOC entry 2041 (class 2606 OID 27249)
-- Dependencies: 1676 1676 1676
-- Name: jbpm_decisionconditions_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_decisionconditions
    ADD CONSTRAINT jbpm_decisionconditions_pkey PRIMARY KEY (decision_, index_);


--
-- TOC entry 2043 (class 2606 OID 27257)
-- Dependencies: 1677 1677
-- Name: jbpm_delegation_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_delegation
    ADD CONSTRAINT jbpm_delegation_pkey PRIMARY KEY (id_);


--
-- TOC entry 2045 (class 2606 OID 27262)
-- Dependencies: 1678 1678
-- Name: jbpm_event_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_event
    ADD CONSTRAINT jbpm_event_pkey PRIMARY KEY (id_);


--
-- TOC entry 2047 (class 2606 OID 27270)
-- Dependencies: 1679 1679
-- Name: jbpm_exceptionhandler_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_exceptionhandler
    ADD CONSTRAINT jbpm_exceptionhandler_pkey PRIMARY KEY (id_);


--
-- TOC entry 2049 (class 2606 OID 27275)
-- Dependencies: 1680 1680
-- Name: jbpm_id_group_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_id_group
    ADD CONSTRAINT jbpm_id_group_pkey PRIMARY KEY (id_);


--
-- TOC entry 2051 (class 2606 OID 27280)
-- Dependencies: 1681 1681
-- Name: jbpm_id_membership_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_id_membership
    ADD CONSTRAINT jbpm_id_membership_pkey PRIMARY KEY (id_);


--
-- TOC entry 2053 (class 2606 OID 27288)
-- Dependencies: 1683 1683
-- Name: jbpm_id_user_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_id_user
    ADD CONSTRAINT jbpm_id_user_pkey PRIMARY KEY (id_);


--
-- TOC entry 2055 (class 2606 OID 27296)
-- Dependencies: 1684 1684
-- Name: jbpm_job_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_job
    ADD CONSTRAINT jbpm_job_pkey PRIMARY KEY (id_);


--
-- TOC entry 2057 (class 2606 OID 27304)
-- Dependencies: 1685 1685
-- Name: jbpm_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT jbpm_log_pkey PRIMARY KEY (id_);


--
-- TOC entry 2059 (class 2606 OID 27309)
-- Dependencies: 1686 1686
-- Name: jbpm_moduledefinition_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_moduledefinition
    ADD CONSTRAINT jbpm_moduledefinition_pkey PRIMARY KEY (id_);


--
-- TOC entry 2061 (class 2606 OID 27314)
-- Dependencies: 1687 1687
-- Name: jbpm_moduleinstance_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_moduleinstance
    ADD CONSTRAINT jbpm_moduleinstance_pkey PRIMARY KEY (id_);


--
-- TOC entry 2063 (class 2606 OID 27322)
-- Dependencies: 1688 1688
-- Name: jbpm_node_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_node
    ADD CONSTRAINT jbpm_node_pkey PRIMARY KEY (id_);


--
-- TOC entry 2065 (class 2606 OID 27327)
-- Dependencies: 1689 1689
-- Name: jbpm_pooledactor_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_pooledactor
    ADD CONSTRAINT jbpm_pooledactor_pkey PRIMARY KEY (id_);


--
-- TOC entry 2067 (class 2606 OID 27335)
-- Dependencies: 1690 1690
-- Name: jbpm_processdefinition_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_processdefinition
    ADD CONSTRAINT jbpm_processdefinition_pkey PRIMARY KEY (id_);


--
-- TOC entry 2069 (class 2606 OID 27340)
-- Dependencies: 1691 1691
-- Name: jbpm_processinstance_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_processinstance
    ADD CONSTRAINT jbpm_processinstance_pkey PRIMARY KEY (id_);


--
-- TOC entry 2071 (class 2606 OID 27345)
-- Dependencies: 1692 1692
-- Name: jbpm_runtimeaction_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_runtimeaction
    ADD CONSTRAINT jbpm_runtimeaction_pkey PRIMARY KEY (id_);


--
-- TOC entry 2073 (class 2606 OID 27350)
-- Dependencies: 1693 1693
-- Name: jbpm_swimlane_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_swimlane
    ADD CONSTRAINT jbpm_swimlane_pkey PRIMARY KEY (id_);


--
-- TOC entry 2075 (class 2606 OID 27355)
-- Dependencies: 1694 1694
-- Name: jbpm_swimlaneinstance_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_swimlaneinstance
    ADD CONSTRAINT jbpm_swimlaneinstance_pkey PRIMARY KEY (id_);


--
-- TOC entry 2077 (class 2606 OID 27363)
-- Dependencies: 1695 1695
-- Name: jbpm_task_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_task
    ADD CONSTRAINT jbpm_task_pkey PRIMARY KEY (id_);


--
-- TOC entry 2079 (class 2606 OID 27368)
-- Dependencies: 1696 1696 1696
-- Name: jbpm_taskactorpool_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_taskactorpool
    ADD CONSTRAINT jbpm_taskactorpool_pkey PRIMARY KEY (taskinstance_, pooledactor_);


--
-- TOC entry 2081 (class 2606 OID 27373)
-- Dependencies: 1697 1697
-- Name: jbpm_taskcontroller_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_taskcontroller
    ADD CONSTRAINT jbpm_taskcontroller_pkey PRIMARY KEY (id_);


--
-- TOC entry 2083 (class 2606 OID 27381)
-- Dependencies: 1698 1698
-- Name: jbpm_taskinstance_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_taskinstance
    ADD CONSTRAINT jbpm_taskinstance_pkey PRIMARY KEY (id_);


--
-- TOC entry 2085 (class 2606 OID 27386)
-- Dependencies: 1699 1699
-- Name: jbpm_token_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_token
    ADD CONSTRAINT jbpm_token_pkey PRIMARY KEY (id_);


--
-- TOC entry 2087 (class 2606 OID 27391)
-- Dependencies: 1700 1700
-- Name: jbpm_tokenvariablemap_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_tokenvariablemap
    ADD CONSTRAINT jbpm_tokenvariablemap_pkey PRIMARY KEY (id_);


--
-- TOC entry 2089 (class 2606 OID 27399)
-- Dependencies: 1701 1701
-- Name: jbpm_transition_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_transition
    ADD CONSTRAINT jbpm_transition_pkey PRIMARY KEY (id_);


--
-- TOC entry 2091 (class 2606 OID 27404)
-- Dependencies: 1702 1702
-- Name: jbpm_variableaccess_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_variableaccess
    ADD CONSTRAINT jbpm_variableaccess_pkey PRIMARY KEY (id_);


--
-- TOC entry 2093 (class 2606 OID 27409)
-- Dependencies: 1703 1703
-- Name: jbpm_variableinstance_pkey; Type: CONSTRAINT; Schema: public; Owner: -; Tablespace: 
--

ALTER TABLE ONLY jbpm_variableinstance
    ADD CONSTRAINT jbpm_variableinstance_pkey PRIMARY KEY (id_);


--
-- TOC entry 2270 (class 2606 OID 27770)
-- Dependencies: 1672 2042 1677
-- Name: fk_action_actndel; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_action
    ADD CONSTRAINT fk_action_actndel FOREIGN KEY (actiondelegation_) REFERENCES jbpm_delegation(id_);


--
-- TOC entry 2266 (class 2606 OID 27750)
-- Dependencies: 1672 1678 2044
-- Name: fk_action_event; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_action
    ADD CONSTRAINT fk_action_event FOREIGN KEY (event_) REFERENCES jbpm_event(id_);


--
-- TOC entry 2267 (class 2606 OID 27755)
-- Dependencies: 1672 2046 1679
-- Name: fk_action_expthdl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_action
    ADD CONSTRAINT fk_action_expthdl FOREIGN KEY (exceptionhandler_) REFERENCES jbpm_exceptionhandler(id_);


--
-- TOC entry 2268 (class 2606 OID 27760)
-- Dependencies: 1672 1690 2066
-- Name: fk_action_procdef; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_action
    ADD CONSTRAINT fk_action_procdef FOREIGN KEY (processdefinition_) REFERENCES jbpm_processdefinition(id_);


--
-- TOC entry 2271 (class 2606 OID 27775)
-- Dependencies: 2032 1672 1672
-- Name: fk_action_refact; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_action
    ADD CONSTRAINT fk_action_refact FOREIGN KEY (referencedaction_) REFERENCES jbpm_action(id_);


--
-- TOC entry 2272 (class 2606 OID 27780)
-- Dependencies: 2058 1673 1686
-- Name: fk_bytearr_fildef; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_bytearray
    ADD CONSTRAINT fk_bytearr_fildef FOREIGN KEY (filedefinition_) REFERENCES jbpm_moduledefinition(id_);


--
-- TOC entry 2273 (class 2606 OID 27785)
-- Dependencies: 1673 1674 2034
-- Name: fk_byteblock_file; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_byteblock
    ADD CONSTRAINT fk_byteblock_file FOREIGN KEY (processfile_) REFERENCES jbpm_bytearray(id_);


--
-- TOC entry 2355 (class 2606 OID 28195)
-- Dependencies: 1703 2034 1673
-- Name: fk_byteinst_array; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_variableinstance
    ADD CONSTRAINT fk_byteinst_array FOREIGN KEY (bytearrayvalue_) REFERENCES jbpm_bytearray(id_);


--
-- TOC entry 2274 (class 2606 OID 27790)
-- Dependencies: 1699 2084 1675
-- Name: fk_comment_token; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_comment
    ADD CONSTRAINT fk_comment_token FOREIGN KEY (token_) REFERENCES jbpm_token(id_);


--
-- TOC entry 2275 (class 2606 OID 27795)
-- Dependencies: 1675 2082 1698
-- Name: fk_comment_tsk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_comment
    ADD CONSTRAINT fk_comment_tsk FOREIGN KEY (taskinstance_) REFERENCES jbpm_taskinstance(id_);


--
-- TOC entry 2269 (class 2606 OID 27765)
-- Dependencies: 2032 1672 1672
-- Name: fk_crtetimeract_ta; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_action
    ADD CONSTRAINT fk_crtetimeract_ta FOREIGN KEY (timeraction_) REFERENCES jbpm_action(id_);


--
-- TOC entry 2276 (class 2606 OID 27800)
-- Dependencies: 2062 1676 1688
-- Name: fk_deccond_dec; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_decisionconditions
    ADD CONSTRAINT fk_deccond_dec FOREIGN KEY (decision_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2311 (class 2606 OID 27975)
-- Dependencies: 2042 1677 1688
-- Name: fk_decision_deleg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_node
    ADD CONSTRAINT fk_decision_deleg FOREIGN KEY (decisiondelegation) REFERENCES jbpm_delegation(id_);


--
-- TOC entry 2277 (class 2606 OID 27805)
-- Dependencies: 1677 1690 2066
-- Name: fk_delegation_prcd; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_delegation
    ADD CONSTRAINT fk_delegation_prcd FOREIGN KEY (processdefinition_) REFERENCES jbpm_processdefinition(id_);


--
-- TOC entry 2279 (class 2606 OID 27815)
-- Dependencies: 2062 1678 1688
-- Name: fk_event_node; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_event
    ADD CONSTRAINT fk_event_node FOREIGN KEY (node_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2278 (class 2606 OID 27810)
-- Dependencies: 1690 2066 1678
-- Name: fk_event_procdef; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_event
    ADD CONSTRAINT fk_event_procdef FOREIGN KEY (processdefinition_) REFERENCES jbpm_processdefinition(id_);


--
-- TOC entry 2281 (class 2606 OID 27825)
-- Dependencies: 1695 2076 1678
-- Name: fk_event_task; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_event
    ADD CONSTRAINT fk_event_task FOREIGN KEY (task_) REFERENCES jbpm_task(id_);


--
-- TOC entry 2280 (class 2606 OID 27820)
-- Dependencies: 2088 1678 1701
-- Name: fk_event_trans; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_event
    ADD CONSTRAINT fk_event_trans FOREIGN KEY (transition_) REFERENCES jbpm_transition(id_);


--
-- TOC entry 2282 (class 2606 OID 27830)
-- Dependencies: 1680 2048 1680
-- Name: fk_id_grp_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_id_group
    ADD CONSTRAINT fk_id_grp_parent FOREIGN KEY (parent_) REFERENCES jbpm_id_group(id_);


--
-- TOC entry 2283 (class 2606 OID 27835)
-- Dependencies: 2048 1680 1681
-- Name: fk_id_memship_grp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_id_membership
    ADD CONSTRAINT fk_id_memship_grp FOREIGN KEY (group_) REFERENCES jbpm_id_group(id_);


--
-- TOC entry 2284 (class 2606 OID 27840)
-- Dependencies: 1681 2052 1683
-- Name: fk_id_memship_usr; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_id_membership
    ADD CONSTRAINT fk_id_memship_usr FOREIGN KEY (user_) REFERENCES jbpm_id_user(id_);


--
-- TOC entry 2288 (class 2606 OID 27860)
-- Dependencies: 1684 1672 2032
-- Name: fk_job_action; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_job
    ADD CONSTRAINT fk_job_action FOREIGN KEY (action_) REFERENCES jbpm_action(id_);


--
-- TOC entry 2286 (class 2606 OID 27850)
-- Dependencies: 1688 2062 1684
-- Name: fk_job_node; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_job
    ADD CONSTRAINT fk_job_node FOREIGN KEY (node_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2287 (class 2606 OID 27855)
-- Dependencies: 1691 2068 1684
-- Name: fk_job_prinst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_job
    ADD CONSTRAINT fk_job_prinst FOREIGN KEY (processinstance_) REFERENCES jbpm_processinstance(id_);


--
-- TOC entry 2285 (class 2606 OID 27845)
-- Dependencies: 1699 2084 1684
-- Name: fk_job_token; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_job
    ADD CONSTRAINT fk_job_token FOREIGN KEY (token_) REFERENCES jbpm_token(id_);


--
-- TOC entry 2289 (class 2606 OID 27865)
-- Dependencies: 1684 1698 2082
-- Name: fk_job_tskinst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_job
    ADD CONSTRAINT fk_job_tskinst FOREIGN KEY (taskinstance_) REFERENCES jbpm_taskinstance(id_);


--
-- TOC entry 2300 (class 2606 OID 27920)
-- Dependencies: 1672 2032 1685
-- Name: fk_log_action; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT fk_log_action FOREIGN KEY (action_) REFERENCES jbpm_action(id_);


--
-- TOC entry 2294 (class 2606 OID 27890)
-- Dependencies: 1685 1699 2084
-- Name: fk_log_childtoken; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT fk_log_childtoken FOREIGN KEY (child_) REFERENCES jbpm_token(id_);


--
-- TOC entry 2295 (class 2606 OID 27895)
-- Dependencies: 1688 1685 2062
-- Name: fk_log_destnode; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT fk_log_destnode FOREIGN KEY (destinationnode_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2293 (class 2606 OID 27885)
-- Dependencies: 1673 1685 2034
-- Name: fk_log_newbytes; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT fk_log_newbytes FOREIGN KEY (newbytearray_) REFERENCES jbpm_bytearray(id_);


--
-- TOC entry 2299 (class 2606 OID 27915)
-- Dependencies: 2062 1688 1685
-- Name: fk_log_node; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT fk_log_node FOREIGN KEY (node_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2292 (class 2606 OID 27880)
-- Dependencies: 2034 1685 1673
-- Name: fk_log_oldbytes; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT fk_log_oldbytes FOREIGN KEY (oldbytearray_) REFERENCES jbpm_bytearray(id_);


--
-- TOC entry 2298 (class 2606 OID 27910)
-- Dependencies: 2056 1685 1685
-- Name: fk_log_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT fk_log_parent FOREIGN KEY (parent_) REFERENCES jbpm_log(id_);


--
-- TOC entry 2290 (class 2606 OID 27870)
-- Dependencies: 2062 1688 1685
-- Name: fk_log_sourcenode; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT fk_log_sourcenode FOREIGN KEY (sourcenode_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2297 (class 2606 OID 27905)
-- Dependencies: 1694 2074 1685
-- Name: fk_log_swiminst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT fk_log_swiminst FOREIGN KEY (swimlaneinstance_) REFERENCES jbpm_swimlaneinstance(id_);


--
-- TOC entry 2296 (class 2606 OID 27900)
-- Dependencies: 1685 1698 2082
-- Name: fk_log_taskinst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT fk_log_taskinst FOREIGN KEY (taskinstance_) REFERENCES jbpm_taskinstance(id_);


--
-- TOC entry 2291 (class 2606 OID 27875)
-- Dependencies: 1699 1685 2084
-- Name: fk_log_token; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT fk_log_token FOREIGN KEY (token_) REFERENCES jbpm_token(id_);


--
-- TOC entry 2302 (class 2606 OID 27930)
-- Dependencies: 2088 1685 1701
-- Name: fk_log_transition; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT fk_log_transition FOREIGN KEY (transition_) REFERENCES jbpm_transition(id_);


--
-- TOC entry 2301 (class 2606 OID 27925)
-- Dependencies: 2092 1685 1703
-- Name: fk_log_varinst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_log
    ADD CONSTRAINT fk_log_varinst FOREIGN KEY (variableinstance_) REFERENCES jbpm_variableinstance(id_);


--
-- TOC entry 2304 (class 2606 OID 27940)
-- Dependencies: 1690 2066 1686
-- Name: fk_moddef_procdef; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_moduledefinition
    ADD CONSTRAINT fk_moddef_procdef FOREIGN KEY (processdefinition_) REFERENCES jbpm_processdefinition(id_);


--
-- TOC entry 2306 (class 2606 OID 27950)
-- Dependencies: 2068 1691 1687
-- Name: fk_modinst_prcinst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_moduleinstance
    ADD CONSTRAINT fk_modinst_prcinst FOREIGN KEY (processinstance_) REFERENCES jbpm_processinstance(id_);


--
-- TOC entry 2310 (class 2606 OID 27970)
-- Dependencies: 2032 1672 1688
-- Name: fk_node_action; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_node
    ADD CONSTRAINT fk_node_action FOREIGN KEY (action_) REFERENCES jbpm_action(id_);


--
-- TOC entry 2308 (class 2606 OID 27960)
-- Dependencies: 2066 1690 1688
-- Name: fk_node_procdef; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_node
    ADD CONSTRAINT fk_node_procdef FOREIGN KEY (processdefinition_) REFERENCES jbpm_processdefinition(id_);


--
-- TOC entry 2309 (class 2606 OID 27965)
-- Dependencies: 1688 2032 1672
-- Name: fk_node_script; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_node
    ADD CONSTRAINT fk_node_script FOREIGN KEY (script_) REFERENCES jbpm_action(id_);


--
-- TOC entry 2312 (class 2606 OID 27980)
-- Dependencies: 1688 2062 1688
-- Name: fk_node_superstate; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_node
    ADD CONSTRAINT fk_node_superstate FOREIGN KEY (superstate_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2313 (class 2606 OID 27985)
-- Dependencies: 1694 2074 1689
-- Name: fk_pooledactor_sli; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_pooledactor
    ADD CONSTRAINT fk_pooledactor_sli FOREIGN KEY (swimlaneinstance_) REFERENCES jbpm_swimlaneinstance(id_);


--
-- TOC entry 2314 (class 2606 OID 27990)
-- Dependencies: 2062 1688 1690
-- Name: fk_procdef_strtsta; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_processdefinition
    ADD CONSTRAINT fk_procdef_strtsta FOREIGN KEY (startstate_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2315 (class 2606 OID 27995)
-- Dependencies: 1690 1691 2066
-- Name: fk_procin_procdef; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_processinstance
    ADD CONSTRAINT fk_procin_procdef FOREIGN KEY (processdefinition_) REFERENCES jbpm_processdefinition(id_);


--
-- TOC entry 2316 (class 2606 OID 28000)
-- Dependencies: 1691 1699 2084
-- Name: fk_procin_roottkn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_processinstance
    ADD CONSTRAINT fk_procin_roottkn FOREIGN KEY (roottoken_) REFERENCES jbpm_token(id_);


--
-- TOC entry 2317 (class 2606 OID 28005)
-- Dependencies: 1691 2084 1699
-- Name: fk_procin_sproctkn; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_processinstance
    ADD CONSTRAINT fk_procin_sproctkn FOREIGN KEY (superprocesstoken_) REFERENCES jbpm_token(id_);


--
-- TOC entry 2307 (class 2606 OID 27955)
-- Dependencies: 2066 1688 1690
-- Name: fk_procst_sbprcdef; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_node
    ADD CONSTRAINT fk_procst_sbprcdef FOREIGN KEY (subprocessdefinition_) REFERENCES jbpm_processdefinition(id_);


--
-- TOC entry 2319 (class 2606 OID 28015)
-- Dependencies: 2032 1672 1692
-- Name: fk_rtactn_action; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_runtimeaction
    ADD CONSTRAINT fk_rtactn_action FOREIGN KEY (action_) REFERENCES jbpm_action(id_);


--
-- TOC entry 2318 (class 2606 OID 28010)
-- Dependencies: 1691 1692 2068
-- Name: fk_rtactn_procinst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_runtimeaction
    ADD CONSTRAINT fk_rtactn_procinst FOREIGN KEY (processinstance_) REFERENCES jbpm_processinstance(id_);


--
-- TOC entry 2323 (class 2606 OID 28035)
-- Dependencies: 1693 1694 2072
-- Name: fk_swimlaneinst_sl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_swimlaneinstance
    ADD CONSTRAINT fk_swimlaneinst_sl FOREIGN KEY (swimlane_) REFERENCES jbpm_swimlane(id_);


--
-- TOC entry 2322 (class 2606 OID 28030)
-- Dependencies: 1694 2060 1687
-- Name: fk_swimlaneinst_tm; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_swimlaneinstance
    ADD CONSTRAINT fk_swimlaneinst_tm FOREIGN KEY (taskmgmtinstance_) REFERENCES jbpm_moduleinstance(id_);


--
-- TOC entry 2320 (class 2606 OID 28020)
-- Dependencies: 2042 1693 1677
-- Name: fk_swl_assdel; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_swimlane
    ADD CONSTRAINT fk_swl_assdel FOREIGN KEY (assignmentdelegation_) REFERENCES jbpm_delegation(id_);


--
-- TOC entry 2321 (class 2606 OID 28025)
-- Dependencies: 1686 1693 2058
-- Name: fk_swl_tskmgmtdef; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_swimlane
    ADD CONSTRAINT fk_swl_tskmgmtdef FOREIGN KEY (taskmgmtdefinition_) REFERENCES jbpm_moduledefinition(id_);


--
-- TOC entry 2325 (class 2606 OID 28045)
-- Dependencies: 2042 1677 1695
-- Name: fk_task_assdel; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_task
    ADD CONSTRAINT fk_task_assdel FOREIGN KEY (assignmentdelegation_) REFERENCES jbpm_delegation(id_);


--
-- TOC entry 2327 (class 2606 OID 28055)
-- Dependencies: 2066 1690 1695
-- Name: fk_task_procdef; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_task
    ADD CONSTRAINT fk_task_procdef FOREIGN KEY (processdefinition_) REFERENCES jbpm_processdefinition(id_);


--
-- TOC entry 2328 (class 2606 OID 28060)
-- Dependencies: 1695 2062 1688
-- Name: fk_task_startst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_task
    ADD CONSTRAINT fk_task_startst FOREIGN KEY (startstate_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2330 (class 2606 OID 28070)
-- Dependencies: 1693 2072 1695
-- Name: fk_task_swimlane; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_task
    ADD CONSTRAINT fk_task_swimlane FOREIGN KEY (swimlane_) REFERENCES jbpm_swimlane(id_);


--
-- TOC entry 2329 (class 2606 OID 28065)
-- Dependencies: 1686 1695 2058
-- Name: fk_task_taskmgtdef; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_task
    ADD CONSTRAINT fk_task_taskmgtdef FOREIGN KEY (taskmgmtdefinition_) REFERENCES jbpm_moduledefinition(id_);


--
-- TOC entry 2326 (class 2606 OID 28050)
-- Dependencies: 2062 1688 1695
-- Name: fk_task_tasknode; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_task
    ADD CONSTRAINT fk_task_tasknode FOREIGN KEY (tasknode_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2332 (class 2606 OID 28080)
-- Dependencies: 1696 1698 2082
-- Name: fk_taskactpl_tski; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_taskactorpool
    ADD CONSTRAINT fk_taskactpl_tski FOREIGN KEY (taskinstance_) REFERENCES jbpm_taskinstance(id_);


--
-- TOC entry 2337 (class 2606 OID 28105)
-- Dependencies: 2074 1694 1698
-- Name: fk_taskinst_slinst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_taskinstance
    ADD CONSTRAINT fk_taskinst_slinst FOREIGN KEY (swimlaninstance_) REFERENCES jbpm_swimlaneinstance(id_);


--
-- TOC entry 2338 (class 2606 OID 28110)
-- Dependencies: 1695 1698 2076
-- Name: fk_taskinst_task; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_taskinstance
    ADD CONSTRAINT fk_taskinst_task FOREIGN KEY (task_) REFERENCES jbpm_task(id_);


--
-- TOC entry 2335 (class 2606 OID 28095)
-- Dependencies: 2060 1687 1698
-- Name: fk_taskinst_tminst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_taskinstance
    ADD CONSTRAINT fk_taskinst_tminst FOREIGN KEY (taskmgmtinstance_) REFERENCES jbpm_moduleinstance(id_);


--
-- TOC entry 2336 (class 2606 OID 28100)
-- Dependencies: 2084 1699 1698
-- Name: fk_taskinst_token; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_taskinstance
    ADD CONSTRAINT fk_taskinst_token FOREIGN KEY (token_) REFERENCES jbpm_token(id_);


--
-- TOC entry 2305 (class 2606 OID 27945)
-- Dependencies: 1687 2058 1686
-- Name: fk_taskmgtinst_tmd; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_moduleinstance
    ADD CONSTRAINT fk_taskmgtinst_tmd FOREIGN KEY (taskmgmtdefinition_) REFERENCES jbpm_moduledefinition(id_);


--
-- TOC entry 2343 (class 2606 OID 28135)
-- Dependencies: 1700 2060 1687
-- Name: fk_tkvarmap_ctxt; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_tokenvariablemap
    ADD CONSTRAINT fk_tkvarmap_ctxt FOREIGN KEY (contextinstance_) REFERENCES jbpm_moduleinstance(id_);


--
-- TOC entry 2344 (class 2606 OID 28140)
-- Dependencies: 2084 1700 1699
-- Name: fk_tkvarmap_token; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_tokenvariablemap
    ADD CONSTRAINT fk_tkvarmap_token FOREIGN KEY (token_) REFERENCES jbpm_token(id_);


--
-- TOC entry 2340 (class 2606 OID 28120)
-- Dependencies: 1688 2062 1699
-- Name: fk_token_node; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_token
    ADD CONSTRAINT fk_token_node FOREIGN KEY (node_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2339 (class 2606 OID 28115)
-- Dependencies: 1699 2084 1699
-- Name: fk_token_parent; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_token
    ADD CONSTRAINT fk_token_parent FOREIGN KEY (parent_) REFERENCES jbpm_token(id_);


--
-- TOC entry 2341 (class 2606 OID 28125)
-- Dependencies: 2068 1691 1699
-- Name: fk_token_procinst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_token
    ADD CONSTRAINT fk_token_procinst FOREIGN KEY (processinstance_) REFERENCES jbpm_processinstance(id_);


--
-- TOC entry 2342 (class 2606 OID 28130)
-- Dependencies: 1691 1699 2068
-- Name: fk_token_subpi; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_token
    ADD CONSTRAINT fk_token_subpi FOREIGN KEY (subprocessinstance_) REFERENCES jbpm_processinstance(id_);


--
-- TOC entry 2346 (class 2606 OID 28150)
-- Dependencies: 2066 1690 1701
-- Name: fk_trans_procdef; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_transition
    ADD CONSTRAINT fk_trans_procdef FOREIGN KEY (processdefinition_) REFERENCES jbpm_processdefinition(id_);


--
-- TOC entry 2347 (class 2606 OID 28155)
-- Dependencies: 1688 2062 1701
-- Name: fk_transition_from; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_transition
    ADD CONSTRAINT fk_transition_from FOREIGN KEY (from_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2345 (class 2606 OID 28145)
-- Dependencies: 1701 1688 2062
-- Name: fk_transition_to; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_transition
    ADD CONSTRAINT fk_transition_to FOREIGN KEY (to_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2324 (class 2606 OID 28040)
-- Dependencies: 2080 1695 1697
-- Name: fk_tsk_tskctrl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_task
    ADD CONSTRAINT fk_tsk_tskctrl FOREIGN KEY (taskcontroller_) REFERENCES jbpm_taskcontroller(id_);


--
-- TOC entry 2331 (class 2606 OID 28075)
-- Dependencies: 1696 2064 1689
-- Name: fk_tskactpol_plact; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_taskactorpool
    ADD CONSTRAINT fk_tskactpol_plact FOREIGN KEY (pooledactor_) REFERENCES jbpm_pooledactor(id_);


--
-- TOC entry 2333 (class 2606 OID 28085)
-- Dependencies: 1697 2042 1677
-- Name: fk_tskctrl_deleg; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_taskcontroller
    ADD CONSTRAINT fk_tskctrl_deleg FOREIGN KEY (taskcontrollerdelegation_) REFERENCES jbpm_delegation(id_);


--
-- TOC entry 2303 (class 2606 OID 27935)
-- Dependencies: 1695 2076 1686
-- Name: fk_tskdef_start; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_moduledefinition
    ADD CONSTRAINT fk_tskdef_start FOREIGN KEY (starttask_) REFERENCES jbpm_task(id_);


--
-- TOC entry 2334 (class 2606 OID 28090)
-- Dependencies: 2068 1698 1691
-- Name: fk_tskins_prcins; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_taskinstance
    ADD CONSTRAINT fk_tskins_prcins FOREIGN KEY (procinst_) REFERENCES jbpm_processinstance(id_);


--
-- TOC entry 2354 (class 2606 OID 28190)
-- Dependencies: 1703 1698 2082
-- Name: fk_var_tskinst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_variableinstance
    ADD CONSTRAINT fk_var_tskinst FOREIGN KEY (taskinstance_) REFERENCES jbpm_taskinstance(id_);


--
-- TOC entry 2350 (class 2606 OID 28170)
-- Dependencies: 2062 1688 1702
-- Name: fk_varacc_procst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_variableaccess
    ADD CONSTRAINT fk_varacc_procst FOREIGN KEY (processstate_) REFERENCES jbpm_node(id_);


--
-- TOC entry 2349 (class 2606 OID 28165)
-- Dependencies: 2032 1702 1672
-- Name: fk_varacc_script; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_variableaccess
    ADD CONSTRAINT fk_varacc_script FOREIGN KEY (script_) REFERENCES jbpm_action(id_);


--
-- TOC entry 2348 (class 2606 OID 28160)
-- Dependencies: 1702 1697 2080
-- Name: fk_varacc_tskctrl; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_variableaccess
    ADD CONSTRAINT fk_varacc_tskctrl FOREIGN KEY (taskcontroller_) REFERENCES jbpm_taskcontroller(id_);


--
-- TOC entry 2353 (class 2606 OID 28185)
-- Dependencies: 2068 1691 1703
-- Name: fk_varinst_prcinst; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_variableinstance
    ADD CONSTRAINT fk_varinst_prcinst FOREIGN KEY (processinstance_) REFERENCES jbpm_processinstance(id_);


--
-- TOC entry 2351 (class 2606 OID 28175)
-- Dependencies: 1699 2084 1703
-- Name: fk_varinst_tk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_variableinstance
    ADD CONSTRAINT fk_varinst_tk FOREIGN KEY (token_) REFERENCES jbpm_token(id_);


--
-- TOC entry 2352 (class 2606 OID 28180)
-- Dependencies: 1700 2086 1703
-- Name: fk_varinst_tkvarmp; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY jbpm_variableinstance
    ADD CONSTRAINT fk_varinst_tkvarmp FOREIGN KEY (tokenvariablemap_) REFERENCES jbpm_tokenvariablemap(id_);


--
-- TOC entry 2361 (class 2606 OID 28225)
-- Dependencies: 1707 1705 2100
-- Name: hel_aclclass_aclobjid_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_acl_object_identity
    ADD CONSTRAINT hel_aclclass_aclobjid_fk FOREIGN KEY (object_id_class) REFERENCES hel_acl_class(id);


--
-- TOC entry 2357 (class 2606 OID 28205)
-- Dependencies: 1706 2104 1707
-- Name: hel_aclobjid_aclentry_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_acl_entry
    ADD CONSTRAINT hel_aclobjid_aclentry_fk FOREIGN KEY (acl_object_identity) REFERENCES hel_acl_object_identity(id);


--
-- TOC entry 2360 (class 2606 OID 28220)
-- Dependencies: 2104 1707 1707
-- Name: hel_aclobjid_aclobjid_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_acl_object_identity
    ADD CONSTRAINT hel_aclobjid_aclobjid_fk FOREIGN KEY (parent_object) REFERENCES hel_acl_object_identity(id);


--
-- TOC entry 2358 (class 2606 OID 28210)
-- Dependencies: 2106 1708 1706
-- Name: hel_aclsid_aclentry_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_acl_entry
    ADD CONSTRAINT hel_aclsid_aclentry_fk FOREIGN KEY (sid) REFERENCES hel_acl_sid(id);


--
-- TOC entry 2359 (class 2606 OID 28215)
-- Dependencies: 2106 1708 1707
-- Name: hel_aclsid_aclobjid_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_acl_object_identity
    ADD CONSTRAINT hel_aclsid_aclobjid_fk FOREIGN KEY (owner_sid) REFERENCES hel_acl_sid(id);


--
-- TOC entry 2366 (class 2606 OID 28250)
-- Dependencies: 1711 1711 2114
-- Name: hel_area_area_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_area
    ADD CONSTRAINT hel_area_area_fk FOREIGN KEY (pare_id) REFERENCES hel_area(id);


--
-- TOC entry 2368 (class 2606 OID 28260)
-- Dependencies: 1711 1713 2114
-- Name: hel_area_areamembre_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_area_membre
    ADD CONSTRAINT hel_area_areamembre_fk FOREIGN KEY (area_id) REFERENCES hel_area(id);


--
-- TOC entry 2380 (class 2606 OID 28320)
-- Dependencies: 1719 1711 2114
-- Name: hel_area_carrec_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_carrec
    ADD CONSTRAINT hel_area_carrec_fk FOREIGN KEY (area_id) REFERENCES hel_area(id);


--
-- TOC entry 2367 (class 2606 OID 28255)
-- Dependencies: 1711 1714 2124
-- Name: hel_areatipus_area_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_area
    ADD CONSTRAINT hel_areatipus_area_fk FOREIGN KEY (tipus_id) REFERENCES hel_area_tipus(id);


--
-- TOC entry 2378 (class 2606 OID 28310)
-- Dependencies: 1718 1715 2128
-- Name: hel_camp_camptasca_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_camp_tasca
    ADD CONSTRAINT hel_camp_camptasca_fk FOREIGN KEY (camp_id) REFERENCES hel_camp(id);


--
-- TOC entry 2389 (class 2606 OID 28365)
-- Dependencies: 1725 1715 2128
-- Name: hel_camp_document_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_document
    ADD CONSTRAINT hel_camp_document_fk FOREIGN KEY (camp_data_id) REFERENCES hel_camp(id);


--
-- TOC entry 2375 (class 2606 OID 28295)
-- Dependencies: 2128 1715 1717
-- Name: hel_camp_regmembre_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_camp_registre
    ADD CONSTRAINT hel_camp_regmembre_fk FOREIGN KEY (membre_id) REFERENCES hel_camp(id);


--
-- TOC entry 2376 (class 2606 OID 28300)
-- Dependencies: 2128 1715 1717
-- Name: hel_camp_regregistre_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_camp_registre
    ADD CONSTRAINT hel_camp_regregistre_fk FOREIGN KEY (registre_id) REFERENCES hel_camp(id);


--
-- TOC entry 2414 (class 2606 OID 28490)
-- Dependencies: 1751 1715 2128
-- Name: hel_camp_validacio_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_validacio
    ADD CONSTRAINT hel_camp_validacio_fk FOREIGN KEY (camp_id) REFERENCES hel_camp(id);


--
-- TOC entry 2372 (class 2606 OID 28280)
-- Dependencies: 2132 1716 1715
-- Name: hel_campagrup_camp_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_camp
    ADD CONSTRAINT hel_campagrup_camp_fk FOREIGN KEY (camp_agrupacio_id) REFERENCES hel_camp_agrup(id);


--
-- TOC entry 2383 (class 2606 OID 28335)
-- Dependencies: 1722 1721 2154
-- Name: hel_consulta_concamp_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_consulta_camp
    ADD CONSTRAINT hel_consulta_concamp_fk FOREIGN KEY (consulta_id) REFERENCES hel_consulta(id);


--
-- TOC entry 2356 (class 2606 OID 28200)
-- Dependencies: 1724 2164 1704
-- Name: hel_defproc_accio_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_accio
    ADD CONSTRAINT hel_defproc_accio_fk FOREIGN KEY (definicio_proces_id) REFERENCES hel_definicio_proces(id);


--
-- TOC entry 2371 (class 2606 OID 28275)
-- Dependencies: 1715 2164 1724
-- Name: hel_defproc_camp_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_camp
    ADD CONSTRAINT hel_defproc_camp_fk FOREIGN KEY (definicio_proces_id) REFERENCES hel_definicio_proces(id);


--
-- TOC entry 2374 (class 2606 OID 28290)
-- Dependencies: 2164 1724 1716
-- Name: hel_defproc_campagrup_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_camp_agrup
    ADD CONSTRAINT hel_defproc_campagrup_fk FOREIGN KEY (definicio_proces_id) REFERENCES hel_definicio_proces(id);


--
-- TOC entry 2388 (class 2606 OID 28360)
-- Dependencies: 1724 1725 2164
-- Name: hel_defproc_document_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_document
    ADD CONSTRAINT hel_defproc_document_fk FOREIGN KEY (definicio_proces_id) REFERENCES hel_definicio_proces(id);


--
-- TOC entry 2408 (class 2606 OID 28460)
-- Dependencies: 1745 1724 2164
-- Name: hel_defproc_tasca_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_tasca
    ADD CONSTRAINT hel_defproc_tasca_fk FOREIGN KEY (definicio_proces_id) REFERENCES hel_definicio_proces(id);


--
-- TOC entry 2409 (class 2606 OID 28465)
-- Dependencies: 1724 1746 2164
-- Name: hel_defproc_termini_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_termini
    ADD CONSTRAINT hel_defproc_termini_fk FOREIGN KEY (definicio_proces_id) REFERENCES hel_definicio_proces(id);


--
-- TOC entry 2401 (class 2606 OID 28425)
-- Dependencies: 1733 1734 2196
-- Name: hel_desti_exprel_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_expedient_rels
    ADD CONSTRAINT hel_desti_exprel_fk FOREIGN KEY (desti_id) REFERENCES hel_expedient(id);


--
-- TOC entry 2391 (class 2606 OID 28375)
-- Dependencies: 1725 1727 2168
-- Name: hel_document_doctasca_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_document_tasca
    ADD CONSTRAINT hel_document_doctasca_fk FOREIGN KEY (document_id) REFERENCES hel_document(id);


--
-- TOC entry 2405 (class 2606 OID 28445)
-- Dependencies: 2168 1737 1725
-- Name: hel_document_firtasca_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_firma_tasca
    ADD CONSTRAINT hel_document_firtasca_fk FOREIGN KEY (document_id) REFERENCES hel_document(id);


--
-- TOC entry 2370 (class 2606 OID 28270)
-- Dependencies: 2180 1715 1728
-- Name: hel_domini_camp_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_camp
    ADD CONSTRAINT hel_domini_camp_fk FOREIGN KEY (domini_id) REFERENCES hel_domini(id);


--
-- TOC entry 2362 (class 2606 OID 28230)
-- Dependencies: 1729 1710 2184
-- Name: hel_entorn_alerta_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_alerta
    ADD CONSTRAINT hel_entorn_alerta_fk FOREIGN KEY (entorn_id) REFERENCES hel_entorn(id);


--
-- TOC entry 2365 (class 2606 OID 28245)
-- Dependencies: 1711 1729 2184
-- Name: hel_entorn_area_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_area
    ADD CONSTRAINT hel_entorn_area_fk FOREIGN KEY (entorn_id) REFERENCES hel_entorn(id);


--
-- TOC entry 2369 (class 2606 OID 28265)
-- Dependencies: 2184 1729 1714
-- Name: hel_entorn_areatipus_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_area_tipus
    ADD CONSTRAINT hel_entorn_areatipus_fk FOREIGN KEY (entorn_id) REFERENCES hel_entorn(id);


--
-- TOC entry 2379 (class 2606 OID 28315)
-- Dependencies: 1719 1729 2184
-- Name: hel_entorn_carrec_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_carrec
    ADD CONSTRAINT hel_entorn_carrec_fk FOREIGN KEY (entorn_id) REFERENCES hel_entorn(id);


--
-- TOC entry 2381 (class 2606 OID 28325)
-- Dependencies: 2184 1729 1721
-- Name: hel_entorn_consulta_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_consulta
    ADD CONSTRAINT hel_entorn_consulta_fk FOREIGN KEY (entorn_id) REFERENCES hel_entorn(id);


--
-- TOC entry 2386 (class 2606 OID 28350)
-- Dependencies: 2184 1729 1724
-- Name: hel_entorn_defproc_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_definicio_proces
    ADD CONSTRAINT hel_entorn_defproc_fk FOREIGN KEY (entorn_id) REFERENCES hel_entorn(id);


--
-- TOC entry 2392 (class 2606 OID 28380)
-- Dependencies: 1729 2184 1728
-- Name: hel_entorn_domini_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_domini
    ADD CONSTRAINT hel_entorn_domini_fk FOREIGN KEY (entorn_id) REFERENCES hel_entorn(id);


--
-- TOC entry 2394 (class 2606 OID 28390)
-- Dependencies: 2184 1730 1729
-- Name: hel_entorn_enumeracio_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_enumeracio
    ADD CONSTRAINT hel_entorn_enumeracio_fk FOREIGN KEY (entorn_id) REFERENCES hel_entorn(id);


--
-- TOC entry 2398 (class 2606 OID 28410)
-- Dependencies: 1733 2184 1729
-- Name: hel_entorn_expedient_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_expedient
    ADD CONSTRAINT hel_entorn_expedient_fk FOREIGN KEY (entorn_id) REFERENCES hel_entorn(id);


ALTER TABLE ONLY hel_expedient_log
    ADD CONSTRAINT hel_expedient_logs_fk FOREIGN KEY (expedient_id) REFERENCES hel_expedient (id);


--
-- TOC entry 2403 (class 2606 OID 28435)
-- Dependencies: 1735 2184 1729
-- Name: hel_entorn_exptipus_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_expedient_tipus
    ADD CONSTRAINT hel_entorn_exptipus_fk FOREIGN KEY (entorn_id) REFERENCES hel_entorn(id);


--
-- TOC entry 2373 (class 2606 OID 28285)
-- Dependencies: 1730 2188 1715
-- Name: hel_enumeracio_camp_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_camp
    ADD CONSTRAINT hel_enumeracio_camp_fk FOREIGN KEY (enumeracio_id) REFERENCES hel_enumeracio(id);


--
-- TOC entry 2396 (class 2606 OID 28400)
-- Dependencies: 1730 2188 1731
-- Name: hel_enumeracio_valors_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_enumeracio_valors
    ADD CONSTRAINT hel_enumeracio_valors_fk FOREIGN KEY (enumeracio_id) REFERENCES hel_enumeracio(id);


--
-- TOC entry 2399 (class 2606 OID 28415)
-- Dependencies: 1732 2194 1733
-- Name: hel_estat_expedient_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_expedient
    ADD CONSTRAINT hel_estat_expedient_fk FOREIGN KEY (estat_id) REFERENCES hel_estat(id);


--
-- TOC entry 2363 (class 2606 OID 28235)
-- Dependencies: 1710 1733 2196
-- Name: hel_expedient_alerta_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_alerta
    ADD CONSTRAINT hel_expedient_alerta_fk FOREIGN KEY (expedient_id) REFERENCES hel_expedient(id);


--
-- TOC entry 2382 (class 2606 OID 28330)
-- Dependencies: 1721 1735 2204
-- Name: hel_exptip_consulta_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_consulta
    ADD CONSTRAINT hel_exptip_consulta_fk FOREIGN KEY (expedient_tipus_id) REFERENCES hel_expedient_tipus(id);


--
-- TOC entry 2387 (class 2606 OID 28355)
-- Dependencies: 2204 1735 1724
-- Name: hel_exptip_defproc_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_definicio_proces
    ADD CONSTRAINT hel_exptip_defproc_fk FOREIGN KEY (expedient_tipus_id) REFERENCES hel_expedient_tipus(id);


--
-- TOC entry 2393 (class 2606 OID 28385)
-- Dependencies: 1735 1728 2204
-- Name: hel_exptip_domini_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_domini
    ADD CONSTRAINT hel_exptip_domini_fk FOREIGN KEY (expedient_tipus_id) REFERENCES hel_expedient_tipus(id);


--
-- TOC entry 2395 (class 2606 OID 28395)
-- Dependencies: 2204 1730 1735
-- Name: hel_exptip_enumeracio_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_enumeracio
    ADD CONSTRAINT hel_exptip_enumeracio_fk FOREIGN KEY (expedient_tipus_id) REFERENCES hel_expedient_tipus(id);


--
-- TOC entry 2397 (class 2606 OID 28405)
-- Dependencies: 2204 1735 1732
-- Name: hel_exptipus_estat_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_estat
    ADD CONSTRAINT hel_exptipus_estat_fk FOREIGN KEY (expedient_tipus_id) REFERENCES hel_expedient_tipus(id);


--
-- TOC entry 2400 (class 2606 OID 28420)
-- Dependencies: 2204 1733 1735
-- Name: hel_exptipus_expedient_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_expedient
    ADD CONSTRAINT hel_exptipus_expedient_fk FOREIGN KEY (tipus_id) REFERENCES hel_expedient_tipus(id);


--
-- TOC entry 2406 (class 2606 OID 28450)
-- Dependencies: 1739 1735 2204
-- Name: hel_exptipus_map_sistra_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_map_sistra
    ADD CONSTRAINT hel_exptipus_map_sistra_fk FOREIGN KEY (expedient_tipus_id) REFERENCES hel_expedient_tipus(id);


--
-- TOC entry 2385 (class 2606 OID 28345)
-- Dependencies: 1721 1723 2154
-- Name: hel_fill_consultasub_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_consulta_sub
    ADD CONSTRAINT hel_fill_consultasub_fk FOREIGN KEY (pare_id) REFERENCES hel_consulta(id);


--
-- TOC entry 2402 (class 2606 OID 28430)
-- Dependencies: 1734 1733 2196
-- Name: hel_origen_exprel_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_expedient_rels
    ADD CONSTRAINT hel_origen_exprel_fk FOREIGN KEY (origen_id) REFERENCES hel_expedient(id);


--
-- TOC entry 2384 (class 2606 OID 28340)
-- Dependencies: 2154 1721 1723
-- Name: hel_pare_consultasub_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_consulta_sub
    ADD CONSTRAINT hel_pare_consultasub_fk FOREIGN KEY (fill_id) REFERENCES hel_consulta(id);


--
-- TOC entry 2411 (class 2606 OID 28475)
-- Dependencies: 2252 1748 1749
-- Name: hel_permis_usuari_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_usuari_permis
    ADD CONSTRAINT hel_permis_usuari_fk FOREIGN KEY (codi) REFERENCES hel_usuari(codi);


--
-- TOC entry 2407 (class 2606 OID 28455)
-- Dependencies: 2230 1741 1741
-- Name: hel_relleu_persona_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_persona
    ADD CONSTRAINT hel_relleu_persona_fk FOREIGN KEY (relleu_id) REFERENCES hel_persona(id);


--
-- TOC entry 2377 (class 2606 OID 28305)
-- Dependencies: 1718 1745 2242
-- Name: hel_tasca_camptasca_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_camp_tasca
    ADD CONSTRAINT hel_tasca_camptasca_fk FOREIGN KEY (tasca_id) REFERENCES hel_tasca(id);


--
-- TOC entry 2390 (class 2606 OID 28370)
-- Dependencies: 2242 1727 1745
-- Name: hel_tasca_doctasca_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_document_tasca
    ADD CONSTRAINT hel_tasca_doctasca_fk FOREIGN KEY (tasca_id) REFERENCES hel_tasca(id);


--
-- TOC entry 2404 (class 2606 OID 28440)
-- Dependencies: 1737 1745 2242
-- Name: hel_tasca_firtasca_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_firma_tasca
    ADD CONSTRAINT hel_tasca_firtasca_fk FOREIGN KEY (tasca_id) REFERENCES hel_tasca(id);


--
-- TOC entry 2413 (class 2606 OID 28485)
-- Dependencies: 1751 2242 1745
-- Name: hel_tasca_validacio_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_validacio
    ADD CONSTRAINT hel_tasca_validacio_fk FOREIGN KEY (tasca_id) REFERENCES hel_tasca(id);


--
-- TOC entry 2364 (class 2606 OID 28240)
-- Dependencies: 1710 1747 2248
-- Name: hel_termini_alerta_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_alerta
    ADD CONSTRAINT hel_termini_alerta_fk FOREIGN KEY (termini_iniciat_id) REFERENCES hel_termini_iniciat(id);


--
-- TOC entry 2410 (class 2606 OID 28470)
-- Dependencies: 1747 1746 2246
-- Name: hel_termini_terminic_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_termini_iniciat
    ADD CONSTRAINT hel_termini_terminic_fk FOREIGN KEY (termini_id) REFERENCES hel_termini(id);


--
-- TOC entry 2412 (class 2606 OID 28480)
-- Dependencies: 1740 1749 2226
-- Name: hel_usuari_permis_fk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY hel_usuari_permis
    ADD CONSTRAINT hel_usuari_permis_fk FOREIGN KEY (permis) REFERENCES hel_permis(codi);



-- Completed on 2012-03-28 13:29:47 CEST

--
-- PostgreSQL database dump complete
--

