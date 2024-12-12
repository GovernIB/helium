-- Helium 3.3.5.1

-- #1801 Adaptar el manteniment d'interessats als camps SICRES
-- Posa null a les dates d'actualització i sincronització per actualitzar el CIF de totes les unitats
UPDATE HEL_PARAMETRE SET VALOR = NULL WHERE CODI LIKE 'app.net.caib.helium.unitats.organitzatives.data.sincronitzacio';
UPDATE HEL_PARAMETRE SET VALOR = NULL WHERE CODI LIKE 'app.net.caib.helium.unitats.organitzatives.data.actualitzacio';