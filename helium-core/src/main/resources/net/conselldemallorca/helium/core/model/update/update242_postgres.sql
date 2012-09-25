-- Millorar logs portasignatures
alter table hel_portasignatures add column data_cb_dar timestamp(6) without time zone;
alter table hel_portasignatures add column data_cb_pro timestamp(6) without time zone;
alter table hel_portasignatures add column error_cb_proces text;

----nou camp check per evitar esborrar el contingut de la variable en cas de retrocedir expedient
alter table hel_camp add column ignored BOOLEAN SET DEFAULT 0;
