-------------------------------------------------------------------
-- #1034 Opció de no retrocedir documents
-------------------------------------------------------------------
ALTER TABLE HEL_DOCUMENT ADD COLUMN IGNORED BOOLEAN DEFAULT FALSE;