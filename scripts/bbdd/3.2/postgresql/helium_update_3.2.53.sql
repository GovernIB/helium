-------------------------------------------------------------------
-- #1034 Opció de no retrocedir documents
-------------------------------------------------------------------
ALTER TABLE HELIUM.HEL_DOCUMENT ADD COLUMN "IGNORED" BOOLEAN DEFAULT FALSE;