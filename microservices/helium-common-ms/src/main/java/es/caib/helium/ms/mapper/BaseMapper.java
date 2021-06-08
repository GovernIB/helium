package es.caib.helium.ms.mapper;

public interface BaseMapper<E, D> {

    D entityToDto(E entity);
    E dtoToEntity(D dto);

}
