--
-- 01 identity
--
create table ACT_ID_PROPERTY (
								 NAME_ NVARCHAR2(64),
	                             VALUE_ NVARCHAR2(300),
	                             REV_ INTEGER,
	                             primary key (NAME_)
);

insert into ACT_ID_PROPERTY
values ('schema.version', '6.8.0.0', 1);

create table ACT_ID_BYTEARRAY (
								  ID_ NVARCHAR2(64),
	                              REV_ INTEGER,
	                              NAME_ NVARCHAR2(255),
	                              BYTES_ BLOB,
	                              primary key (ID_)
);

create table ACT_ID_GROUP (
							  ID_ NVARCHAR2(64),
	                          REV_ INTEGER,
	                          NAME_ NVARCHAR2(255),
	                          TYPE_ NVARCHAR2(255),
	                          primary key (ID_)
);

create table ACT_ID_MEMBERSHIP (
								   USER_ID_ NVARCHAR2(64),
	                               GROUP_ID_ NVARCHAR2(64),
	                               primary key (USER_ID_, GROUP_ID_)
);

create table ACT_ID_USER (
							 ID_ NVARCHAR2(64),
	                         REV_ INTEGER,
	                         FIRST_ NVARCHAR2(255),
	                         LAST_ NVARCHAR2(255),
	                         DISPLAY_NAME_ NVARCHAR2(255),
	                         EMAIL_ NVARCHAR2(255),
	                         PWD_ NVARCHAR2(255),
	                         PICTURE_ID_ NVARCHAR2(64),
	                         TENANT_ID_ NVARCHAR2(255) default '',
	                         primary key (ID_)
);

create table ACT_ID_INFO (
							 ID_ NVARCHAR2(64),
	                         REV_ INTEGER,
	                         USER_ID_ NVARCHAR2(64),
	                         TYPE_ NVARCHAR2(64),
	                         KEY_ NVARCHAR2(255),
	                         VALUE_ NVARCHAR2(255),
	                         PASSWORD_ BLOB,
	                         PARENT_ID_ NVARCHAR2(255),
	                         primary key (ID_)
);

create table ACT_ID_TOKEN (
							  ID_ NVARCHAR2(64) not null,
	                          REV_ INTEGER,
	                          TOKEN_VALUE_ NVARCHAR2(255),
	                          TOKEN_DATE_ TIMESTAMP(6),
	                          IP_ADDRESS_ NVARCHAR2(255),
	                          USER_AGENT_ NVARCHAR2(255),
	                          USER_ID_ NVARCHAR2(255),
	                          TOKEN_DATA_ NVARCHAR2(2000),
	                          primary key (ID_)
);

create table ACT_ID_PRIV (
							 ID_ NVARCHAR2(64) not null,
	                         NAME_ NVARCHAR2(255) not null,
	                         primary key (ID_)
);

create table ACT_ID_PRIV_MAPPING (
									 ID_ NVARCHAR2(64) not null,
	                                 PRIV_ID_ NVARCHAR2(64) not null,
	                                 USER_ID_ NVARCHAR2(255),
	                                 GROUP_ID_ NVARCHAR2(255),
	                                 primary key (ID_)
);

create index ACT_IDX_MEMB_GROUP on ACT_ID_MEMBERSHIP(GROUP_ID_);
alter table ACT_ID_MEMBERSHIP
	add constraint ACT_FK_MEMB_GROUP
		foreign key (GROUP_ID_)
			references ACT_ID_GROUP (ID_);

create index ACT_IDX_MEMB_USER on ACT_ID_MEMBERSHIP(USER_ID_);
alter table ACT_ID_MEMBERSHIP
	add constraint ACT_FK_MEMB_USER
		foreign key (USER_ID_)
			references ACT_ID_USER (ID_);

create index ACT_IDX_PRIV_MAPPING on ACT_ID_PRIV_MAPPING(PRIV_ID_);
alter table ACT_ID_PRIV_MAPPING
	add constraint ACT_FK_PRIV_MAPPING
		foreign key (PRIV_ID_)
			references ACT_ID_PRIV (ID_);

create index ACT_IDX_PRIV_USER on ACT_ID_PRIV_MAPPING(USER_ID_);
create index ACT_IDX_PRIV_GROUP on ACT_ID_PRIV_MAPPING(GROUP_ID_);

alter table ACT_ID_PRIV
	add constraint ACT_UNIQ_PRIV_NAME
		unique (NAME_);

--
-- 02 common
--
create table ACT_GE_PROPERTY (
								 NAME_ NVARCHAR2(64),
	                             VALUE_ NVARCHAR2(300),
	                             REV_ INTEGER,
	                             primary key (NAME_)
);

create table ACT_GE_BYTEARRAY (
								  ID_ NVARCHAR2(64),
	                              REV_ INTEGER,
	                              NAME_ NVARCHAR2(255),
	                              DEPLOYMENT_ID_ NVARCHAR2(64),
	                              BYTES_ BLOB,
	                              GENERATED_ NUMBER(1,0) CHECK (GENERATED_ IN (1,0)),
	                              primary key (ID_)
);

insert into ACT_GE_PROPERTY
values ('common.schema.version', '6.8.0.0', 1);

insert into ACT_GE_PROPERTY
values ('next.dbid', '1', 1);

--
-- 03 identitylink
--
create table ACT_RU_IDENTITYLINK (
									 ID_ NVARCHAR2(64),
	                                 REV_ INTEGER,
	                                 GROUP_ID_ NVARCHAR2(255),
	                                 TYPE_ NVARCHAR2(255),
	                                 USER_ID_ NVARCHAR2(255),
	                                 TASK_ID_ NVARCHAR2(64),
	                                 PROC_INST_ID_ NVARCHAR2(64),
	                                 PROC_DEF_ID_ NVARCHAR2(64),
	                                 SCOPE_ID_ NVARCHAR2(255),
	                                 SUB_SCOPE_ID_ NVARCHAR2(255),
	                                 SCOPE_TYPE_ NVARCHAR2(255),
	                                 SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                                 primary key (ID_)
);

create index ACT_IDX_IDENT_LNK_USER on ACT_RU_IDENTITYLINK(USER_ID_);
create index ACT_IDX_IDENT_LNK_GROUP on ACT_RU_IDENTITYLINK(GROUP_ID_);
create index ACT_IDX_IDENT_LNK_SCOPE on ACT_RU_IDENTITYLINK(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_IDENT_LNK_SUB_SCOPE on ACT_RU_IDENTITYLINK(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_IDENT_LNK_SCOPE_DEF on ACT_RU_IDENTITYLINK(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

insert into ACT_GE_PROPERTY values ('identitylink.schema.version', '6.8.0.0', 1);

--
-- 04 identitylink.history
--
create table ACT_HI_IDENTITYLINK (
									 ID_ NVARCHAR2(64),
	                                 GROUP_ID_ NVARCHAR2(255),
	                                 TYPE_ NVARCHAR2(255),
	                                 USER_ID_ NVARCHAR2(255),
	                                 TASK_ID_ NVARCHAR2(64),
	                                 CREATE_TIME_ TIMESTAMP(6),
	                                 PROC_INST_ID_ NVARCHAR2(64),
	                                 SCOPE_ID_ NVARCHAR2(255),
	                                 SUB_SCOPE_ID_ NVARCHAR2(255),
	                                 SCOPE_TYPE_ NVARCHAR2(255),
	                                 SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                                 primary key (ID_)
);

create index ACT_IDX_HI_IDENT_LNK_USER on ACT_HI_IDENTITYLINK(USER_ID_);
create index ACT_IDX_HI_IDENT_LNK_SCOPE on ACT_HI_IDENTITYLINK(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_HI_IDENT_LNK_SUB_SCOPE on ACT_HI_IDENTITYLINK(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_HI_IDENT_LNK_SCOPE_DEF on ACT_HI_IDENTITYLINK(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

--
-- 05 entitylink
--
create table ACT_RU_ENTITYLINK (
								   ID_ NVARCHAR2(64),
	                               REV_ INTEGER,
	                               CREATE_TIME_ TIMESTAMP(6),
	                               LINK_TYPE_ NVARCHAR2(255),
	                               SCOPE_ID_ NVARCHAR2(255),
	                               SUB_SCOPE_ID_ NVARCHAR2(255),
	                               SCOPE_TYPE_ NVARCHAR2(255),
	                               SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                               PARENT_ELEMENT_ID_ NVARCHAR2(255),
	                               REF_SCOPE_ID_ NVARCHAR2(255),
	                               REF_SCOPE_TYPE_ NVARCHAR2(255),
	                               REF_SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                               ROOT_SCOPE_ID_ NVARCHAR2(255),
	                               ROOT_SCOPE_TYPE_ NVARCHAR2(255),
	                               HIERARCHY_TYPE_ NVARCHAR2(255),
	                               primary key (ID_)
);

create index ACT_IDX_ENT_LNK_SCOPE on ACT_RU_ENTITYLINK(SCOPE_ID_, SCOPE_TYPE_, LINK_TYPE_);
create index ACT_IDX_ENT_LNK_REF_SCOPE on ACT_RU_ENTITYLINK(REF_SCOPE_ID_, REF_SCOPE_TYPE_, LINK_TYPE_);
create index ACT_IDX_ENT_LNK_ROOT_SCOPE on ACT_RU_ENTITYLINK(ROOT_SCOPE_ID_, ROOT_SCOPE_TYPE_, LINK_TYPE_);
create index ACT_IDX_ENT_LNK_SCOPE_DEF on ACT_RU_ENTITYLINK(SCOPE_DEFINITION_ID_, SCOPE_TYPE_, LINK_TYPE_);

insert into ACT_GE_PROPERTY values ('entitylink.schema.version', '6.8.0.0', 1);

--
-- 06 entitylink.history
--
create table ACT_HI_ENTITYLINK (
								   ID_ NVARCHAR2(64),
	                               LINK_TYPE_ NVARCHAR2(255),
	                               CREATE_TIME_ TIMESTAMP(6),
	                               SCOPE_ID_ NVARCHAR2(255),
	                               SUB_SCOPE_ID_ NVARCHAR2(255),
	                               SCOPE_TYPE_ NVARCHAR2(255),
	                               SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                               PARENT_ELEMENT_ID_ NVARCHAR2(255),
	                               REF_SCOPE_ID_ NVARCHAR2(255),
	                               REF_SCOPE_TYPE_ NVARCHAR2(255),
	                               REF_SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                               ROOT_SCOPE_ID_ NVARCHAR2(255),
	                               ROOT_SCOPE_TYPE_ NVARCHAR2(255),
	                               HIERARCHY_TYPE_ NVARCHAR2(255),
	                               primary key (ID_)
);

create index ACT_IDX_HI_ENT_LNK_SCOPE on ACT_HI_ENTITYLINK(SCOPE_ID_, SCOPE_TYPE_, LINK_TYPE_);
create index ACT_IDX_HI_ENT_LNK_REF_SCOPE on ACT_HI_ENTITYLINK(REF_SCOPE_ID_, REF_SCOPE_TYPE_, LINK_TYPE_);
create index ACT_IDX_HI_ENT_LNK_ROOT_SCOPE on ACT_HI_ENTITYLINK(ROOT_SCOPE_ID_, ROOT_SCOPE_TYPE_, LINK_TYPE_);
create index ACT_IDX_HI_ENT_LNK_SCOPE_DEF on ACT_HI_ENTITYLINK(SCOPE_DEFINITION_ID_, SCOPE_TYPE_, LINK_TYPE_);

--
-- 07 eventsubscription
--
create table ACT_RU_EVENT_SUBSCR (
									 ID_ NVARCHAR2(64) not null,
	                                 REV_ integer,
	                                 EVENT_TYPE_ NVARCHAR2(255) not null,
	                                 EVENT_NAME_ NVARCHAR2(255),
	                                 EXECUTION_ID_ NVARCHAR2(64),
	                                 PROC_INST_ID_ NVARCHAR2(64),
	                                 ACTIVITY_ID_ NVARCHAR2(64),
	                                 CONFIGURATION_ NVARCHAR2(255),
	                                 CREATED_ TIMESTAMP(6) not null,
	                                 PROC_DEF_ID_ NVARCHAR2(64),
	                                 SUB_SCOPE_ID_ NVARCHAR2(64),
	                                 SCOPE_ID_ NVARCHAR2(64),
	                                 SCOPE_DEFINITION_ID_ NVARCHAR2(64),
	                                 SCOPE_TYPE_ NVARCHAR2(64),
	                                 LOCK_TIME_ TIMESTAMP(6),
	                                 LOCK_OWNER_ NVARCHAR2(255),
	                                 TENANT_ID_ NVARCHAR2(255) DEFAULT '',
	                                 primary key (ID_)
);

create index ACT_IDX_EVENT_SUBSCR_CONFIG_ on ACT_RU_EVENT_SUBSCR(CONFIGURATION_);
create index ACT_IDX_EVENT_SUBSCR on ACT_RU_EVENT_SUBSCR(EXECUTION_ID_);
create index ACT_IDX_EVENT_SUBSCR_SCOPEREF_ on ACT_RU_EVENT_SUBSCR(SCOPE_ID_, SCOPE_TYPE_);

insert into ACT_GE_PROPERTY values ('eventsubscription.schema.version', '6.8.0.0', 1);

--
-- 08 task
--
create table ACT_RU_TASK (
							 ID_ NVARCHAR2(64),
	                         REV_ INTEGER,
	                         EXECUTION_ID_ NVARCHAR2(64),
	                         PROC_INST_ID_ NVARCHAR2(64),
	                         PROC_DEF_ID_ NVARCHAR2(64),
	                         TASK_DEF_ID_ NVARCHAR2(64),
	                         SCOPE_ID_ NVARCHAR2(255),
	                         SUB_SCOPE_ID_ NVARCHAR2(255),
	                         SCOPE_TYPE_ NVARCHAR2(255),
	                         SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                         PROPAGATED_STAGE_INST_ID_ NVARCHAR2(255),
	                         NAME_ NVARCHAR2(255),
	                         PARENT_TASK_ID_ NVARCHAR2(64),
	                         DESCRIPTION_ NVARCHAR2(2000),
	                         TASK_DEF_KEY_ NVARCHAR2(255),
	                         OWNER_ NVARCHAR2(255),
	                         ASSIGNEE_ NVARCHAR2(255),
	                         DELEGATION_ NVARCHAR2(64),
	                         PRIORITY_ INTEGER,
	                         CREATE_TIME_ TIMESTAMP(6),
	                         DUE_DATE_ TIMESTAMP(6),
	                         CATEGORY_ NVARCHAR2(255),
	                         SUSPENSION_STATE_ INTEGER,
	                         TENANT_ID_ NVARCHAR2(255) DEFAULT '',
	                         FORM_KEY_ NVARCHAR2(255),
	                         CLAIM_TIME_ TIMESTAMP(6),
	                         IS_COUNT_ENABLED_ NUMBER(1,0) CHECK (IS_COUNT_ENABLED_ IN (1,0)),
	                         VAR_COUNT_ INTEGER,
	                         ID_LINK_COUNT_ INTEGER,
	                         SUB_TASK_COUNT_ INTEGER,
	                         primary key (ID_)
);

create index ACT_IDX_TASK_CREATE on ACT_RU_TASK(CREATE_TIME_);
create index ACT_IDX_TASK_SCOPE on ACT_RU_TASK(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_TASK_SUB_SCOPE on ACT_RU_TASK(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_TASK_SCOPE_DEF on ACT_RU_TASK(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

insert into ACT_GE_PROPERTY values ('task.schema.version', '6.8.0.0', 1);

--
-- 09 task.history
--
create table ACT_HI_TASKINST (
								 ID_ NVARCHAR2(64) not null,
	                             REV_ INTEGER default 1,
	                             PROC_DEF_ID_ NVARCHAR2(64),
	                             TASK_DEF_ID_ NVARCHAR2(64),
	                             TASK_DEF_KEY_ NVARCHAR2(255),
	                             PROC_INST_ID_ NVARCHAR2(64),
	                             EXECUTION_ID_ NVARCHAR2(64),
	                             SCOPE_ID_ NVARCHAR2(255),
	                             SUB_SCOPE_ID_ NVARCHAR2(255),
	                             SCOPE_TYPE_ NVARCHAR2(255),
	                             SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                             PROPAGATED_STAGE_INST_ID_ NVARCHAR2(255),
	                             PARENT_TASK_ID_ NVARCHAR2(64),
	                             NAME_ NVARCHAR2(255),
	                             DESCRIPTION_ NVARCHAR2(2000),
	                             OWNER_ NVARCHAR2(255),
	                             ASSIGNEE_ NVARCHAR2(255),
	                             START_TIME_ TIMESTAMP(6) not null,
	                             CLAIM_TIME_ TIMESTAMP(6),
	                             END_TIME_ TIMESTAMP(6),
	                             DURATION_ NUMBER(19,0),
	                             DELETE_REASON_ NVARCHAR2(2000),
	                             PRIORITY_ INTEGER,
	                             DUE_DATE_ TIMESTAMP(6),
	                             FORM_KEY_ NVARCHAR2(255),
	                             CATEGORY_ NVARCHAR2(255),
	                             TENANT_ID_ NVARCHAR2(255) default '',
	                             LAST_UPDATED_TIME_ TIMESTAMP(6),
	                             primary key (ID_)
);

create table ACT_HI_TSK_LOG (
								ID_ NUMBER(19),
	                            TYPE_ NVARCHAR2(64),
	                            TASK_ID_ NVARCHAR2(64) not null,
	                            TIME_STAMP_ TIMESTAMP(6) not null,
	                            USER_ID_ NVARCHAR2(255),
	                            DATA_ NVARCHAR2(2000),
	                            EXECUTION_ID_ NVARCHAR2(64),
	                            PROC_INST_ID_ NVARCHAR2(64),
	                            PROC_DEF_ID_ NVARCHAR2(64),
	                            SCOPE_ID_ NVARCHAR2(255),
	                            SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                            SUB_SCOPE_ID_ NVARCHAR2(255),
	                            SCOPE_TYPE_ NVARCHAR2(255),
	                            TENANT_ID_ NVARCHAR2(255) default '',
	                            primary key (ID_)
);

create sequence act_hi_task_evt_log_seq start with 1 increment by 1;

create index ACT_IDX_HI_TASK_SCOPE on ACT_HI_TASKINST(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_HI_TASK_SUB_SCOPE on ACT_HI_TASKINST(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_HI_TASK_SCOPE_DEF on ACT_HI_TASKINST(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

--
-- 10 variable
--
create table ACT_RU_VARIABLE (
								 ID_ NVARCHAR2(64) not null,
	                             REV_ INTEGER,
	                             TYPE_ NVARCHAR2(255) not null,
	                             NAME_ NVARCHAR2(255) not null,
	                             EXECUTION_ID_ NVARCHAR2(64),
	                             PROC_INST_ID_ NVARCHAR2(64),
	                             TASK_ID_ NVARCHAR2(64),
	                             SCOPE_ID_ NVARCHAR2(255),
	                             SUB_SCOPE_ID_ NVARCHAR2(255),
	                             SCOPE_TYPE_ NVARCHAR2(255),
	                             BYTEARRAY_ID_ NVARCHAR2(64),
	                             DOUBLE_ NUMBER(*,10),
	                             LONG_ NUMBER(19,0),
	                             TEXT_ NVARCHAR2(2000),
	                             TEXT2_ NVARCHAR2(2000),
	                             primary key (ID_)
);

create index ACT_IDX_RU_VAR_SCOPE_ID_TYPE on ACT_RU_VARIABLE(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_RU_VAR_SUB_ID_TYPE on ACT_RU_VARIABLE(SUB_SCOPE_ID_, SCOPE_TYPE_);

create index ACT_IDX_VAR_BYTEARRAY on ACT_RU_VARIABLE(BYTEARRAY_ID_);
alter table ACT_RU_VARIABLE
	add constraint ACT_FK_VAR_BYTEARRAY
		foreign key (BYTEARRAY_ID_)
			references ACT_GE_BYTEARRAY (ID_);

insert into ACT_GE_PROPERTY values ('variable.schema.version', '6.8.0.0', 1);

--
-- 11 variable.history
--
create table ACT_HI_VARINST (
								ID_ NVARCHAR2(64) not null,
	                            REV_ INTEGER default 1,
	                            PROC_INST_ID_ NVARCHAR2(64),
	                            EXECUTION_ID_ NVARCHAR2(64),
	                            TASK_ID_ NVARCHAR2(64),
	                            NAME_ NVARCHAR2(255) not null,
	                            VAR_TYPE_ NVARCHAR2(100),
	                            SCOPE_ID_ NVARCHAR2(255),
	                            SUB_SCOPE_ID_ NVARCHAR2(255),
	                            SCOPE_TYPE_ NVARCHAR2(255),
	                            BYTEARRAY_ID_ NVARCHAR2(64),
	                            DOUBLE_ NUMBER(*,10),
	                            LONG_ NUMBER(19,0),
	                            TEXT_ NVARCHAR2(2000),
	                            TEXT2_ NVARCHAR2(2000),
	                            CREATE_TIME_ TIMESTAMP(6),
	                            LAST_UPDATED_TIME_ TIMESTAMP(6),
	                            primary key (ID_)
);

create index ACT_IDX_HI_PROCVAR_NAME_TYPE on ACT_HI_VARINST(NAME_, VAR_TYPE_);
create index ACT_IDX_HI_VAR_SCOPE_ID_TYPE on ACT_HI_VARINST(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_HI_VAR_SUB_ID_TYPE on ACT_HI_VARINST(SUB_SCOPE_ID_, SCOPE_TYPE_);

--
-- 12 batch
--
create table FLW_RU_BATCH (
							  ID_ NVARCHAR2(64) not null,
	                          REV_ INTEGER,
	                          TYPE_ NVARCHAR2(64) not null,
	                          SEARCH_KEY_ NVARCHAR2(255),
	                          SEARCH_KEY2_ NVARCHAR2(255),
	                          CREATE_TIME_ TIMESTAMP(6) not null,
	                          COMPLETE_TIME_ TIMESTAMP(6),
	                          STATUS_ NVARCHAR2(255),
	                          BATCH_DOC_ID_ NVARCHAR2(64),
	                          TENANT_ID_ NVARCHAR2(255) default '',
	                          primary key (ID_)
);

create table FLW_RU_BATCH_PART (
								   ID_ NVARCHAR2(64) not null,
	                               REV_ INTEGER,
	                               BATCH_ID_ NVARCHAR2(64),
	                               TYPE_ NVARCHAR2(64) not null,
	                               SCOPE_ID_ NVARCHAR2(64),
	                               SUB_SCOPE_ID_ NVARCHAR2(64),
	                               SCOPE_TYPE_ NVARCHAR2(64),
	                               SEARCH_KEY_ NVARCHAR2(255),
	                               SEARCH_KEY2_ NVARCHAR2(255),
	                               CREATE_TIME_ TIMESTAMP(6) not null,
	                               COMPLETE_TIME_ TIMESTAMP(6),
	                               STATUS_ NVARCHAR2(255),
	                               RESULT_DOC_ID_ NVARCHAR2(64),
	                               TENANT_ID_ NVARCHAR2(255) default '',
	                               primary key (ID_)
);

create index FLW_IDX_BATCH_PART on FLW_RU_BATCH_PART(BATCH_ID_);

alter table FLW_RU_BATCH_PART
	add constraint FLW_FK_BATCH_PART_PARENT
		foreign key (BATCH_ID_)
			references FLW_RU_BATCH (ID_);

insert into ACT_GE_PROPERTY values ('batch.schema.version', '6.8.0.0', 1);

--
-- 13 job
--
create table ACT_RU_JOB (
							ID_ NVARCHAR2(64) NOT NULL,
	                        REV_ INTEGER,
	                        CATEGORY_ NVARCHAR2(255),
	                        TYPE_ NVARCHAR2(255) NOT NULL,
	                        LOCK_EXP_TIME_ TIMESTAMP(6),
	                        LOCK_OWNER_ NVARCHAR2(255),
	                        EXCLUSIVE_ NUMBER(1,0) CHECK (EXCLUSIVE_ IN (1,0)),
	                        EXECUTION_ID_ NVARCHAR2(64),
	                        PROCESS_INSTANCE_ID_ NVARCHAR2(64),
	                        PROC_DEF_ID_ NVARCHAR2(64),
	                        ELEMENT_ID_ NVARCHAR2(255),
	                        ELEMENT_NAME_ NVARCHAR2(255),
	                        SCOPE_ID_ NVARCHAR2(255),
	                        SUB_SCOPE_ID_ NVARCHAR2(255),
	                        SCOPE_TYPE_ NVARCHAR2(255),
	                        SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                        CORRELATION_ID_ NVARCHAR2(255),
	                        RETRIES_ INTEGER,
	                        EXCEPTION_STACK_ID_ NVARCHAR2(64),
	                        EXCEPTION_MSG_ NVARCHAR2(2000),
	                        DUEDATE_ TIMESTAMP(6),
	                        REPEAT_ NVARCHAR2(255),
	                        HANDLER_TYPE_ NVARCHAR2(255),
	                        HANDLER_CFG_ NVARCHAR2(2000),
	                        CUSTOM_VALUES_ID_ NVARCHAR2(64),
	                        CREATE_TIME_ TIMESTAMP(6),
	                        TENANT_ID_ NVARCHAR2(255) DEFAULT '',
	                        primary key (ID_)
);

create table ACT_RU_TIMER_JOB (
								  ID_ NVARCHAR2(64) NOT NULL,
	                              REV_ INTEGER,
	                              CATEGORY_ NVARCHAR2(255),
	                              TYPE_ NVARCHAR2(255) NOT NULL,
	                              LOCK_EXP_TIME_ TIMESTAMP(6),
	                              LOCK_OWNER_ NVARCHAR2(255),
	                              EXCLUSIVE_ NUMBER(1,0) CHECK (EXCLUSIVE_ IN (1,0)),
	                              EXECUTION_ID_ NVARCHAR2(64),
	                              PROCESS_INSTANCE_ID_ NVARCHAR2(64),
	                              PROC_DEF_ID_ NVARCHAR2(64),
	                              ELEMENT_ID_ NVARCHAR2(255),
	                              ELEMENT_NAME_ NVARCHAR2(255),
	                              SCOPE_ID_ NVARCHAR2(255),
	                              SUB_SCOPE_ID_ NVARCHAR2(255),
	                              SCOPE_TYPE_ NVARCHAR2(255),
	                              SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                              CORRELATION_ID_ NVARCHAR2(255),
	                              RETRIES_ INTEGER,
	                              EXCEPTION_STACK_ID_ NVARCHAR2(64),
	                              EXCEPTION_MSG_ NVARCHAR2(2000),
	                              DUEDATE_ TIMESTAMP(6),
	                              REPEAT_ NVARCHAR2(255),
	                              HANDLER_TYPE_ NVARCHAR2(255),
	                              HANDLER_CFG_ NVARCHAR2(2000),
	                              CUSTOM_VALUES_ID_ NVARCHAR2(64),
	                              CREATE_TIME_ TIMESTAMP(6),
	                              TENANT_ID_ NVARCHAR2(255) DEFAULT '',
	                              primary key (ID_)
);

create table ACT_RU_SUSPENDED_JOB (
									  ID_ NVARCHAR2(64) NOT NULL,
	                                  REV_ INTEGER,
	                                  CATEGORY_ NVARCHAR2(255),
	                                  TYPE_ NVARCHAR2(255) NOT NULL,
	                                  EXCLUSIVE_ NUMBER(1,0) CHECK (EXCLUSIVE_ IN (1,0)),
	                                  EXECUTION_ID_ NVARCHAR2(64),
	                                  PROCESS_INSTANCE_ID_ NVARCHAR2(64),
	                                  PROC_DEF_ID_ NVARCHAR2(64),
	                                  ELEMENT_ID_ NVARCHAR2(255),
	                                  ELEMENT_NAME_ NVARCHAR2(255),
	                                  SCOPE_ID_ NVARCHAR2(255),
	                                  SUB_SCOPE_ID_ NVARCHAR2(255),
	                                  SCOPE_TYPE_ NVARCHAR2(255),
	                                  SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                                  CORRELATION_ID_ NVARCHAR2(255),
	                                  RETRIES_ INTEGER,
	                                  EXCEPTION_STACK_ID_ NVARCHAR2(64),
	                                  EXCEPTION_MSG_ NVARCHAR2(2000),
	                                  DUEDATE_ TIMESTAMP(6),
	                                  REPEAT_ NVARCHAR2(255),
	                                  HANDLER_TYPE_ NVARCHAR2(255),
	                                  HANDLER_CFG_ NVARCHAR2(2000),
	                                  CUSTOM_VALUES_ID_ NVARCHAR2(64),
	                                  CREATE_TIME_ TIMESTAMP(6),
	                                  TENANT_ID_ NVARCHAR2(255) DEFAULT '',
	                                  primary key (ID_)
);

create table ACT_RU_DEADLETTER_JOB (
									   ID_ NVARCHAR2(64) NOT NULL,
	                                   REV_ INTEGER,
	                                   CATEGORY_ NVARCHAR2(255),
	                                   TYPE_ NVARCHAR2(255) NOT NULL,
	                                   EXCLUSIVE_ NUMBER(1,0) CHECK (EXCLUSIVE_ IN (1,0)),
	                                   EXECUTION_ID_ NVARCHAR2(64),
	                                   PROCESS_INSTANCE_ID_ NVARCHAR2(64),
	                                   PROC_DEF_ID_ NVARCHAR2(64),
	                                   ELEMENT_ID_ NVARCHAR2(255),
	                                   ELEMENT_NAME_ NVARCHAR2(255),
	                                   SCOPE_ID_ NVARCHAR2(255),
	                                   SUB_SCOPE_ID_ NVARCHAR2(255),
	                                   SCOPE_TYPE_ NVARCHAR2(255),
	                                   SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                                   CORRELATION_ID_ NVARCHAR2(255),
	                                   EXCEPTION_STACK_ID_ NVARCHAR2(64),
	                                   EXCEPTION_MSG_ NVARCHAR2(2000),
	                                   DUEDATE_ TIMESTAMP(6),
	                                   REPEAT_ NVARCHAR2(255),
	                                   HANDLER_TYPE_ NVARCHAR2(255),
	                                   HANDLER_CFG_ NVARCHAR2(2000),
	                                   CUSTOM_VALUES_ID_ NVARCHAR2(64),
	                                   CREATE_TIME_ TIMESTAMP(6),
	                                   TENANT_ID_ NVARCHAR2(255) DEFAULT '',
	                                   primary key (ID_)
);

create table ACT_RU_HISTORY_JOB (
									ID_ NVARCHAR2(64) NOT NULL,
	                                REV_ INTEGER,
	                                LOCK_EXP_TIME_ TIMESTAMP(6),
	                                LOCK_OWNER_ NVARCHAR2(255),
	                                RETRIES_ INTEGER,
	                                EXCEPTION_STACK_ID_ NVARCHAR2(64),
	                                EXCEPTION_MSG_ NVARCHAR2(2000),
	                                HANDLER_TYPE_ NVARCHAR2(255),
	                                HANDLER_CFG_ NVARCHAR2(2000),
	                                CUSTOM_VALUES_ID_ NVARCHAR2(64),
	                                ADV_HANDLER_CFG_ID_ NVARCHAR2(64),
	                                CREATE_TIME_ TIMESTAMP(6),
	                                SCOPE_TYPE_ NVARCHAR2(255),
	                                TENANT_ID_ NVARCHAR2(255) DEFAULT '',
	                                primary key (ID_)
);

create table ACT_RU_EXTERNAL_JOB (
									 ID_ NVARCHAR2(64) NOT NULL,
	                                 REV_ INTEGER,
	                                 CATEGORY_ NVARCHAR2(255),
	                                 TYPE_ NVARCHAR2(255) NOT NULL,
	                                 LOCK_EXP_TIME_ TIMESTAMP(6),
	                                 LOCK_OWNER_ NVARCHAR2(255),
	                                 EXCLUSIVE_ NUMBER(1,0) CHECK (EXCLUSIVE_ IN (1,0)),
	                                 EXECUTION_ID_ NVARCHAR2(64),
	                                 PROCESS_INSTANCE_ID_ NVARCHAR2(64),
	                                 PROC_DEF_ID_ NVARCHAR2(64),
	                                 ELEMENT_ID_ NVARCHAR2(255),
	                                 ELEMENT_NAME_ NVARCHAR2(255),
	                                 SCOPE_ID_ NVARCHAR2(255),
	                                 SUB_SCOPE_ID_ NVARCHAR2(255),
	                                 SCOPE_TYPE_ NVARCHAR2(255),
	                                 SCOPE_DEFINITION_ID_ NVARCHAR2(255),
	                                 CORRELATION_ID_ NVARCHAR2(255),
	                                 RETRIES_ INTEGER,
	                                 EXCEPTION_STACK_ID_ NVARCHAR2(64),
	                                 EXCEPTION_MSG_ NVARCHAR2(2000),
	                                 DUEDATE_ TIMESTAMP(6),
	                                 REPEAT_ NVARCHAR2(255),
	                                 HANDLER_TYPE_ NVARCHAR2(255),
	                                 HANDLER_CFG_ NVARCHAR2(2000),
	                                 CUSTOM_VALUES_ID_ NVARCHAR2(64),
	                                 CREATE_TIME_ TIMESTAMP(6),
	                                 TENANT_ID_ NVARCHAR2(255) DEFAULT '',
	                                 primary key (ID_)
);

create index ACT_IDX_JOB_EXCEPTION on ACT_RU_JOB(EXCEPTION_STACK_ID_);
create index ACT_IDX_JOB_CUSTOM_VAL_ID on ACT_RU_JOB(CUSTOM_VALUES_ID_);
create index ACT_IDX_JOB_CORRELATION_ID on ACT_RU_JOB(CORRELATION_ID_);

create index ACT_IDX_TJOB_EXCEPTION on ACT_RU_TIMER_JOB(EXCEPTION_STACK_ID_);
create index ACT_IDX_TJOB_CUSTOM_VAL_ID on ACT_RU_TIMER_JOB(CUSTOM_VALUES_ID_);
create index ACT_IDX_TJOB_CORRELATION_ID on ACT_RU_TIMER_JOB(CORRELATION_ID_);
create index ACT_IDX_TJOB_DUEDATE on ACT_RU_TIMER_JOB(DUEDATE_);

create index ACT_IDX_SJOB_EXCEPTION on ACT_RU_SUSPENDED_JOB(EXCEPTION_STACK_ID_);
create index ACT_IDX_SJOB_CUSTOM_VAL_ID on ACT_RU_SUSPENDED_JOB(CUSTOM_VALUES_ID_);
create index ACT_IDX_SJOB_CORRELATION_ID on ACT_RU_SUSPENDED_JOB(CORRELATION_ID_);

create index ACT_IDX_DJOB_EXCEPTION on ACT_RU_DEADLETTER_JOB(EXCEPTION_STACK_ID_);
create index ACT_IDX_DJOB_CUSTOM_VAL_ID on ACT_RU_DEADLETTER_JOB(CUSTOM_VALUES_ID_);
create index ACT_IDX_DJOB_CORRELATION_ID on ACT_RU_DEADLETTER_JOB(CORRELATION_ID_);

create index ACT_IDX_EJOB_EXCEPTION on ACT_RU_EXTERNAL_JOB(EXCEPTION_STACK_ID_);
create index ACT_IDX_EJOB_CUSTOM_VAL_ID on ACT_RU_EXTERNAL_JOB(CUSTOM_VALUES_ID_);
create index ACT_IDX_EJOB_CORRELATION_ID on ACT_RU_EXTERNAL_JOB(CORRELATION_ID_);

alter table ACT_RU_JOB
	add constraint ACT_FK_JOB_EXCEPTION
		foreign key (EXCEPTION_STACK_ID_)
			references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_JOB
	add constraint ACT_FK_JOB_CUSTOM_VAL
		foreign key (CUSTOM_VALUES_ID_)
			references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_TIMER_JOB
	add constraint ACT_FK_TJOB_EXCEPTION
		foreign key (EXCEPTION_STACK_ID_)
			references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_TIMER_JOB
	add constraint ACT_FK_TJOB_CUSTOM_VAL
		foreign key (CUSTOM_VALUES_ID_)
			references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_SUSPENDED_JOB
	add constraint ACT_FK_SJOB_EXCEPTION
		foreign key (EXCEPTION_STACK_ID_)
			references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_SUSPENDED_JOB
	add constraint ACT_FK_SJOB_CUSTOM_VAL
		foreign key (CUSTOM_VALUES_ID_)
			references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_DEADLETTER_JOB
	add constraint ACT_FK_DJOB_EXCEPTION
		foreign key (EXCEPTION_STACK_ID_)
			references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_DEADLETTER_JOB
	add constraint ACT_FK_DJOB_CUSTOM_VAL
		foreign key (CUSTOM_VALUES_ID_)
			references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_EXTERNAL_JOB
	add constraint ACT_FK_EJOB_EXCEPTION
		foreign key (EXCEPTION_STACK_ID_)
			references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_EXTERNAL_JOB
	add constraint ACT_FK_EJOB_CUSTOM_VAL
		foreign key (CUSTOM_VALUES_ID_)
			references ACT_GE_BYTEARRAY (ID_);

create index ACT_IDX_JOB_SCOPE on ACT_RU_JOB(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_JOB_SUB_SCOPE on ACT_RU_JOB(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_JOB_SCOPE_DEF on ACT_RU_JOB(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_TJOB_SCOPE on ACT_RU_TIMER_JOB(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_TJOB_SUB_SCOPE on ACT_RU_TIMER_JOB(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_TJOB_SCOPE_DEF on ACT_RU_TIMER_JOB(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_SJOB_SCOPE on ACT_RU_SUSPENDED_JOB(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_SJOB_SUB_SCOPE on ACT_RU_SUSPENDED_JOB(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_SJOB_SCOPE_DEF on ACT_RU_SUSPENDED_JOB(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_DJOB_SCOPE on ACT_RU_DEADLETTER_JOB(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_DJOB_SUB_SCOPE on ACT_RU_DEADLETTER_JOB(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_DJOB_SCOPE_DEF on ACT_RU_DEADLETTER_JOB(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

create index ACT_IDX_EJOB_SCOPE on ACT_RU_EXTERNAL_JOB(SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_EJOB_SUB_SCOPE on ACT_RU_EXTERNAL_JOB(SUB_SCOPE_ID_, SCOPE_TYPE_);
create index ACT_IDX_EJOB_SCOPE_DEF on ACT_RU_EXTERNAL_JOB(SCOPE_DEFINITION_ID_, SCOPE_TYPE_);

insert into ACT_GE_PROPERTY values ('job.schema.version', '6.8.0.0', 1);

--
-- 14 engine
--
create table ACT_RE_DEPLOYMENT (
								   ID_ NVARCHAR2(64),
	                               NAME_ NVARCHAR2(255),
	                               CATEGORY_ NVARCHAR2(255),
	                               KEY_ NVARCHAR2(255),
	                               TENANT_ID_ NVARCHAR2(255) DEFAULT '',
	                               DEPLOY_TIME_ TIMESTAMP(6),
	                               DERIVED_FROM_ NVARCHAR2(64),
	                               DERIVED_FROM_ROOT_ NVARCHAR2(64),
	                               PARENT_DEPLOYMENT_ID_ NVARCHAR2(255),
	                               ENGINE_VERSION_ NVARCHAR2(255),
	                               primary key (ID_)
);

create table ACT_RE_MODEL (
							  ID_ NVARCHAR2(64) not null,
	                          REV_ INTEGER,
	                          NAME_ NVARCHAR2(255),
	                          KEY_ NVARCHAR2(255),
	                          CATEGORY_ NVARCHAR2(255),
	                          CREATE_TIME_ TIMESTAMP(6),
	                          LAST_UPDATE_TIME_ TIMESTAMP(6),
	                          VERSION_ INTEGER,
	                          META_INFO_ NVARCHAR2(2000),
	                          DEPLOYMENT_ID_ NVARCHAR2(64),
	                          EDITOR_SOURCE_VALUE_ID_ NVARCHAR2(64),
	                          EDITOR_SOURCE_EXTRA_VALUE_ID_ NVARCHAR2(64),
	                          TENANT_ID_ NVARCHAR2(255) DEFAULT '',
	                          primary key (ID_)
);

create table ACT_RU_EXECUTION (
								  ID_ NVARCHAR2(64),
	                              REV_ INTEGER,
	                              PROC_INST_ID_ NVARCHAR2(64),
	                              BUSINESS_KEY_ NVARCHAR2(255),
	                              PARENT_ID_ NVARCHAR2(64),
	                              PROC_DEF_ID_ NVARCHAR2(64),
	                              SUPER_EXEC_ NVARCHAR2(64),
	                              ROOT_PROC_INST_ID_ NVARCHAR2(64),
	                              ACT_ID_ NVARCHAR2(255),
	                              IS_ACTIVE_ NUMBER(1,0) CHECK (IS_ACTIVE_ IN (1,0)),
	                              IS_CONCURRENT_ NUMBER(1,0) CHECK (IS_CONCURRENT_ IN (1,0)),
	                              IS_SCOPE_ NUMBER(1,0) CHECK (IS_SCOPE_ IN (1,0)),
	                              IS_EVENT_SCOPE_ NUMBER(1,0) CHECK (IS_EVENT_SCOPE_ IN (1,0)),
	                              IS_MI_ROOT_ NUMBER(1,0) CHECK (IS_MI_ROOT_ IN (1,0)),
	                              SUSPENSION_STATE_ INTEGER,
	                              CACHED_ENT_STATE_ INTEGER,
	                              TENANT_ID_ NVARCHAR2(255) DEFAULT '',
	                              NAME_ NVARCHAR2(255),
	                              START_ACT_ID_ NVARCHAR2(255),
	                              START_TIME_ TIMESTAMP(6),
	                              START_USER_ID_ NVARCHAR2(255),
	                              LOCK_TIME_ TIMESTAMP(6),
	                              LOCK_OWNER_ NVARCHAR2(255),
	                              IS_COUNT_ENABLED_ NUMBER(1,0) CHECK (IS_COUNT_ENABLED_ IN (1,0)),
	                              EVT_SUBSCR_COUNT_ INTEGER,
	                              TASK_COUNT_ INTEGER,
	                              JOB_COUNT_ INTEGER,
	                              TIMER_JOB_COUNT_ INTEGER,
	                              SUSP_JOB_COUNT_ INTEGER,
	                              DEADLETTER_JOB_COUNT_ INTEGER,
	                              EXTERNAL_WORKER_JOB_COUNT_ INTEGER,
	                              VAR_COUNT_ INTEGER,
	                              ID_LINK_COUNT_ INTEGER,
	                              CALLBACK_ID_ NVARCHAR2(255),
	                              CALLBACK_TYPE_ NVARCHAR2(255),
	                              REFERENCE_ID_ NVARCHAR2(255),
	                              REFERENCE_TYPE_ NVARCHAR2(255),
	                              PROPAGATED_STAGE_INST_ID_ NVARCHAR2(255),
	                              BUSINESS_STATUS_ NVARCHAR2(255),
	                              primary key (ID_)
);

create table ACT_RE_PROCDEF (
								ID_ NVARCHAR2(64) NOT NULL,
	                            REV_ INTEGER,
	                            CATEGORY_ NVARCHAR2(255),
	                            NAME_ NVARCHAR2(255),
	                            KEY_ NVARCHAR2(255) NOT NULL,
	                            VERSION_ INTEGER NOT NULL,
	                            DEPLOYMENT_ID_ NVARCHAR2(64),
	                            RESOURCE_NAME_ NVARCHAR2(2000),
	                            DGRM_RESOURCE_NAME_ varchar(4000),
	                            DESCRIPTION_ NVARCHAR2(2000),
	                            HAS_START_FORM_KEY_ NUMBER(1,0) CHECK (HAS_START_FORM_KEY_ IN (1,0)),
	                            HAS_GRAPHICAL_NOTATION_ NUMBER(1,0) CHECK (HAS_GRAPHICAL_NOTATION_ IN (1,0)),
	                            SUSPENSION_STATE_ INTEGER,
	                            TENANT_ID_ NVARCHAR2(255) DEFAULT '',
	                            DERIVED_FROM_ NVARCHAR2(64),
	                            DERIVED_FROM_ROOT_ NVARCHAR2(64),
	                            DERIVED_VERSION_ INTEGER DEFAULT 0 NOT NULL,
	                            ENGINE_VERSION_ NVARCHAR2(255),
	                            primary key (ID_)
);

create table ACT_EVT_LOG (
							 LOG_NR_ NUMBER(19),
	                         TYPE_ NVARCHAR2(64),
	                         PROC_DEF_ID_ NVARCHAR2(64),
	                         PROC_INST_ID_ NVARCHAR2(64),
	                         EXECUTION_ID_ NVARCHAR2(64),
	                         TASK_ID_ NVARCHAR2(64),
	                         TIME_STAMP_ TIMESTAMP(6) not null,
	                         USER_ID_ NVARCHAR2(255),
	                         DATA_ BLOB,
	                         LOCK_OWNER_ NVARCHAR2(255),
	                         LOCK_TIME_ TIMESTAMP(6) null,
	                         IS_PROCESSED_ NUMBER(3) default 0,
	                         primary key (LOG_NR_)
);

create sequence act_evt_log_seq;

create table ACT_PROCDEF_INFO (
								  ID_ NVARCHAR2(64) not null,
	                              PROC_DEF_ID_ NVARCHAR2(64) not null,
	                              REV_ integer,
	                              INFO_JSON_ID_ NVARCHAR2(64),
	                              primary key (ID_)
);

create table ACT_RU_ACTINST (
								ID_ NVARCHAR2(64) not null,
	                            REV_ INTEGER default 1,
	                            PROC_DEF_ID_ NVARCHAR2(64) not null,
	                            PROC_INST_ID_ NVARCHAR2(64) not null,
	                            EXECUTION_ID_ NVARCHAR2(64) not null,
	                            ACT_ID_ NVARCHAR2(255) not null,
	                            TASK_ID_ NVARCHAR2(64),
	                            CALL_PROC_INST_ID_ NVARCHAR2(64),
	                            ACT_NAME_ NVARCHAR2(255),
	                            ACT_TYPE_ NVARCHAR2(255) not null,
	                            ASSIGNEE_ NVARCHAR2(255),
	                            START_TIME_ TIMESTAMP(6) not null,
	                            END_TIME_ TIMESTAMP(6),
	                            DURATION_ NUMBER(19,0),
	                            TRANSACTION_ORDER_ INTEGER,
	                            DELETE_REASON_ NVARCHAR2(2000),
	                            TENANT_ID_ NVARCHAR2(255) default '',
	                            primary key (ID_)
);

create index ACT_IDX_EXEC_BUSKEY on ACT_RU_EXECUTION(BUSINESS_KEY_);
create index ACT_IDX_EXEC_ROOT on ACT_RU_EXECUTION(ROOT_PROC_INST_ID_);
create index ACT_IDX_EXEC_REF_ID_ on ACT_RU_EXECUTION(REFERENCE_ID_);
create index ACT_IDX_VARIABLE_TASK_ID on ACT_RU_VARIABLE(TASK_ID_);

create index ACT_IDX_RU_ACTI_START on ACT_RU_ACTINST(START_TIME_);
create index ACT_IDX_RU_ACTI_END on ACT_RU_ACTINST(END_TIME_);
create index ACT_IDX_RU_ACTI_PROC on ACT_RU_ACTINST(PROC_INST_ID_);
create index ACT_IDX_RU_ACTI_PROC_ACT on ACT_RU_ACTINST(PROC_INST_ID_, ACT_ID_);
create index ACT_IDX_RU_ACTI_EXEC on ACT_RU_ACTINST(EXECUTION_ID_);
create index ACT_IDX_RU_ACTI_EXEC_ACT on ACT_RU_ACTINST(EXECUTION_ID_, ACT_ID_);
create index ACT_IDX_RU_ACTI_TASK on ACT_RU_ACTINST(TASK_ID_);

create index ACT_IDX_BYTEAR_DEPL on ACT_GE_BYTEARRAY(DEPLOYMENT_ID_);
alter table ACT_GE_BYTEARRAY
	add constraint ACT_FK_BYTEARR_DEPL
		foreign key (DEPLOYMENT_ID_)
			references ACT_RE_DEPLOYMENT (ID_);

alter table ACT_RE_PROCDEF
	add constraint ACT_UNIQ_PROCDEF
		unique (KEY_,VERSION_, DERIVED_VERSION_, TENANT_ID_);

create index ACT_IDX_EXE_PROCINST on ACT_RU_EXECUTION(PROC_INST_ID_);
alter table ACT_RU_EXECUTION
	add constraint ACT_FK_EXE_PROCINST
		foreign key (PROC_INST_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_EXE_PARENT on ACT_RU_EXECUTION(PARENT_ID_);
alter table ACT_RU_EXECUTION
	add constraint ACT_FK_EXE_PARENT
		foreign key (PARENT_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_EXE_SUPER on ACT_RU_EXECUTION(SUPER_EXEC_);
alter table ACT_RU_EXECUTION
	add constraint ACT_FK_EXE_SUPER
		foreign key (SUPER_EXEC_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_EXE_PROCDEF on ACT_RU_EXECUTION(PROC_DEF_ID_);
alter table ACT_RU_EXECUTION
	add constraint ACT_FK_EXE_PROCDEF
		foreign key (PROC_DEF_ID_)
			references ACT_RE_PROCDEF (ID_);

create index ACT_IDX_TSKASS_TASK on ACT_RU_IDENTITYLINK(TASK_ID_);
alter table ACT_RU_IDENTITYLINK
	add constraint ACT_FK_TSKASS_TASK
		foreign key (TASK_ID_)
			references ACT_RU_TASK (ID_);

create index ACT_IDX_ATHRZ_PROCEDEF  on ACT_RU_IDENTITYLINK(PROC_DEF_ID_);
alter table ACT_RU_IDENTITYLINK
	add constraint ACT_FK_ATHRZ_PROCEDEF
		foreign key (PROC_DEF_ID_)
			references ACT_RE_PROCDEF (ID_);

create index ACT_IDX_IDL_PROCINST on ACT_RU_IDENTITYLINK(PROC_INST_ID_);
alter table ACT_RU_IDENTITYLINK
	add constraint ACT_FK_IDL_PROCINST
		foreign key (PROC_INST_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_TASK_EXEC on ACT_RU_TASK(EXECUTION_ID_);
alter table ACT_RU_TASK
	add constraint ACT_FK_TASK_EXE
		foreign key (EXECUTION_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_TASK_PROCINST on ACT_RU_TASK(PROC_INST_ID_);
alter table ACT_RU_TASK
	add constraint ACT_FK_TASK_PROCINST
		foreign key (PROC_INST_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_TASK_PROCDEF on ACT_RU_TASK(PROC_DEF_ID_);
alter table ACT_RU_TASK
	add constraint ACT_FK_TASK_PROCDEF
		foreign key (PROC_DEF_ID_)
			references ACT_RE_PROCDEF (ID_);

create index ACT_IDX_VAR_EXE on ACT_RU_VARIABLE(EXECUTION_ID_);
alter table ACT_RU_VARIABLE
	add constraint ACT_FK_VAR_EXE
		foreign key (EXECUTION_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_VAR_PROCINST on ACT_RU_VARIABLE(PROC_INST_ID_);
alter table ACT_RU_VARIABLE
	add constraint ACT_FK_VAR_PROCINST
		foreign key (PROC_INST_ID_)
			references ACT_RU_EXECUTION(ID_);

create index ACT_IDX_JOB_EXECUTION_ID on ACT_RU_JOB(EXECUTION_ID_);
alter table ACT_RU_JOB
	add constraint ACT_FK_JOB_EXECUTION
		foreign key (EXECUTION_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_JOB_PROC_INST_ID on ACT_RU_JOB(PROCESS_INSTANCE_ID_);
alter table ACT_RU_JOB
	add constraint ACT_FK_JOB_PROCESS_INSTANCE
		foreign key (PROCESS_INSTANCE_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_JOB_PROC_DEF_ID on ACT_RU_JOB(PROC_DEF_ID_);
alter table ACT_RU_JOB
	add constraint ACT_FK_JOB_PROC_DEF
		foreign key (PROC_DEF_ID_)
			references ACT_RE_PROCDEF (ID_);

create index ACT_IDX_TJOB_EXECUTION_ID on ACT_RU_TIMER_JOB(EXECUTION_ID_);
alter table ACT_RU_TIMER_JOB
	add constraint ACT_FK_TJOB_EXECUTION
		foreign key (EXECUTION_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_TJOB_PROC_INST_ID on ACT_RU_TIMER_JOB(PROCESS_INSTANCE_ID_);
alter table ACT_RU_TIMER_JOB
	add constraint ACT_FK_TJOB_PROCESS_INSTANCE
		foreign key (PROCESS_INSTANCE_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_TJOB_PROC_DEF_ID on ACT_RU_TIMER_JOB(PROC_DEF_ID_);
alter table ACT_RU_TIMER_JOB
	add constraint ACT_FK_TJOB_PROC_DEF
		foreign key (PROC_DEF_ID_)
			references ACT_RE_PROCDEF (ID_);

create index ACT_IDX_SJOB_EXECUTION_ID on ACT_RU_SUSPENDED_JOB(EXECUTION_ID_);
alter table ACT_RU_SUSPENDED_JOB
	add constraint ACT_FK_SJOB_EXECUTION
		foreign key (EXECUTION_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_SJOB_PROC_INST_ID on ACT_RU_SUSPENDED_JOB(PROCESS_INSTANCE_ID_);
alter table ACT_RU_SUSPENDED_JOB
	add constraint ACT_FK_SJOB_PROCESS_INSTANCE
		foreign key (PROCESS_INSTANCE_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_SJOB_PROC_DEF_ID on ACT_RU_SUSPENDED_JOB(PROC_DEF_ID_);
alter table ACT_RU_SUSPENDED_JOB
	add constraint ACT_FK_SJOB_PROC_DEF
		foreign key (PROC_DEF_ID_)
			references ACT_RE_PROCDEF (ID_);

create index ACT_IDX_DJOB_EXECUTION_ID on ACT_RU_DEADLETTER_JOB(EXECUTION_ID_);
alter table ACT_RU_DEADLETTER_JOB
	add constraint ACT_FK_DJOB_EXECUTION
		foreign key (EXECUTION_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_DJOB_PROC_INST_ID on ACT_RU_DEADLETTER_JOB(PROCESS_INSTANCE_ID_);
alter table ACT_RU_DEADLETTER_JOB
	add constraint ACT_FK_DJOB_PROCESS_INSTANCE
		foreign key (PROCESS_INSTANCE_ID_)
			references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_DJOB_PROC_DEF_ID on ACT_RU_DEADLETTER_JOB(PROC_DEF_ID_);
alter table ACT_RU_DEADLETTER_JOB
	add constraint ACT_FK_DJOB_PROC_DEF
		foreign key (PROC_DEF_ID_)
			references ACT_RE_PROCDEF (ID_);

alter table ACT_RU_EVENT_SUBSCR
	add constraint ACT_FK_EVENT_EXEC
		foreign key (EXECUTION_ID_)
			references ACT_RU_EXECUTION(ID_);

create index ACT_IDX_MODEL_SOURCE on ACT_RE_MODEL(EDITOR_SOURCE_VALUE_ID_);
alter table ACT_RE_MODEL
	add constraint ACT_FK_MODEL_SOURCE
		foreign key (EDITOR_SOURCE_VALUE_ID_)
			references ACT_GE_BYTEARRAY (ID_);

create index ACT_IDX_MODEL_SOURCE_EXTRA on ACT_RE_MODEL(EDITOR_SOURCE_EXTRA_VALUE_ID_);
alter table ACT_RE_MODEL
	add constraint ACT_FK_MODEL_SOURCE_EXTRA
		foreign key (EDITOR_SOURCE_EXTRA_VALUE_ID_)
			references ACT_GE_BYTEARRAY (ID_);

create index ACT_IDX_MODEL_DEPLOYMENT on ACT_RE_MODEL(DEPLOYMENT_ID_);
alter table ACT_RE_MODEL
	add constraint ACT_FK_MODEL_DEPLOYMENT
		foreign key (DEPLOYMENT_ID_)
			references ACT_RE_DEPLOYMENT (ID_);

create index ACT_IDX_PROCDEF_INFO_JSON on ACT_PROCDEF_INFO(INFO_JSON_ID_);
alter table ACT_PROCDEF_INFO
	add constraint ACT_FK_INFO_JSON_BA
		foreign key (INFO_JSON_ID_)
			references ACT_GE_BYTEARRAY (ID_);

create index ACT_IDX_PROCDEF_INFO_PROC on ACT_PROCDEF_INFO(PROC_DEF_ID_);
alter table ACT_PROCDEF_INFO
	add constraint ACT_FK_INFO_PROCDEF
		foreign key (PROC_DEF_ID_)
			references ACT_RE_PROCDEF (ID_);

alter table ACT_PROCDEF_INFO
	add constraint ACT_UNIQ_INFO_PROCDEF
		unique (PROC_DEF_ID_);

insert into ACT_GE_PROPERTY
values ('schema.version', '6.8.0.0', 1);

insert into ACT_GE_PROPERTY
values ('schema.history', 'create(6.8.0.0)', 1);

--
-- 15 history
--
create table ACT_HI_PROCINST (
								 ID_ NVARCHAR2(64) not null,
	                             REV_ INTEGER default 1,
	                             PROC_INST_ID_ NVARCHAR2(64) not null,
	                             BUSINESS_KEY_ NVARCHAR2(255),
	                             PROC_DEF_ID_ NVARCHAR2(64) not null,
	                             START_TIME_ TIMESTAMP(6) not null,
	                             END_TIME_ TIMESTAMP(6),
	                             DURATION_ NUMBER(19,0),
	                             START_USER_ID_ NVARCHAR2(255),
	                             START_ACT_ID_ NVARCHAR2(255),
	                             END_ACT_ID_ NVARCHAR2(255),
	                             SUPER_PROCESS_INSTANCE_ID_ NVARCHAR2(64),
	                             DELETE_REASON_ NVARCHAR2(2000),
	                             TENANT_ID_ NVARCHAR2(255) default '',
	                             NAME_ NVARCHAR2(255),
	                             CALLBACK_ID_ NVARCHAR2(255),
	                             CALLBACK_TYPE_ NVARCHAR2(255),
	                             REFERENCE_ID_ NVARCHAR2(255),
	                             REFERENCE_TYPE_ NVARCHAR2(255),
	                             PROPAGATED_STAGE_INST_ID_ NVARCHAR2(255),
	                             BUSINESS_STATUS_ NVARCHAR2(255),
	                             primary key (ID_),
	                             unique (PROC_INST_ID_)
);

create table ACT_HI_ACTINST (
								ID_ NVARCHAR2(64) not null,
	                            REV_ INTEGER default 1,
	                            PROC_DEF_ID_ NVARCHAR2(64) not null,
	                            PROC_INST_ID_ NVARCHAR2(64) not null,
	                            EXECUTION_ID_ NVARCHAR2(64) not null,
	                            ACT_ID_ NVARCHAR2(255) not null,
	                            TASK_ID_ NVARCHAR2(64),
	                            CALL_PROC_INST_ID_ NVARCHAR2(64),
	                            ACT_NAME_ NVARCHAR2(255),
	                            ACT_TYPE_ NVARCHAR2(255) not null,
	                            ASSIGNEE_ NVARCHAR2(255),
	                            START_TIME_ TIMESTAMP(6) not null,
	                            END_TIME_ TIMESTAMP(6),
	                            TRANSACTION_ORDER_ INTEGER,
	                            DURATION_ NUMBER(19,0),
	                            DELETE_REASON_ NVARCHAR2(2000),
	                            TENANT_ID_ NVARCHAR2(255) default '',
	                            primary key (ID_)
);

create table ACT_HI_DETAIL (
							   ID_ NVARCHAR2(64) not null,
	                           TYPE_ NVARCHAR2(255) not null,
	                           PROC_INST_ID_ NVARCHAR2(64),
	                           EXECUTION_ID_ NVARCHAR2(64),
	                           TASK_ID_ NVARCHAR2(64),
	                           ACT_INST_ID_ NVARCHAR2(64),
	                           NAME_ NVARCHAR2(255) not null,
	                           VAR_TYPE_ NVARCHAR2(64),
	                           REV_ INTEGER,
	                           TIME_ TIMESTAMP(6) not null,
	                           BYTEARRAY_ID_ NVARCHAR2(64),
	                           DOUBLE_ NUMBER(*,10),
	                           LONG_ NUMBER(19,0),
	                           TEXT_ NVARCHAR2(2000),
	                           TEXT2_ NVARCHAR2(2000),
	                           primary key (ID_)
);

create table ACT_HI_COMMENT (
								ID_ NVARCHAR2(64) not null,
	                            TYPE_ NVARCHAR2(255),
	                            TIME_ TIMESTAMP(6) not null,
	                            USER_ID_ NVARCHAR2(255),
	                            TASK_ID_ NVARCHAR2(64),
	                            PROC_INST_ID_ NVARCHAR2(64),
	                            ACTION_ NVARCHAR2(255),
	                            MESSAGE_ NVARCHAR2(2000),
	                            FULL_MSG_ BLOB,
	                            primary key (ID_)
);

create table ACT_HI_ATTACHMENT (
								   ID_ NVARCHAR2(64) not null,
	                               REV_ INTEGER,
	                               USER_ID_ NVARCHAR2(255),
	                               NAME_ NVARCHAR2(255),
	                               DESCRIPTION_ NVARCHAR2(2000),
	                               TYPE_ NVARCHAR2(255),
	                               TASK_ID_ NVARCHAR2(64),
	                               PROC_INST_ID_ NVARCHAR2(64),
	                               URL_ NVARCHAR2(2000),
	                               CONTENT_ID_ NVARCHAR2(64),
	                               TIME_ TIMESTAMP(6),
	                               primary key (ID_)
);

create index ACT_IDX_HI_PRO_INST_END on ACT_HI_PROCINST(END_TIME_);
create index ACT_IDX_HI_PRO_I_BUSKEY on ACT_HI_PROCINST(BUSINESS_KEY_);
create index ACT_IDX_HI_PRO_SUPER_PROCINST on ACT_HI_PROCINST(SUPER_PROCESS_INSTANCE_ID_);
create index ACT_IDX_HI_ACT_INST_START on ACT_HI_ACTINST(START_TIME_);
create index ACT_IDX_HI_ACT_INST_END on ACT_HI_ACTINST(END_TIME_);
create index ACT_IDX_HI_DETAIL_PROC_INST on ACT_HI_DETAIL(PROC_INST_ID_);
create index ACT_IDX_HI_DETAIL_ACT_INST on ACT_HI_DETAIL(ACT_INST_ID_);
create index ACT_IDX_HI_DETAIL_TIME on ACT_HI_DETAIL(TIME_);
create index ACT_IDX_HI_DETAIL_NAME on ACT_HI_DETAIL(NAME_);
create index ACT_IDX_HI_DETAIL_TASK_ID on ACT_HI_DETAIL(TASK_ID_);
create index ACT_IDX_HI_PROCVAR_PROC_INST on ACT_HI_VARINST(PROC_INST_ID_);
create index ACT_IDX_HI_PROCVAR_TASK_ID on ACT_HI_VARINST(TASK_ID_);
create index ACT_IDX_HI_PROCVAR_EXE on ACT_HI_VARINST(EXECUTION_ID_);
create index ACT_IDX_HI_IDENT_LNK_TASK on ACT_HI_IDENTITYLINK(TASK_ID_);
create index ACT_IDX_HI_IDENT_LNK_PROCINST on ACT_HI_IDENTITYLINK(PROC_INST_ID_);

create index ACT_IDX_HI_ACT_INST_PROCINST on ACT_HI_ACTINST(PROC_INST_ID_, ACT_ID_);
create index ACT_IDX_HI_ACT_INST_EXEC on ACT_HI_ACTINST(EXECUTION_ID_, ACT_ID_);
create index ACT_IDX_HI_TASK_INST_PROCINST on ACT_HI_TASKINST(PROC_INST_ID_);


-- Taules pendents de creació

-- FLW_CHANNEL_DEFINITION
-- FLW_EVENT_DEFINITION
-- FLW_EVENT_DEPLOYMENT
-- FLW_EVENT_RESOURCE
-- FLW_EV_DATABASECHANGELOG
-- FLW_EV_DATABASECHANGELOGLOCK
