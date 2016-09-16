CREATE TABLE HEL_ACCIO
(
  ID                   BIGINT               NOT NULL,
  CODI                 VARCHAR(64)        NOT NULL,
  CRON                 VARCHAR(255),
  DESCRIPCIO           VARCHAR(255),
  JBPM_ACTION          VARCHAR(255)       NOT NULL,
  NOM                  VARCHAR(255)       NOT NULL,
  OCULTA               BOOLEAN,
  PUBLICA              BOOLEAN,
  ROLS                 VARCHAR(2048),
  DEFINICIO_PROCES_ID  BIGINT               NOT NULL
);


CREATE TABLE HEL_ACL_CLASS
(
  ID     BIGSERIAL                        NOT NULL,
  CLASS  VARCHAR(255)                     NOT NULL
);


CREATE TABLE HEL_ACL_ENTRY
(
  ID                   BIGSERIAL               NOT NULL,
  ACE_ORDER            INT               NOT NULL,
  AUDIT_FAILURE        BOOLEAN                NOT NULL,
  AUDIT_SUCCESS        BOOLEAN                NOT NULL,
  GRANTING             BOOLEAN                NOT NULL,
  MASK                 INT               NOT NULL,
  ACL_OBJECT_IDENTITY  BIGINT,
  SID                  BIGINT
);


CREATE TABLE HEL_ACL_OBJECT_IDENTITY
(
  ID                  BIGSERIAL                NOT NULL,
  ENTRIES_INHERITING  BOOLEAN                 NOT NULL,
  OBJECT_ID_IDENTITY  BIGINT                NOT NULL,
  OBJECT_ID_CLASS     BIGINT,
  OWNER_SID           BIGINT,
  PARENT_OBJECT       BIGINT
);


CREATE TABLE HEL_ACL_SID
(
  ID         BIGSERIAL                         NOT NULL,
  PRINCIPAL  BOOLEAN,
  SID        VARCHAR(100)                 NOT NULL
);


CREATE TABLE HEL_ACTION_LOG
(
  ID          BIGINT                        NOT NULL,
  ACCIO       VARCHAR(255)                NOT NULL,
  COLUMNA_PK  VARCHAR(255)                NOT NULL,
  DATA        TIMESTAMP(6)                      NOT NULL,
  TAULA       VARCHAR(255)                NOT NULL,
  USUARI      VARCHAR(255)                NOT NULL,
  VALORS      VARCHAR(255)                NOT NULL
);


CREATE TABLE HEL_ALERTA
(
  ID                  BIGINT                NOT NULL,
  CAUSA               VARCHAR(255),
  DATA_CREACIO        TIMESTAMP(6)              NOT NULL,
  DATA_ELIMINACIO     TIMESTAMP(6),
  DATA_LECTURA        TIMESTAMP(6),
  DESTINATARI         VARCHAR(255)        NOT NULL,
  PRIORITAT           BIGINT,
  TEXT                VARCHAR(1024),
  ENTORN_ID           BIGINT                NOT NULL,
  EXPEDIENT_ID        BIGINT                NOT NULL,
  TERMINI_INICIAT_ID  BIGINT
);


CREATE TABLE HEL_AREA
(
  ID          BIGINT                        NOT NULL,
  CODI        VARCHAR(64)                 NOT NULL,
  DESCRIPCIO  VARCHAR(255),
  NOM         VARCHAR(255)                NOT NULL,
  ENTORN_ID   BIGINT                        NOT NULL,
  PARE_ID     BIGINT,
  TIPUS_ID    BIGINT                        NOT NULL
);


CREATE TABLE HEL_AREA_JBPMID
(
  ID          BIGINT                        NOT NULL,
  CODI        VARCHAR(64)                 NOT NULL,
  DESCRIPCIO  VARCHAR(255),
  NOM         VARCHAR(255)                NOT NULL
);


CREATE TABLE HEL_AREA_MEMBRE
(
  ID       BIGINT                           NOT NULL,
  CODI     VARCHAR(64)                    NOT NULL,
  AREA_ID  BIGINT                           NOT NULL
);


CREATE TABLE HEL_AREA_TIPUS
(
  ID          BIGINT                        NOT NULL,
  CODI        VARCHAR(64)                 NOT NULL,
  DESCRIPCIO  VARCHAR(255),
  NOM         VARCHAR(255)                NOT NULL,
  ENTORN_ID   BIGINT                        NOT NULL
);


CREATE TABLE HEL_CAMP
(
  ID                   BIGINT               NOT NULL,
  CODI                 VARCHAR(64)        NOT NULL,
  DOMINI_CACHE_TEXT    BOOLEAN,
  DOMINI_CAMP_TEXT     VARCHAR(64),
  DOMINI_CAMP_VALOR    VARCHAR(64),
  DOMINI_PARAMID       VARCHAR(255),
  DOMINI_PARAMS        VARCHAR(255),
  DOMINI_INTERN        BOOLEAN,
  CONSULTA_CAMP_TEXT   VARCHAR(256),
  CONSULTA_CAMP_VALOR  VARCHAR(256),
  CONSULTA_PARAMS      VARCHAR(1020),
  CONSULTA_ID          BIGINT,
  ETIQUETA             VARCHAR(255)       NOT NULL,
  JBPM_ACTION          VARCHAR(255),
  MULTIPLE             BOOLEAN,
  OBSERVACIONS         VARCHAR(255),
  OCULT                BOOLEAN,
  IGNORED              BOOLEAN,
  ORDRE                BIGINT,
  TIPUS                BIGINT               NOT NULL,
  CAMP_AGRUPACIO_ID    BIGINT,
  DEFINICIO_PROCES_ID  BIGINT               NOT NULL,
  DOMINI_ID            BIGINT,
  ENUMERACIO_ID        BIGINT
);


CREATE TABLE HEL_CAMP_AGRUP
(
  ID                   BIGINT               NOT NULL,
  CODI                 VARCHAR(64)        NOT NULL,
  DESCRIPCIO           VARCHAR(255),
  NOM                  VARCHAR(255)       NOT NULL,
  ORDRE                BIGINT,
  DEFINICIO_PROCES_ID  BIGINT               NOT NULL
);


CREATE TABLE HEL_CAMP_REGISTRE
(
  ID           BIGINT                       NOT NULL,
  LLISTAR      BOOLEAN,
  OBLIGATORI   BOOLEAN,
  ORDRE        BIGINT,
  MEMBRE_ID    BIGINT                       NOT NULL,
  REGISTRE_ID  BIGINT                       NOT NULL
);


CREATE TABLE HEL_CAMP_TASCA
(
  ID        BIGINT                          NOT NULL,
  ORDRE     BIGINT,
  RF        BOOLEAN,
  RO        BOOLEAN,
  RQ        BOOLEAN,
  WT        BOOLEAN,
  CAMP_ID   BIGINT                          NOT NULL,
  TASCA_ID  BIGINT                          NOT NULL
);


CREATE TABLE HEL_CARREC
(
  ID               BIGINT                   NOT NULL,
  CODI             VARCHAR(64)            NOT NULL,
  DESCRIPCIO       VARCHAR(255),
  NOM_DONA         VARCHAR(255)           NOT NULL,
  NOM_HOME         VARCHAR(255)           NOT NULL,
  PERSONA_CODI     VARCHAR(64),
  PERSONA_SEXE     BIGINT,
  CARREC_DONA      VARCHAR(255)           NOT NULL,
  TRACTAMENT_HOME  VARCHAR(255)           NOT NULL,
  AREA_ID          BIGINT                   NOT NULL,
  ENTORN_ID        BIGINT                   NOT NULL
);


CREATE TABLE HEL_CARREC_JBPMID
(
  ID               BIGINT                   NOT NULL,
  CODI             VARCHAR(64)            NOT NULL,
  DESCRIPCIO       VARCHAR(255),
  GRUP             VARCHAR(64)            NOT NULL,
  NOM_DONA         VARCHAR(255)           NOT NULL,
  NOM_HOME         VARCHAR(255)           NOT NULL,
  PERSONA_SEXE     BIGINT,
  CARREC_DONA      VARCHAR(255)           NOT NULL,
  TRACTAMENT_HOME  VARCHAR(255)           NOT NULL
);


CREATE TABLE HEL_CONSULTA
(
  ID                  BIGINT                NOT NULL,
  CODI                VARCHAR(64)         NOT NULL,
  DESCRIPCIO          VARCHAR(255),
  EXPORTAR_ACTIU      BOOLEAN,
  OCULTAR_ACTIU       BOOLEAN,
  FORMAT_EXPORTACIO   VARCHAR(16),
  GENERICA            BOOLEAN,
  INFORME_CONTINGUT   OID,
  INFORME_NOM         VARCHAR(255),
  NOM                 VARCHAR(255)        NOT NULL,
  ORDRE               BIGINT                NOT NULL,
  VALORS_PREDEF       VARCHAR(1024),
  ENTORN_ID           BIGINT                NOT NULL,
  EXPEDIENT_TIPUS_ID  BIGINT                NOT NULL
);


CREATE TABLE HEL_CONSULTA_CAMP
(
  ID               BIGINT                   NOT NULL,
  CAMP_CODI        VARCHAR(64)            NOT NULL,
  DEFPROC_JBPMKEY  VARCHAR(255),
  DEFPROC_VERSIO   BIGINT,
  ORDRE            BIGINT                   NOT NULL,
  TIPUS            BIGINT                   NOT NULL,
  CONSULTA_ID      BIGINT                   NOT NULL,
  PARAM_TIPUS 	   BIGINT,
  CAMP_DESCRIPCIO  VARCHAR(255)
);


CREATE TABLE HEL_CONSULTA_SUB
(
  PARE_ID  BIGINT                           NOT NULL,
  FILL_ID  BIGINT                           NOT NULL
);


CREATE TABLE HEL_DEFINICIO_PROCES
(
  ID                  BIGINT                NOT NULL,
  DATACREACIO         TIMESTAMP(6)              NOT NULL,
  ETIQUETA            VARCHAR(64),
  JBPM_ID             VARCHAR(255)        NOT NULL,
  JBPM_KEY            VARCHAR(255)        NOT NULL,
  VERSIO              BIGINT,
  ENTORN_ID           BIGINT                NOT NULL,
  EXPEDIENT_TIPUS_ID  BIGINT
);


CREATE TABLE HEL_DOCUMENT
(
  ID                     BIGINT             NOT NULL,
  ADJUNTAR_AUTO          BOOLEAN,
  ARXIU_CONTINGUT        OID,
  ARXIU_NOM              VARCHAR(255),
  CODI                   VARCHAR(64)      NOT NULL,
  CONTENT_TYPE           VARCHAR(255),
  CONVERTIR_EXT          VARCHAR(10),
  CUSTODIA_CODI          VARCHAR(255),
  DESCRIPCIO             VARCHAR(255),
  EXTENSIONS_PERMESES    VARCHAR(255),
  NOM                    VARCHAR(255)     NOT NULL,
  PLANTILLA              BOOLEAN,
  TIPUS_PORTASIGNATURES  BIGINT,
  CAMP_DATA_ID           BIGINT,
  DEFINICIO_PROCES_ID    BIGINT             NOT NULL
);


CREATE TABLE HEL_DOCUMENT_STORE
(
  ID                   BIGINT               NOT NULL,
  ADJUNT               BOOLEAN,
  ADJUNT_TITOL         VARCHAR(255),
  ARXIU_CONTINGUT      OID,
  ARXIU_NOM            VARCHAR(255)       NOT NULL,
  DATA_CREACIO         TIMESTAMP(6)             NOT NULL,
  DATA_DOCUMENT        DATE                     NOT NULL,
  DATA_MODIFICACIO     TIMESTAMP(6)             NOT NULL,
  FONT                 BIGINT               NOT NULL,
  JBPM_VARIABLE        VARCHAR(255)       NOT NULL,
  PROCESS_INSTANCE_ID  VARCHAR(64)        NOT NULL,
  REF_CUSTODIA         VARCHAR(255),
  REF_FONT             VARCHAR(255),
  REGISTRE_DATA        TIMESTAMP(6),
  REGISTRE_ENTRADA     BOOLEAN,
  REGISTRE_NUM         VARCHAR(255),
  REGISTRE_OFCODI      VARCHAR(255),
  REGISTRE_OFNOM       VARCHAR(255),
  REGISTRE_ORGCODI     VARCHAR(255),
  SIGNAT               BOOLEAN
);


CREATE TABLE HEL_DOCUMENT_TASCA
(
  ID           BIGINT                       NOT NULL,
  ORDRE        BIGINT,
  RO           BOOLEAN,
  RQ           BOOLEAN,
  DOCUMENT_ID  BIGINT                       NOT NULL,
  TASCA_ID     BIGINT                       NOT NULL
);


CREATE TABLE HEL_DOMINI
(
  ID                  BIGINT                NOT NULL,
  CACHE_SEGONS        BIGINT,
  TIMEOUT        	  BIGINT,
  CODI                VARCHAR(64)         NOT NULL,
  CONTRASENYA         VARCHAR(255),
  DESCRIPCIO          VARCHAR(255),
  JNDI_DATASOURCE     VARCHAR(255),
  NOM                 VARCHAR(255)        NOT NULL,
  ORDRE_PARAMS        VARCHAR(255),
  ORIGEN_CREDS        BIGINT,
  SQLEXPR             VARCHAR(1024),
  TIPUS               BIGINT                NOT NULL,
  TIPUS_AUTH          BIGINT,
  URL                 VARCHAR(255),
  USUARI              VARCHAR(255),
  ENTORN_ID           BIGINT                NOT NULL,
  EXPEDIENT_TIPUS_ID  BIGINT
);


CREATE TABLE HEL_ENTORN
(
  ID          BIGINT                        NOT NULL,
  ACTIU       BOOLEAN,
  CODI        VARCHAR(64)                 NOT NULL,
  DESCRIPCIO  VARCHAR(255),
  NOM         VARCHAR(255)                NOT NULL
);


CREATE TABLE HEL_ENUMERACIO
(
  ID                  BIGINT                NOT NULL,
  CODI                VARCHAR(64)         NOT NULL,
  NOM                 VARCHAR(255)        NOT NULL,
  VALORS              VARCHAR(4000),
  ENTORN_ID           BIGINT                NOT NULL,
  EXPEDIENT_TIPUS_ID  BIGINT
);


CREATE TABLE HEL_ENUMERACIO_VALORS
(
  ID             BIGINT                     NOT NULL,
  CODI           VARCHAR(64)              NOT NULL,
  NOM            VARCHAR(255)             NOT NULL,
  ORDRE          BIGINT                     NOT NULL,
  ENUMERACIO_ID  BIGINT                     NOT NULL
);


CREATE TABLE HEL_ESTAT
(
  ID                  BIGINT                NOT NULL,
  CODI                VARCHAR(64)         NOT NULL,
  NOM                 VARCHAR(255)        NOT NULL,
  ORDRE               BIGINT                NOT NULL,
  EXPEDIENT_TIPUS_ID  BIGINT                NOT NULL
);


CREATE TABLE HEL_EXPEDIENT
(
  ID                   BIGINT               NOT NULL,
  ANULAT               BOOLEAN,
  AUTENTICAT           BOOLEAN,
  AVISOS_EMAIL         VARCHAR(255),
  AVISOS_HABILITAT     BOOLEAN,
  AVISOS_MOBIL         VARCHAR(255),
  COMENTARI            VARCHAR(255),
  COMENTARIANULAT	   VARCHAR(255),
  DATA_FI              TIMESTAMP(6),
  DATA_INICI           TIMESTAMP(6)             NOT NULL,
  ERROR_FULL2		   TEXT,
  ERROR_FULL		   TEXT,
  ERROR_DESC		   VARCHAR(255),
  ERRORS_INTEGS 	   BOOLEAN,
  GEO_POSX             numeric,
  GEO_POSY             numeric,
  GEO_REFERENCIA       VARCHAR(64),
  IDIOMA               VARCHAR(8),
  INFO_ATURAT          VARCHAR(1024),
  INICIADOR_CODI       VARCHAR(64),
  INICIADOR_TIPUS      BIGINT,
  INTERESSAT_NIF       VARCHAR(16),
  INTERESSAT_NOM       VARCHAR(255),
  NOTTEL_HABILITAT     BOOLEAN,
  NUMERO               VARCHAR(64),
  NUMERO_DEFAULT       VARCHAR(64),
  PROCESS_INSTANCE_ID  VARCHAR(255)       NOT NULL,
  REGISTRE_DATA        TIMESTAMP(6),
  REGISTRE_NUM         VARCHAR(64),
  REPRESENTANT_NIF     VARCHAR(16),
  REPRESENTANT_NOM     VARCHAR(255),
  RESPONSABLE_CODI     VARCHAR(64),
  TITOL                VARCHAR(255),
  TRAMEXP_CLAU         VARCHAR(255),
  TRAMEXP_ID           VARCHAR(255),
  TRAMITADOR_NIF       VARCHAR(16),
  TRAMITADOR_NOM       VARCHAR(255),
  GRUP_CODI            VARCHAR(64),
  UNITAT_ADM           BIGINT,
  ENTORN_ID            BIGINT               NOT NULL,
  ESTAT_ID             BIGINT,
  TIPUS_ID             BIGINT               NOT NULL,
  AMB_RETROACCIO       BOOLEAN DEFAULT TRUE      NOT NULL,
  REINDEXAR_DATA	   TIMESTAMP(6),
  REINDEXAR_ERROR      BOOLEAN DEFAULT FALSE      NOT NULL
);

CREATE TABLE HEL_EXEC_MASEXP (
	ID BIGINT NOT NULL, 
	DATA_FI DATE, 
	DATA_INICI DATE, 
	ORDRE BIGINT NOT NULL, 
	EXECMAS_ID BIGINT NOT NULL, 
	EXPEDIENT_ID BIGINT, 
	ESTAT BIGINT, 
	ERROR TEXT, 
	TASCA_ID VARCHAR(255), 
	PROCINST_ID VARCHAR(255)
);

CREATE TABLE HEL_EXEC_MASSIVA (
	ID BIGINT NOT NULL, 
	DATA_FI DATE, 
	DATA_INICI DATE NOT NULL, 
	PARAM1 VARCHAR(255), 
	TIPUS BIGINT NOT NULL, 
	USUARI VARCHAR(64) NOT NULL, 
	EXPEDIENT_TIPUS_ID BIGINT, 
	ENV_CORREU BOOLEAN, 
	PARAM2 OID, 
	ENTORN BIGINT, 
	ROLS VARCHAR(2000), 
	CREDENCIALS OID
);

CREATE TABLE HEL_EXPEDIENT_NOTIF_ELECTR (
  ID 					BIGINT      NOT NULL ,
  DATA 					TIMESTAMP(6) 	NOT NULL ,
  EXPEDIENT_ID 			BIGINT		NOT NULL ,
  NUMERO 				VARCHAR(255) 	NOT NULL ,
  RDS_CODI 				VARCHAR(255) 	NOT NULL ,
  RDS_CLAVE 			VARCHAR(255) 	NOT NULL 
);

CREATE TABLE HEL_EXPEDIENT_LOG(
  ID                   BIGINT               NOT NULL,
  ACCIO_TIPUS          BIGINT               NOT NULL,
  ACCIO_PARAMS         VARCHAR(2048),
  DATA                 TIMESTAMP(6)             NOT NULL,
  ESTAT                BIGINT               NOT NULL,
  INI_RETROCES         BIGINT,
  JBPM_LOGID           BIGINT,
  PROCESS_INSTANCE_ID  BIGINT,
  TARGET_ID            VARCHAR(255)       NOT NULL,
  USUARI               VARCHAR(255)       NOT NULL,
  EXPEDIENT_ID         BIGINT               NOT NULL
);

CREATE TABLE HEL_EXPEDIENT_RELS
(
  ORIGEN_ID  BIGINT                         NOT NULL,
  DESTI_ID   BIGINT                         NOT NULL
);


CREATE TABLE HEL_EXPEDIENT_TIPUS
(
  ID                    BIGINT               NOT NULL,
  ANY_ACTUAL            BIGINT,
  CODI                  VARCHAR(64)        NOT NULL,
  DEMANA_NUMERO         BOOLEAN,
  DEMANA_TITOL          BOOLEAN,
  EXPRESSIO_NUMERO      VARCHAR(255),
  FORMEXT_CONTRASENYA   VARCHAR(255),
  FORMEXT_URL           VARCHAR(255),
  FORMEXT_USUARI        VARCHAR(255),
  JBPM_PD_KEY           VARCHAR(255),
  NOM                   VARCHAR(255)       NOT NULL,
  REINICIAR_ANUAL       BOOLEAN,
  RESPDEFAULT_CODI      VARCHAR(64),
  SEQUENCIA             BIGINT,
  SEQUENCIA_DEF         BIGINT,
  SISTRA_CODTRA         VARCHAR(64),
  SISTRA_MAPADJ         VARCHAR(2048),
  SISTRA_MAPCAMPS       VARCHAR(2048),
  SISTRA_MAPDOCS        VARCHAR(2048),
  TE_NUMERO             BOOLEAN,
  TE_TITOL              BOOLEAN,
  RESTRINGIR_GRUP       BOOLEAN,
  TRAM_MASSIVA          BOOLEAN,
  ENTORN_ID             BIGINT               NOT NULL,
  AMB_RETROACCIO	    BOOLEAN DEFAULT FALSE      NOT NULL,
  REINDEXACIO_ASINCRONA BOOLEAN DEFAULT FALSE      NOT NULL,
  SELECCIONAR_ANY       BOOLEAN DEFAULT FALSE      NOT NULL
);


CREATE TABLE HEL_EXPEDIENT_TIPUS_SEQANY (
  ID                BIGINT NOT NULL,
  ANY_              BIGINT,
  SEQUENCIA         BIGINT,
  EXPEDIENT_TIPUS   BIGINT
);


CREATE TABLE HEL_EXPEDIENT_TIPUS_SEQDEFANY
(
  ID                BIGINT                  NOT NULL,
  ANY_              BIGINT                  NOT NULL,
  SEQUENCIADEFAULT  BIGINT                  NOT NULL,
  EXPEDIENT_TIPUS   BIGINT
);


CREATE TABLE HEL_FESTIU
(
  ID    BIGINT                              NOT NULL,
  DATA  DATE                                    NOT NULL
);


CREATE TABLE HEL_FIRMA_TASCA
(
  ID           BIGINT                       NOT NULL,
  ORDRE        BIGINT,
  RQ           BOOLEAN,
  DOCUMENT_ID  BIGINT                       NOT NULL,
  TASCA_ID     BIGINT                       NOT NULL
);


CREATE TABLE HEL_FORMEXT
(
  ID               BIGINT                   NOT NULL,
  DATA_DARRPET     TIMESTAMP(6),
  DATA_INICI       TIMESTAMP(6)                 NOT NULL,
  DATA_RECDADES    TIMESTAMP(6),
  DATA_FORMHEIGHT  BIGINT,
  DATA_FORMWIDTH   BIGINT,
  FORMID           VARCHAR(255)           NOT NULL,
  TASKID           VARCHAR(255)           NOT NULL,
  URL              VARCHAR(1024)          NOT NULL
);


CREATE TABLE HEL_IDGEN
(
  TAULA  VARCHAR(255),
  VALOR  BIGINT
);


CREATE TABLE HEL_MAP_SISTRA
(
  ID                  BIGINT                NOT NULL,
  CODIHELIUM          VARCHAR(255)        NOT NULL,
  CODISISTRA          VARCHAR(255)        NOT NULL,
  TIPUS               BIGINT                NOT NULL,
  EXPEDIENT_TIPUS_ID  BIGINT                NOT NULL
);


CREATE TABLE HEL_PERMIS
(
  CODI        VARCHAR(64)                 NOT NULL,
  DESCRIPCIO  VARCHAR(255)
);


CREATE TABLE HEL_PERSONA
(
  ID              BIGINT                    NOT NULL,
  AVIS_CORREU     BOOLEAN                     NOT NULL,
  CODI            VARCHAR(64)             NOT NULL,
  DATA_NAIXEMENT  DATE,
  DNI             VARCHAR(64),
  EMAIL           VARCHAR(255),
  FONT            BIGINT,
  LLINATGE1       VARCHAR(255)            NOT NULL,
  LLINATGE2       VARCHAR(255),
  LLINATGES       VARCHAR(255),
  NOM             VARCHAR(255)            NOT NULL,
  NOM_SENCER      VARCHAR(255),
  SEXE            BIGINT                    NOT NULL,
  RELLEU_ID       BIGINT
);


CREATE TABLE HEL_PORTASIGNATURES
(
  ID                   BIGINT                 NOT NULL,
  DOCUMENT_ID          BIGINT                 NOT NULL,
  TOKEN_ID             BIGINT                 NOT NULL,
  DATA_ENVIAT          TIMESTAMP(6),
  ESTAT                BIGINT,
  TRANSICIO            BIGINT,
  DOCUMENT_STORE_ID    BIGINT,
  MOTIU_REBUIG         VARCHAR(255),
  TRANSICIO_OK         VARCHAR(255),
  TRANSICIO_KO         VARCHAR(255),
  DATA_CB_PRI          TIMESTAMP(6),
  DATA_CB_DAR          TIMESTAMP(6),
  DATA_SIGNAT_REBUTJAT TIMESTAMP(6),
  DATA_CUSTODIA_INTENT TIMESTAMP(6),
  DATA_CUSTODIA_OK     TIMESTAMP(6),
  DATA_SIGNAL_INTENT   TIMESTAMP(6),
  DATA_SIGNAL_OK       TIMESTAMP(6),
  ERROR_CB_PROCES      TEXT,
  PROCESS_INSTANCE_ID  VARCHAR(256)            NOT NULL,
  EXPEDIENT_ID         BIGINT);


CREATE TABLE HEL_REDIR
(
  ID                  BIGINT                   NOT NULL,
  DATA_CANCELACIO     TIMESTAMP(6),
  DATA_FI             TIMESTAMP(6)                 NOT NULL,
  DATA_INICI          TIMESTAMP(6)                 NOT NULL,
  USUARI_DESTI        VARCHAR(255)           NOT NULL,
  USUARI_ORIGEN       VARCHAR(255)           NOT NULL,
  EXPEDIENT_TIPUS_ID  BIGINT
);


CREATE TABLE HEL_REGISTRE
(
  ID                   BIGINT               NOT NULL,
  ACCIO                BIGINT               NOT NULL,
  DATA                 TIMESTAMP(6)             NOT NULL,
  ENTITAT              BIGINT               NOT NULL,
  ENTITAT_ID           VARCHAR(255)       NOT NULL,
  EXPEDIENT_ID         BIGINT               NOT NULL,
  MISSATGE             VARCHAR(1024),
  PROCESS_INSTANCE_ID  VARCHAR(255),
  RESPONSABLE_CODI     VARCHAR(64)        NOT NULL,
  VALOR_NOU            VARCHAR(4000),
  VALOR_VELL           VARCHAR(4000)
);


CREATE TABLE HEL_TASCA
(
  ID                   BIGINT               NOT NULL,
  EXPRESSIO_DELEGACIO  VARCHAR(255),
  FORM_EXTERN          VARCHAR(255),
  JBPM_NAME            VARCHAR(255)       NOT NULL,
  MISSATGE_INFO        VARCHAR(255),
  MISSATGE_WARN        VARCHAR(255),
  NOM                  VARCHAR(255),
  NOM_SCRIPT           VARCHAR(1024),
  RECURS_FORM          VARCHAR(255),
  TIPUS                BIGINT               NOT NULL,
  TRAM_MASSIVA         BOOLEAN,
  DEFINICIO_PROCES_ID  BIGINT               NOT NULL,
  FIN_SEGON_PLA 	   BOOLEAN DEFAULT TRUE 		NOT NULL
);


CREATE TABLE HEL_TERMINI
(
  ID                   BIGINT               NOT NULL,
  ALERTA_COMPLETAT     BOOLEAN,
  ALERTA_FINAL         BOOLEAN,
  ALERTA_PREVIA        BOOLEAN,
  ANYS                 BIGINT,
  CODI                 VARCHAR(64)        NOT NULL,
  DESCRIPCIO           VARCHAR(255),
  DIES                 BIGINT,
  DIES_PREVIS_AVIS     BIGINT,
  DURADA_PREDEF        BOOLEAN,
  LABORABLE            BOOLEAN,
  MANUAL               BOOLEAN,
  MESOS                BIGINT,
  NOM                  VARCHAR(255)       NOT NULL,
  DEFINICIO_PROCES_ID  BIGINT               NOT NULL
);


CREATE TABLE HEL_TERMINI_INICIAT
(
  ID                   BIGINT               NOT NULL,
  ALERTA_COMPLETAT     BOOLEAN,
  ALERTA_FINAL         BOOLEAN,
  ALERTA_PREVIA        BOOLEAN,
  ANYS                 BIGINT,
  DATA_ATURADA         DATE,
  DATA_CANCEL          DATE,
  DATA_COMPLETAT       DATE,
  DATA_FI              DATE                     NOT NULL,
  DATA_FI_PRORROGA     DATE,
  DATA_INICI           DATE                     NOT NULL,
  DIES                 BIGINT,
  DIES_ATURAT          BIGINT,
  MESOS                BIGINT,
  PROCESS_INSTANCE_ID  VARCHAR(255)       NOT NULL,
  TASK_INSTANCE_ID     VARCHAR(255),
  TIMER_IDS            VARCHAR(1024),
  TERMINI_ID           BIGINT               NOT NULL
);


CREATE TABLE HEL_USUARI
(
  CODI         VARCHAR(64)                NOT NULL,
  ACTIU        BOOLEAN                        NOT NULL,
  CONTRASENYA  VARCHAR(255)
);


CREATE TABLE HEL_USUARI_PERMIS
(
  CODI    VARCHAR(64)                     NOT NULL,
  PERMIS  VARCHAR(64)                     NOT NULL
);


CREATE TABLE HEL_USUARI_PREFS
(
  CODI                    VARCHAR(64)     NOT NULL,
  DEFAULT_ENTORN          VARCHAR(64),
  IDIOMA                  VARCHAR(255),
  CABECERA_REDUCIDA       BOOLEAN,
  LISTADO                 BOOLEAN,
  CONSULTA_ID             BIGINT,
  FILTRO_TAREAS_ACTIVAS   BOOLEAN,
  NUM_ELEMENTOS_PAGINA    BIGINT,
  DEFAULT_TIPUS_EXPEDIENT BIGINT
);

CREATE TABLE HEL_VALIDACIO
(
  ID         BIGINT                         NOT NULL,
  EXPRESSIO  VARCHAR(1024)                NOT NULL,
  MISSATGE   VARCHAR(255)                 NOT NULL,
  NOM        VARCHAR(255),
  ORDRE      BIGINT                         NOT NULL,
  CAMP_ID    BIGINT,
  TASCA_ID   BIGINT
);


CREATE TABLE HEL_VERSIO
(
  ID                    BIGINT              NOT NULL,
  CODI                  VARCHAR(64)       NOT NULL,
  DATA_CREACIO          TIMESTAMP(6)            NOT NULL,
  DATA_EXECUCIO_PROCES  TIMESTAMP(6),
  DATA_EXECUCIO_SCRIPT  TIMESTAMP(6),
  DESCRIPCIO            VARCHAR(255),
  ORDRE                 BIGINT              NOT NULL,
  PROCES_EXECUTAT       BOOLEAN,
  SCRIPT_EXECUTAT       BOOLEAN
);


CREATE TABLE HEL_REPRO
(
  ID                  BIGINT                NOT NULL,
  USUARI          	  VARCHAR(255),
  EXPTIP_ID			  BIGINT,
  NOM				  VARCHAR(255),
  VALORS   			  VARCHAR(4000)
);

CREATE TABLE JBPM_ACTION
(
  ID_                     BIGINT            NOT NULL,
  CLASS                   CHAR(1)          NOT NULL,
  NAME_                   VARCHAR(255),
  ISPROPAGATIONALLOWED_   BOOLEAN,
  ACTIONEXPRESSION_       VARCHAR(255),
  ISASYNC_                BOOLEAN,
  REFERENCEDACTION_       BIGINT,
  ACTIONDELEGATION_       BIGINT,
  EVENT_                  BIGINT,
  PROCESSDEFINITION_      BIGINT,
  EXPRESSION_             TEXT,
  TIMERNAME_              VARCHAR(255),
  DUEDATE_                VARCHAR(255),
  REPEAT_                 VARCHAR(255),
  TRANSITIONNAME_         VARCHAR(255),
  TIMERACTION_            BIGINT,
  EVENTINDEX_             BIGINT,
  EXCEPTIONHANDLER_       BIGINT,
  EXCEPTIONHANDLERINDEX_  BIGINT
);


CREATE TABLE JBPM_BYTEARRAY
(
  ID_              BIGINT                   NOT NULL,
  NAME_            VARCHAR(255),
  FILEDEFINITION_  BIGINT
);


CREATE TABLE JBPM_BYTEBLOCK
(
  PROCESSFILE_  BIGINT                      NOT NULL,
  BYTES_        BYTEA,
  INDEX_        BIGINT                      NOT NULL
);


CREATE TABLE JBPM_COMMENT
(
  ID_                 BIGINT                NOT NULL,
  VERSION_            BIGINT                NOT NULL,
  ACTORID_            VARCHAR(255),
  TIME_               TIMESTAMP(6),
  MESSAGE_            TEXT,
  TOKEN_              BIGINT,
  TASKINSTANCE_       BIGINT,
  TOKENINDEX_         BIGINT,
  TASKINSTANCEINDEX_  BIGINT
);


CREATE TABLE JBPM_DECISIONCONDITIONS
(
  DECISION_        BIGINT                   NOT NULL,
  TRANSITIONNAME_  VARCHAR(255),
  EXPRESSION_      VARCHAR(255),
  INDEX_           BIGINT                   NOT NULL
);


CREATE TABLE JBPM_DELEGATION
(
  ID_                 BIGINT                NOT NULL,
  CLASSNAME_          TEXT,
  CONFIGURATION_      TEXT,
  CONFIGTYPE_         VARCHAR(255),
  PROCESSDEFINITION_  BIGINT
);


CREATE TABLE JBPM_EVENT
(
  ID_                 BIGINT                NOT NULL,
  EVENTTYPE_          VARCHAR(255),
  TYPE_               CHAR(1),
  GRAPHELEMENT_       BIGINT,
  PROCESSDEFINITION_  BIGINT,
  NODE_               BIGINT,
  TRANSITION_         BIGINT,
  TASK_               BIGINT
);


CREATE TABLE JBPM_EXCEPTIONHANDLER
(
  ID_                  BIGINT               NOT NULL,
  EXCEPTIONCLASSNAME_  TEXT,
  TYPE_                CHAR(1),
  GRAPHELEMENT_        BIGINT,
  PROCESSDEFINITION_   BIGINT,
  GRAPHELEMENTINDEX_   BIGINT,
  NODE_                BIGINT,
  TRANSITION_          BIGINT,
  TASK_                BIGINT
);


CREATE TABLE JBPM_ID_GROUP
(
  ID_      BIGINT                           NOT NULL,
  CLASS_   CHAR(1)                         NOT NULL,
  NAME_    VARCHAR(255),
  TYPE_    VARCHAR(255),
  PARENT_  BIGINT
);


CREATE TABLE JBPM_ID_MEMBERSHIP
(
  ID_     BIGINT                            NOT NULL,
  CLASS_  CHAR(1)                          NOT NULL,
  NAME_   VARCHAR(255),
  ROLE_   VARCHAR(255),
  USER_   BIGINT,
  GROUP_  BIGINT
);


CREATE TABLE JBPM_ID_PERMISSIONS
(
  ENTITY_  BIGINT                           NOT NULL,
  CLASS_   VARCHAR(255),
  NAME_    VARCHAR(255),
  ACTION_  VARCHAR(255)
);


CREATE TABLE JBPM_ID_USER
(
  ID_        BIGINT                         NOT NULL,
  CLASS_     CHAR(1)                       NOT NULL,
  NAME_      VARCHAR(255),
  EMAIL_     VARCHAR(255),
  PASSWORD_  VARCHAR(255)
);


CREATE TABLE JBPM_JOB
(
  ID_                BIGINT                 NOT NULL,
  CLASS_             CHAR(1)               NOT NULL,
  VERSION_           BIGINT                 NOT NULL,
  DUEDATE_           TIMESTAMP(6),
  PROCESSINSTANCE_   BIGINT,
  TOKEN_             BIGINT,
  TASKINSTANCE_      BIGINT,
  ISSUSPENDED_       BOOLEAN,
  ISEXCLUSIVE_       BOOLEAN,
  LOCKOWNER_         VARCHAR(255),
  LOCKTIME_          TIMESTAMP(6),
  EXCEPTION_         TEXT,
  RETRIES_           BIGINT,
  NAME_              VARCHAR(255),
  REPEAT_            VARCHAR(255),
  TRANSITIONNAME_    VARCHAR(255),
  ACTION_            BIGINT,
  GRAPHELEMENTTYPE_  VARCHAR(255),
  GRAPHELEMENT_      BIGINT,
  NODE_              BIGINT
);


CREATE TABLE JBPM_LOG
(
  ID_                BIGINT                 NOT NULL,
  CLASS_             CHAR(1)               NOT NULL,
  INDEX_             BIGINT,
  DATE_              TIMESTAMP(6),
  TOKEN_             BIGINT,
  PARENT_            BIGINT,
  MESSAGE_           TEXT,
  EXCEPTION_         TEXT,
  ACTION_            BIGINT,
  NODE_              BIGINT,
  ENTER_             TIMESTAMP(6),
  LEAVE_             TIMESTAMP(6),
  DURATION_          BIGINT,
  NEWLONGVALUE_      BIGINT,
  TRANSITION_        BIGINT,
  CHILD_             BIGINT,
  SOURCENODE_        BIGINT,
  DESTINATIONNODE_   BIGINT,
  VARIABLEINSTANCE_  BIGINT,
  OLDBYTEARRAY_      BIGINT,
  NEWBYTEARRAY_      BIGINT,
  OLDDATEVALUE_      TIMESTAMP(6),
  NEWDATEVALUE_      TIMESTAMP(6),
  OLDDOUBLEVALUE_    numeric,
  NEWDOUBLEVALUE_    numeric,
  OLDLONGIDCLASS_    VARCHAR(255),
  OLDLONGIDVALUE_    BIGINT,
  NEWLONGIDCLASS_    VARCHAR(255),
  NEWLONGIDVALUE_    BIGINT,
  OLDSTRINGIDCLASS_  VARCHAR(255),
  OLDSTRINGIDVALUE_  VARCHAR(255),
  NEWSTRINGIDCLASS_  VARCHAR(255),
  NEWSTRINGIDVALUE_  VARCHAR(255),
  OLDLONGVALUE_      BIGINT,
  OLDSTRINGVALUE_    TEXT,
  NEWSTRINGVALUE_    TEXT,
  TASKINSTANCE_      BIGINT,
  TASKACTORID_       VARCHAR(255),
  TASKOLDACTORID_    VARCHAR(255),
  SWIMLANEINSTANCE_  BIGINT
);


CREATE TABLE JBPM_MODULEDEFINITION
(
  ID_                 BIGINT                NOT NULL,
  CLASS_              CHAR(1)              NOT NULL,
  NAME_               VARCHAR(255),
  PROCESSDEFINITION_  BIGINT,
  STARTTASK_          BIGINT
);


CREATE TABLE JBPM_MODULEINSTANCE
(
  ID_                  BIGINT               NOT NULL,
  CLASS_               CHAR(1)             NOT NULL,
  VERSION_             BIGINT               NOT NULL,
  PROCESSINSTANCE_     BIGINT,
  TASKMGMTDEFINITION_  BIGINT,
  NAME_                VARCHAR(255)
);


CREATE TABLE JBPM_NODE
(
  ID_                    BIGINT             NOT NULL,
  CLASS_                 CHAR(1)           NOT NULL,
  NAME_                  VARCHAR(255),
  DESCRIPTION_           TEXT,
  PROCESSDEFINITION_     BIGINT,
  ISASYNC_               BOOLEAN,
  ISASYNCEXCL_           BOOLEAN,
  ACTION_                BIGINT,
  SUPERSTATE_            BIGINT,
  SUBPROCNAME_           VARCHAR(255),
  SUBPROCESSDEFINITION_  BIGINT,
  DECISIONEXPRESSION_    VARCHAR(255),
  DECISIONDELEGATION     BIGINT,
  SCRIPT_                BIGINT,
  PARENTLOCKMODE_        VARCHAR(255),
  SIGNAL_                BIGINT,
  CREATETASKS_           BOOLEAN,
  ENDTASKS_              BOOLEAN,
  NODECOLLECTIONINDEX_   BIGINT
);


CREATE TABLE JBPM_POOLEDACTOR
(
  ID_                BIGINT                 NOT NULL,
  VERSION_           BIGINT                 NOT NULL,
  ACTORID_           VARCHAR(255),
  SWIMLANEINSTANCE_  BIGINT
);


CREATE TABLE JBPM_PROCESSDEFINITION
(
  ID_                     BIGINT            NOT NULL,
  CLASS_                  CHAR(1)          NOT NULL,
  NAME_                   VARCHAR(255),
  DESCRIPTION_            TEXT,
  VERSION_                BIGINT,
  ISTERMINATIONIMPLICIT_  BOOLEAN,
  STARTSTATE_             BIGINT
);


CREATE TABLE JBPM_PROCESSINSTANCE
(
  ID_                 BIGINT                NOT NULL,
  VERSION_            BIGINT                NOT NULL,
  KEY_                VARCHAR(255),
  START_              TIMESTAMP(6),
  END_                TIMESTAMP(6),
  ISSUSPENDED_        BOOLEAN,
  PROCESSDEFINITION_  BIGINT,
  ROOTTOKEN_          BIGINT,
  SUPERPROCESSTOKEN_  BIGINT,
  EXPEDIENT_ID_       BIGINT
);


CREATE TABLE JBPM_RUNTIMEACTION
(
  ID_                    BIGINT             NOT NULL,
  VERSION_               BIGINT             NOT NULL,
  EVENTTYPE_             VARCHAR(255),
  TYPE_                  CHAR(1),
  GRAPHELEMENT_          BIGINT,
  PROCESSINSTANCE_       BIGINT,
  ACTION_                BIGINT,
  PROCESSINSTANCEINDEX_  BIGINT
);


CREATE TABLE JBPM_SWIMLANE
(
  ID_                      BIGINT           NOT NULL,
  NAME_                    VARCHAR(255),
  ACTORIDEXPRESSION_       VARCHAR(255),
  POOLEDACTORSEXPRESSION_  VARCHAR(255),
  ASSIGNMENTDELEGATION_    BIGINT,
  TASKMGMTDEFINITION_      BIGINT
);


CREATE TABLE JBPM_SWIMLANEINSTANCE
(
  ID_                BIGINT                 NOT NULL,
  VERSION_           BIGINT                 NOT NULL,
  NAME_              VARCHAR(255),
  ACTORID_           VARCHAR(255),
  SWIMLANE_          BIGINT,
  TASKMGMTINSTANCE_  BIGINT
);


CREATE TABLE JBPM_TASK
(
  ID_                      BIGINT           NOT NULL,
  NAME_                    VARCHAR(255),
  DESCRIPTION_             TEXT,
  PROCESSDEFINITION_       BIGINT,
  ISBLOCKING_              BOOLEAN,
  ISSIGNALLING_            BOOLEAN,
  CONDITION_               VARCHAR(255),
  DUEDATE_                 VARCHAR(255),
  PRIORITY_                BIGINT,
  ACTORIDEXPRESSION_       VARCHAR(255),
  POOLEDACTORSEXPRESSION_  VARCHAR(255),
  TASKMGMTDEFINITION_      BIGINT,
  TASKNODE_                BIGINT,
  STARTSTATE_              BIGINT,
  ASSIGNMENTDELEGATION_    BIGINT,
  SWIMLANE_                BIGINT,
  TASKCONTROLLER_          BIGINT
);


CREATE TABLE JBPM_TASKACTORPOOL
(
  TASKINSTANCE_  BIGINT                     NOT NULL,
  POOLEDACTOR_   BIGINT                     NOT NULL
);


CREATE TABLE JBPM_TASKCONTROLLER
(
  ID_                        BIGINT         NOT NULL,
  TASKCONTROLLERDELEGATION_  BIGINT
);


CREATE TABLE JBPM_TASKINSTANCE
(
  ID_                BIGINT                 NOT NULL,
  CLASS_             CHAR(1)               NOT NULL,
  VERSION_           BIGINT                 NOT NULL,
  NAME_              VARCHAR(255),
  DESCRIPTION_       TEXT,
  ACTORID_           VARCHAR(255),
  CREATE_            TIMESTAMP(6),
  START_             TIMESTAMP(6),
  END_               TIMESTAMP(6),
  DUEDATE_           TIMESTAMP(6),
  PRIORITY_          BIGINT,
  ISCANCELLED_       BOOLEAN,
  ISSUSPENDED_       BOOLEAN,
  ISOPEN_            BOOLEAN,
  ISSIGNALLING_      BOOLEAN,
  ISBLOCKING_        BOOLEAN,
  TASK_              BIGINT,
  TOKEN_             BIGINT,
  PROCINST_          BIGINT,
  SWIMLANINSTANCE_   BIGINT,
  TASKMGMTINSTANCE_  BIGINT,
  MARCADAFINALITZAR_ TIMESTAMP(6),
  INICIFINALITZACIO_ TIMESTAMP(6),
  ERRORFINALITZACIO_ VARCHAR(1000),
  TITOLACTUALITZAT_  BOOLEAN DEFAULT FALSE 		NOT NULL,
  SELECTEDOUTCOME_   VARCHAR(255)
);


CREATE TABLE JBPM_TOKEN
(
  ID_                        BIGINT         NOT NULL,
  VERSION_                   BIGINT         NOT NULL,
  NAME_                      VARCHAR(255),
  START_                     TIMESTAMP(6),
  END_                       TIMESTAMP(6),
  NODEENTER_                 TIMESTAMP(6),
  NEXTLOGINDEX_              BIGINT,
  ISABLETOREACTIVATEPARENT_  BOOLEAN,
  ISTERMINATIONIMPLICIT_     BOOLEAN,
  ISSUSPENDED_               BOOLEAN,
  LOCK_                      VARCHAR(255),
  NODE_                      BIGINT,
  PROCESSINSTANCE_           BIGINT,
  PARENT_                    BIGINT,
  SUBPROCESSINSTANCE_        BIGINT
);


CREATE TABLE JBPM_TOKENVARIABLEMAP
(
  ID_               BIGINT                  NOT NULL,
  VERSION_          BIGINT                  NOT NULL,
  TOKEN_            BIGINT,
  CONTEXTINSTANCE_  BIGINT
);


CREATE TABLE JBPM_TRANSITION
(
  ID_                 BIGINT                NOT NULL,
  NAME_               VARCHAR(255),
  DESCRIPTION_        TEXT,
  PROCESSDEFINITION_  BIGINT,
  FROM_               BIGINT,
  TO_                 BIGINT,
  CONDITION_          VARCHAR(255),
  FROMINDEX_          BIGINT
);


CREATE TABLE JBPM_VARIABLEACCESS
(
  ID_              BIGINT                   NOT NULL,
  VARIABLENAME_    VARCHAR(255),
  ACCESS_          VARCHAR(255),
  MAPPEDNAME_      VARCHAR(255),
  SCRIPT_          BIGINT,
  PROCESSSTATE_    BIGINT,
  TASKCONTROLLER_  BIGINT,
  INDEX_           BIGINT
);


CREATE TABLE JBPM_VARIABLEINSTANCE
(
  ID_                BIGINT                 NOT NULL,
  CLASS_             CHAR(1)               NOT NULL,
  VERSION_           BIGINT                 NOT NULL,
  NAME_              VARCHAR(255),
  CONVERTER_         CHAR(1),
  TOKEN_             BIGINT,
  TOKENVARIABLEMAP_  BIGINT,
  PROCESSINSTANCE_   BIGINT,
  BYTEARRAYVALUE_    BIGINT,
  DATEVALUE_         TIMESTAMP(6),
  DOUBLEVALUE_       numeric,
  LONGIDCLASS_       VARCHAR(255),
  LONGVALUE_         BIGINT,
  STRINGIDCLASS_     VARCHAR(255),
  STRINGVALUE_       TEXT,
  TASKINSTANCE_      BIGINT
);


CREATE INDEX HEL_CAMPREG_REGISTRE_I ON HEL_CAMP_REGISTRE
(REGISTRE_ID);

CREATE INDEX HEL_CAMPREG_MEMBRE_I ON HEL_CAMP_REGISTRE
(MEMBRE_ID);

CREATE INDEX HEL_CAMPAGRUP_DEFPROC_I ON HEL_CAMP_AGRUP
(DEFINICIO_PROCES_ID);

CREATE INDEX HEL_CAMP_AGRUP_I ON HEL_CAMP
(CAMP_AGRUPACIO_ID);

CREATE INDEX HEL_CAMP_ENUM_I ON HEL_CAMP
(ENUMERACIO_ID);

CREATE INDEX HEL_CAMP_DOMINI_I ON HEL_CAMP
(DOMINI_ID);

CREATE INDEX HEL_CAMP_DEFPROC_I ON HEL_CAMP
(DEFINICIO_PROCES_ID);

CREATE INDEX HEL_AREATIPUS_ENTORN_I ON HEL_AREA_TIPUS
(ENTORN_ID);

CREATE INDEX HEL_AREAMEMBRE_AREA_I ON HEL_AREA_MEMBRE
(AREA_ID);

CREATE INDEX HEL_AREA_TIPUS_I ON HEL_AREA
(TIPUS_ID);

CREATE INDEX HEL_AREA_ENTORN_I ON HEL_AREA
(ENTORN_ID);

CREATE INDEX HEL_AREA_PARE_I ON HEL_AREA
(PARE_ID);

CREATE INDEX HEL_ALERTA_EXPEDIENT_I ON HEL_ALERTA
(EXPEDIENT_ID);

CREATE INDEX HEL_ALERTA_ENTORN_I ON HEL_ALERTA
(ENTORN_ID);

CREATE INDEX HEL_ACCIO_DEFPROC_I ON HEL_ACCIO
(DEFINICIO_PROCES_ID);

CREATE INDEX HEL_VALIDACIO_TASCA_I ON HEL_VALIDACIO
(TASCA_ID);

CREATE INDEX HEL_VALIDACIO_CAMP_I ON HEL_VALIDACIO
(CAMP_ID);

CREATE INDEX HEL_TERMINIC_TERMINI_I ON HEL_TERMINI_INICIAT
(TERMINI_ID);

CREATE INDEX HEL_TERMINI_DEFPROC_I ON HEL_TERMINI
(DEFINICIO_PROCES_ID);

CREATE INDEX HEL_TASCA_DEFPROC_I ON HEL_TASCA
(DEFINICIO_PROCES_ID);

CREATE INDEX HEL_TASCA_JBPMNAME_I ON HEL_TASCA
(JBPM_NAME);

CREATE INDEX HEL_PERSONA_RELLEU_I ON HEL_PERSONA
(RELLEU_ID);

CREATE INDEX HEL_FIRTASCA_TASCA_I ON HEL_FIRMA_TASCA
(TASCA_ID);

CREATE INDEX HEL_FIRTASCA_DOCUMENT_I ON HEL_FIRMA_TASCA
(DOCUMENT_ID);

CREATE INDEX HEL_EXPTIP_ENTORN_I ON HEL_EXPEDIENT_TIPUS
(ENTORN_ID);

CREATE INDEX HEL_EXPEDIENT_TIPUS_I ON HEL_EXPEDIENT
(TIPUS_ID);

CREATE INDEX HEL_EXPEDIENT_ENTORN_I ON HEL_EXPEDIENT
(ENTORN_ID);

CREATE INDEX HEL_EXPEDIENT_ESTAT_I ON HEL_EXPEDIENT
(ESTAT_ID);

CREATE INDEX HEL_EXMASEXP_EXEMAS_I ON HEL_EXEC_MASEXP
(EXECMAS_ID);

CREATE INDEX HEL_ESTAT_EXPTIP_I ON HEL_ESTAT
(EXPEDIENT_TIPUS_ID);

CREATE INDEX HEL_ENUM_ENTORN_I ON HEL_ENUMERACIO
(ENTORN_ID);

CREATE INDEX HEL_DOMINI_EXPTIP_I ON HEL_DOMINI
(EXPEDIENT_TIPUS_ID);

CREATE INDEX HEL_DOMINI_ENTORN_I ON HEL_DOMINI
(ENTORN_ID);

CREATE INDEX HEL_DOCTASCA_DOCUMENT_I ON HEL_DOCUMENT_TASCA
(DOCUMENT_ID);

CREATE INDEX HEL_DOCTASCA_TASCA_I ON HEL_DOCUMENT_TASCA
(TASCA_ID);

CREATE INDEX HEL_DOCUMENT_DEFPROC_I ON HEL_DOCUMENT
(DEFINICIO_PROCES_ID);

CREATE INDEX HEL_DOCUMENT_CAMPDATA_I ON HEL_DOCUMENT
(CAMP_DATA_ID);

CREATE INDEX HEL_DEFPROC_ENTORN_I ON HEL_DEFINICIO_PROCES
(ENTORN_ID);

CREATE INDEX HEL_DEFPROC_EXPTIP_I ON HEL_DEFINICIO_PROCES
(EXPEDIENT_TIPUS_ID);

CREATE INDEX HEL_CONSULTACAMP_CONSULTA_I ON HEL_CONSULTA_CAMP
(CONSULTA_ID);

CREATE INDEX HEL_CONSULTA_ENTORN_I ON HEL_CONSULTA
(ENTORN_ID);

CREATE INDEX HEL_CONSULTA_EXPTIP_I ON HEL_CONSULTA
(EXPEDIENT_TIPUS_ID);

CREATE INDEX HEL_CARREC_ENTORN_I ON HEL_CARREC
(ENTORN_ID);

CREATE INDEX HEL_CARREC_AREA_I ON HEL_CARREC
(AREA_ID);

CREATE INDEX HEL_CAMPTASCA_CAMP_I ON HEL_CAMP_TASCA
(CAMP_ID);

CREATE INDEX HEL_CAMPTASCA_TASCA_I ON HEL_CAMP_TASCA
(TASCA_ID);

CREATE INDEX HEL_EXPNOTELE_EXPEDIENT_ID_I ON HEL_EXPEDIENT_NOTIF_ELECTR 
(EXPEDIENT_ID);

CREATE INDEX IDX_VARINST_PRCINS ON JBPM_VARIABLEINSTANCE
(PROCESSINSTANCE_);

CREATE INDEX IDX_VARINST_TKVARMP ON JBPM_VARIABLEINSTANCE
(TOKENVARIABLEMAP_);

CREATE INDEX IDX_VARINST_TK ON JBPM_VARIABLEINSTANCE
(TOKEN_);

CREATE INDEX IDX_TRANSIT_TO ON JBPM_TRANSITION
(TO_);

CREATE INDEX IDX_TRANSIT_FROM ON JBPM_TRANSITION
(FROM_);

CREATE INDEX IDX_TRANS_PROCDEF ON JBPM_TRANSITION
(PROCESSDEFINITION_);

CREATE INDEX IDX_TKVARMAP_CTXT ON JBPM_TOKENVARIABLEMAP
(CONTEXTINSTANCE_);

CREATE INDEX IDX_TKVVARMP_TOKEN ON JBPM_TOKENVARIABLEMAP
(TOKEN_);

CREATE INDEX IDX_TOKEN_SUBPI ON JBPM_TOKEN
(SUBPROCESSINSTANCE_);

CREATE INDEX IDX_TOKEN_NODE ON JBPM_TOKEN
(NODE_);

CREATE INDEX IDX_TOKEN_PROCIN ON JBPM_TOKEN
(PROCESSINSTANCE_);

CREATE INDEX IDX_TOKEN_PARENT ON JBPM_TOKEN
(PARENT_);

CREATE INDEX IDX_TASKINST_PROC ON JBPM_TASKINSTANCE
(PROCINST_);

CREATE INDEX IDX_TASKINST_TSKPROC ON JBPM_TASKINSTANCE
(TASK_, PROCINST_);

CREATE INDEX IDX_TASK_ACTORID ON JBPM_TASKINSTANCE
(ACTORID_);

CREATE INDEX IDX_TASKINST_TOKN ON JBPM_TASKINSTANCE
(TOKEN_);

CREATE INDEX IDX_TSKINST_SLINST ON JBPM_TASKINSTANCE
(SWIMLANINSTANCE_);

CREATE INDEX IDX_TSKINST_TMINST ON JBPM_TASKINSTANCE
(TASKMGMTINSTANCE_);

CREATE INDEX IDX_TASKINST_TSK ON JBPM_TASKINSTANCE
(TASK_);

CREATE INDEX IDX_TASK_TASKMGTDF ON JBPM_TASK
(TASKMGMTDEFINITION_);

CREATE INDEX IDX_TASK_TSKNODE ON JBPM_TASK
(TASKNODE_);

CREATE INDEX IDX_TASK_PROCDEF ON JBPM_TASK
(PROCESSDEFINITION_);

CREATE INDEX IDX_SWIMLINST_SL ON JBPM_SWIMLANEINSTANCE
(SWIMLANE_);

CREATE INDEX IDX_RTACTN_PRCINST ON JBPM_RUNTIMEACTION
(PROCESSINSTANCE_);

CREATE INDEX IDX_RTACTN_ACTION ON JBPM_RUNTIMEACTION
(ACTION_);

CREATE INDEX IDX_PROCIN_KEY ON JBPM_PROCESSINSTANCE
(KEY_);

CREATE INDEX IDX_PROCIN_PROCDEF ON JBPM_PROCESSINSTANCE
(PROCESSDEFINITION_);

CREATE INDEX IDX_PROCIN_ROOTTK ON JBPM_PROCESSINSTANCE
(ROOTTOKEN_);

CREATE INDEX IDX_PROCIN_SPROCTK ON JBPM_PROCESSINSTANCE
(SUPERPROCESSTOKEN_);

CREATE INDEX HEL_PROCINST_EXPEDIENT_ID_I ON JBPM_PROCESSINSTANCE
(EXPEDIENT_ID_);

CREATE INDEX IDX_PROCDEF_STRTST ON JBPM_PROCESSDEFINITION
(STARTSTATE_);

CREATE INDEX IDX_PLDACTR_ACTID ON JBPM_POOLEDACTOR
(ACTORID_);

CREATE INDEX IDX_TSKINST_SWLANE ON JBPM_POOLEDACTOR
(SWIMLANEINSTANCE_);

CREATE INDEX IDX_NODE_SUPRSTATE ON JBPM_NODE
(SUPERSTATE_);

CREATE INDEX IDX_NODE_ACTION ON JBPM_NODE
(ACTION_);

CREATE INDEX IDX_NODE_PROCDEF ON JBPM_NODE
(PROCESSDEFINITION_);

CREATE INDEX IDX_PSTATE_SBPRCDEF ON JBPM_NODE
(SUBPROCESSDEFINITION_);

CREATE INDEX IDX_MODINST_PRINST ON JBPM_MODULEINSTANCE
(PROCESSINSTANCE_);

CREATE INDEX IDX_MODDEF_PROCDF ON JBPM_MODULEDEFINITION
(PROCESSDEFINITION_);

CREATE INDEX IDX_JOB_PRINST ON JBPM_JOB
(PROCESSINSTANCE_);

CREATE INDEX IDX_JOB_TOKEN ON JBPM_JOB
(TOKEN_);

CREATE INDEX IDX_JOB_TSKINST ON JBPM_JOB
(TASKINSTANCE_);

CREATE INDEX IDX_DELEG_PRCD ON JBPM_DELEGATION
(PROCESSDEFINITION_);

CREATE INDEX IDX_COMMENT_TOKEN ON JBPM_COMMENT
(TOKEN_);

CREATE INDEX IDX_COMMENT_TSK ON JBPM_COMMENT
(TASKINSTANCE_);

CREATE INDEX IDX_ACTION_EVENT ON JBPM_ACTION
(EVENT_);

CREATE INDEX IDX_ACTION_PROCDF ON JBPM_ACTION
(PROCESSDEFINITION_);

CREATE INDEX IDX_ACTION_ACTNDL ON JBPM_ACTION
(ACTIONDELEGATION_);

CREATE INDEX IDX_LOG_ACTION ON JBPM_LOG
(ACTION_);

CREATE INDEX IDX_LOG_CHILD ON JBPM_LOG
(CHILD_);

CREATE INDEX IDX_LOG_DESTINATIONNODE ON JBPM_LOG
(DESTINATIONNODE_);

CREATE INDEX IDX_LOG_INDEX_NEWBYTEARRAY ON JBPM_LOG
(NEWBYTEARRAY_);

CREATE INDEX IDX_LOG_NODE ON JBPM_LOG
(NODE_);

CREATE INDEX IDX_LOG_OLDBYTEARRAY ON JBPM_LOG
(OLDBYTEARRAY_);

CREATE INDEX IDX_LOG_PARENT ON JBPM_LOG
(PARENT_);

CREATE INDEX IDX_LOG_SOURCENODE ON JBPM_LOG
(SOURCENODE_);

CREATE INDEX IDX_LOG_SWIMINST ON JBPM_LOG
(SWIMLANEINSTANCE_);

CREATE INDEX IDX_LOG_TASKINSTANCE ON JBPM_LOG
(TASKINSTANCE_);

CREATE INDEX IDX_LOG_TOKEN ON JBPM_LOG
(TOKEN_);

CREATE INDEX IDX_LOG_TRANSITION ON JBPM_LOG
(TRANSITION_);

CREATE INDEX IDX_LOG_VARINSTANCE ON JBPM_LOG
(VARIABLEINSTANCE_);

CREATE INDEX IDX_BYTEARR_FILE ON JBPM_BYTEARRAY
(FILEDEFINITION_);

CREATE INDEX IDX_VARINST_TINSTANCE ON JBPM_VARIABLEINSTANCE
(TASKINSTANCE_);

CREATE INDEX IDX_VARINST_BARR ON JBPM_VARIABLEINSTANCE
(BYTEARRAYVALUE_);

CREATE INDEX IDX_MODINST_DF ON JBPM_MODULEINSTANCE
(TASKMGMTDEFINITION_);

CREATE INDEX JBPM_SWIMLANEINSTANCE_I ON JBPM_SWIMLANEINSTANCE
(TASKMGMTINSTANCE_);

CREATE INDEX HEL_EXPLOG_EXPID_I ON HEL_EXPEDIENT_LOG 
(EXPEDIENT_ID);

CREATE INDEX HEL_EXPLOG_TARGETID_I ON HEL_EXPEDIENT_LOG 
(TARGET_ID);

ALTER TABLE HEL_ACCIO ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI, DEFINICIO_PROCES_ID);

ALTER TABLE HEL_ACL_CLASS ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CLASS);

ALTER TABLE HEL_ACL_ENTRY ADD
  PRIMARY KEY
 (ID);

ALTER TABLE HEL_ACL_OBJECT_IDENTITY ADD
  PRIMARY KEY
 (ID);

ALTER TABLE HEL_ACL_SID ADD
  PRIMARY KEY
 (ID);

ALTER TABLE HEL_ACTION_LOG ADD
  PRIMARY KEY
 (ID);

ALTER TABLE HEL_ALERTA ADD
  PRIMARY KEY
 (ID);

ALTER TABLE HEL_AREA ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI, ENTORN_ID);

ALTER TABLE HEL_AREA_JBPMID ADD
  PRIMARY KEY
 (ID);

ALTER TABLE HEL_AREA_MEMBRE ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI, AREA_ID);

ALTER TABLE HEL_AREA_TIPUS ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI, ENTORN_ID);

ALTER TABLE HEL_CAMP ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI, DEFINICIO_PROCES_ID);

ALTER TABLE HEL_CAMP_AGRUP ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI, DEFINICIO_PROCES_ID);

ALTER TABLE HEL_CAMP_REGISTRE ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (REGISTRE_ID, ORDRE), ADD 
  UNIQUE (REGISTRE_ID, MEMBRE_ID);

ALTER TABLE HEL_CAMP_TASCA ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (TASCA_ID, ORDRE), ADD 
  UNIQUE (CAMP_ID, TASCA_ID);

ALTER TABLE HEL_CARREC ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI, ENTORN_ID);

ALTER TABLE HEL_CARREC_JBPMID ADD
  PRIMARY KEY
 (ID);

ALTER TABLE HEL_CONSULTA ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI, ENTORN_ID);

ALTER TABLE HEL_CONSULTA_CAMP ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CONSULTA_ID, CAMP_CODI, DEFPROC_JBPMKEY, DEFPROC_VERSIO, TIPUS);

ALTER TABLE HEL_CONSULTA_SUB ADD
  PRIMARY KEY
 (PARE_ID, FILL_ID);

ALTER TABLE HEL_DEFINICIO_PROCES ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (JBPM_ID);

ALTER TABLE HEL_DOCUMENT ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI, DEFINICIO_PROCES_ID);

ALTER TABLE HEL_DOCUMENT_STORE ADD
  PRIMARY KEY
 (ID);

ALTER TABLE HEL_DOCUMENT_TASCA ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (TASCA_ID, ORDRE), ADD 
  UNIQUE (DOCUMENT_ID, TASCA_ID);

ALTER TABLE HEL_DOMINI ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI, ENTORN_ID);

ALTER TABLE HEL_ENTORN ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI);

ALTER TABLE HEL_ENUMERACIO ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI, ENTORN_ID);

ALTER TABLE HEL_ENUMERACIO_VALORS ADD
  PRIMARY KEY
 (ID);

ALTER TABLE HEL_ESTAT ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI, EXPEDIENT_TIPUS_ID);

ALTER TABLE HEL_EXPEDIENT ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (PROCESS_INSTANCE_ID);

ALTER TABLE HEL_EXEC_MASEXP ADD
  PRIMARY KEY
 (ID);
  
ALTER TABLE HEL_EXEC_MASSIVA ADD
  PRIMARY KEY
 (ID);  

ALTER TABLE HEL_EXPEDIENT_NOTIF_ELECTR ADD
  PRIMARY KEY
 (ID);  

ALTER TABLE HEL_EXPEDIENT_LOG ADD
  PRIMARY KEY
 (ID);

ALTER TABLE HEL_EXPEDIENT_RELS ADD
  PRIMARY KEY
 (ORIGEN_ID, DESTI_ID);

ALTER TABLE HEL_EXPEDIENT_TIPUS ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (SISTRA_CODTRA), ADD 
  UNIQUE (CODI, ENTORN_ID);

ALTER TABLE HEL_EXPEDIENT_TIPUS_SEQANY ADD
  PRIMARY KEY (ID);

ALTER TABLE HEL_EXPEDIENT_TIPUS_SEQDEFANY ADD
  PRIMARY KEY (ID);

ALTER TABLE HEL_FESTIU ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (DATA);

ALTER TABLE HEL_FIRMA_TASCA ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (TASCA_ID, ORDRE), ADD 
  UNIQUE (DOCUMENT_ID, TASCA_ID);

ALTER TABLE HEL_FORMEXT ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (FORMID);

ALTER TABLE HEL_MAP_SISTRA ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODIHELIUM, EXPEDIENT_TIPUS_ID);

ALTER TABLE HEL_PERMIS ADD
  PRIMARY KEY
 (CODI);

ALTER TABLE HEL_PERSONA ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI);

ALTER TABLE HEL_PORTASIGNATURES ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (DOCUMENT_ID, TOKEN_ID);

ALTER TABLE HEL_REDIR ADD
  PRIMARY KEY
 (ID);

ALTER TABLE HEL_REGISTRE ADD
  PRIMARY KEY
 (ID);

ALTER TABLE HEL_TASCA ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (JBPM_NAME, DEFINICIO_PROCES_ID);

ALTER TABLE HEL_TERMINI ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI, DEFINICIO_PROCES_ID);

ALTER TABLE HEL_TERMINI_INICIAT ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (TERMINI_ID, PROCESS_INSTANCE_ID);

ALTER TABLE HEL_USUARI ADD
  PRIMARY KEY
 (CODI);

ALTER TABLE HEL_USUARI_PERMIS ADD
  PRIMARY KEY
 (CODI, PERMIS);

ALTER TABLE HEL_USUARI_PREFS ADD
  PRIMARY KEY
 (CODI);

ALTER TABLE HEL_VALIDACIO ADD
  PRIMARY KEY
 (ID);

ALTER TABLE HEL_VERSIO ADD
  PRIMARY KEY
 (ID), ADD 
  UNIQUE (CODI), ADD 
  UNIQUE (ORDRE);
  
ALTER TABLE HEL_REPRO ADD
  PRIMARY KEY
 (ID);

ALTER TABLE JBPM_ACTION ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_BYTEARRAY ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_BYTEBLOCK ADD
  PRIMARY KEY
 (PROCESSFILE_, INDEX_);

ALTER TABLE JBPM_COMMENT ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_DECISIONCONDITIONS ADD
  PRIMARY KEY
 (DECISION_, INDEX_);

ALTER TABLE JBPM_DELEGATION ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_EVENT ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_EXCEPTIONHANDLER ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_ID_GROUP ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_ID_MEMBERSHIP ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_ID_USER ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_JOB ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_LOG ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_MODULEDEFINITION ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_MODULEINSTANCE ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_NODE ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_POOLEDACTOR ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_PROCESSDEFINITION ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_PROCESSINSTANCE ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_RUNTIMEACTION ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_SWIMLANE ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_SWIMLANEINSTANCE ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_TASK ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_TASKACTORPOOL ADD
  PRIMARY KEY
 (TASKINSTANCE_, POOLEDACTOR_);

ALTER TABLE JBPM_TASKCONTROLLER ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_TASKINSTANCE ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_TOKEN ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_TOKENVARIABLEMAP ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_TRANSITION ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_VARIABLEACCESS ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE JBPM_VARIABLEINSTANCE ADD
  PRIMARY KEY
 (ID_);

ALTER TABLE HEL_ACCIO ADD
  CONSTRAINT HEL_DEFPROC_ACCIO_FK 
 FOREIGN KEY (DEFINICIO_PROCES_ID) 
 REFERENCES HEL_DEFINICIO_PROCES (ID);
	
ALTER TABLE HEL_EXPEDIENT_NOTIF_ELECTR ADD
  CONSTRAINT HEL_EXPEDIENT_EXPNOTELE_FK 
 FOREIGN KEY (EXPEDIENT_ID) 
 REFERENCES HEL_EXPEDIENT (ID);
			 
ALTER TABLE HEL_ACL_ENTRY ADD
  CONSTRAINT HEL_ACLOBJID_ACLENTRY_FK 
 FOREIGN KEY (ACL_OBJECT_IDENTITY) 
 REFERENCES HEL_ACL_OBJECT_IDENTITY (ID), ADD 
  CONSTRAINT HEL_ACLSID_ACLENTRY_FK 
 FOREIGN KEY (SID) 
 REFERENCES HEL_ACL_SID (ID);

ALTER TABLE HEL_ACL_OBJECT_IDENTITY ADD
  CONSTRAINT HEL_ACLSID_ACLOBJID_FK 
 FOREIGN KEY (OWNER_SID) 
 REFERENCES HEL_ACL_SID (ID), ADD 
  CONSTRAINT HEL_ACLOBJID_ACLOBJID_FK 
 FOREIGN KEY (PARENT_OBJECT) 
 REFERENCES HEL_ACL_OBJECT_IDENTITY (ID), ADD 
  CONSTRAINT HEL_ACLCLASS_ACLOBJID_FK 
 FOREIGN KEY (OBJECT_ID_CLASS) 
 REFERENCES HEL_ACL_CLASS (ID);

ALTER TABLE HEL_ALERTA ADD
  CONSTRAINT HEL_ENTORN_ALERTA_FK 
 FOREIGN KEY (ENTORN_ID) 
 REFERENCES HEL_ENTORN (ID), ADD 
  CONSTRAINT HEL_EXPEDIENT_ALERTA_FK 
 FOREIGN KEY (EXPEDIENT_ID) 
 REFERENCES HEL_EXPEDIENT (ID), ADD 
  CONSTRAINT HEL_TERMINI_ALERTA_FK 
 FOREIGN KEY (TERMINI_INICIAT_ID) 
 REFERENCES HEL_TERMINI_INICIAT (ID);

ALTER TABLE HEL_AREA ADD
  CONSTRAINT HEL_ENTORN_AREA_FK 
 FOREIGN KEY (ENTORN_ID) 
 REFERENCES HEL_ENTORN (ID), ADD 
  CONSTRAINT HEL_AREA_AREA_FK 
 FOREIGN KEY (PARE_ID) 
 REFERENCES HEL_AREA (ID), ADD 
  CONSTRAINT HEL_AREATIPUS_AREA_FK 
 FOREIGN KEY (TIPUS_ID) 
 REFERENCES HEL_AREA_TIPUS (ID);

ALTER TABLE HEL_AREA_MEMBRE ADD
  CONSTRAINT HEL_AREA_AREAMEMBRE_FK 
 FOREIGN KEY (AREA_ID) 
 REFERENCES HEL_AREA (ID);

ALTER TABLE HEL_AREA_TIPUS ADD
  CONSTRAINT HEL_ENTORN_AREATIPUS_FK 
 FOREIGN KEY (ENTORN_ID) 
 REFERENCES HEL_ENTORN (ID);

ALTER TABLE HEL_CAMP ADD
  CONSTRAINT HEL_DEFPROC_CAMP_FK 
 FOREIGN KEY (DEFINICIO_PROCES_ID) 
 REFERENCES HEL_DEFINICIO_PROCES (ID), ADD 
  CONSTRAINT HEL_CAMPAGRUP_CAMP_FK 
 FOREIGN KEY (CAMP_AGRUPACIO_ID) 
 REFERENCES HEL_CAMP_AGRUP (ID), ADD 
  CONSTRAINT HEL_ENUMERACIO_CAMP_FK 
 FOREIGN KEY (ENUMERACIO_ID) 
 REFERENCES HEL_ENUMERACIO (ID), ADD 
  CONSTRAINT HEL_DOMINI_CAMP_FK 
 FOREIGN KEY (DOMINI_ID) 
 REFERENCES HEL_DOMINI (ID), ADD 
  CONSTRAINT HEL_CONSULTA_CAMP_FK 
 FOREIGN KEY (CONSULTA_ID) 
 REFERENCES HEL_CONSULTA (ID);

ALTER TABLE HEL_CAMP_AGRUP ADD
  CONSTRAINT HEL_DEFPROC_CAMPAGRUP_FK 
 FOREIGN KEY (DEFINICIO_PROCES_ID) 
 REFERENCES HEL_DEFINICIO_PROCES (ID);

ALTER TABLE HEL_CAMP_REGISTRE ADD
  CONSTRAINT HEL_CAMP_REGMEMBRE_FK 
 FOREIGN KEY (MEMBRE_ID) 
 REFERENCES HEL_CAMP (ID), ADD 
  CONSTRAINT HEL_CAMP_REGREGISTRE_FK 
 FOREIGN KEY (REGISTRE_ID) 
 REFERENCES HEL_CAMP (ID);

ALTER TABLE HEL_CAMP_TASCA ADD
  CONSTRAINT HEL_TASCA_CAMPTASCA_FK 
 FOREIGN KEY (TASCA_ID) 
 REFERENCES HEL_TASCA (ID), ADD 
  CONSTRAINT HEL_CAMP_CAMPTASCA_FK 
 FOREIGN KEY (CAMP_ID) 
 REFERENCES HEL_CAMP (ID);

ALTER TABLE HEL_CARREC ADD
  CONSTRAINT HEL_ENTORN_CARREC_FK 
 FOREIGN KEY (ENTORN_ID) 
 REFERENCES HEL_ENTORN (ID), ADD 
  CONSTRAINT HEL_AREA_CARREC_FK 
 FOREIGN KEY (AREA_ID) 
 REFERENCES HEL_AREA (ID);

ALTER TABLE HEL_CONSULTA ADD
  CONSTRAINT HEL_ENTORN_CONSULTA_FK 
 FOREIGN KEY (ENTORN_ID) 
 REFERENCES HEL_ENTORN (ID), ADD 
  CONSTRAINT HEL_EXPTIP_CONSULTA_FK 
 FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

ALTER TABLE HEL_CONSULTA_CAMP ADD
  CONSTRAINT HEL_CONSULTA_CONCAMP_FK 
 FOREIGN KEY (CONSULTA_ID) 
 REFERENCES HEL_CONSULTA (ID);

ALTER TABLE HEL_CONSULTA_SUB ADD
  CONSTRAINT HEL_PARE_CONSULTASUB_FK 
 FOREIGN KEY (FILL_ID) 
 REFERENCES HEL_CONSULTA (ID), ADD 
  CONSTRAINT HEL_FILL_CONSULTASUB_FK 
 FOREIGN KEY (PARE_ID) 
 REFERENCES HEL_CONSULTA (ID);

ALTER TABLE HEL_DEFINICIO_PROCES ADD
  CONSTRAINT HEL_ENTORN_DEFPROC_FK 
 FOREIGN KEY (ENTORN_ID) 
 REFERENCES HEL_ENTORN (ID), ADD 
  CONSTRAINT HEL_EXPTIP_DEFPROC_FK 
 FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

ALTER TABLE HEL_DOCUMENT ADD
  CONSTRAINT HEL_DEFPROC_DOCUMENT_FK 
 FOREIGN KEY (DEFINICIO_PROCES_ID) 
 REFERENCES HEL_DEFINICIO_PROCES (ID), ADD 
  CONSTRAINT HEL_CAMP_DOCUMENT_FK 
 FOREIGN KEY (CAMP_DATA_ID) 
 REFERENCES HEL_CAMP (ID);

ALTER TABLE HEL_DOCUMENT_TASCA ADD
  CONSTRAINT HEL_TASCA_DOCTASCA_FK 
 FOREIGN KEY (TASCA_ID) 
 REFERENCES HEL_TASCA (ID), ADD 
  CONSTRAINT HEL_DOCUMENT_DOCTASCA_FK 
 FOREIGN KEY (DOCUMENT_ID) 
 REFERENCES HEL_DOCUMENT (ID);

ALTER TABLE HEL_DOMINI ADD
  CONSTRAINT HEL_ENTORN_DOMINI_FK 
 FOREIGN KEY (ENTORN_ID) 
 REFERENCES HEL_ENTORN (ID), ADD 
  CONSTRAINT HEL_EXPTIP_DOMINI_FK 
 FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

ALTER TABLE HEL_ENUMERACIO ADD
  CONSTRAINT HEL_ENTORN_ENUMERACIO_FK 
 FOREIGN KEY (ENTORN_ID) 
 REFERENCES HEL_ENTORN (ID), ADD 
  CONSTRAINT HEL_EXPTIP_ENUMERACIO_FK 
 FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

ALTER TABLE HEL_ENUMERACIO_VALORS ADD
  CONSTRAINT HEL_ENUMERACIO_VALORS_FK 
 FOREIGN KEY (ENUMERACIO_ID) 
 REFERENCES HEL_ENUMERACIO (ID);

ALTER TABLE HEL_ESTAT ADD
  CONSTRAINT HEL_EXPTIPUS_ESTAT_FK 
 FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

ALTER TABLE HEL_EXPEDIENT ADD
  CONSTRAINT HEL_ENTORN_EXPEDIENT_FK 
 FOREIGN KEY (ENTORN_ID) 
 REFERENCES HEL_ENTORN (ID), ADD 
  CONSTRAINT HEL_ESTAT_EXPEDIENT_FK 
 FOREIGN KEY (ESTAT_ID) 
 REFERENCES HEL_ESTAT (ID), ADD 
  CONSTRAINT HEL_EXPTIPUS_EXPEDIENT_FK 
 FOREIGN KEY (TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

ALTER TABLE HEL_EXEC_MASEXP ADD
  CONSTRAINT HEL_EXPEDIENT_EXEMASEX_FK
 FOREIGN KEY (EXPEDIENT_ID) 
 REFERENCES HEL_EXPEDIENT (ID), ADD 
CONSTRAINT HEL_EXECMAS_EXEMASEX_FK
 FOREIGN KEY (EXECMAS_ID) 
 REFERENCES HEL_EXEC_MASSIVA (ID);
 
ALTER TABLE HEL_EXEC_MASSIVA ADD
  CONSTRAINT HEL_EXPTIPUS_EXEMAS_FK
 FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

ALTER TABLE HEL_EXPEDIENT_LOG ADD
  CONSTRAINT HEL_EXPEDIENT_LOGS_FK
 FOREIGN KEY (EXPEDIENT_ID) 
 REFERENCES HEL_EXPEDIENT (ID);

ALTER TABLE HEL_EXPEDIENT_RELS ADD
  CONSTRAINT HEL_DESTI_EXPREL_FK 
 FOREIGN KEY (DESTI_ID) 
 REFERENCES HEL_EXPEDIENT (ID), ADD 
  CONSTRAINT HEL_ORIGEN_EXPREL_FK 
 FOREIGN KEY (ORIGEN_ID) 
 REFERENCES HEL_EXPEDIENT (ID);

ALTER TABLE HEL_EXPEDIENT_TIPUS ADD
  CONSTRAINT HEL_ENTORN_EXPTIPUS_FK 
 FOREIGN KEY (ENTORN_ID) 
 REFERENCES HEL_ENTORN (ID);

ALTER TABLE HEL_EXPEDIENT_TIPUS_SEQANY ADD
  CONSTRAINT HEL_EXPTIPUS_SEQANY_FK
 FOREIGN KEY (EXPEDIENT_TIPUS)
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

ALTER TABLE HEL_EXPEDIENT_TIPUS_SEQDEFANY ADD
   CONSTRAINT HEL_EXPTIPUS_SEQDEFANY_FK 
  FOREIGN KEY (EXPEDIENT_TIPUS) 
  REFERENCES HEL_EXPEDIENT_TIPUS (ID);

ALTER TABLE HEL_FIRMA_TASCA ADD
  CONSTRAINT HEL_TASCA_FIRTASCA_FK 
 FOREIGN KEY (TASCA_ID) 
 REFERENCES HEL_TASCA (ID), ADD 
  CONSTRAINT HEL_DOCUMENT_FIRTASCA_FK 
 FOREIGN KEY (DOCUMENT_ID) 
 REFERENCES HEL_DOCUMENT (ID);

ALTER TABLE HEL_MAP_SISTRA ADD
  CONSTRAINT HEL_EXPTIPUS_MAP_SISTRA_FK 
 FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

ALTER TABLE HEL_PERSONA ADD
  CONSTRAINT HEL_RELLEU_PERSONA_FK 
 FOREIGN KEY (RELLEU_ID) 
 REFERENCES HEL_PERSONA (ID);

ALTER TABLE HEL_PORTASIGNATURES ADD
   CONSTRAINT HEL_EXPEDIENT_PSIGNA_FK
  FOREIGN KEY (EXPEDIENT_ID) 
  REFERENCES HEL_EXPEDIENT (ID);

ALTER TABLE HEL_TASCA ADD
  CONSTRAINT HEL_DEFPROC_TASCA_FK 
 FOREIGN KEY (DEFINICIO_PROCES_ID) 
 REFERENCES HEL_DEFINICIO_PROCES (ID);

ALTER TABLE HEL_TERMINI ADD
  CONSTRAINT HEL_DEFPROC_TERMINI_FK 
 FOREIGN KEY (DEFINICIO_PROCES_ID) 
 REFERENCES HEL_DEFINICIO_PROCES (ID);

ALTER TABLE HEL_TERMINI_INICIAT ADD
  CONSTRAINT HEL_TERMINI_TERMINIC_FK 
 FOREIGN KEY (TERMINI_ID) 
 REFERENCES HEL_TERMINI (ID);

ALTER TABLE HEL_USUARI_PERMIS ADD
  CONSTRAINT HEL_PERMIS_USUARI_FK 
 FOREIGN KEY (CODI) 
 REFERENCES HEL_USUARI (CODI), ADD 
  CONSTRAINT HEL_USUARI_PERMIS_FK 
 FOREIGN KEY (PERMIS) 
 REFERENCES HEL_PERMIS (CODI);

ALTER TABLE HEL_VALIDACIO ADD
  CONSTRAINT HEL_TASCA_VALIDACIO_FK 
 FOREIGN KEY (TASCA_ID) 
 REFERENCES HEL_TASCA (ID), ADD 
  CONSTRAINT HEL_CAMP_VALIDACIO_FK 
 FOREIGN KEY (CAMP_ID) 
 REFERENCES HEL_CAMP (ID);
 
ALTER TABLE HEL_REPRO ADD
  CONSTRAINT HEL_EXPTIP_REPRO_FK 
 FOREIGN KEY (EXPTIP_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID);

ALTER TABLE JBPM_ACTION ADD
  CONSTRAINT FK_ACTION_EVENT 
 FOREIGN KEY (EVENT_) 
 REFERENCES JBPM_EVENT (ID_), ADD 
  CONSTRAINT FK_ACTION_EXPTHDL 
 FOREIGN KEY (EXCEPTIONHANDLER_) 
 REFERENCES JBPM_EXCEPTIONHANDLER (ID_), ADD 
  CONSTRAINT FK_ACTION_PROCDEF 
 FOREIGN KEY (PROCESSDEFINITION_) 
 REFERENCES JBPM_PROCESSDEFINITION (ID_), ADD 
  CONSTRAINT FK_CRTETIMERACT_TA 
 FOREIGN KEY (TIMERACTION_) 
 REFERENCES JBPM_ACTION (ID_), ADD 
  CONSTRAINT FK_ACTION_ACTNDEL 
 FOREIGN KEY (ACTIONDELEGATION_) 
 REFERENCES JBPM_DELEGATION (ID_), ADD 
  CONSTRAINT FK_ACTION_REFACT 
 FOREIGN KEY (REFERENCEDACTION_) 
 REFERENCES JBPM_ACTION (ID_);

ALTER TABLE JBPM_BYTEARRAY ADD
  CONSTRAINT FK_BYTEARR_FILDEF 
 FOREIGN KEY (FILEDEFINITION_) 
 REFERENCES JBPM_MODULEDEFINITION (ID_);

ALTER TABLE JBPM_BYTEBLOCK ADD
  CONSTRAINT FK_BYTEBLOCK_FILE 
 FOREIGN KEY (PROCESSFILE_) 
 REFERENCES JBPM_BYTEARRAY (ID_);

ALTER TABLE JBPM_COMMENT ADD
  CONSTRAINT FK_COMMENT_TOKEN 
 FOREIGN KEY (TOKEN_) 
 REFERENCES JBPM_TOKEN (ID_), ADD 
  CONSTRAINT FK_COMMENT_TSK 
 FOREIGN KEY (TASKINSTANCE_) 
 REFERENCES JBPM_TASKINSTANCE (ID_);

ALTER TABLE JBPM_DECISIONCONDITIONS ADD
  CONSTRAINT FK_DECCOND_DEC 
 FOREIGN KEY (DECISION_) 
 REFERENCES JBPM_NODE (ID_);

ALTER TABLE JBPM_DELEGATION ADD
  CONSTRAINT FK_DELEGATION_PRCD 
 FOREIGN KEY (PROCESSDEFINITION_) 
 REFERENCES JBPM_PROCESSDEFINITION (ID_);

ALTER TABLE JBPM_EVENT ADD
  CONSTRAINT FK_EVENT_PROCDEF 
 FOREIGN KEY (PROCESSDEFINITION_) 
 REFERENCES JBPM_PROCESSDEFINITION (ID_), ADD 
  CONSTRAINT FK_EVENT_NODE 
 FOREIGN KEY (NODE_) 
 REFERENCES JBPM_NODE (ID_), ADD 
  CONSTRAINT FK_EVENT_TRANS 
 FOREIGN KEY (TRANSITION_) 
 REFERENCES JBPM_TRANSITION (ID_), ADD 
  CONSTRAINT FK_EVENT_TASK 
 FOREIGN KEY (TASK_) 
 REFERENCES JBPM_TASK (ID_);

ALTER TABLE JBPM_ID_GROUP ADD
  CONSTRAINT FK_ID_GRP_PARENT 
 FOREIGN KEY (PARENT_) 
 REFERENCES JBPM_ID_GROUP (ID_);

ALTER TABLE JBPM_ID_MEMBERSHIP ADD
  CONSTRAINT FK_ID_MEMSHIP_GRP 
 FOREIGN KEY (GROUP_) 
 REFERENCES JBPM_ID_GROUP (ID_), ADD 
  CONSTRAINT FK_ID_MEMSHIP_USR 
 FOREIGN KEY (USER_) 
 REFERENCES JBPM_ID_USER (ID_);

ALTER TABLE JBPM_JOB ADD
  CONSTRAINT FK_JOB_TOKEN 
 FOREIGN KEY (TOKEN_) 
 REFERENCES JBPM_TOKEN (ID_), ADD 
  CONSTRAINT FK_JOB_NODE 
 FOREIGN KEY (NODE_) 
 REFERENCES JBPM_NODE (ID_), ADD 
  CONSTRAINT FK_JOB_PRINST 
 FOREIGN KEY (PROCESSINSTANCE_) 
 REFERENCES JBPM_PROCESSINSTANCE (ID_), ADD 
  CONSTRAINT FK_JOB_ACTION 
 FOREIGN KEY (ACTION_) 
 REFERENCES JBPM_ACTION (ID_), ADD 
  CONSTRAINT FK_JOB_TSKINST 
 FOREIGN KEY (TASKINSTANCE_) 
 REFERENCES JBPM_TASKINSTANCE (ID_);

ALTER TABLE JBPM_LOG ADD
  CONSTRAINT FK_LOG_SOURCENODE 
 FOREIGN KEY (SOURCENODE_) 
 REFERENCES JBPM_NODE (ID_), ADD 
  CONSTRAINT FK_LOG_TOKEN 
 FOREIGN KEY (TOKEN_) 
 REFERENCES JBPM_TOKEN (ID_), ADD 
  CONSTRAINT FK_LOG_OLDBYTES 
 FOREIGN KEY (OLDBYTEARRAY_) 
 REFERENCES JBPM_BYTEARRAY (ID_), ADD 
  CONSTRAINT FK_LOG_NEWBYTES 
 FOREIGN KEY (NEWBYTEARRAY_) 
 REFERENCES JBPM_BYTEARRAY (ID_), ADD 
  CONSTRAINT FK_LOG_CHILDTOKEN 
 FOREIGN KEY (CHILD_) 
 REFERENCES JBPM_TOKEN (ID_), ADD 
  CONSTRAINT FK_LOG_DESTNODE 
 FOREIGN KEY (DESTINATIONNODE_) 
 REFERENCES JBPM_NODE (ID_), ADD 
  CONSTRAINT FK_LOG_TASKINST 
 FOREIGN KEY (TASKINSTANCE_) 
 REFERENCES JBPM_TASKINSTANCE (ID_), ADD 
  CONSTRAINT FK_LOG_SWIMINST 
 FOREIGN KEY (SWIMLANEINSTANCE_) 
 REFERENCES JBPM_SWIMLANEINSTANCE (ID_), ADD 
  CONSTRAINT FK_LOG_PARENT 
 FOREIGN KEY (PARENT_) 
 REFERENCES JBPM_LOG (ID_), ADD 
  CONSTRAINT FK_LOG_NODE 
 FOREIGN KEY (NODE_) 
 REFERENCES JBPM_NODE (ID_), ADD 
  CONSTRAINT FK_LOG_ACTION 
 FOREIGN KEY (ACTION_) 
 REFERENCES JBPM_ACTION (ID_), ADD 
  CONSTRAINT FK_LOG_VARINST 
 FOREIGN KEY (VARIABLEINSTANCE_) 
 REFERENCES JBPM_VARIABLEINSTANCE (ID_), ADD 
  CONSTRAINT FK_LOG_TRANSITION 
 FOREIGN KEY (TRANSITION_) 
 REFERENCES JBPM_TRANSITION (ID_);

ALTER TABLE JBPM_MODULEDEFINITION ADD
  CONSTRAINT FK_TSKDEF_START 
 FOREIGN KEY (STARTTASK_) 
 REFERENCES JBPM_TASK (ID_), ADD 
  CONSTRAINT FK_MODDEF_PROCDEF 
 FOREIGN KEY (PROCESSDEFINITION_) 
 REFERENCES JBPM_PROCESSDEFINITION (ID_);

ALTER TABLE JBPM_MODULEINSTANCE ADD
  CONSTRAINT FK_TASKMGTINST_TMD 
 FOREIGN KEY (TASKMGMTDEFINITION_) 
 REFERENCES JBPM_MODULEDEFINITION (ID_), ADD 
  CONSTRAINT FK_MODINST_PRCINST 
 FOREIGN KEY (PROCESSINSTANCE_) 
 REFERENCES JBPM_PROCESSINSTANCE (ID_);

ALTER TABLE JBPM_NODE ADD
  CONSTRAINT FK_PROCST_SBPRCDEF 
 FOREIGN KEY (SUBPROCESSDEFINITION_) 
 REFERENCES JBPM_PROCESSDEFINITION (ID_), ADD 
  CONSTRAINT FK_NODE_PROCDEF 
 FOREIGN KEY (PROCESSDEFINITION_) 
 REFERENCES JBPM_PROCESSDEFINITION (ID_), ADD 
  CONSTRAINT FK_NODE_SCRIPT 
 FOREIGN KEY (SCRIPT_) 
 REFERENCES JBPM_ACTION (ID_), ADD 
  CONSTRAINT FK_NODE_ACTION 
 FOREIGN KEY (ACTION_) 
 REFERENCES JBPM_ACTION (ID_), ADD 
  CONSTRAINT FK_DECISION_DELEG 
 FOREIGN KEY (DECISIONDELEGATION) 
 REFERENCES JBPM_DELEGATION (ID_), ADD 
  CONSTRAINT FK_NODE_SUPERSTATE 
 FOREIGN KEY (SUPERSTATE_) 
 REFERENCES JBPM_NODE (ID_);

ALTER TABLE JBPM_POOLEDACTOR ADD
  CONSTRAINT FK_POOLEDACTOR_SLI 
 FOREIGN KEY (SWIMLANEINSTANCE_) 
 REFERENCES JBPM_SWIMLANEINSTANCE (ID_);

ALTER TABLE JBPM_PROCESSDEFINITION ADD
  CONSTRAINT FK_PROCDEF_STRTSTA 
 FOREIGN KEY (STARTSTATE_) 
 REFERENCES JBPM_NODE (ID_);

ALTER TABLE JBPM_PROCESSINSTANCE ADD
  CONSTRAINT FK_PROCIN_PROCDEF 
 FOREIGN KEY (PROCESSDEFINITION_) 
 REFERENCES JBPM_PROCESSDEFINITION (ID_), ADD 
  CONSTRAINT FK_PROCIN_ROOTTKN 
 FOREIGN KEY (ROOTTOKEN_) 
 REFERENCES JBPM_TOKEN (ID_), ADD 
  CONSTRAINT FK_PROCIN_SPROCTKN 
 FOREIGN KEY (SUPERPROCESSTOKEN_) 
 REFERENCES JBPM_TOKEN (ID_), ADD 
  CONSTRAINT FK_PROCIN_EXP
 FOREIGN KEY (EXPEDIENT_ID_) 
 REFERENCES HEL_EXPEDIENT (ID);

 ALTER TABLE JBPM_PROCESSINSTANCE ADD
  CONSTRAINT FK236E60DAA61A6148 FOREIGN KEY (EXPEDIENT_ID_) 
    REFERENCES HEL_EXPEDIENT (ID);
 
ALTER TABLE JBPM_RUNTIMEACTION ADD
  CONSTRAINT FK_RTACTN_PROCINST 
 FOREIGN KEY (PROCESSINSTANCE_) 
 REFERENCES JBPM_PROCESSINSTANCE (ID_), ADD 
  CONSTRAINT FK_RTACTN_ACTION 
 FOREIGN KEY (ACTION_) 
 REFERENCES JBPM_ACTION (ID_);

ALTER TABLE JBPM_SWIMLANE ADD
  CONSTRAINT FK_SWL_ASSDEL 
 FOREIGN KEY (ASSIGNMENTDELEGATION_) 
 REFERENCES JBPM_DELEGATION (ID_), ADD 
  CONSTRAINT FK_SWL_TSKMGMTDEF 
 FOREIGN KEY (TASKMGMTDEFINITION_) 
 REFERENCES JBPM_MODULEDEFINITION (ID_);

ALTER TABLE JBPM_SWIMLANEINSTANCE ADD
  CONSTRAINT FK_SWIMLANEINST_TM 
 FOREIGN KEY (TASKMGMTINSTANCE_) 
 REFERENCES JBPM_MODULEINSTANCE (ID_), ADD 
  CONSTRAINT FK_SWIMLANEINST_SL 
 FOREIGN KEY (SWIMLANE_) 
 REFERENCES JBPM_SWIMLANE (ID_);

ALTER TABLE JBPM_TASK ADD
  CONSTRAINT FK_TSK_TSKCTRL 
 FOREIGN KEY (TASKCONTROLLER_) 
 REFERENCES JBPM_TASKCONTROLLER (ID_), ADD 
  CONSTRAINT FK_TASK_ASSDEL 
 FOREIGN KEY (ASSIGNMENTDELEGATION_) 
 REFERENCES JBPM_DELEGATION (ID_), ADD 
  CONSTRAINT FK_TASK_TASKNODE 
 FOREIGN KEY (TASKNODE_) 
 REFERENCES JBPM_NODE (ID_), ADD 
  CONSTRAINT FK_TASK_PROCDEF 
 FOREIGN KEY (PROCESSDEFINITION_) 
 REFERENCES JBPM_PROCESSDEFINITION (ID_), ADD 
  CONSTRAINT FK_TASK_STARTST 
 FOREIGN KEY (STARTSTATE_) 
 REFERENCES JBPM_NODE (ID_), ADD 
  CONSTRAINT FK_TASK_TASKMGTDEF 
 FOREIGN KEY (TASKMGMTDEFINITION_) 
 REFERENCES JBPM_MODULEDEFINITION (ID_), ADD 
  CONSTRAINT FK_TASK_SWIMLANE 
 FOREIGN KEY (SWIMLANE_) 
 REFERENCES JBPM_SWIMLANE (ID_);

ALTER TABLE JBPM_TASKACTORPOOL ADD
  CONSTRAINT FK_TSKACTPOL_PLACT 
 FOREIGN KEY (POOLEDACTOR_) 
 REFERENCES JBPM_POOLEDACTOR (ID_), ADD 
  CONSTRAINT FK_TASKACTPL_TSKI 
 FOREIGN KEY (TASKINSTANCE_) 
 REFERENCES JBPM_TASKINSTANCE (ID_);

ALTER TABLE JBPM_TASKCONTROLLER ADD
  CONSTRAINT FK_TSKCTRL_DELEG 
 FOREIGN KEY (TASKCONTROLLERDELEGATION_) 
 REFERENCES JBPM_DELEGATION (ID_);

ALTER TABLE JBPM_TASKINSTANCE ADD
  CONSTRAINT FK_TSKINS_PRCINS 
 FOREIGN KEY (PROCINST_) 
 REFERENCES JBPM_PROCESSINSTANCE (ID_), ADD 
  CONSTRAINT FK_TASKINST_TMINST 
 FOREIGN KEY (TASKMGMTINSTANCE_) 
 REFERENCES JBPM_MODULEINSTANCE (ID_), ADD 
  CONSTRAINT FK_TASKINST_TOKEN 
 FOREIGN KEY (TOKEN_) 
 REFERENCES JBPM_TOKEN (ID_), ADD 
  CONSTRAINT FK_TASKINST_SLINST 
 FOREIGN KEY (SWIMLANINSTANCE_) 
 REFERENCES JBPM_SWIMLANEINSTANCE (ID_), ADD 
  CONSTRAINT FK_TASKINST_TASK 
 FOREIGN KEY (TASK_) 
 REFERENCES JBPM_TASK (ID_);

ALTER TABLE JBPM_TOKEN ADD
  CONSTRAINT FK_TOKEN_PARENT 
 FOREIGN KEY (PARENT_) 
 REFERENCES JBPM_TOKEN (ID_), ADD 
  CONSTRAINT FK_TOKEN_NODE 
 FOREIGN KEY (NODE_) 
 REFERENCES JBPM_NODE (ID_), ADD 
  CONSTRAINT FK_TOKEN_PROCINST 
 FOREIGN KEY (PROCESSINSTANCE_) 
 REFERENCES JBPM_PROCESSINSTANCE (ID_), ADD 
  CONSTRAINT FK_TOKEN_SUBPI 
 FOREIGN KEY (SUBPROCESSINSTANCE_) 
 REFERENCES JBPM_PROCESSINSTANCE (ID_);

ALTER TABLE JBPM_TOKENVARIABLEMAP ADD
  CONSTRAINT FK_TKVARMAP_CTXT 
 FOREIGN KEY (CONTEXTINSTANCE_) 
 REFERENCES JBPM_MODULEINSTANCE (ID_), ADD 
  CONSTRAINT FK_TKVARMAP_TOKEN 
 FOREIGN KEY (TOKEN_) 
 REFERENCES JBPM_TOKEN (ID_);

ALTER TABLE JBPM_TRANSITION ADD
  CONSTRAINT FK_TRANSITION_TO 
 FOREIGN KEY (TO_) 
 REFERENCES JBPM_NODE (ID_), ADD 
  CONSTRAINT FK_TRANS_PROCDEF 
 FOREIGN KEY (PROCESSDEFINITION_) 
 REFERENCES JBPM_PROCESSDEFINITION (ID_), ADD 
  CONSTRAINT FK_TRANSITION_FROM 
 FOREIGN KEY (FROM_) 
 REFERENCES JBPM_NODE (ID_);

ALTER TABLE JBPM_VARIABLEACCESS ADD
  CONSTRAINT FK_VARACC_TSKCTRL 
 FOREIGN KEY (TASKCONTROLLER_) 
 REFERENCES JBPM_TASKCONTROLLER (ID_), ADD 
  CONSTRAINT FK_VARACC_SCRIPT 
 FOREIGN KEY (SCRIPT_) 
 REFERENCES JBPM_ACTION (ID_), ADD 
  CONSTRAINT FK_VARACC_PROCST 
 FOREIGN KEY (PROCESSSTATE_) 
 REFERENCES JBPM_NODE (ID_);

ALTER TABLE JBPM_VARIABLEINSTANCE ADD
  CONSTRAINT FK_VARINST_TK 
 FOREIGN KEY (TOKEN_) 
 REFERENCES JBPM_TOKEN (ID_), ADD 
  CONSTRAINT FK_VARINST_TKVARMP 
 FOREIGN KEY (TOKENVARIABLEMAP_) 
 REFERENCES JBPM_TOKENVARIABLEMAP (ID_), ADD 
  CONSTRAINT FK_VARINST_PRCINST 
 FOREIGN KEY (PROCESSINSTANCE_) 
 REFERENCES JBPM_PROCESSINSTANCE (ID_), ADD 
  CONSTRAINT FK_VAR_TSKINST 
 FOREIGN KEY (TASKINSTANCE_) 
 REFERENCES JBPM_TASKINSTANCE (ID_), ADD 
  CONSTRAINT FK_BYTEINST_ARRAY 
 FOREIGN KEY (BYTEARRAYVALUE_) 
 REFERENCES JBPM_BYTEARRAY (ID_);

CREATE SEQUENCE HIBERNATE_SEQUENCE;