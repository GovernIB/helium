-- Millorar logs portasignatures
ALTER TABLE HEL_PORTASIGNATURES ADD DATA_CB_DAR TIMESTAMP(6);
ALTER TABLE HEL_PORTASIGNATURES ADD DATA_CB_PRI TIMESTAMP(6);
ALTER TABLE HEL_PORTASIGNATURES ADD ERROR_CB_PROCES CLOB;

--nou camp check per evitar esborrar el contingut de la variable en cas de retrocedir expedient
ALTER TABLE HEL_CAMP add ignored number(1) DEFAULT 0;


--assignació de tasques per tipus d'expedient
ALTER TABLE HEL_REDIR ADD tipusExpedientId NUMBER(19);