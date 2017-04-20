-------------------------------------------------------------------
-- #1034 Opció de no retrocedir documents
-------------------------------------------------------------------
ALTER TABLE HELIUM.HEL_DOCUMENT ADD (IGNORED NUMBER (1) DEFAULT 0);