-- Millorar logs portasignatures
alter table hel_portasignatures add column data_cb_dar timestamp(6) without time zone;
alter table hel_portasignatures add column data_cb_pro timestamp(6) without time zone;
alter table hel_portasignatures add column error_cb_proces text;
