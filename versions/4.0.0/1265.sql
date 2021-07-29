-- #1265 Helium MS. Fase2 Passar Helium a nou estàndard

-- Adapta el nom de les llistes de control d'accés

-- Oracle

UPDATE HEL_ACL_CLASS SET CLASS = 'es.caib.helium.persist.entity.Entorn'
WHERE CLASS = 'net.conselldemallorca.helium.core.model.hibernate.Entorn';

UPDATE HEL_ACL_CLASS SET CLASS = 'es.caib.helium.persist.entity.ExpedientTipus'
WHERE CLASS = 'net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus';


-- Postgreslq

UPDATE HEL_ACL_CLASS SET CLASS = 'es.caib.helium.persist.entity.Entorn'
WHERE CLASS = 'net.conselldemallorca.helium.core.model.hibernate.Entorn';

UPDATE HEL_ACL_CLASS SET CLASS = 'es.caib.helium.persist.entity.ExpedientTipus'
WHERE CLASS = 'net.conselldemallorca.helium.core.model.hibernate.ExpedientTipus';
