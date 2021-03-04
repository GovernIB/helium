package es.caib.helium.base.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Repository base per a totes les entitats.
 *
 * @author Limit Tecnologies
 */
@NoRepositoryBean
public interface _BaseRepository<E, PK> extends JpaRepository<E, PK>, JpaSpecificationExecutor<E> {

}
