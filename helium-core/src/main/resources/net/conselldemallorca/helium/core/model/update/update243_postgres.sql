--nou camp check per ocultar expedients a les consultes per tipus
alter table hel_consulta add column ocultar_actiu BOOLEAN SET DEFAULT 0;