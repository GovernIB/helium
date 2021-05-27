package es.caib.helium.integracio.repository.notificacio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.caib.helium.integracio.domini.notificacio.DocumentNotificacio;

public interface NotificacioRepository extends JpaRepository<DocumentNotificacio, Long>{

}
