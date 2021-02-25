package es.caib.helium.domini.mapper;

public interface BaseMapper<E, D> {

    D entityToDto(E entity);
    E dtoToEntity(D dto);

}
