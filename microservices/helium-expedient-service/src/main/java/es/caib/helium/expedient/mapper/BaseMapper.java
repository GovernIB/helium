package es.caib.helium.expedient.mapper;

public interface BaseMapper<E, D> {

    D entityToDto(E entity);
    E dtoToEntity(D dto);

}
