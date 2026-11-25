-- #2109 Canviar el permís "Actualitzar defproc." per "Alta des de CSV" per l'alta d'expedients amb CSV

-- Esborra permisos donats per modificar la versió de la definició de procés
DELETE FROM HEL_ACL_ENTRY
WHERE 
MASK = 33554432;