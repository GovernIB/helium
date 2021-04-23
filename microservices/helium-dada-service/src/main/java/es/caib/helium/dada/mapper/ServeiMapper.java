package es.caib.helium.dada.mapper;

public interface ServeiMapper<E, D> {

    D entityToDto(E entity);
    E dtoToEntity(D dto);

}
