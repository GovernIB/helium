INSERT INTO HEL_PERMIS ( CODI, DESCRIPCIO ) VALUES ( 
'HEL_ADMIN', 'Administrador');

INSERT INTO HEL_PERMIS ( CODI, DESCRIPCIO ) VALUES ( 
'HEL_USER', 'Usuari'); 
commit;
INSERT INTO HEL_PERSONA ( ID, AVIS_CORREU, CODI, DATA_NAIXEMENT, EMAIL, FONT, LLINATGE1, LLINATGE2,
LLINATGES, NOM, NOM_SENCER, SEXE, RELLEU_ID ) VALUES ( 
1, 0, 'admin', NULL, 'admin@helium.org', NULL, 'Administrador', NULL, 'Administrador'
, 'Usuari', 'Usuari Administrador', 0, NULL); 
commit;
INSERT INTO HEL_USUARI ( CODI, ACTIU, CONTRASENYA ) VALUES ( 
'admin', 1, '21232f297a57a5a743894a0e4a801fc3'); 
commit;
INSERT INTO HEL_USUARI_PERMIS ( CODI, PERMIS ) VALUES ( 
'admin', 'HEL_ADMIN'); 
commit;
INSERT INTO HEL_VERSIO ( ID, CODI, DATA_CREACIO, DESCRIPCIO, ORDRE, PROCES_EXECUTAT, SCRIPT_EXECUTAT ) VALUES ( 
1, 'inicial', CURRENT_DATE, NULL, 0, 0, 0); 
commit;
INSERT INTO HEL_IDGEN ( TAULA, VALOR ) VALUES ( 
'hel_persona', 1); 
INSERT INTO HEL_IDGEN ( TAULA, VALOR ) VALUES ( 
'hel_versio', 1); 

INSERT INTO HEL_VERSIO (
    ID,
    CODI,
    ORDRE,
    DATA_CREACIO,
    PROCES_EXECUTAT,
    SCRIPT_EXECUTAT,
    DATA_EXECUCIO_SCRIPT)
SELECT
    2 ID,
    '3.2.0' CODI,
    300 ORDRE,
    SYSDATE DATA_CREACIO,
    0 PROCES_EXECUTAT,
    1 SCRIPT_EXECUTAT,
    SYSDATE DATA_EXECUCIO_SCRIPT
FROM DUAL
WHERE (SELECT COUNT(*) FROM HEL_VERSIO WHERE ORDRE = 300) = 0;

	
-- Insert a la taula de HEL_ACL_ENTRY
INSERT INTO HEL_ACL_CLASS (ID, CLASS) 
VALUES (HEL_ACL_CLASS_SEQ.NEXTVAL, 'net.conselldemallorca.helium.core.model.hibernate.ExpedientTipusUnitatOrganitzativa');  

INSERT INTO HEL_PARAMETRE (ID, CODI, NOM, DESCRIPCIO, VALOR)
      VALUES (1, 
      		 'app.configuracio.propagar.esborrar.expedients',
      		 'Propagar esborrat d''expedients', 
      		 'Si s''habilita es permetrà la propagació de l''esborrat d''expedients quan s''esborri un tipus d''expedient',
      		 '0');
INSERT INTO HEL_PARAMETRE (ID, CODI, NOM, DESCRIPCIO, VALOR)
      VALUES (2, 
      		 'app.net.caib.helium.unitats.organitzatives.arrel.codi',
      		 'Codi de l''unitat arrel', 
      		 NULL,
      		 'A04003003');
INSERT INTO HEL_PARAMETRE (ID, CODI, NOM, DESCRIPCIO, VALOR)
      VALUES (3, 
      		 'app.net.caib.helium.unitats.organitzatives.data.sincronitzacio',
      		 'Data sincronització Unitats Organitzatives', 
      		 'Data de la primera sincronització d''unitats organitzatives. Indica la data inicial de sincronització.',
      		 NULL);
      		 
INSERT INTO HEL_PARAMETRE (ID, CODI, NOM, DESCRIPCIO, VALOR)
      VALUES (4, 
      		 'app.net.caib.helium.unitats.organitzatives.data.actualitzacio',
      		 'Data actualització Unitats Organitzatives', 
      		 'Data de la darrera actualització d''unitats organitzatives. Indica la darrera data d''actualització a partir de la qual es demanen els canvis.'
      		 NULL);
      		 
      		 
commit;