package es.caib.helium.client.dada.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import lombok.Data;
import lombok.experimental.Delegate	;

/**
 * Classe usada per activar les validacions per cada element d'una llista amb @Valid en el {@link es.caib.helium.dada.controller.ExpedientController}
 */
@Data
public class ValidList<E> implements List<E> {
    @Valid
    @Delegate
    private List<E> list = new ArrayList<>();
}