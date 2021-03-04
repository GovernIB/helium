package es.caib.helium.base.mapper;

public interface _BaseMapper<E, D> {

    D entityToDto(E entity);
    E dtoToEntity(D dto);

}
