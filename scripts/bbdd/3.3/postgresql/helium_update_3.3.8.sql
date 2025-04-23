-- Helium 3.3.8

-- #1846 Evitar el canvi d'entorn funcional involuntari

ALTER TABLE HEL_USUARI_PREFS ADD ENTORN_ACTUAL VARCHAR(64);
ALTER TABLE HEL_USUARI_PREFS ADD ENTORN_ACTUAL_DATA timestamp without time zone;