package es.caib.helium.dada.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import lombok.Data;
import lombok.experimental.Delegate	;

@Data
public class ValidList<E> implements List<E> {
    @Valid
    @Delegate
    private List<E> list = new ArrayList<>();
}