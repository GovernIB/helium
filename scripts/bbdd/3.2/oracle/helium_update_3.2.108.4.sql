
-------------------------------------------------------------------
-- #1384 Poder posar etiquetes per agrupar camps en els formularis
-- Afegir columna per mostrar o ocultar agrupacions de camps al 
-- formulari de la tasca
-------------------------------------------------------------------
ALTER TABLE HEL_TASCA ADD MOSTRAR_AGRUPACIONS NUMBER(1) DEFAULT 0;
UPDATE HEL_TASCA SET MOSTRAR_AGRUPACIONS = 0;