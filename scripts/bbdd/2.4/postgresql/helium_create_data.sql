--
-- Create Data Script 
--   Database Version   : 
--   Schema             : HELIUM 
--   Script Created by  : HELIUM 
--   Script Created at  :  
--   Physical Location  :  
--   Notes              :  
--


INSERT INTO HEL_PERMIS ( CODI, DESCRIPCIO ) VALUES ( 
'HEL_ADMIN', 'Administrador');

INSERT INTO HEL_PERMIS ( CODI, DESCRIPCIO ) VALUES ( 
'HEL_USER', 'Usuari'); 
commit;
INSERT INTO HEL_PERSONA ( ID, AVIS_CORREU, CODI, DATA_NAIXEMENT, EMAIL, FONT, LLINATGE1, LLINATGE2,
LLINATGES, NOM, NOM_SENCER, SEXE, RELLEU_ID ) VALUES ( 
1, false, 'admin', NULL, 'admin@helium.org', NULL, 'Administrador', NULL, 'Administrador'
, 'Usuari', 'Usuari Administrador', 0, NULL); 
commit;
INSERT INTO HEL_USUARI ( CODI, ACTIU, CONTRASENYA ) VALUES ( 
'admin', true, '21232f297a57a5a743894a0e4a801fc3'); 
commit;
INSERT INTO HEL_USUARI_PERMIS ( CODI, PERMIS ) VALUES ( 
'admin', 'HEL_ADMIN'); 
commit;
INSERT INTO HEL_VERSIO ( ID, CODI, DESCRIPCIO, ORDRE ) VALUES ( 
1, 'inicial', NULL, 0); 
commit;
INSERT INTO HEL_IDGEN ( TAULA, VALOR ) VALUES ( 
'hel_persona', 1); 
INSERT INTO HEL_IDGEN ( TAULA, VALOR ) VALUES ( 
'hel_versio', 1);


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
    '2.4.0' codi,
    240 ordre,
    'now' data_creacio,
    false proces_executat,
    true script_executat,
    'now' data_execucio_script
where (select count(*) from hel_versio where ordre = 240) = 0;


commit;