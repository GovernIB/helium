-- Consultes avançades --
alter table HEL_CONSULTA move lob (INFORME_CONTINGUT) store as HEL_CONSULTA_CONT_LOB (tablespace HELIUM_LOB);
 