package net.conselldemallorca.helium.v3.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.conselldemallorca.helium.core.model.hibernate.Usuari;

public interface UsuariRepository extends JpaRepository<Usuari, String> {

}
