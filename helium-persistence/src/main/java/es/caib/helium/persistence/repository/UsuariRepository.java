package es.caib.helium.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.caib.helium.persistence.entity.Usuari;

public interface UsuariRepository extends JpaRepository<Usuari, String> {

}
