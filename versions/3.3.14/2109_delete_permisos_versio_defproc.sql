-- Esborra permisos donats per modificar la versió de la definició de procés

DELETE FROM HEL_ACL_ENTRY
WHERE 
MASK = 33554432;