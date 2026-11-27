package es.caib.helium.logic.helper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/**
 * Fila d'un registre per resoldre els paràmetres de domini que fan referència a un membre del registre
 * ({@link VariableHelper#filaRegistre}).
 */
public class VariableHelperFilaRegistreTest {

	private static final Object[] FILA_A = {"a", 1};
	private static final Object[] FILA_B = {"b", 2};

	/** Com arriba un registre múltiple llegit de les dades de l'expedient (JSON): Object[] de files Object[]. */
	@Test
	public void registreMultipleLlegitDeLesDades() {
		Object valor = new Object[] {FILA_A, FILA_B};
		assertArrayEquals(FILA_A, VariableHelper.filaRegistre(true, valor, 0));
		assertArrayEquals(FILA_B, VariableHelper.filaRegistre(true, valor, 1));
	}

	@Test
	public void registreMultipleComArrayBidimensional() {
		Object valor = new Object[][] {FILA_A, FILA_B};
		assertArrayEquals(FILA_B, VariableHelper.filaRegistre(true, valor, 1));
	}

	@Test
	public void registreMultipleSenseLaFila() {
		Object valor = new Object[] {FILA_A, null};
		assertNull(VariableHelper.filaRegistre(true, valor, 1));
		assertNull(VariableHelper.filaRegistre(true, valor, 2));
		assertNull(VariableHelper.filaRegistre(true, valor, -1));
		assertNull(VariableHelper.filaRegistre(true, valor, null));
		assertNull(VariableHelper.filaRegistre(true, "no és un registre", 0));
	}

	@Test
	public void registreSimple() {
		assertArrayEquals(FILA_A, VariableHelper.filaRegistre(false, FILA_A, null));
		assertArrayEquals(FILA_A, VariableHelper.filaRegistre(false, FILA_A, 0));
		assertNull(VariableHelper.filaRegistre(false, FILA_A, 1));
	}

}
