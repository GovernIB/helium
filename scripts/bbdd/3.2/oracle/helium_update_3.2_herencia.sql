-------------------------------------------------------------------
-- #1043 Herència de tipus d’expedient
-------------------------------------------------------------------

-- Afegeix la propietat d'heretable i la referència al TE pare a la taula de tipus d'expedients
ALTER TABLE HELIUM.HEL_EXPEDIENT_TIPUS ADD (HERETABLE NUMBER (1) DEFAULT 0);
ALTER TABLE HELIUM.HEL_EXPEDIENT_TIPUS ADD (EXPEDIENT_TIPUS_PARE_ID NUMBER (19));

ALTER TABLE HEL_EXPEDIENT_TIPUS ADD (
  CONSTRAINT HEL_EXPTIPUS_PARE_EXPTIPUS_FK FOREIGN KEY (EXPEDIENT_TIPUS_PARE_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID)
);

CREATE INDEX HELIUM.HEL_EXTIP_PARE_I on HELIUM.HEL_EXPEDIENT_TIPUS
(EXPEDIENT_TIPUS_PARE_ID);


-------------------------------------------------------------------
-- #1080 Escriptura de les tasques heretades. Poder afegir variables
-------------------------------------------------------------------

-- Modifica la taula camp tasca per tenir una referència al tipus d'expedient en el cas que hi hagi herència
ALTER TABLE HELIUM.HEL_CAMP_TASCA ADD (EXPEDIENT_TIPUS_ID NUMBER (19));

ALTER TABLE HELIUM.HEL_CAMP_TASCA ADD (
  CONSTRAINT HEL_EXPTIPUS_CAMPTASCA_FK FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID)
);

CREATE INDEX HELIUM.HEL_CAMP_TASCA_EXTIP_I on HELIUM.HEL_CAMP_TASCA
(EXPEDIENT_TIPUS_ID);

-- Modifica la taula camp document per tenir una referència al tipus d'expedient en el cas que hi hagi herència
ALTER TABLE HELIUM.HEL_DOCUMENT_TASCA ADD (EXPEDIENT_TIPUS_ID NUMBER (19));

ALTER TABLE HELIUM.HEL_DOCUMENT_TASCA ADD (
  CONSTRAINT HEL_EXPTIPUS_DOCTASCA_FK FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID)
);

CREATE INDEX HELIUM.HEL_DOCTASCA_EXTIP_I on HELIUM.HEL_DOCUMENT_TASCA
(EXPEDIENT_TIPUS_ID);

-- Modifica la taula firma tasca per tenir una referència al tipus d'expedient en el cas que hi hagi herència
ALTER TABLE HELIUM.HEL_FIRMA_TASCA ADD (EXPEDIENT_TIPUS_ID NUMBER (19));

ALTER TABLE HELIUM.HEL_FIRMA_TASCA ADD (
  CONSTRAINT HEL_EXPTIPUS_FIRTASCA_FK FOREIGN KEY (EXPEDIENT_TIPUS_ID) 
 REFERENCES HEL_EXPEDIENT_TIPUS (ID)
);

CREATE INDEX HELIUM.HEL_FIRTASCA_EXTIP_I on HELIUM.HEL_FIRMA_TASCA
(EXPEDIENT_TIPUS_ID);
