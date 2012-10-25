-- Nou camp per a ocultar expedients a les consultes per tipus
alter table hel_consulta add column ocultar_actiu boolean not null set default 0;
