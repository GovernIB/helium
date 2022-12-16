--#1625 Mapeig documents i variables en incorporar una anotació a un expedient
--Modificar el manteniment del mapeig de variables i documents de SISTRA per marcar quins no se sobreescriuen

ALTER TABLE HEL_MAP_SISTRA ADD NO_SOBREESCRIURE NUMBER(1) DEFAULT 0;