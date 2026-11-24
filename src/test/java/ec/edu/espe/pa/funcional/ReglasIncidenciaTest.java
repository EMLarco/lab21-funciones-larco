package ec.edu.espe.pa.funcional;

import ec.edu.espe.pa.funcional.funcional.ProcesadorFuncional;
import ec.edu.espe.pa.funcional.modelo.Incidencia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static ec.edu.espe.pa.funcional.reglas.ReglasIncidencia.*;
import static org.junit.jupiter.api.Assertions.*;

class ReglasIncidenciaTest {

    private final Incidencia abiertaAlta = new Incidencia("INC-001", "Caída del servidor", "ABIERTA", 5);
    private final Incidencia abiertaBaja = new Incidencia("INC-002", "Error tipográfico", "ABIERTA", 2);
    private final Incidencia cerradaAlta = new Incidencia("INC-003", "Fallo de respaldo", "CERRADA", 5);

    @Test
    @DisplayName("REQUIERE_ATENCION: solo si está ABIERTA y es de prioridad alta")
    void requiereAtencionSoloSiEstaAbiertaYEsDePrioridadAlta() {
        assertTrue(REQUIERE_ATENCION.test(abiertaAlta));
        assertFalse(REQUIERE_ATENCION.test(abiertaBaja));
        assertFalse(REQUIERE_ATENCION.test(cerradaAlta));
    }

    @ParameterizedTest(name = "prioridad {0} -> prioridad alta = {1}")
    @CsvSource({"3, false", "4, true", "5, true"})
    @DisplayName("PRIORIDAD_ALTA respeta el umbral (límites 3, 4 y 5)")
    void prioridadAltaRespetaElUmbral(int prioridad, boolean esperado) {
        Incidencia incidencia = new Incidencia("INC-L", "Límite", "ABIERTA", prioridad);
        assertEquals(esperado, PRIORIDAD_ALTA.test(incidencia));
    }

    @Test
    @DisplayName("RESUMEN y CATEGORIA transforman correctamente")
    void resumenYCategoriaTransformanCorrectamente() {
        assertEquals("INC-001 - Caída del servidor", RESUMEN.apply(abiertaAlta));
        assertEquals("CRITICA", CATEGORIA.apply(abiertaAlta));
        assertEquals("INC-001 - CAÍDA DEL SERVIDOR",
                RESUMEN.andThen(String::toUpperCase).apply(abiertaAlta));
    }

    @Test
    @DisplayName("Consumer: la bitácora registra solo las incidencias seleccionadas")
    void consumerRegistraSoloLasSeleccionadas() {
        List<String> bitacora = new ArrayList<>();
        Consumer<Incidencia> registrar = i -> bitacora.add(i.id());
        new ProcesadorFuncional().ejecutar(List.of(abiertaAlta, abiertaBaja, cerradaAlta),
                REQUIERE_ATENCION, registrar);
        assertEquals(List.of("INC-001"), bitacora);
    }

    @ParameterizedTest(name = "prioridad {0} -> IllegalArgumentException")
    @ValueSource(ints = {0, 6})
    @DisplayName("Negativo: el record rechaza prioridades fuera de rango")
    void rechazaPrioridadesFueraDeRango(int prioridad) {
        assertThrows(IllegalArgumentException.class,
                () -> new Incidencia("INC-X", "Inválida", "ABIERTA", prioridad));
    }

    // TODO 1 resuelto: CATEGORIA para las prioridades 1, 3, 4 y 5 (tabla de casos)
    @ParameterizedTest(name = "prioridad {0} -> {1}")
    @CsvSource({"1, BAJA", "2, MEDIA", "3, MEDIA", "4, ALTA", "5, CRITICA"})
    @DisplayName("CATEGORIA devuelve la etiqueta de cada prioridad")
    void categoriaSegunPrioridad(int prioridad, String esperado) {
        Incidencia incidencia = new Incidencia("INC-C", "Categoría", "ABIERTA", prioridad);
        assertEquals(esperado, CATEGORIA.apply(incidencia));
    }

    // TODO 2 resuelto: el Supplier entrega ids distintos y prioridades en 1..5
    @Test
    @DisplayName("Supplier: dos llamadas a get() dan ids distintos y prioridad en 1..5")
    void generadorSinteticoEntregaIncidenciasDistintasYValidas() {
        Supplier<Incidencia> generador = generadorSintetico();
        Incidencia primera = generador.get();
        Incidencia segunda = generador.get();
        assertNotEquals(primera.id(), segunda.id());
        assertTrue(primera.prioridad() >= 1 && primera.prioridad() <= 5);
        assertTrue(segunda.prioridad() >= 1 && segunda.prioridad() <= 5);
    }

    // Tabla completa de casos de la guía (INC-001 a INC-007)
    @ParameterizedTest(name = "{0} {1} p={2} -> abierta={3}, alta={4}, atención={5}, {6}")
    @CsvSource({
            "INC-001, ABIERTA,    5, true,  true,  true,  CRITICA",
            "INC-002, ABIERTA,    2, true,  false, false, MEDIA",
            "INC-003, CERRADA,    5, false, true,  false, CRITICA",
            "INC-004, ABIERTA,    3, true,  false, false, MEDIA",
            "INC-005, ABIERTA,    4, true,  true,  true,  ALTA",
            "INC-006, ABIERTA,    1, true,  false, false, BAJA",
            "INC-007, EN_PROCESO, 4, false, true,  false, ALTA"})
    @DisplayName("Tabla de casos: cada Predicate en true y false, y CATEGORIA")
    void tablaDeCasos(String id, String estado, int prioridad, boolean abierta,
                      boolean alta, boolean atencion, String categoria) {
        Incidencia incidencia = new Incidencia(id, "Caso " + id, estado, prioridad);
        assertAll(
                () -> assertEquals(abierta, ESTA_ABIERTA.test(incidencia)),
                () -> assertEquals(alta, PRIORIDAD_ALTA.test(incidencia)),
                () -> assertEquals(atencion, REQUIERE_ATENCION.test(incidencia)),
                () -> assertEquals(categoria, CATEGORIA.apply(incidencia)));
    }

    @Test
    @DisplayName("Composición: negate, or, and+negate, andThen y compose")
    void composicionDeReglas() {
        Incidencia enProcesoAlta = new Incidencia("INC-007", "En proceso", "EN_PROCESO", 4);
        Predicate<Incidencia> noAbierta = ESTA_ABIERTA.negate();
        Predicate<Incidencia> pendienteBaja = ESTA_ABIERTA.and(PRIORIDAD_ALTA.negate());
        Predicate<Incidencia> revisarHoy = REQUIERE_ATENCION.or(i -> i.prioridad() == 5);

        assertTrue(noAbierta.test(cerradaAlta));
        assertFalse(noAbierta.test(abiertaAlta));
        assertTrue(pendienteBaja.test(abiertaBaja));
        assertFalse(pendienteBaja.test(abiertaAlta));
        assertTrue(revisarHoy.test(cerradaAlta));      // no requiere atención, pero es prioridad 5
        assertFalse(revisarHoy.test(enProcesoAlta));

        Function<String, String> aMayusculas = String::toUpperCase;
        assertEquals(RESUMEN.andThen(aMayusculas).apply(abiertaAlta),
                aMayusculas.compose(RESUMEN).apply(abiertaAlta));
        assertEquals("[ALTA]", CATEGORIA.andThen(c -> "[" + c + "]").apply(enProcesoAlta));
    }

    @Test
    @DisplayName("esCritica (referencia a método) solo acepta prioridad 5")
    void esCriticaComoReferenciaAMetodo() {
        Predicate<Incidencia> critica = ec.edu.espe.pa.funcional.reglas.ReglasIncidencia::esCritica;
        assertTrue(critica.test(abiertaAlta));
        assertFalse(critica.test(new Incidencia("INC-005", "Umbral", "ABIERTA", 4)));
    }
}
