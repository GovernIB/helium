package es.caib.helium.persist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.helium.persist.entity.Usuari;

public interface UsuariRepository extends JpaRepository<Usuari, String> {

}
