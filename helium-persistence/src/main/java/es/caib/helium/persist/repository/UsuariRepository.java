package es.caib.helium.persist.repository;

import es.caib.helium.persist.entity.Usuari;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsuariRepository extends JpaRepository<Usuari, String> {

    @Query("FROM Usuari u WHERE u.codi in (:codis)")
    List<Usuari> findAllByCodi(List<String> codis);
}
